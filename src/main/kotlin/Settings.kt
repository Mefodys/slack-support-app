object Settings {

    private const val CHANNELNAME = "#eap"
    val channelNameForFetch = mapOf(CHANNELNAME to "C0KLZSCHF")[CHANNELNAME]
//    val channelNameForFetch = mapOf("#Kotlin" to "C0288G57R")[CHANNELNAME]

    val fromDate = dateToUnixTimestamp(2023, 12, 17).toString()
    val tillDate = dateToUnixTimestamp(2023, 12, 19).toString()

}