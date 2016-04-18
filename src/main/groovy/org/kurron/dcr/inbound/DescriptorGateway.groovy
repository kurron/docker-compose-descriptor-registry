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
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.web.bind.annotation.RequestMethod.GET
import groovy.transform.CompileDynamic
import java.util.function.Supplier
import javax.servlet.http.HttpServletRequest
import org.kurron.categories.ByteArrayEnhancements
import org.kurron.dcr.models.MessagingContext
import org.kurron.stereotype.InboundRestGateway
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Inbound HTTP gateway that supports the Docker Compose descriptor resource.  Rundeck has particular
 * formatting needs so it will only get application/json forms.  Other clients can use the hypermedia
 * control.
 **/
@InboundRestGateway
@RequestMapping
class DescriptorGateway extends BaseGateway {

    @RequestMapping( path = '/descriptor/stacks', method = [GET], produces = [MIME_TYPE] )
    ResponseEntity<HypermediaControl> fetchApplicationList( HttpServletRequest request ) {
        def control = defaultControl( request )
        control.stacks = gateway.distinctStacks()
        ResponseEntity.ok( control )
    }

    @RequestMapping( path = '/descriptor/stacks', method = [GET], produces = [APPLICATION_JSON_VALUE] )
    ResponseEntity<List<String>> fetchApplicationList() {
        def applications = gateway.distinctStacks()
        ResponseEntity.ok( applications )
    }

    @RequestMapping( path = '/descriptor/stack/{stack}', method = [GET],  produces = [MIME_TYPE] )
    ResponseEntity<HypermediaControl> fetchReleasesList( HttpServletRequest request, @PathVariable String stack ) {
        def control = defaultControl( request )
        control.stacks = [stack]
        control.releases = gateway.distinctReleases( stack )
        ResponseEntity.ok( control )
    }

    @RequestMapping( path = '/descriptor/stack/{stack}', method = [GET], produces = [APPLICATION_JSON_VALUE] )
    ResponseEntity<List<String>> fetchReleasesList( @PathVariable String stack ) {
        def releases = gateway.distinctReleases( stack )
        ResponseEntity.ok( releases )
    }


    @RequestMapping( path = '/descriptor/stack/{stack}/{release}', method = [GET], produces = [MIME_TYPE] )
    ResponseEntity<HypermediaControl> fetchVersionList( HttpServletRequest request,
                                                        @PathVariable String stack,
                                                        @PathVariable String release ) {
        def control = defaultControl( request )
        control.stacks = [stack]
        control.releases = [release]
        control.versions = gateway.distinctVersions( stack, release )
        ResponseEntity.ok( control )
    }

    @RequestMapping( path = '/descriptor/stack/{stack}/{release}', method = [GET], produces = [APPLICATION_JSON_VALUE] )
    ResponseEntity<List<String>> fetchVersionList( @PathVariable String stack,
                                                   @PathVariable String release ) {
        def versions = gateway.distinctVersions( stack, release ).collect { it.toString() }
        ResponseEntity.ok( versions )
    }

    @RequestMapping( path = '/descriptor/stack/{stack}/{release}/{version}', method = [GET], produces = [MIME_TYPE]  )
    @CompileDynamic // the use of Traits require this 8-(
    ResponseEntity<HypermediaControl> fetchDescriptor( HttpServletRequest request,
                                                       @PathVariable String stack,
                                                       @PathVariable String release,
                                                       @PathVariable Integer version ) {
        def control = defaultControl( request )
        control.stacks = [stack]
        control.releases = [release]
        control.versions = [version]
        def optional = gateway.findOne( stack, release, version )
        def error = { new ResourceNotFoundError( MessagingContext.RESOURCE_NOT_FOUND, extractPath( request ) ) } as Supplier<ResourceNotFoundError>
        optional.orElseThrow( error )

        optional.ifPresent { descriptor ->
            control.descriptor = use( ByteArrayEnhancements ) { ->
                descriptor.descriptor.toStringBase64()
            }
        }
        new ResponseEntity<HypermediaControl>( control, optional.present ? OK : NOT_FOUND )
    }
}
