object Settings {

    const val CHANNEL_TO_FETCH = "#eap"
    private val channelToId = mapOf(
        "#Kotlin" to "C0288G57R", "#eap" to "C0KLZSCHF"
    )

    val channelToFetch = channelToId[CHANNEL_TO_FETCH]!!

    val fromDate = dateToUnixTimestamp(2023, 11, 1).toString()
    val tillDate = dateToUnixTimestamp(2024, 12, 1).toString()

}
//private const val CHANNELNAME = "#eap"
//val channelNameForFetch = mapOf(CHANNELNAME to "C0KLZSCHF")[CHANNELNAME]
