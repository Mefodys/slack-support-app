object Settings {

    private const val CHANNELNAME = "#Kotlin"
    val channelNameForFetch = mapOf(CHANNELNAME to "C0288G57R")[CHANNELNAME]

    val fromDate = dateToUnixTimestamp(2023, 9, 1).toString()
    val tillDate = dateToUnixTimestamp(2023, 12, 1).toString()


}