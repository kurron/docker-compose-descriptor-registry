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
import org.kurron.categories.ByteArrayEnhancements
import org.kurron.dcr.outbound.DockerComposeDescriptorGateway
import org.kurron.stereotype.InboundRestGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

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
    ResponseEntity<HypermediaControl> fetchApplicationList() {
        def control = defaultControl()
        control.applications = gateway.distinctApplications()
        ResponseEntity.ok( control )
    }

    @RequestMapping( path = '/application/{application}', method = [RequestMethod.GET],  consumes = [MIME_TYPE], produces = [MIME_TYPE] )
    ResponseEntity<HypermediaControl> fetchReleasesList( @RequestParam( name = 'application' ) String application ) {
        def control = defaultControl()
        control.applications = [application]
        control.releases = gateway.distinctReleases( application )
        ResponseEntity.ok( control )
    }

    @RequestMapping( path = '/application/{application}/{release}', method = [RequestMethod.GET],  consumes = [MIME_TYPE], produces = [MIME_TYPE] )
    ResponseEntity<HypermediaControl> fetchVersionList( @RequestParam( name = 'application' ) String application,
                                                        @RequestParam( name = 'release' ) String release ) {
        def control = defaultControl()
        control.applications = [application]
        control.releases = [release]
        control.versions = gateway.distinctVersions( application, release )
        ResponseEntity.ok( control )
    }

    @RequestMapping( path = '/application/{application}/{release}/{version}', method = [RequestMethod.GET], produces = [MIME_TYPE]  )
    ResponseEntity<HypermediaControl> fetchDescriptor( @RequestParam( name = 'application' ) String application,
                                                       @RequestParam( name = 'release' ) String release,
                                                       @RequestParam( name = 'version' ) Integer version ) {
        def control = defaultControl()
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

    private static HypermediaControl defaultControl() {
        new HypermediaControl( status: HttpStatus.OK.value(), timestamp: Instant.now().toString() )
    }
}
