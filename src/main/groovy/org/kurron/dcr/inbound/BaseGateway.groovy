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

import java.time.Instant
import javax.servlet.http.HttpServletRequest
import org.kurron.dcr.models.MessagingContext
import org.kurron.dcr.outbound.DockerComposeDescriptorGateway
import org.kurron.feedback.AbstractFeedbackAware
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.HandlerMapping

/**
 * A base class used to share common behavior.
 **/
class BaseGateway extends AbstractFeedbackAware {

    @Autowired
    protected DockerComposeDescriptorGateway gateway

    protected HypermediaControl defaultControl( HttpServletRequest request ) {
        def path = request.getAttribute( HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE ) as String
        feedbackProvider.sendFeedback( MessagingContext.PROCESSING_RESOURCE, path )
        new HypermediaControl( status: HttpStatus.OK.value(), timestamp: Instant.now().toString(), path: path )
    }
}
