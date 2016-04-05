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

/**
 * An individual Docker Compose fragment.
 **/
@Canonical
class DockerComposeFragment {

    /**
     * Binary representation of the Docker Compose fragment (YAML does not play well with JSON)
     */
    byte[] fragment

    /**
     * The release this fragment belongs to, eg. "milestone", "release", "rc", etc.
     */
    String release

    /**
     * The collection of application stacks that this fragment is associated with.  For example,
     * "reporting application" or "firewall".
     */
    List<String> stacks
}
