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
import org.kurron.dcr.core.FragmentAssembler
import org.kurron.dcr.models.DockerComposeFragment
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
 * Integration test of the DescriptorGateway.
 */
@Category( InboundIntegrationTest )
@WebIntegrationTest( randomPort = true )
@ContextConfiguration( classes = Application, loader = SpringApplicationContextLoader )
class DescriptorGatewayIntegrationTest extends Specification implements GenerationAbility, RestCapable, YamlCapable {

    @Value( '${local.server.port}' )
    int port

    private def template = new TestRestTemplate()

    @Autowired
    FragmentAssembler assembler

    @Autowired
    MongoOperations mongodb

    List<DockerComposeFragment> fragments

    def setup() {
        assert mongodb
        mongodb.collectionNames.findAll { !it.startsWith( 'system.' ) }.each {
            mongodb.remove( new Query(), it )
        }

        assert assembler
        fragments = (1..10).collect {
            new DockerComposeFragment( applications: (1..3).collect { randomHexString() },
                                       release: randomHexString(),
                                       fragment: createYmlBytes() )
        }
        fragments.each {
            assembler.assemble( it )
        }
    }

    def 'verify GET /descriptor/application'() {
        given: 'a proper testing environment'
        assert port

        when: 'we GET /descriptor/application'
        def uri = buildURI( port, '/descriptor/application', [:] )
        def response = template.exchange( uri, HttpMethod.GET, buildRequest(), HypermediaControl )

        then: 'we get a proper response'
        HttpStatus.OK == response.statusCode
        response.body.applications
    }

    def 'verify GET /rundeck/application'() {
        given: 'a proper testing environment'
        assert port

        when: 'we GET /rundeck/application'
        def uri = buildURI( port, '/rundeck/application', [:] )
        def response = template.exchange( uri, HttpMethod.GET, buildRequest(), String )

        then: 'we get a proper response'
        HttpStatus.OK == response.statusCode
        response.body
    }

    def 'verify GET /descriptor/application/{application}'() {
        given: 'a proper testing environment'
        assert port

        when: 'we GET /descriptor/application/{application}'
        def application = fragments.first().applications.first()
        def uri = buildURI( port, '/descriptor/application/{application}', [application: application] )
        def response = template.exchange( uri, HttpMethod.GET, buildRequest(), HypermediaControl )

        then: 'we get a proper response'
        HttpStatus.OK == response.statusCode
        response.body.applications
        response.body.releases
    }

    def 'verify GET /rundeck/application/{application}'() {
        given: 'a proper testing environment'
        assert port

        when: 'we GET /rundeck/application/{application}'
        def application = fragments.first().applications.first()
        def uri = buildURI( port, '/rundeck/application/{application}', [application: application] )
        def response = template.exchange( uri, HttpMethod.GET, buildRequest(), String )

        then: 'we get a proper response'
        HttpStatus.OK == response.statusCode
        response.body
    }

    def 'verify GET /descriptor/application/{application}/{release}'() {
        given: 'a proper testing environment'
        assert port

        when: 'we GET /descriptor/application/{application}/{release}'
        def application = fragments.first().applications.first()
        def release = fragments.first().release
        def uri = buildURI( port, '/descriptor/application/{application}/{release}', [application: application, release: release] )
        def response = template.exchange( uri, HttpMethod.GET, buildRequest(), HypermediaControl )

        then: 'we get a proper response'
        HttpStatus.OK == response.statusCode
        response.body.applications
        response.body.releases
        response.body.versions
    }

    def 'verify GET /rundeck/application/{application}/{release}'() {
        given: 'a proper testing environment'
        assert port

        when: 'we GET /rundeck/application/{application}/{release}'
        def application = fragments.first().applications.first()
        def release = fragments.first().release
        def uri = buildURI( port, '/rundeck/application/{application}/{release}', [application: application, release: release] )
        def response = template.exchange( uri, HttpMethod.GET, buildRequest(), String )

        then: 'we get a proper response'
        HttpStatus.OK == response.statusCode
        response.body
    }

    def 'verify GET /descriptor/application/{application}/{release}/{version}'() {
        given: 'a proper testing environment'
        assert port

        when: 'we GET /descriptor/application/{application}/{release}/{version}'
        def application = fragments.first().applications.first()
        def release = fragments.first().release
        def version = loadVersion( application, release )
        def uri = buildURI( port, '/descriptor/application/{application}/{release}/{version}', [application: application, release: release, version: version] )
        def response = template.exchange( uri, HttpMethod.GET, buildRequest(), HypermediaControl )

        then: 'we get a proper response'
        HttpStatus.OK == response.statusCode
        response.body.applications
        response.body.releases
        response.body.versions
        response.body.descriptor
    }

    Integer loadVersion( String application, String release ) {
        def uri = buildURI( port, '/descriptor/application/{application}/{release}', [application: application, release: release] )
        def response = template.exchange( uri, HttpMethod.GET, buildRequest(), HypermediaControl )
        assert response.statusCode == HttpStatus.OK
        response.body.versions.first()
    }

}
