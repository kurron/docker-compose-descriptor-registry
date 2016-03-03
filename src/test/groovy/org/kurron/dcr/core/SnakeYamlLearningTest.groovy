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

import org.kurron.traits.GenerationAbility
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification

/**
 * A learning test to see how SnakeYaml works.
 */
class SnakeYamlLearningTest extends Specification implements GenerationAbility {

    def sut = new Yaml()

    def 'explore treating the document as a map'() {
        given: 'a yaml document stream'
        def yaml = SnakeYamlLearningTest.classLoader.getResourceAsStream( 'docker-compose.yml' )

        when: 'we parse the stream'
        def documents = sut.load( yaml ) as Map

        then: 'we can pull the individual pieces out'
        documents
        def transformed = documents.collectEntries { key, value ->
            println "${key} = ${value}"
            key == 'owncloud' ? [(key): 'replaced'] : [key, value]
        }

        println transformed
    }

}
