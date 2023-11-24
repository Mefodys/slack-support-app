data class Message(
    val dateTime: String,
    val link: String,
    val realName: String? = null,
    val slackEmail: String? = null,

//    val project1: String? = null,
//    val project2: String? = null,
//    val project3: String? = null,
//    val project4: String? = null,

    val reactionYT: Boolean? = null,
    val reactionInProgress: Boolean? = null,
    val reactionWhiteCheckMark: Boolean? = null,

//    val issueID: String? = null,
//    val issueType: String? = null,
//    val subsystem: String? = null,
//    val stateInYT: String? = null
)
