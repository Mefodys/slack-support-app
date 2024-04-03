package types

data class YouTrackTicketCsvDTO(
    val issueID: String,
    val issueType: String? = null,
    val subsystem: String? = null,
    val stateInYT: String? = null
) : CsvSerializable


data class YouTrackTicketCsvDTO2(
    val id: String,
    val type: String,
    val subsystem: String,
    val state: String
) : CsvSerializable

