import com.slack.api.Slack

//Mef comment: request to obtain all messages(history) from the Slack API
fun fetchSlackHistory(id: String?, oldest: String?, latest: String?): List<com.slack.api.model.Message> {

    val slackBotToken = System.getenv("SLACK_BOT_TOKEN")
    val client = Slack.getInstance().methods()

    val result = client.conversationsHistory {
        it
            .token(slackBotToken)
            .channel(id)
            .limit(500)
            .oldest(oldest)
            .latest(latest)
    }

    //Mef comment: the result of the request in human readable form (a list of Messages)
    val allRawMessages = result.messages

    //Mef comment: filter messages (optimize returned list of messages)
    val messagesWithText = allRawMessages
        .filter { it.subtype != "channel_join" && it.subtype != "channel_leave" } //!no warning from IDEA plugin - KTIJ-27938

    return messagesWithText
}

fun obtainTwoMapsWithUserIDUserNameEmail(rawSlackMessages: List<com.slack.api.model.Message>): List<MutableMap<String, String>> {

    val client = Slack.getInstance().methods()

    val userRealNamesDict = mutableMapOf<String, String>()
    val userEmailsDict = mutableMapOf<String, String>()

    val slackBotToken = System.getenv("SLACK_BOT_TOKEN")
    for (message in rawSlackMessages) {

        //Mef comment: Addditional check if the usedID not null (if null then there will NPE during client.userInfo request)
        if (message.user != null) {
            val userInfo = client.usersInfo {
                it
                    .token(slackBotToken)
                    .user(message.user)
            }

            //Mef comment: filling of two maps with userdata
            userRealNamesDict[message.user] = userInfo.user.realName
            userEmailsDict[message.user] = userInfo.user.profile.email
        }

    }

    //Mef comment: an additional function to make a list of maps (somewhy does not work by default)
    fun listOfUsersDicts(
        userRealNamesDict: MutableMap<String, String>,
        userEmailsDict: MutableMap<String, String>,
    ): List<MutableMap<String, String>> {
        return listOf(userRealNamesDict, userEmailsDict)
    }

    return listOfUsersDicts(userRealNamesDict, userEmailsDict)
}
