import com.slack.api.Slack
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse
import java.util.*

//Mef comment: convert date to unix timestamp
fun dateToUnixTimestamp(year: Int, month: Int, day: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day) // Calendar month is 0-based, so subtract 1
    return calendar.timeInMillis / 1000L // Convert milliseconds to seconds
}

//Mef comment: request to obtain all messages(history) from the Slack API
fun fetchSlackHistory(id: String?, oldest: String?, latest: String?): MutableList<com.slack.api.model.Message> {

    val slackBotToken = System.getenv("SLACK_BOT_TOKEN")
    val client = Slack.getInstance().methods()
    val result: ConversationsHistoryResponse = client.conversationsHistory { request ->
        request
            .token(slackBotToken)
            .channel(id)
            .limit(500)
            .oldest(oldest)
            .latest(latest)
    }
    return result.messages
}