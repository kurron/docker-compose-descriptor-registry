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

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.web.util.UriComponentsBuilder

/**
 * Traits shared by all test that are capable of exercising a REST API.
 **/
trait RestCapable {

    /**
     * Constructs a local URL, expanding and supplied path variables.
     * @param port the port the service is listening on.
     * @param path the path to expand.
     * @param variables any path variables that need to be inserted into the path.  Cannot be null.
     * @return fully constructed URI.
     */
    URI buildURI( int port, String path, Map variables ) {
        UriComponentsBuilder.newInstance().scheme( 'http' ).host( 'localhost' ).port( port ).path( path ).buildAndExpand( variables ).toUri()
    }

    /**
     * Constructs an request with only the proper headers filled in -- no body.
     * @return constructed request.
     */
    HttpEntity buildRequest() {
        def headers = new HttpHeaders()
        headers.setContentType( HypermediaControl.MEDIA_TYPE )
        headers.setAccept( [HypermediaControl.MEDIA_TYPE] )
        new HttpEntity( headers )
    }
}