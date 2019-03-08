package no.bakkenbaeck.porchpirateprotector

import io.ktor.application.*
import io.ktor.auth.authentication
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.HttpHeaders
import io.ktor.routing.*
import kotlinx.html.*


fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    routing {
        get("/") {
            call.respondHtml {
                head {
                    title { +"Ktor: jetty" }
                }
                body {
                    p {
                        +"Hello from Ktor Jetty engine sample application"
                    }
                }
            }
        }
    }
}

