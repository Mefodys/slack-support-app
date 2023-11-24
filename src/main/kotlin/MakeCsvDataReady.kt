fun makeListOfMessagesReadyforFirstCSV(
    rawSlackMessages: List<com.slack.api.model.Message>?,
    listOfTwoMapsWithUserData: List<MutableMap<String, String>>,
    //mapForEmailAndTeamName: MutableMap<String, MutableList<String>>,
): MutableList<Message> {
    val listOfMessages: MutableList<Message> = mutableListOf()

    var wcmReaction = false
    var eyesReaction = false
    var heartReaction = false

    if (rawSlackMessages != null) {
        for (message in rawSlackMessages) {
            if (message.text != null) {

                val messageTs = message.ts.filterNot { it == '.' }
                val newConvertedTimestamp = convertTStoReadableDateTime(message)
                val messagePermalink = messagePermalink + messageTs

                if (message.reactions != null) {
                    val messageReactions = message.reactions
                    for (reaction in messageReactions) {
                        if (reaction.name == "white_check_mark") {
                            wcmReaction = true
                        }
                        if (reaction.name == "in_progress") {
                            eyesReaction = true
                        }
                        if (reaction.name == "youtrack") {
                            heartReaction = true
                        }
                    }
                }

                val readyMessage = Message(
                    dateTime = newConvertedTimestamp,
                    link = messagePermalink,
                    realName = listOfTwoMapsWithUserData[0][message.user],
                    slackEmail = listOfTwoMapsWithUserData[1][message.user],
                    reactionYT = heartReaction,
                    reactionInProgress = eyesReaction,
                    reactionWhiteCheckMark = wcmReaction)
            }
        }
    }
    val reversedListOfMessages = listOfMessages.reversed() as MutableList
    return reversedListOfMessages
}