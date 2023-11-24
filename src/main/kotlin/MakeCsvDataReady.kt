fun makeListOfMessagesReadyforFirstCSV(
    rawSlackMessages: List<com.slack.api.model.Message>?,
    listOfTwoMapsWithUserData: List<MutableMap<String, String>>,
): MutableList<Message1> {
    val listOfMessages: MutableList<Message1> = mutableListOf()



    if (rawSlackMessages != null) {
        for (message in rawSlackMessages) {

            var wcmReaction = false
            var inProgressReaction = false
            var youtrackReaction = false

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
                        inProgressReaction = true
                    }
                    if (reaction.name == "youtrack") {
                        youtrackReaction = true
                    }
                }
            }

            val readyMessage = Message1(
                dateTime = newConvertedTimestamp,
                slackLink = messagePermalink,
                realName = listOfTwoMapsWithUserData[0][message.user],
                slackEmail = listOfTwoMapsWithUserData[1][message.user],
                reactionYT = youtrackReaction,
                reactionInProgress = inProgressReaction,
                reactionWhiteCheckMark = wcmReaction
            )

            listOfMessages.add(readyMessage)

        }
    }
    val reversedListOfMessages = listOfMessages
    return reversedListOfMessages
}


fun listOfMessagesReadyforSecondCsv(mapForEmailAndTeamName: MutableMap<String, MutableList<String>>): MutableList<Message2> {
    val listOfProjects: MutableList<Message2> = mutableListOf()

    for (message in mapForEmailAndTeamName) {
        val projects = message.value
        for (i in projects) {
            if (i != "null") {
                val readyMessage = Message2(
                    project = i
                )
                listOfProjects.add(readyMessage)
            }
        }
    }
    return listOfProjects
}