package org.kurron.example

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Custom configuration properties that are driven by Spring Boot and its application.yml file.
 */
@ConfigurationProperties( value = 'example', ignoreUnknownFields = false )
class ApplicationProperties {

    /**
     * This property controls...
     */
    String foo

}
