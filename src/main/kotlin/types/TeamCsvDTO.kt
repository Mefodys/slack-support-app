package types

data class TeamCsvDTO(
    val project: String? = null
) : CsvSerializable
