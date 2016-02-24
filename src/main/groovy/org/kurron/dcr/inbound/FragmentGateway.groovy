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
import static org.springframework.web.bind.annotation.RequestMethod.PUT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Inbound HTTP gateway that supports the Docker Compose fragment resource.
 **/
@RestController
@RequestMapping( '/fragment' )
class FragmentGateway {

    @RequestMapping( consumes = [MIME_TYPE], produces = [MIME_TYPE], method = [PUT] )
    ResponseEntity<HypermediaControl> addFragment() {
        ResponseEntity.ok( new HypermediaControl( ) )
    }
}
