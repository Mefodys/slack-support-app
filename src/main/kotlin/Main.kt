import api.SlackAPI
import api.makeMapForEmailAndTeam

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import serialization.makeMessagesforSecondCsv
import serialization.makeMessagesforThirdCsv
import serialization.makeMessagesForFirstCsv
import serialization.writeCsv1
import serialization.writeCsv2
import serialization.writeCsv3
import java.io.FileOutputStream





suspend fun main() {
    val slackAPI = SlackAPI(System.getenv("SLACK_BOT_TOKEN"))

    //Mef comment: make a request to obtain Slack History
    val rawSlackMessages =
        slackAPI.fetchSlackHistory(Settings.channelNameForFetch, Settings.fromDate, Settings.tillDate)

    //Mef comment: make a request for userinfo to obtain a list with two maps(userid-username, userid-email) based on rawMessages.
    val (users, messageWithUser) = slackAPI.obtainTwoMapsWithUserIDUserNameEmail(rawSlackMessages)


    //Mef comment: Gather info from Space and make a map (Email -> 4 Projects max)
    val mapForEmailAndTeamName = makeMapForEmailAndTeam(users)

//    val messagesWithUsers = makeMessagesWithUsers(users, rawSlackMessages)

    //Mef comment: create a list ready for the first csv
    val messagesReadyForFirstCsv = makeMessagesForFirstCsv(
        rawSlackMessages,
        users,
        messageWithUser
    )

    //Mef comment: create a list ready for the second csv (teams)
    val messagesForSecondCsv = makeMessagesforSecondCsv(
        mapForEmailAndTeamName
    )

    //Mef comment: create a list ready for the third csv (YouTrack tickets and its details)
    val messagesForThirdCsv = makeMessagesforThirdCsv(rawSlackMessages, mapForEmailAndTeamName)

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
