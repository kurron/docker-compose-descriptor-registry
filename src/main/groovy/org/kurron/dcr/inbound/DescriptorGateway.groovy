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
import groovy.transform.CompileDynamic
import javax.servlet.http.HttpServletRequest
import org.kurron.categories.ByteArrayEnhancements
import org.kurron.stereotype.InboundRestGateway
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * Inbound HTTP gateway that supports the Docker Compose descriptor resource.
 **/
@InboundRestGateway
@RequestMapping
class DescriptorGateway extends BaseGateway {

    @RequestMapping( path = '/descriptor/application', method = [RequestMethod.GET], produces = [MIME_TYPE] )
    ResponseEntity<HypermediaControl> fetchApplicationList( HttpServletRequest request ) {
        def control = defaultControl( request )
        control.applications = gateway.distinctApplications()
        ResponseEntity.ok( control )
    }

    @RequestMapping( path = '/descriptor/application/{application}', method = [RequestMethod.GET],  produces = [MIME_TYPE] )
    ResponseEntity<HypermediaControl> fetchReleasesList( HttpServletRequest request, @PathVariable String application ) {
        def control = defaultControl( request )
        control.applications = [application]
        control.releases = gateway.distinctReleases( application )
        ResponseEntity.ok( control )
    }

    @RequestMapping( path = '/descriptor/application/{application}/{release}', method = [RequestMethod.GET], produces = [MIME_TYPE] )
    ResponseEntity<HypermediaControl> fetchVersionList( HttpServletRequest request,
                                                        @PathVariable String application,
                                                        @PathVariable String release ) {
        def control = defaultControl( request )
        control.applications = [application]
        control.releases = [release]
        control.versions = gateway.distinctVersions( application, release )
        ResponseEntity.ok( control )
    }

    @RequestMapping( path = '/descriptor/application/{application}/{release}/{version}', method = [RequestMethod.GET], produces = [MIME_TYPE]  )
    @CompileDynamic // the use of Traits require this 8-(
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

    // ------------ Rundeck variants -----------------

    @RequestMapping( path = '/rundeck/application', method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE] )
    ResponseEntity<List<String>> fetchApplicationList() {
        def applications = gateway.distinctApplications()
        ResponseEntity.ok( applications )
    }

    @RequestMapping( path = '/rundeck/application/{application}', method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE] )
    ResponseEntity<List<String>> fetchReleasesList( @PathVariable String application ) {
        def releases = gateway.distinctReleases( application )
        ResponseEntity.ok( releases )
    }

    @RequestMapping( path = '/rundeck/application/{application}/{release}', method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE] )
    ResponseEntity<List<String>> fetchVersionList( @PathVariable String application,
                                                        @PathVariable String release ) {
        def versions = gateway.distinctVersions( application, release ).collect { it.toString() }
        ResponseEntity.ok( versions )
    }


}
