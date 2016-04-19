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
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Learning test to see how easy it is to build download descriptors. Expect the database to already be
 * populated via populate-registry.groovy.
 */
@Ignore
@Category( InboundIntegrationTest )
@WebIntegrationTest( randomPort = true )
@ContextConfiguration( classes = Application, loader = SpringApplicationContextLoader )
class DescriptorGatewayDownloadLearningTest extends Specification implements GenerationAbility, RestCapable, YamlCapable {

    @Value( '${local.server.port}' )
    int port

    private def template = new TestRestTemplate()

    def 'verify GET /descriptor/stack/{stack}/{release}/{version}'() {
        given: 'a proper testing environment'
        assert port

        and: 'the list of available stacks'
        def applicationURI = buildURI( port, '/descriptor/stacks', [:] )
        def applicationResponse = template.exchange( applicationURI, HttpMethod.GET, buildRequest( HypermediaControl.MEDIA_TYPE ), HypermediaControl )
        assert HttpStatus.OK == applicationResponse.statusCode
        def applications = applicationResponse.body.stacks

        and: 'the list of available releases'
        def releases = applications.collect {
            def releaseURI = buildURI( port, '/descriptor/stack/{stack}', [stack: it] )
            def releaseResponse = template.exchange( releaseURI, HttpMethod.GET, buildRequest( HypermediaControl.MEDIA_TYPE ), HypermediaControl )
            assert HttpStatus.OK == releaseResponse.statusCode
            releaseResponse.body.releases
        }.flatten().unique()

        and: 'the list of available versions'
        def versions = [applications, releases].combinations { application, release ->
            def versionURI = buildURI( port, '/descriptor/stack/{stack}/{release}', [stack: application, release: release] )
            def versionResponse = template.exchange( versionURI, HttpMethod.GET, buildRequest( HypermediaControl.MEDIA_TYPE ), HypermediaControl )
            assert HttpStatus.OK == versionResponse.statusCode
            versionResponse.body.versions
        }.flatten().unique()

        when: 'we grab all of the descriptors'
//      def descriptors = [applications, releases, versions].combinations { application, release, version ->
        def descriptors = [['TLO GE'], ['Milestone'], versions].combinations { stack, release, version ->
            def uri = buildURI( port, '/descriptor/stack/{stack}/{release}/{version}', [stack: stack, release: release, version: version] )
            def response = template.exchange( uri, HttpMethod.GET, buildRequest( HypermediaControl.MEDIA_TYPE ), HypermediaControl )
            assert HttpStatus.OK == response.statusCode
            response.body
        }

        then: 'we dump them out'
        descriptors.each { HypermediaControl control ->
            println control.path
            println new String( control.descriptor.decodeBase64(), 'UTF-8' )
            println()
        }
    }
}
