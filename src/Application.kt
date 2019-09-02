package com.example

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.method
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.css.*
import kotlinx.html.*
import kotlin.collections.set

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val client = HttpClient(Apache) {
    }

    routing {
        method(HttpMethod.Get) {
            route("/") {
                route("json") {
                    get {
                       call.respondText(getTrelloBoards(), contentType = ContentType.Application.Json)
                    }
                }
            }
        }
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }

        get("/trello") {
            val trelloResponse = getTrelloBoards()

            call.respondHtml {
                body {
                    h1 { +"TrelloTest"}
                    p { +trelloResponse }
                }
            }
        }

        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.red
                }
                p {
                    fontSize = 2.em
                }
                rule("p.myclass") {
                    color = Color.blue
                }
            }
        }
    }
}

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

suspend fun getTrelloBoards(): String {
    val client = HttpClient()
    val boardId = "RsU5w4Bn"

    val call = TrelloCall()
    call.parameters["lists"] = "open"
    call.request = "boards/$boardId"
    println("Executing Request | ${call.getURL()}")
    val apiTest3 = call.execute<String>(client)

    //val apiTest2 = executeApiCall<String>(client, Constants.API_KEY, Constants.OAUTH_TOKEN, "boards/$boardId", parameters)
    //val apiTest = client.get<ByteArray>("https://api.trello.com/1/boards/$boardId?key=${Constants.API_KEY}&token=${Constants.OAUTH_TOKEN}")

    client.close()
    return apiTest3
}
