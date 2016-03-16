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

/**
 * This script will churn through the Docker Compose files in the current directory
 * and publish them to the Docker Compose registry.
 **/

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.PUT
@Grab( 'org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
import groovy.json.JsonBuilder
import groovyx.net.http.HTTPBuilder

def currentDir = new File('.')
def files = []
currentDir.eachFileMatch(  ~/.*.yml/ ) {
    files << it.name

    def map = [applications: ['A','B','C','D'], releases: ['Milestone'], fragment: it.getBytes().encodeBase64().toString()]
    def jsonBuilder = new JsonBuilder( map )
    def json = jsonBuilder.toPrettyString()

    def http = new HTTPBuilder( 'http://localhost:8080/fragment' )
    http.request( PUT, JSON ) { req ->
        body = json

        response.success = { resp, reader ->
            println "Success! ${resp.status}"
        }

        response.failure = { resp ->
            println "Failed: ${resp.status}"
            println "Failed: ${resp.contentType}"
            println "Failed: ${resp.data}"
            println()
        }
    }
}
