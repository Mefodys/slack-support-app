fun main() {

    //Mef comment: make a request to obtain Slack History
    val rawMessages =
        fetchSlackHistory(Settings.channelNameForFetch, Settings.fromDate, Settings.tillDate)


    //Mef comment: make a request for userinfo to obtain a list with two maps(userid-username, userid-email) based on rawMessages.
    val mapForSlackUserIDandSlackName = mapForSlackUserIDandSlackName(rawMessages)

}