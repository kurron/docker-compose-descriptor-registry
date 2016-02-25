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
import org.kurron.dcr.DockerComposeFragment
import org.kurron.traits.GenerationAbility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Integration test of the DockerComposeFragmentGateway.
 */
@WebIntegrationTest( randomPort = true )
@ContextConfiguration( classes = Application, loader = SpringApplicationContextLoader )
class DockerComposeFragmentGatewayIntegrationTest extends Specification implements GenerationAbility {

    @Autowired
    DockerComposeFragmentGateway sut

    def 'verify crud methods'() {
        given: 'the gateway was injected'
        sut

        when: 'we insert a document'
        def toSave = new DockerComposeFragment( release: randomHexString(),
                                                version: randomHexString(),
                                                applications: (1..3).collect { randomHexString() },
                                                fragment: randomByteArray( 8 ) )
        DockerComposeFragment saved = sut.save( toSave )

        then: 'we can read it back out'
        DockerComposeFragment read = sut.findOne( saved.id )
        read
    }
}
