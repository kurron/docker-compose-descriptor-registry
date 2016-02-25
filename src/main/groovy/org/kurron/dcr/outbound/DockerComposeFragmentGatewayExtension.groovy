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

/**
 * Custom repository extension.
 **/
interface DockerComposeFragmentGatewayExtension {

    /**
     * Constructs a collection of the distinct applications associated with the various fragments in the database.
     * @return a collection of distinct applications.
     */
    List<String> distinctApplications()

    /**
     * Constructs a collection of the distinct releases associated with a particular application.
     * @param application the application name to query against.
     * @return a collection of distinct releases.
     */
    List<String> distinctReleases( String application )

    /**
     * Constructs a collection of the distinct versions associated with a particular application and release.
     * @param application the application name to query against.
     * @param release the release name to query against.
     * @return a collection of distinct versions.
     */
    List<String> distinctVersions( String application, String release )

    /**
     * Loads the specific fragment fro, the database.
     * @param application the application name to query against.
     * @param release the release name to query against.
     * @param version the version to query against.
     * @return the loaded instance.
     */
    DockerComposeFragment findOne( String application, String release, String version )
}