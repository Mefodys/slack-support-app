import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

suspend fun main() {

    //Mef comment: make a request to obtain Slack History
    val rawSlackMessages =
        fetchSlackHistory(Settings.channelNameForFetch, Settings.fromDate, Settings.tillDate)

    //Mef comment: make a request for userinfo to obtain a list with two maps(userid-username, userid-email) based on rawMessages.
    val listOfTwoMapsWithUserData = obtainTwoMapsWithUserIDUserNameEmail(rawSlackMessages)

    //Mef comment: Gather info from Space and make a map (Email -> 4 Projects max)
    val mapForEmailAndTeamName = makeMapForEmailandTeam(listOfTwoMapsWithUserData[1])

    //Mef comment: create a list ready for the first csv
    val listOfMessagesReadyforFirstCsv = makeListOfMessagesReadyforFirstCSV(
        rawSlackMessages,
        listOfTwoMapsWithUserData
    )

    //Mef comment: create a list ready for the second csv (teams)
    val listOfMessagesReadyforSecondCsv = listOfMessagesReadyforSecondCsv(
        mapForEmailAndTeamName
    )

    //Mef comment: create a list ready for the third csv (YouTrack tickets and its details)
    val listOfMessagesReadyforThirdCsv = listOfMessagesReadyforThirdCsv()

    //Mef comment: output the first CSV file
    withContext(Dispatchers.IO) {
        FileOutputStream("filename.csv").apply { writeCsv1(listOfMessagesReadyforFirstCsv) }
    }

    //Mef comment: output the second CSV file
    withContext(Dispatchers.IO) {
        FileOutputStream("filename2.csv").apply { writeCsv2(listOfMessagesReadyforSecondCsv) }
    }

    //Mef comment: output the third CSV file
    withContext(Dispatchers.IO) {
        FileOutputStream("filename3.csv").apply { writeCsv3(listOfMessagesReadyforThirdCsv) }
    }

}