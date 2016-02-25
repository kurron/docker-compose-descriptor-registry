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

package org.kurron.dcr

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * A MongoDB document that stores individual Docker Compose fragments.
 **/
@Document
class DockerComposeFragment {

    /**
     * Primary key of the document.
     */
    @Id
    ObjectId id

    /**
     * Binary representation of the Docker Compose fragment (YAML does not play well with JSON)
     */
    byte[] fragment

    /**
     * The release this fragment belongs to, eg. "milestone", "release", "rc", etc.
     */
    @Indexed
    String release

    /**
     * The version of the fragment, eg module-1.2.3.RELEASE.
     */
    String version

    /**
     * The collection of applications that this fragment is associated with.  For example,
     * "reporting application" or "firewall".
     */
    @Indexed
    List<String> applications
}
