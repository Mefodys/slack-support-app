package api.yt

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.appendPathSegments
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import types.YouTrackTicketCsvDTO2

const val ISSUES_URL = "https://youtrack.jetbrains.com:443/api"
const val STEP = 10

val json = Json { ignoreUnknownKeys = true }


const val fieldsParam =
//    "\$type," +
    "tags(name,owner(fullName))," + "created," + "resolved," + "customFields(\$type,id,name,value(\$type,id,name))," +
//        "description,id,idReadable," +
            "idReadable," +
//        "links(\$type,direction,id,linkType(\$type,id,localizedName,name))," +
//        "numberInProject," +
            "project(shortName)," + "summary"
//        "reporter(\$type,id,login)," +
//        "resolved,summary,updated," +
//        "updater(\$type,id,login),"
//        "visibility(\$type,id,permittedGroups(\$type,id,name,ringId)," +
//        "permittedUsers(\$type,id,login,ringId))"


class YouTrackAPI(
    private val token: String,
) {
    private val client = HttpClient {
        install(HttpTimeout)
    }

    private suspend fun issues(skip: Int, top: Int = STEP, query: String): List<Issue> {
        val response = client.get(ISSUES_URL) {
            timeout {
                requestTimeoutMillis = 20000
                socketTimeoutMillis = 20000
            }
            url {
                appendPathSegments("issues")
                parameters.append("fields", fieldsParam)
                parameters.append("\$skip", "$skip")
                parameters.append("\$top", "$top")
                parameters.append("query", query)

            }
            headers {
                append("Authorization", token)
            }
        }.bodyAsText()

        return json.decodeFromString<List<Issue>>(response)
    }

    fun fetchIssues(issueIds: List<String>): List<YouTrackTicketCsvDTO2> = runBlocking {
        issueIds
            .chunked(STEP)
            .flatMap {
                val query = "issue id: " + it.joinToString(", ")
                val issues = issues(0, STEP, query)

                val getCustomField = { it: Issue, name: String ->
                    it.customFields
                        .find { it.name == name }?.value?.joinToString("|") {
                            it.name.replace(",", ".")
                        } ?: throw Exception("Cant find $name property in ${it.idReadable}")
                }

                delay(3000)
                issues.mapNotNull {
                    try {
                        val subsystem = getCustomField(it, "Subsystems")
                        val issueType = getCustomField(it, "Type")
                        val issueState = getCustomField(it, "State")

                        YouTrackTicketCsvDTO2(
                            id = it.idReadable, type = issueType, subsystem = subsystem, state = issueState
                        )
                    } catch (e: Throwable) {
                        println(e.message)
                        null
                    }
                }
            }
    }

}
