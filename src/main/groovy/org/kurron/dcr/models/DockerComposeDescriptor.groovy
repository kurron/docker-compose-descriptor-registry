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

package org.kurron.dcr.models

import groovy.transform.Canonical
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document

/**
 * A complete Docker Compose descriptor that can persisted.
 **/
@Canonical
@Document
@CompoundIndexes( [@CompoundIndex( name = 'unique_index', def = "{ 'stack': 1, 'release': 1, 'version': -1 }", unique = true )] )
class DockerComposeDescriptor {

    /**
     * Primary key of the document.
     */
    @Id
    ObjectId id

    /**
     * The stack that this descriptor is associated with.  For example,
     * "storm".
     */
    String stack

    /**
     * The release this descriptor belongs to, eg. "milestone", "release", "rc", etc.
     */
    String release

    /**
     * The version of the descriptor, eg 199.
     */
    int version

    /**
     * Binary representation of the Docker Compose document (YAML does not play well with JSON)
     */
    byte[] descriptor


}
