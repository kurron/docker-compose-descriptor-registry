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

import org.kurron.feedback.Audience
import org.kurron.feedback.FeedbackContext
import org.kurron.feedback.FeedbackLevel

/**
 * The enumeration of all logged messages produced by the system.
 **/
enum MessagingContext implements FeedbackContext {

    CURRENT_TIME( 1000, FeedbackLevel.WARN, Audience.QA, 'The current time is {}.' ),
    FORCED_ERROR( 1001, FeedbackLevel.ERROR, Audience.OPERATIONS, 'I was forced to fail!' ),
    GENERATED_TRACING_HEADER( 1002, FeedbackLevel.DEBUG, Audience.DEVELOPMENT, 'Generated trace id for OPTIONS call: {} = {}' ),
    MISSING_HTTP_HEADER_ERROR( 1003, FeedbackLevel.WARN, Audience.QA, 'Required {} header is missing!' ),
    GENERIC_ERROR( 1007, FeedbackLevel.ERROR, Audience.QA, 'The following error has occurred and was caught by the global error handler: {}' ),
    VALIDATION_ERROR( 1008, FeedbackLevel.INFO, Audience.QA, 'The property {} is invalid. Cause: {}' ),
    MESSAGING_UPDATE( 1009, FeedbackLevel.INFO, Audience.QA, 'Sending {} to RabbitMQ' ),
    JUST_HEARD( 1010, FeedbackLevel.INFO, Audience.QA, 'Just heard {} from RabbitMQ' ),
    SAVING( 1011, FeedbackLevel.INFO, Audience.QA, 'Saving {} to MongoDB' ),
    PROCESSING_RESOURCE( 1012, FeedbackLevel.DEBUG, Audience.QA, 'Processing resource {}' ),
    RESOURCE_NOT_FOUND( 1013, FeedbackLevel.INFO, Audience.QA, 'The resource {} was not found.' )

    private final int code
    private final String formatString
    private final FeedbackLevel feedbackLevel
    private final Audience audience

    MessagingContext( final int aCode,
                      final FeedbackLevel aFeedbackLevel,
                      final Audience aAudience,
                      final String aFormatString ) {
        code = aCode
        formatString = aFormatString
        feedbackLevel = aFeedbackLevel
        audience = aAudience
    }

    @Override
    int getCode() {
        code
    }

    @Override
    String getFormatString() {
        formatString
    }

    @Override
    FeedbackLevel getFeedbackLevel() {
        feedbackLevel
    }

    @Override
    Audience getAudience() {
        audience
    }
}