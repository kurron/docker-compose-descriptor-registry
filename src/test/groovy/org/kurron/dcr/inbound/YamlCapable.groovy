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

package org.kurron.dcr.inbound

import org.kurron.categories.ByteArrayEnhancements
import org.kurron.categories.StringEnhancements
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

/**
 * Traits shared by all test that are capable of exercising a REST API.
 **/
trait YamlCapable {

    /**
     * Create an example Yaml file (Docker Compose format) suitable for testing.
     * @return UTF-8 encoded bytes of the Yaml file.
     */
    byte[] createYmlBytes() {
        def options = new DumperOptions()
        options.canonical = false
        options.indent = 4
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        options.defaultScalarStyle = DumperOptions.ScalarStyle.PLAIN
        options.prettyFlow = true
        def parser = new Yaml( options )
        def map = ['redis': ['image'         : 'redis',
                   'container_name': 'redis',
                   'volumes_from'  : ['redis-data'],
                   'restart'       : 'always',
                   'ports'         : ['6379:6379'],
                   'labels'        : ['com.example.revision': '0'],
                   'net'           : 'host']]
        def dumped = parser.dump( map )
        use( StringEnhancements ) { ->
            dumped.utf8Bytes
        }
    }

    /**
     * Create an example Yaml file (Docker Compose format) suitable for testing.
     * @return Base64 encoded bytes of the Yaml file.
     */
    String createYmlBase64() {
        def bytes = createYmlBytes()
        use( ByteArrayEnhancements ) { ->
            bytes.toStringBase64()
        }
    }

}