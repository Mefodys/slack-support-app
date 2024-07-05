object Settings {

    private const val CHANNEL_TO_FETCH = "#Kotlin"
    private val channelToId = mapOf(
        "#Kotlin" to "C0288G57R"
    )

    val channelToFetch = channelToId[CHANNEL_TO_FETCH]!!

    val fromDate = dateToUnixTimestamp(2024, 4, 1).toString()
    val tillDate = dateToUnixTimestamp(2024, 7, 1).toString()

}
