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
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Inbound HTTP gateway that supports the Docker Compose descriptor resource.
 **/
@RestController
@RequestMapping( path = '/descriptor', consumes = [MIME_TYPE], produces = [MIME_TYPE] )
class DescriptorGateway {

    @RequestMapping( path = '/application', method = [RequestMethod.GET] )
    ResponseEntity<HypermediaControl> fetchApplicationList() {
        ResponseEntity.ok( new HypermediaControl( ) )
    }

    @RequestMapping( path = '/application/{release}', method = [RequestMethod.GET] )
    ResponseEntity<HypermediaControl> fetchReleasesList() {
        ResponseEntity.ok( new HypermediaControl( ) )
    }

    @RequestMapping( path = '/application/{release}/{version}', method = [RequestMethod.GET] )
    ResponseEntity<HypermediaControl> fetchVersionList() {
        ResponseEntity.ok( new HypermediaControl( ) )
    }

    @RequestMapping( path = '/{id}', method = [RequestMethod.GET] )
    ResponseEntity<HypermediaControl> fetchDescriptor() {
        ResponseEntity.ok( new HypermediaControl( ) )
    }
}
