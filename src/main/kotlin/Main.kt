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
}