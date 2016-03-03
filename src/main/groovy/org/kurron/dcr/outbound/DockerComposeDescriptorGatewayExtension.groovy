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

/**
 * Outbound gateway that knows how to interact with the persistence store.
 **/
interface DockerComposeDescriptorGatewayExtension {

    /**
     * Obtains the next number in the sequence.
     * @param application the application portion of the key.
     * @param release the portion portion of the key.
     * @return next available sequence.
     */
    int nextSequence( String application, String release )
}