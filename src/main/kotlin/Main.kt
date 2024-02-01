import api.SlackAPI
import api.SpaceAPI

import serialization.messagesForSecondCsv
import serialization.messagesForThirdCsv
import serialization.messagesForFirstCsv
import serialization.writeCsv1
import serialization.writeCsv2
import serialization.writeCsv3
import java.io.FileOutputStream
import kotlin.time.TimeSource.Monotonic.markNow
import kotlin.time.measureTime


suspend fun main() {
    val slackAPI = SlackAPI(System.getenv("SLACK_BOT_TOKEN"))
    val spaceAPI = SpaceAPI(System.getenv("SPACE_TOKEN"))
    val mark = markNow()

    val (users, messageWithUser) = slackAPI.getData(Settings.channelToFetch, Settings.fromDate, Settings.tillDate, 500)

    //Mef comment: Gather info from Space and make a map (Email -> 4 Projects max)
    val emailToFirst4TeamNames = spaceAPI.getEmailToFirst4TeamNames(users)

    val messagesReadyForFirstCsv = messagesForFirstCsv(messageWithUser)

    val messagesForSecondCsv = messagesForSecondCsv(emailToFirst4TeamNames)

    // third csv (YouTrack tickets and its details)
    val messagesForThirdCsv = messagesForThirdCsv(slackAPI,messageWithUser)


    println("apiTime = ${mark.elapsedNow()}")

    val csvTime = measureTime {
        //Mef comment: output the first CSV file
            FileOutputStream("filename.csv").apply { writeCsv1(messagesReadyForFirstCsv) }

        //Mef comment: output the second CSV file
            FileOutputStream("filename2.csv").apply { writeCsv2(messagesForSecondCsv) }

        //Mef comment: output the third CSV file
            FileOutputStream("filename3.csv").apply { writeCsv3(messagesForThirdCsv) }
    }
    println("csvTime is $csvTime")

}


