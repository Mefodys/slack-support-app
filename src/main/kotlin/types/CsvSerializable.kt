package types

sealed interface CsvSerializable

fun CsvSerializable.serialize(): String? =
    when (this) {
        is MainCsvDTO -> "$dateTime, $slackLink, $realName, $slackEmail, $reactionYT, $reactionInProgress, $reactionWhiteCheckMark"
        is TeamCsvDTO -> project
        is YouTrackTicketCsvDTO -> "$issueID, $issueType, $subsystem, $stateInYT"
        is YouTrackTicketCsvDTO2-> "$id, $type, $subsystem, $state"
    }


