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

import org.kurron.dcr.DockerComposeFragment
import org.kurron.dcr.outbound.DockerComposeDescriptor
import org.kurron.dcr.outbound.DockerComposeFragmentGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Production implementation of the FragmentAssembler interface.
 **/
@Service
class DefaultFragmentAssembler implements FragmentAssembler {

    /**
     * Manages interactions with the database.
     */
    private final DockerComposeFragmentGateway theGateway

    @Autowired
    DefaultFragmentAssembler( final DockerComposeFragmentGateway aGateway ) {
        theGateway = aGateway
    }

    @Override
    List<DockerComposeDescriptor> assemble( final DockerComposeFragment fragment ) {
        // 01) persist the fragment -- do we really need to save it?
        // 02) create a new descriptor for each application-release the fragment belongs to
        // 03) return the newly generated descriptors -- not persisted

        def descriptors = fragment.applications.collect { application ->
            // 01) find all fragments stored for the combination of application+release
            // 02) remove the one that resembles the new fragment
            // 03) create a new descriptor
            // How do we get the highest versions of the fragments?
        }
        throw new UnsupportedOperationException( 'assemble' )
    }
}
