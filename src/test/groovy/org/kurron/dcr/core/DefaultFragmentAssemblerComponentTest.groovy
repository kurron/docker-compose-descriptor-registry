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
import org.kurron.categories.ByteArrayEnhancements
import org.kurron.categories.ComponentTest
import org.kurron.categories.StringEnhancements
import org.kurron.dcr.Application
import org.kurron.dcr.models.DockerComposeFragment
import org.kurron.traits.GenerationAbility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ContextConfiguration
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification

/**
 * Component test of the DockerComposeDescriptorGateway.
 */
@Category( ComponentTest )
@IntegrationTest
@ContextConfiguration( classes = Application, loader = SpringApplicationContextLoader )
class DefaultFragmentAssemblerComponentTest extends Specification implements GenerationAbility {

    @Autowired
    MongoOperations template

    @Autowired
    DefaultFragmentAssembler sut

    def setup() {
        assert template
        template.collectionNames.findAll { !it.startsWith( 'system.' ) }.each {
            template.remove( new Query(), it )
        }
    }

    def 'verify assemble function with empty database'() {
        given: 'a valid subject under test'
        assert sut

        and: 'add a new fragment'
        def applications = (1..3).collect { randomHexString() }
        def release = randomHexString()
        def yml = createYml()
        def fragment = new DockerComposeFragment( stacks: applications, release: release, fragment: yml )

        when: 'we add it to the system'
        def results = sut.assemble( fragment )

        then: 'we get descriptors for each application'
        applications.every { application ->
            results.find { it.stack == application }
        }

        and: 'dump the descriptors for human examination'
        results.each {
            println use( ByteArrayEnhancements ) { ->
                it.descriptor.toStringUtf8()
            }
        }
    }

    def 'verify assemble function with populated database'() {
        given: 'a valid subject under test'
        assert sut

        and: 'and fragment coordinates'
        def applications = (1..3).collect { randomHexString() }
        def release = randomHexString()

        and: 'add the Redis fragment'
        def redis = new DockerComposeFragment( stacks: applications, release: release, fragment: createYml( '10', false ) )
        sut.assemble( redis )

        and: 'add the MongoDB fragment'
        def mongoDB = new DockerComposeFragment( stacks: applications, release: release, fragment: createYml( '10' ) )
        sut.assemble( mongoDB )

        when: 'we add another MongoDB fragment'
        def fragment = new DockerComposeFragment( stacks: applications, release: release, fragment: createYml( '20' ) )
        def results = sut.assemble( fragment )

        then: 'we get descriptors for each application'
        applications.every { application ->
            results.find { it.stack == application }
        }

        and: 'dump the descriptors for human examination'
        results.each {
            println use( ByteArrayEnhancements ) { ->
                it.descriptor.toStringUtf8()
            }
        }
    }

    byte[] createYml( String label = '0', boolean createMongodbFragment = true ) {
        def options = new DumperOptions()
        options.canonical = false
        options.indent = 4
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        options.defaultScalarStyle = DumperOptions.ScalarStyle.PLAIN
        options.prettyFlow = true
        def parser = new Yaml( options )
        def mongoMap = ['mongodb': ['image': 'mongo',
                                    'container_name': 'mongodb',
                                    'volumes_from': ['mongodb-data'],
                                    'restart': 'always',
                                    'ports': ['27017:27017'],
                                    'labels': ['com.example.revision': label],
                                    'net': 'host',
                                    'command': '--storageEngine=wiredTiger --wiredTigerCacheSizeGB=1 --notablescan --journalCommitInterval=300 --directoryperdb']
        ]
        def redisMap = ['redis': ['image': 'redis',
                                  'container_name': 'redis',
                                  'volumes_from': ['redis-data'],
                                  'restart': 'always',
                                  'ports': ['6379:6379'],
                                  'labels': ['com.example.revision': '0'],
                                  'net': 'host']]

        use( StringEnhancements ) { ->
            parser.dump( createMongodbFragment ? mongoMap : redisMap ).utf8Bytes
        }
    }
}
