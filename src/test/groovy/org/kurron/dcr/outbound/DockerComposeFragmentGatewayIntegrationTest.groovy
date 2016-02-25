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
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Integration test of the DockerComposeFragmentGateway.
 */
@WebIntegrationTest( randomPort = true )
@ContextConfiguration( classes = Application, loader = SpringApplicationContextLoader )
class DockerComposeFragmentGatewayIntegrationTest extends Specification implements GenerationAbility {

    @Autowired
    MongoOperations template

    @Autowired
    DockerComposeFragmentGateway sut

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
        def toSave = new DockerComposeFragment( release: randomHexString(),
                                                version: randomHexString(),
                                                applications: (1..3).collect { randomHexString() },
                                                fragment: randomByteArray( 8 ) )
        DockerComposeFragment saved = sut.save( toSave )

        then: 'we can read it back out'
        DockerComposeFragment read = sut.findOne( saved.id )
        read
    }

    def possibleApplications = [ randomHexString(), randomHexString(), randomHexString()].sort( false )
    def possibleReleases = [ randomHexString(), randomHexString(), randomHexString()].sort( false )
    def possibleVersions = [ randomHexString(), randomHexString(), randomHexString()].sort( false )

    def 'verify distinct application retrieval'() {
        given: 'the gateway was injected'
        sut

        when: 'we insert multiple documents'
        def toSave = (1..100).collect {
            new DockerComposeFragment( release: randomHexString(),
                                       version: randomHexString(),
                                       applications: (1..2).collect { randomElement( possibleApplications ) as String },
                                       fragment: randomByteArray( 8 ) )
        }
        sut.save( toSave )

        then: 'we can read out the distinct application values'
        def distinct = sut.distinctApplications().sort( false )
        distinct == possibleApplications
    }

    def 'verify distinct release retrieval'() {
        given: 'the gateway was injected'
        sut

        when: 'we insert multiple documents'
        def toSave = (1..100).collect {
            new DockerComposeFragment( release: randomElement( possibleReleases ) as String,
                                       version: randomHexString(),
                                       applications: (1..2).collect { randomElement( possibleApplications ) as String },
                                       fragment: randomByteArray( 8 ) )
        }
        sut.save( toSave )

        then: 'we can read out the distinct release values'
        def distinct = sut.distinctReleases( toSave.first().applications.first() ).sort( false )
        distinct == possibleReleases
    }

    def 'verify distinct version retrieval'() {
        given: 'the gateway was injected'
        sut

        when: 'we insert multiple documents'
        def toSave = (1..100).collect {
            new DockerComposeFragment( release: randomElement( possibleReleases ) as String,
                                       version: randomElement( possibleVersions ) as String,
                                       applications: (1..2).collect { randomElement( possibleApplications ) as String },
                                       fragment: randomByteArray( 8 ) )
        }
        sut.save( toSave )

        then: 'we can read out the distinct version values'

        def first = toSave.first()
        def distinct = sut.distinctVersions( first.applications.first(), first.release ).sort( false )
        distinct == possibleVersions
    }

    def 'verify findOne'() {
        given: 'the gateway was injected'
        sut

        when: 'we insert multiple documents'
        def toSave = (1..10).collect {
            new DockerComposeFragment( release: randomElement( possibleReleases ) as String,
                                       version: randomElement( possibleVersions ) as String,
                                       applications: (1..2).collect { randomElement( possibleApplications ) as String },
                                       fragment: randomByteArray( 8 ) )
        }
        // scrub the first one so that there is only one of its type is in the db
        toSave.first().with {
            release = 'release'
            version = 'version'
            applications = ['a','b','c']
        }
        sut.save( toSave )

        then: 'we can read out the specific document'
        def first = toSave.first()
        def found = sut.findOne( first.applications.first(), first.release, first.version )
        found == first
    }
}