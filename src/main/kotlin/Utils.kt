import com.slack.api.Slack
import java.util.*

//Mef comment: convert date to unix timestamp
fun dateToUnixTimestamp(year: Int, month: Int, day: Int): Long {

    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day) // Calendar month is 0-based, so subtract 1

    return calendar.timeInMillis / 1000L // Convert milliseconds to seconds

}

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

fun mapForSlackUserIDandSlackName(rawMessages: List<com.slack.api.model.Message>): MutableMap<String, String> {

    val client = Slack.getInstance().methods()

    val userNamesDict = mutableMapOf<String, String>()

    val slackBotToken = System.getenv("SLACK_BOT_TOKEN")
    for (message in rawMessages) {
        if (!userNamesDict.containsKey(message.user)) {
            val userInfo = client.usersInfo {
                it
                    .token(slackBotToken)
                    .user(message.user)
            }
            userNamesDict[message.user] = userInfo.user.name
        }

    }

    return userNamesDict
}