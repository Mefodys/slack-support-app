package serialization

import Settings.channelNameForFetch
import com.slack.api.Slack
import convertTStoReadableDateTime
import messagePermalink
import types.Message1
import types.Message2
import types.Message3
import types.MessageWithUser
import types.User
import kotlin.sequences.joinToString
import kotlin.sequences.map
import kotlin.text.contains
import kotlin.text.filterNot
import kotlin.text.substringBefore
import kotlin.text.toRegex

fun makeMessagesForFirstCsv(
    rawSlackMessages: List<com.slack.api.model.Message>,
    users: List<User>,
    messagesWithUsers: List<MessageWithUser>
): MutableList<Message1> {
//    if (rawSlackMessages == null) return mutableListOf()


    val listOfMessages: MutableList<Message1> = mutableListOf()

    ///
    messagesWithUsers.forEach {
        val message = it.msg
        val user = it.user

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
            realName = user.name,//users[0][message.user],
            slackEmail = user.email, //users[1][message.user],
            reactionYT = youtrackReaction,
            reactionInProgress = inProgressReaction,
            reactionWhiteCheckMark = wcmReaction
        )

        listOfMessages.add(readyMessage)
    }
    ///
//    for (message in rawSlackMessages) {
//
//
//
//        var wcmReaction = false
//        var inProgressReaction = false
//        var youtrackReaction = false
//
//        val messageTs = message.ts.filterNot { it == '.' }
//        val newConvertedTimestamp = convertTStoReadableDateTime(message)
//        val messagePermalink = messagePermalink + messageTs
//
//        if (message.reactions != null) {
//            val messageReactions = message.reactions
//            for (reaction in messageReactions) {
//
//                if (reaction.name == "white_check_mark") {
//                    wcmReaction = true
//                }
//                if (reaction.name == "in_progress") {
//                    inProgressReaction = true
//                }
//                if (reaction.name == "youtrack") {
//                    youtrackReaction = true
//                }
//            }
//        }
//
//        val readyMessage = Message1(
//            dateTime = newConvertedTimestamp,
//            slackLink = messagePermalink,
//            realName = users[0][message.user],
//            slackEmail = users[1][message.user],
//            reactionYT = youtrackReaction,
//            reactionInProgress = inProgressReaction,
//            reactionWhiteCheckMark = wcmReaction
//        )
//
//        listOfMessages.add(readyMessage)
//    }

    val reversedListOfMessages = listOfMessages
    return reversedListOfMessages
}


fun makeMessagesforSecondCsv(mapForEmailAndTeamName: MutableMap<String, MutableList<String>>): MutableList<Message2> {
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


fun makeMessagesforThirdCsv(
    rawSlackMessages: List<com.slack.api.model.Message>?,
    mapForEmailAndTeamName: MutableMap<String, MutableList<String>>
): MutableList<Message3> {

    val listOfTickets: MutableList<Message3> = mutableListOf()

    val ticketSet = mutableSetOf<String>()

    if (rawSlackMessages != null) {
        for (message in rawSlackMessages) {
            if (message.reactions != null) {
                val messageReactions = message.reactions
                for (reaction in messageReactions) {
                    if (reaction.name == "youtrack") {

                        val client = Slack.getInstance().methods()

                        val slackBotToken = System.getenv("SLACK_BOT_TOKEN")
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
                                ticketSet.add(names.substringBefore(","))

                            }
                        }
                    }
                }
            }
        }
    }


    for (ticket in ticketSet) {
        if (ticket != "null") {
            val readyMessage = Message3(
                issueID = ticket
            )
            listOfTickets.add(readyMessage)
        }
    }

    return listOfTickets
}
