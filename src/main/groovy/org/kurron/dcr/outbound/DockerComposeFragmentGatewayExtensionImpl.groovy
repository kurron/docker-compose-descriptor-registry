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

import org.kurron.dcr.DockerComposeFragment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

/**
 * Implementation of the custom repository logic.
 **/
@SuppressWarnings( 'GroovyUnusedDeclaration' )
class DockerComposeFragmentGatewayImpl implements DockerComposeFragmentGatewayExtension {

    @Autowired
    private MongoOperations theTemplate

    @Override
    List<String> distinctApplications() {
        theTemplate.getCollection( theTemplate.getCollectionName( DockerComposeFragment ) ).distinct( 'applications' )
    }

    @Override
    List<String> distinctReleases( final String application ) {
        theTemplate.find( Query.query( Criteria.where( 'applications' ).is( application ) ), DockerComposeFragment ).collect { it.release }.unique( false )
    }

    @Override
    List<String> distinctVersions( final String application, final String release ) {
        theTemplate.find( Query.query( Criteria.where( 'applications' ).is( application ).and( 'release' ).is( release ) ), DockerComposeFragment ).collect { it.version }.unique( false )
    }
}
