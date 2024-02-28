import Settings.channelNameForFetch
import com.slack.api.Slack

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

fun listOfMessagesReadyforThirdCsv(
    rawSlackMessages: List<com.slack.api.model.Message>?,
    //mapForEmailAndTeamName: MutableMap<String, MutableList<String>>
): MutableList<Message3> {

    val listOfTickets: MutableList<Message3> = mutableListOf()

    var ticketSet = mutableSetOf<String>()

    if (rawSlackMessages != null) {
        for (message in rawSlackMessages) {
            if (message.reactions != null) {
                val messageReactions = message.reactions
                for (reaction in messageReactions) {
                    if (reaction.name == "youtrack") {

                        val client = Slack.getInstance().methods()

                        val slackBotToken = System.getenv("SLACK_TOKEN_KOTLINLANG")
                        val messagesInThread = client.conversationsReplies {
                            it
                                .token(slackBotToken)
                                .channel(channelNameForFetch)
                                .ts(message.ts)
                        }

                        val pattern = ("[A-Z]{2,}\\-[\\d]{1,}").toRegex()
                        //val matchList = mutableListOf<String>()

                        for (i in messagesInThread.messages) {
                            if (i.text.contains("https://youtrack.jetbrains.com/issue/")) {
                                val foundTicket = pattern.findAll(i.text)
                                val names = foundTicket.map { it.value }.joinToString()
                                ticketSet.add(names)
                            }
                        }
                    }
                }
            }
        }
    }

    val resultStringList = mutableSetOf<String>()
    for (i in ticketSet) {
        val newStringList: Set<String> = i.split(", ").toSet()
        resultStringList += newStringList
    }

    for (ticket in resultStringList) {
        if (ticket != "null") {
            val readyMessage = Message3(
                issueID = ticket
            )
            listOfTickets.add(readyMessage)
        }
    }

    return listOfTickets
}