@file:Suppress("unused")

package api.yt

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.Serializable


@Serializable
data class Project(val shortName: String)

@Serializable
data class User(val id: Int, val email: String, val first_name: String)

@Serializable
data class Response(val data: List<User>)

@Serializable
data class Owner(val fullName: String)

@Serializable
data class Tag(val name: String, val `$type`: String, val owner: Owner)

@Serializable
data class Reporter(val login: String, val `$type`: String)


@Serializable
data class CustomFieldValue(val name: String)


object CustomFieldListSerializer :
    JsonTransformingSerializer<List<CustomFieldValue>>(ListSerializer(CustomFieldValue.serializer())) {
    override fun transformDeserialize(element: JsonElement): JsonElement =
        if (element !is JsonArray) JsonArray(listOf(element)) else element
}

@Serializable
data class CustomField(
    val name: String,
    @Serializable(with = CustomFieldListSerializer::class)
    val value: List<CustomFieldValue>?
)




@Serializable
data class Issue(
//    val created: Float,
    val summary: String,
//    val description: String,
    val project: Project,
//    val updated: Float,
    val tags: List<Tag>,
//    val reporter: Reporter,
//    val updater: Reporter,
    val idReadable: String,
    val customFields: List<CustomField>,
    val resolved: Long?
)

data class Row(
    var count: Int,
    val tagAuthor: String
)
