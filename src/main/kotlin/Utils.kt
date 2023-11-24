import com.slack.api.Slack
import java.sql.Timestamp
import java.util.*

//Mef comment: convert date to unix timestamp
fun dateToUnixTimestamp(year: Int, month: Int, day: Int): Long {

    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day) // Calendar month is 0-based, so subtract 1

    return calendar.timeInMillis / 1000L // Convert milliseconds to seconds

}

val messagePermalink = "https://" + "jetbrains.slack.com" + "/archives/" + "${Settings.channelNameForFetch}" + "/p"

fun convertTStoReadableDateTime(message: com.slack.api.model.Message): String {
    val newTimestamp = message.ts.replace(".", "").substring(0, 13).toLong()
    val convertedTimestamp = Timestamp(newTimestamp)
    val newConvertedTimestamp = convertedTimestamp.toString().substring(0, 19)

    return newConvertedTimestamp
}