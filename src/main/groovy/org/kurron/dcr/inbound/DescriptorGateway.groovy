/*
 * Copyright (c) 2016. Ronald D. Kurr kurr@jvmguy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kurron.dcr.inbound

import static org.kurron.dcr.inbound.HypermediaControl.MIME_TYPE
import java.time.Instant
import javax.servlet.http.HttpServletRequest
import org.kurron.categories.ByteArrayEnhancements
import org.kurron.dcr.outbound.DockerComposeDescriptorGateway
import org.kurron.stereotype.InboundRestGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.HandlerMapping

/**
 * Inbound HTTP gateway that supports the Docker Compose descriptor resource.
 **/
@InboundRestGateway
@RequestMapping( path = '/descriptor' )
class DescriptorGateway {

    /**
     * Knows how to access the persistence store.
     */
    @Autowired
    private DockerComposeDescriptorGateway gateway

    @RequestMapping( path = '/application', method = [RequestMethod.GET], consumes = [MIME_TYPE], produces = [MIME_TYPE] )
    ResponseEntity<HypermediaControl> fetchApplicationList( HttpServletRequest request ) {
        def control = defaultControl( request )
        control.applications = gateway.distinctApplications()
        ResponseEntity.ok( control )
    }

    @RequestMapping( path = '/application/{application}', method = [RequestMethod.GET],  consumes = [MIME_TYPE], produces = [MIME_TYPE] )
    ResponseEntity<HypermediaControl> fetchReleasesList(  HttpServletRequest request, @PathVariable String application ) {
        def control = defaultControl( request )
        control.applications = [application]
        control.releases = gateway.distinctReleases( application )
        ResponseEntity.ok( control )
    }

    @RequestMapping( path = '/application/{application}/{release}', method = [RequestMethod.GET],  consumes = [MIME_TYPE], produces = [MIME_TYPE] )
    ResponseEntity<HypermediaControl> fetchVersionList( HttpServletRequest request,
                                                        @PathVariable String application,
                                                        @PathVariable String release ) {
        def control = defaultControl( request )
        control.applications = [application]
        control.releases = [release]
        control.versions = gateway.distinctVersions( application, release )
        ResponseEntity.ok( control )
    }

    @RequestMapping( path = '/application/{application}/{release}/{version}', method = [RequestMethod.GET], produces = [MIME_TYPE]  )
    ResponseEntity<HypermediaControl> fetchDescriptor( HttpServletRequest request,
                                                       @PathVariable String application,
                                                       @PathVariable String release,
                                                       @PathVariable Integer version ) {
        def control = defaultControl( request )
        control.applications = [application]
        control.releases = [release]
        control.versions = [version]
        def optional = gateway.findOne( application, release, version )
        control.status = optional.present ? HttpStatus.OK.value() : HttpStatus.NOT_FOUND.value()
        optional.ifPresent { descriptor ->
            control.descriptor = use( ByteArrayEnhancements ) { ->
                descriptor.descriptor.toStringBase64()
            }
        }
        new ResponseEntity<HypermediaControl>( control, optional.present ? HttpStatus.OK : HttpStatus.NOT_FOUND )
    }

    private static HypermediaControl defaultControl( HttpServletRequest request ) {
        def path = request.getAttribute( HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE )
        new HypermediaControl( status: HttpStatus.OK.value(), timestamp: Instant.now().toString(), path: path )
    }
}
