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

import groovy.transform.CompileDynamic
import org.kurron.categories.StringEnhancements
import org.kurron.dcr.models.DockerComposeDescriptor
import org.kurron.dcr.models.DockerComposeFragment
import org.kurron.dcr.outbound.DockerComposeDescriptorGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

/**
 * Production implementation of the FragmentAssembler interface.
 **/
@Service
class DefaultFragmentAssembler implements FragmentAssembler {

    /**
     * Manages interactions with the database.
     */
    private final DockerComposeDescriptorGateway theGateway

    @Autowired
    DefaultFragmentAssembler( final DockerComposeDescriptorGateway aGateway ) {
        theGateway = aGateway
    }

    @Override
    List<DockerComposeDescriptor> assemble( final DockerComposeFragment fragment ) {
        fragment.stacks.collect { application ->
            def descriptor = loadDescriptor( application, fragment )
            replaceFragmentInDescriptor( descriptor, fragment )
            persistDescriptor( application, fragment.release, descriptor )
        }
    }

    private static void replaceFragmentInDescriptor( DockerComposeDescriptor descriptor, DockerComposeFragment fragment ) {
        def descriptorYml = parseYml( descriptor.descriptor )
        def fragmentYml = parseYml( fragment.fragment )
        fragmentYml.keySet().each { key ->
            descriptorYml.put( key, fragmentYml[key] )
        }
        descriptor.descriptor = convertToYmlBytes( descriptorYml )
    }

    @CompileDynamic // the use of Traits require this 8-(
    private static byte[] convertToYmlBytes( Map descriptorYml ) {
        def options = new DumperOptions()
        options.canonical = false
        options.indent = 4
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        options.defaultScalarStyle = DumperOptions.ScalarStyle.PLAIN
        options.prettyFlow = true
        def parser = new Yaml( options )
        use( StringEnhancements ) { ->
            parser.dump( descriptorYml ).utf8Bytes
        }
    }

    private static Map parseYml( byte[] yml ) {
        def parser = new Yaml()
        new ByteArrayInputStream( yml ).withStream {
            parser.load( it ) as Map
        }
    }

    private DockerComposeDescriptor persistDescriptor( String application, String release, DockerComposeDescriptor descriptor ) {
        descriptor.version = theGateway.nextSequence( application, release )
        descriptor.id = null // make sure we get a new document
        theGateway.save( descriptor )
    }

    private DockerComposeDescriptor loadDescriptor( String application, DockerComposeFragment fragment ) {
        def loaded = theGateway.mostCurrent( application, fragment.release )
        loaded.orElse( initialDescriptor( application, fragment ) )
    }

    private static DockerComposeDescriptor initialDescriptor( String application, DockerComposeFragment fragment ) {
        new DockerComposeDescriptor( application: application, release: fragment.release, descriptor: fragment.fragment )
    }
}
