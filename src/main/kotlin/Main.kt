import api.SlackAPI
import api.SpaceAPI
import api.yt.YouTrackAPI
import kotlinx.coroutines.runBlocking
import serialization.createCsv
import serialization.getDataForMainCsv
import serialization.getDataForTeamCsv
import serialization.getIssueIdFromMessages
import kotlin.time.TimeSource.Monotonic.markNow
import kotlin.time.measureTime


fun main() = runBlocking {
    val slackAPI = SlackAPI(System.getenv("SLACK_BOT_TOKEN"))
    val spaceAPI = SpaceAPI(System.getenv("SPACE_TOKEN"))
    val youTrackAPI = YouTrackAPI(System.getenv("YT_TOKEN"))


    val limit = System.getenv("LIMIT")?.toInt() ?: 500

    val mark = markNow()

    val (users, messageWithUser) = slackAPI.getData(Settings.channelToFetch, Settings.fromDate, Settings.tillDate, limit)
    val emailToFirst4TeamNames = spaceAPI.getEmailToFirst4TeamNames(users)

    val mainCsv = getDataForMainCsv(messageWithUser)
    val teamCsv = getDataForTeamCsv(emailToFirst4TeamNames)
    val ytIds = getIssueIdFromMessages(slackAPI, messageWithUser)

    println("apiTime = ${mark.elapsedNow()}")

    val ytCsv = youTrackAPI.fetchIssues(ytIds)

    measureTime {
        createCsv(
            "filename.csv",
            "DateTime, SlackLink, RealName, Email, ReactionYT, ReactionInProgress, ReactionWhiteCheckMark",
            mainCsv
        )
        createCsv("filename2.csv", "Team", teamCsv)
        createCsv("filename3.csv", "IssueID, Type, Subsystem, State", ytCsv)

    }.also { println("csvTime is $it") }

    Unit
}


