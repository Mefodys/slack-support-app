fun makeListOfMessagesReadyforFirstCSV(
    rawSlackMessages: List<com.slack.api.model.Message>?,
    listOfTwoMapsWithUserData: List<MutableMap<String, String>>,
): MutableList<Message> {
    val listOfMessages: MutableList<Message> = mutableListOf()

    var wcmReaction = false
    var inProgressReaction = false
    var youtrackReaction = false

    if (rawSlackMessages != null) {
        for (message in rawSlackMessages) {

                val messageTs = message.ts.filterNot { it == '.' }
                val newConvertedTimestamp = convertTStoReadableDateTime(message)
                val messagePermalink = messagePermalink + messageTs

                if (message.reactions != null) {
                    val messageReactions = message.reactions
                    for (reaction in messageReactions) {

                        //BUG! reactions somewhy are not get correctly. try to look at the issue again with colleagues.

                        if (reaction.name == "white_check_mark") {
                            wcmReaction = true
                        }
                        if (reaction.name == "in_progress") {
                            inProgressReaction = true
                        }
                        if (reaction.name == "youtrack") {
                            youtrackReaction = true
                        }
                    }
                }

                val readyMessage = Message(
                    dateTime = newConvertedTimestamp,
                    slackLink = messagePermalink,
                    realName = listOfTwoMapsWithUserData[0][message.user],
                    slackEmail = listOfTwoMapsWithUserData[1][message.user],
                    reactionYT = youtrackReaction,
                    reactionInProgress = inProgressReaction,
                    reactionWhiteCheckMark = wcmReaction)

                listOfMessages.add(readyMessage)
            }
        }
    val reversedListOfMessages = listOfMessages.reversed() as MutableList
    return reversedListOfMessages
}