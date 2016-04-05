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

import static org.kurron.dcr.inbound.HypermediaControl.MIME_TYPE
import static org.springframework.web.bind.annotation.RequestMethod.PUT
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import org.kurron.dcr.core.FragmentAssembler
import org.kurron.dcr.models.DockerComposeFragment
import org.kurron.stereotype.InboundRestGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Inbound HTTP gateway that supports the Docker Compose fragment resource.
 **/
@InboundRestGateway
@RequestMapping( path = '/fragment', consumes = [MIME_TYPE, MediaType.APPLICATION_JSON_VALUE], produces = [MIME_TYPE, MediaType.APPLICATION_JSON_VALUE] )
class FragmentGateway extends BaseGateway {

    /**
     * Responsible for combining the fragment into a complete descriptor.
     */
    private final FragmentAssembler theAssembler

    @Autowired
    FragmentGateway( final FragmentAssembler anAssembler ) {
        theAssembler = anAssembler
    }

    @RequestMapping( method = [PUT] )
    ResponseEntity<HypermediaControl> addFragment( HttpServletRequest request,
                                                   @RequestBody @Valid HypermediaControl input ) {
        def control = defaultControl( request )
        def descriptors = theAssembler.assemble( toFragment( input ) )
        control.applications = descriptors*.application.sort()
        control.releases = descriptors*.release.unique()

        // there should only be one release, let's verify
        // TODO: figure out what to do if the assumption fails
        1 == control.releases.size()
        ResponseEntity.ok( control )
    }

    /**
     * Transforms the PUT request into our domain model.
     * @param input HTTP request to pick apart.
     * @return newly constructed domain object.
     */
    static DockerComposeFragment toFragment( HypermediaControl input ) {
        assert 1 == input.releases.size()
        new DockerComposeFragment( applications: input.applications,
                                   release: input.releases.first(),
                                   fragment: input.fragment.decodeBase64() )
    }
}
