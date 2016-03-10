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

import org.junit.experimental.categories.Category
import org.kurron.categories.InboundIntegrationTest
import org.kurron.dcr.Application
import org.kurron.traits.GenerationAbility
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.TestRestTemplate
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Specification

/**
 * Integration test of the DescriptorGateway.
 */
@Category( InboundIntegrationTest )
@WebIntegrationTest( randomPort = true )
@ContextConfiguration( classes = Application, loader = SpringApplicationContextLoader )
class DescriptorGatewayIntegrationTest extends Specification implements GenerationAbility {

    @Value( '${local.server.port}' )
    int port

    private def template = new TestRestTemplate()


    def 'verify GET /descriptor/application'() {
        given: 'the port was injected'
        assert port

        when: 'we GET /descriptor/descriptor'
        def response = template.exchange( buildURI( '/descriptor/application' ), HttpMethod.GET, buildRequest(), HypermediaControl )

        then: 'we get a list of applications in the system'
        HttpStatus.OK == response.statusCode
    }

    private static HttpEntity buildRequest() {
        def headers = new HttpHeaders()
        headers.setContentType( HypermediaControl.MIME_TYPE )
        new HttpEntity( headers )
    }

    private URI buildURI( String path ) {
        UriComponentsBuilder.newInstance(  ).scheme( 'http' ).host( 'localhost' ).port( port ).path( path ).build().toUri()
    }
}
