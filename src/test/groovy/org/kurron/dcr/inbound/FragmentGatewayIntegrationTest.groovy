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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.TestRestTemplate
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Integration test of the FragmentGateway.
 */
@Category( InboundIntegrationTest )
@WebIntegrationTest( randomPort = true )
@ContextConfiguration( classes = Application, loader = SpringApplicationContextLoader )
class FragmentGatewayIntegrationTest extends Specification implements GenerationAbility, RestCapable, YamlCapable {

    @Value( '${local.server.port}' )
    int port

    private def template = new TestRestTemplate()

    @Autowired
    MongoOperations mongodb

    def setup() {
        assert mongodb
        mongodb.collectionNames.findAll { !it.startsWith( 'system.' ) }.each {
            mongodb.remove( new Query(), it )
        }
    }

    def 'verify PUT /fragment'() {
        given: 'a proper testing environment'
        assert port

        when: 'we PUT /fragment'
        def uri = buildURI( port, '/fragment', [:] )
        def control = new HypermediaControl( fragment: createYmlBase64(),
                                             applications: [randomHexString()],
                                             releases: [randomHexString()] )
        def response = template.exchange( uri, HttpMethod.PUT, buildRequest( control ), HypermediaControl )

        then: 'we get a proper response'
        HttpStatus.OK == response.statusCode
        response.body.applications
    }

}
