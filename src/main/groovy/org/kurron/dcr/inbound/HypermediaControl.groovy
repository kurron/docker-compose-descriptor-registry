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

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.Canonical
import javax.validation.constraints.NotNull
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.hateoas.ResourceSupport
import org.springframework.http.MediaType

/**
 * The hypermedia object returned by all services.
 **/
@Canonical
// I shouldn't have to do this but Spring Boot is ignoring my configuration!
@JsonInclude( JsonInclude.Include.NON_EMPTY )
class HypermediaControl extends ResourceSupport {

    /**
     * The string form of the control's MIME-TYPE.
     */
//  static final String MIME_TYPE = 'application/hal+json'
    static final String MIME_TYPE = 'application/json'

    /**
     * The object form of the control's MIME-TYPE.
     */
    static final MediaType MEDIA_TYPE = MediaType.parseMediaType( MIME_TYPE )

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
     * Input only, the Base 64 encoded Docker Compose fragment.
     */
    @JsonProperty( 'fragment' )
    @NotNull
    String fragment

    /**
     * As input, the collection of applications this fragment should be associated with. As output, the collection of all available applications.
     */
    @JsonProperty( 'applications' )
    @NotNull
    @NotEmpty
    List<String> applications

    /**
     * As input, the single release the fragment should be associated with. As output, the collection of all available releases.
     */
    @JsonProperty( 'releases' )
    @NotNull
    List<String> releases

    /**
     * Output only, the collection of all available versions.
     */
    @JsonProperty( 'versions' )
    List<Integer> versions

    /**
     * Output only, the Base 64 encoded Docker Compose descriptor.
     */
    @JsonProperty( 'descriptor' )
    String descriptor

}
