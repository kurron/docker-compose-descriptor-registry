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

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.Canonical
import javax.validation.constraints.NotNull
import org.springframework.hateoas.ResourceSupport

/**
 * The hypermedia object returned by all services.
 **/
@Canonical
class HypermediaControl extends ResourceSupport {

    /**
     * The MIME-TYPE of the control.
     */
    static final String MIME_TYPE = 'application/hal+json'

    /**
     * HTTP status of the service, response-only.
     */
    @JsonProperty( 'status' )
    Integer status

    /**
     * ISO 8601 timestamp of when the service completed, response-only.
     */
    @JsonProperty( 'timestamp' )
    String timestamp

    /**
     * Relative path of the completed service, response-only.
     */
    @JsonProperty( 'path' )
    String path

    /**
     * The Base 64 encoded Docker Compose fragment.
     */
    @JsonProperty( 'fragment' )
    @NotNull
    String fragment
}