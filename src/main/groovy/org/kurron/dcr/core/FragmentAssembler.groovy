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

import org.kurron.dcr.DockerComposeDescriptor
import org.kurron.dcr.DockerComposeFragment

/**
 * Knows how to combine Docker Compose fragments into a single file.
 **/
interface FragmentAssembler {

    /**
     * Combines the provided fragment into the complete descriptors that it is associated with.
     * @param fragment the fragment to combine into the complete descriptor.
     * @return a collection of newly completed descriptors.
     */
    List<DockerComposeDescriptor> assemble( DockerComposeFragment fragment )
}
