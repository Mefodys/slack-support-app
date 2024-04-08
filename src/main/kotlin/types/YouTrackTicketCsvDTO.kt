package types



class YouTrackIssueDTO(
    val id: String,
    val type: String,
    val subsystem: String,
    val state: String
) : CsvSerializable

