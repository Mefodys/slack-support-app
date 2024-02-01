import api.SlackAPI
import api.SpaceAPI
import serialization.createCsv
import serialization.getDataForTeamCsv
import serialization.getDataForTicketCsv
import serialization.getDataForMainCsv
import kotlin.time.TimeSource.Monotonic.markNow
import kotlin.time.measureTime


suspend fun main() {
    val slackAPI = SlackAPI(System.getenv("SLACK_BOT_TOKEN"))
    val spaceAPI = SpaceAPI(System.getenv("SPACE_TOKEN"))
    val limit = System.getenv("LIMIT")?.toInt() ?: 500

    val mark = markNow()

    val (users, messageWithUser) = slackAPI.getData(Settings.channelToFetch, Settings.fromDate, Settings.tillDate, limit)
    val emailToFirst4TeamNames = spaceAPI.getEmailToFirst4TeamNames(users)

    val dataForMainCsv = getDataForMainCsv(messageWithUser)
    val dataForTeamCsv = getDataForTeamCsv(emailToFirst4TeamNames)
    val dataForTicketCsv = getDataForTicketCsv(slackAPI, messageWithUser)

    println("apiTime = ${mark.elapsedNow()}")


    measureTime {

        createCsv(
            "filename.csv",
            "DateTime, SlackLink, RealName, Email, ReactionYT, ReactionInProgress, ReactionWhiteCheckMark",
            dataForMainCsv
        )
        createCsv("filename2.csv", "Team", dataForTeamCsv)
        createCsv("filename3.csv", "TicketID, Type, Subsystem, State", dataForTicketCsv)

    }.also { println("csvTime is $it") }


}


