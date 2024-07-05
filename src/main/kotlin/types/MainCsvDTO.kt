package types

class MainCsvDTO(
    val dateTime: String,
    val slackLink: String,
    val realName: String? = null,
    val slackEmail: String? = null,
    val reactionYT: Boolean? = null,
    val reactionInProgress: Boolean? = null,
    val reactionWhiteCheckMark: Boolean? = null,
) : CsvSerializable

