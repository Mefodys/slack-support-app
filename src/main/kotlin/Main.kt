import api.SlackAPI
import api.SpaceAPI
import serialization.createCsv

import serialization.messagesForSecondCsv
import serialization.messagesForThirdCsv
import serialization.messagesForFirstCsv
import kotlin.time.TimeSource.Monotonic.markNow
import kotlin.time.measureTime


suspend fun main() {
    val slackAPI = SlackAPI(System.getenv("SLACK_BOT_TOKEN"))
    val spaceAPI = SpaceAPI(System.getenv("SPACE_TOKEN"))
    val mark = markNow()

    val (users, messageWithUser) = slackAPI.getData(Settings.channelToFetch, Settings.fromDate, Settings.tillDate, 50)

    //Mef comment: Gather info from Space and make a map (Email -> 4 Projects max)
    val emailToFirst4TeamNames = spaceAPI.getEmailToFirst4TeamNames(users)

    val messagesReadyForFirstCsv = messagesForFirstCsv(messageWithUser)

    val messagesForSecondCsv = messagesForSecondCsv(emailToFirst4TeamNames)

    // third csv (YouTrack tickets and its details)
    val messagesForThirdCsv = messagesForThirdCsv(slackAPI, messageWithUser)

    println("apiTime = ${mark.elapsedNow()}")

    measureTime {

        createCsv(
            "filename.csv",
            "DateTime, SlackLink, RealName, Email, ReactionYT, ReactionInProgress, ReactionWhiteCheckMark",
            messagesReadyForFirstCsv
        )

        createCsv("filename2.csv", "Team", messagesForSecondCsv)

        createCsv("filename3.csv", "TicketID, Type, Subsystem, State", messagesForThirdCsv)

    }.also { println("csvTime is $it") }


}


