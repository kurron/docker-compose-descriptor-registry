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

package org.kurron.dcr.core

import org.junit.experimental.categories.Category
import org.kurron.categories.ComponentTest
import org.kurron.dcr.Application
import org.kurron.dcr.DockerComposeFragment
import org.kurron.traits.GenerationAbility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Component test of the DockerComposeDescriptorGateway.
 */
@Category( ComponentTest )
@IntegrationTest
@ContextConfiguration( classes = Application, loader = SpringApplicationContextLoader )
class DefaultFragmentAssemblerComponentTest extends Specification implements GenerationAbility {

    @Autowired
    DefaultFragmentAssembler sut

    def 'verify assemble function'() {
        given: 'a valid subject under test'
        assert sut

        and: 'add a new fragment'
        def applications = (1..3).collect { randomHexString() }
        def release = randomHexString()
        def version = randomHexString()
        def yml = randomByteArray( 32 )
        def fragment = new DockerComposeFragment( applications: applications, release: release, version: version, fragment: yml )

        when: 'we add it to the system'
        def results = sut.assemble( fragment )

        then: 'we get descriptors for each application'
        applications.every { application ->
            results.find { it.application == application }
        }
    }
}
