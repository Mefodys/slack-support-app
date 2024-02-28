import api.SlackAPI
import api.SpaceAPI
import serialization.createCsv
import serialization.deserializeDataForTeamCsv
import serialization.deserializeDataForTicketCsv
import serialization.deserializeDataForMainCsv
import kotlin.time.TimeSource.Monotonic.markNow
import kotlin.time.measureTime


suspend fun main() {
    val slackAPI = SlackAPI(System.getenv("SLACK_BOT_TOKEN"))
    val spaceAPI = SpaceAPI(System.getenv("SPACE_TOKEN"))
    val limit = System.getenv("LIMIT")?.toInt() ?: 500

    val markStart = markNow()
    val (users, messageWithUser) = slackAPI.getData(Settings.channelToFetch, Settings.fromDate, Settings.tillDate, limit)
    println("\nSlack ${markStart.elapsedNow()}")

    val spaceMark = markNow()
    val emailToFirst4TeamNames = spaceAPI.getEmailToFirst4TeamNames(users)
    println("\nSpace ${spaceMark.elapsedNow()}")

    val dataForMainCsv = deserializeDataForMainCsv(messageWithUser)
    val dataForTeamCsv = deserializeDataForTeamCsv(emailToFirst4TeamNames)
    val dataForTicketCsv = deserializeDataForTicketCsv(slackAPI, messageWithUser)

    println("apiTime = ${markStart.elapsedNow()}")


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


