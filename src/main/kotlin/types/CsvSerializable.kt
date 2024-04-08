package types

sealed interface CsvSerializable

fun CsvSerializable.serialize(): String? =
    when (this) {
        is MainCsvDTO -> "$dateTime, $slackLink, $realName, $slackEmail, $reactionYT, $reactionInProgress, $reactionWhiteCheckMark"
        is TeamCsvDTO -> project
        is YouTrackIssueDTO-> "$id, $type, $subsystem, $state"
    }


