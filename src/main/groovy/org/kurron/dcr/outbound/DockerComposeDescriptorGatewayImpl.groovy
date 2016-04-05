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

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options
import static org.springframework.data.mongodb.core.query.Criteria.where
import static org.springframework.data.mongodb.core.query.Query.query
import org.kurron.dcr.models.DockerComposeDescriptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Update

/**
 * Implementation of the custom repository logic.
 **/
@SuppressWarnings( 'GroovyUnusedDeclaration' )
class DockerComposeDescriptorGatewayImpl implements DockerComposeDescriptorGatewayExtension {

    @Autowired
    private MongoOperations theTemplate

    @Override
    int nextSequence( final String application, final String release ) {
        def query = query( where( 'application' ).is( application ).and( 'release' ).is( release ) )
        def update = new Update().inc( 'counter', 1 )
        def options = options().upsert( true ).returnNew( true )
        DescriptorCounter updatedDocument = theTemplate.findAndModify( query, update, options, DescriptorCounter )
        updatedDocument.counter
    }

    @Override
    Optional<DockerComposeDescriptor> mostCurrent( final String application, final String release ) {
        // the index is set up so that the highest version is the first in the collection
        def query = query( where( 'application' ).is( application ).and( 'release' ).is( release ) )
        def found = theTemplate.findOne( query, DockerComposeDescriptor )
        Optional.ofNullable( found )
    }

    @Override
    List<String> distinctApplications() {
        theTemplate.getCollection( theTemplate.getCollectionName( DockerComposeDescriptor ) ).distinct( 'application' )
    }

    @Override
    List<String> distinctReleases( final String application ) {
        theTemplate.find( query( where( 'application' ).is( application ) ), DockerComposeDescriptor ).collect { it.release }.unique( false )
    }

    @Override
    List<Integer> distinctVersions( final String application, final String release ) {
        theTemplate.find( query( where( 'application' ).is( application ).and( 'release' ).is( release ) ), DockerComposeDescriptor ).collect { it.version }.unique( false )
    }

    @Override
    Optional<DockerComposeDescriptor> findOne( final String application, final String release, final Integer version ) {
        DockerComposeDescriptor found = theTemplate.findOne( query( where( 'application' ).is( application ).and( 'release' ).is( release ).and( 'version' ).is( version ) ), DockerComposeDescriptor )
        Optional.ofNullable( found )
    }

}
