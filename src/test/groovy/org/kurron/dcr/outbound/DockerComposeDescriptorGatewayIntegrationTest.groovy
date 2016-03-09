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

package org.kurron.dcr.outbound

import org.kurron.dcr.Application
import org.kurron.dcr.DockerComposeDescriptor
import org.kurron.traits.GenerationAbility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Integration test of the DockerComposeDescriptorGateway.
 */
@WebIntegrationTest( randomPort = true )
@ContextConfiguration( classes = Application, loader = SpringApplicationContextLoader )
class DockerComposeDescriptorGatewayIntegrationTest extends Specification implements GenerationAbility {

    @Autowired
    MongoOperations template

    @Autowired
    DockerComposeDescriptorGateway sut

    def setup() {
        assert template
        template.collectionNames.findAll { !it.startsWith( 'system.' ) }.each {
            template.remove( new Query(), it )
        }
    }

    def 'verify crud methods'() {
        given: 'the gateway was injected'
        sut

        when: 'we insert a document'
        def toSave = new DockerComposeDescriptor( application: randomHexString(),
                                                  release: randomHexString(),
                                                  version: randomPositiveInteger(),
                                                  descriptor: randomByteArray( 8 ) )
        DockerComposeDescriptor saved = sut.save( toSave )

        then: 'we can read it back out'
        DockerComposeDescriptor read = sut.findOne( saved.id )
        read
    }

    // NOTE: we CANNOT use a data driven test because the database gets reset in between iterations
    def 'verify sequence method'() {
        given: 'the gateway was injected'
        sut

        and: 'randomly generated application/release pair'
        def application = randomHexString()
        def release = randomHexString()

        and: 'an expected range of sequence numbers'
        def expected = (1..10)

        when: 'we insert a document multiple times'
        def sequence = expected.collect { sut.nextSequence( application, release ) }

        then: 'the sequence is correct'
        expected == sequence
    }
}
