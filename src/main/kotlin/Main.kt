import api.SlackAPI
import api.mapForEmailAndTeam

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import serialization.makeMessagesforSecondCsv
import serialization.messagesForThirdCsv
import serialization.messagesForFirstCsv
import serialization.writeCsv1
import serialization.writeCsv2
import serialization.writeCsv3
import java.io.FileOutputStream





suspend fun main() {
    val slackAPI = SlackAPI(System.getenv("SLACK_BOT_TOKEN"))
    val (users, messageWithUser) = slackAPI.getData(Settings.channelNameForFetch, Settings.fromDate, Settings.tillDate)


    //Mef comment: Gather info from Space and make a map (Email -> 4 Projects max)
    val mapForEmailAndTeamName = mapForEmailAndTeam(users)

    //Mef comment: create a list ready for the first csv
    val messagesReadyForFirstCsv = messagesForFirstCsv(messageWithUser)

    //Mef comment: create a list ready for the second csv (teams)
    val messagesForSecondCsv = makeMessagesforSecondCsv(
        mapForEmailAndTeamName
    )

    //Mef comment: create a list ready for the third csv (YouTrack tickets and its details)
    val messagesForThirdCsv = messagesForThirdCsv(slackAPI, messageWithUser)

    //Mef comment: output the first CSV file
    withContext(Dispatchers.IO) {
        FileOutputStream("filename.csv").apply { writeCsv1(messagesReadyForFirstCsv) }
    }

    //Mef comment: output the second CSV file
    withContext(Dispatchers.IO) {
        FileOutputStream("filename2.csv").apply { writeCsv2(messagesForSecondCsv) }
    }

    //Mef comment: output the third CSV file
    withContext(Dispatchers.IO) {
        FileOutputStream("filename3.csv").apply { writeCsv3(messagesForThirdCsv) }
    }

}
