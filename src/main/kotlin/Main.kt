fun main() {

    // make a request to obtain Slack History
    val rawMessages =
        fetchSlackHistory(Settings.channelNameForFetch, Settings.fromDate, Settings.tillDate)

    val mapForSlackUserIDandSlackName = mapForSlackUserIDandSlackName(rawMessages)


    println(mapForSlackUserIDandSlackName.map { "${it.key}: ${it.value}" }.joinToString(", "))




}