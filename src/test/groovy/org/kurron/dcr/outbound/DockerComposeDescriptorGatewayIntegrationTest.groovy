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

import org.junit.experimental.categories.Category
import org.kurron.categories.OutboundIntegrationTest
import org.kurron.dcr.Application
import org.kurron.dcr.models.DockerComposeDescriptor
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
@Category( OutboundIntegrationTest )
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
        assert sut

        when: 'we insert a document'
        def toSave = new DockerComposeDescriptor( stack: randomHexString(),
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
        assert sut

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

    def 'verify mostCurrent with empty database'() {
        given: 'the gateway was injected'
        assert sut

        when: 'we call mostCurrent'
        def found = sut.mostCurrent( randomHexString(), randomHexString() )

        then: 'we can detect it was not found'
        !found.present
    }

    def 'verify mostCurrent with a loaded database'() {
        given: 'the gateway was injected'
        assert sut

        and: 'several documents are inserted into the database'
        def application = randomHexString()
        def release = randomHexString()
        def toInsert = (1..1000).collect { new DockerComposeDescriptor( stack: application, release: release, version: it ) }
        def inserted = sut.save( toInsert )

        when: 'we call mostCurrent'
        def found = sut.mostCurrent( application, release )

        then: 'we are returned the highest sequenced document'
        def highest = found.get()
        inserted.sort( false ) { it.id }.reverse( false ).first().version == highest.version
    }

    def possibleApplications = [ randomHexString(), randomHexString(), randomHexString()].sort( false )
    def possibleReleases = [ randomHexString(), randomHexString(), randomHexString()].sort( false )
    def possibleVersions = (1..100000).sort( false )

    def 'verify distinct application retrieval'() {
        given: 'the gateway was injected'
        assert sut

        when: 'we insert multiple documents'
        def toSave = (1..100).collect {
            new DockerComposeDescriptor( release: randomHexString(),
                                         version: randomPositiveInteger(),
                                         stack: randomElement( possibleApplications ) as String,
                                         descriptor: randomByteArray( 8 ) )
        }
        sut.save( toSave )

        then: 'we can read out the distinct application values'
        def distinct = sut.distinctStacks().sort( false )
        distinct == possibleApplications
    }

    def 'verify distinct release retrieval'() {
        given: 'the gateway was injected'
        assert sut

        when: 'we insert multiple documents'
        def toSave = (1..100).collect {
            new DockerComposeDescriptor( release: randomElement( possibleReleases ) as String,
                                         version: randomPositiveInteger(),
                                         stack: randomElement( possibleApplications ) as String,
                                         descriptor: randomByteArray( 8 ) )
        }
        sut.save( toSave )

        then: 'we can read out the distinct release values'
        def distinct = sut.distinctReleases( toSave.first().stack ).sort( false )
        distinct == possibleReleases
    }

    def 'verify distinct version retrieval'() {
        given: 'the gateway was injected'
        assert sut

        when: 'we insert multiple documents'
        def toSave = (1..100).collect {
            new DockerComposeDescriptor( release: randomElement( possibleReleases ) as String,
                                         version: randomElement( possibleVersions ) as Integer,
                                         stack: randomElement( possibleApplications ) as String,
                                         descriptor: randomByteArray( 8 ) )
        }
        sut.save( toSave )

        then: 'we can read out the distinct version values'
        def first = toSave.first()
        def selected = toSave.findAll { it.stack == first.stack && it.release == first.release }.collect { it.version }.sort( false )
        def distinct = sut.distinctVersions( first.stack, first.release ).sort( false )
        selected == distinct
    }

    def 'verify findOne'() {
        given: 'the gateway was injected'
        assert sut

        when: 'we insert multiple documents'
        def toSave = (1..10).collect {
            new DockerComposeDescriptor( release: randomElement( possibleReleases ) as String,
                                         version: randomElement( possibleVersions ) as Integer,
                                         stack: randomElement( possibleApplications ) as String,
                                         descriptor: randomByteArray( 8 ) )
        }
        // modify the first one so that there is only one of its type is in the db
        toSave.first().with {
            stack = randomHexString()
            release = randomHexString()
            version = randomPositiveInteger()
        }
        sut.save( toSave )

        then: 'we can read out the specific document'
        def first = toSave.first()
        def found = sut.findOne( first.stack, first.release, first.version ).get()
        found == first
    }

}
