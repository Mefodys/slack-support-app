package types

data class YouTrackTicketCsvDTO(
    val issueID: String? = null,
    val issueType: String? = null,
    val subsystem: String? = null,
    val stateInYT: String? = null
) : CsvSerializable

