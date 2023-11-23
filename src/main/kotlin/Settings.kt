object Settings {

    private const val CHANNELNAME = "#Kotlin"
    val channelNameForFetch = mapOf(CHANNELNAME to "C0288G57R")[CHANNELNAME]

    val fromDate = dateToUnixTimestamp(2022, 12, 1).toString()
    val tillDate = dateToUnixTimestamp(2023, 3, 1).toString()


}