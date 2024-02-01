package serialization

import api.SlackAPI
import com.slack.api.methods.response.conversations.ConversationsRepliesResponse
import convertTStoReadableDateTime
import messagePermalink
import types.MainCsvDTO
import types.TeamCsvDTO
import types.YouTrackTicketCsvDTO
import types.MessageWithUser
import kotlin.sequences.flatMap
import kotlin.sequences.joinToString
import kotlin.sequences.map
import kotlin.text.contains
import kotlin.text.filterNot
import kotlin.text.substringBefore
import kotlin.text.toRegex

fun messagesForFirstCsv(
    messagesWithUsers: List<MessageWithUser>
): List<MainCsvDTO> {

    val reversedListOfMessages = messagesWithUsers.map {
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

        MainCsvDTO(
            dateTime = newConvertedTimestamp,
            slackLink = messagePermalink,
            realName = user.name,
            slackEmail = user.email,
            reactionYT = youtrackReaction,
            reactionInProgress = inProgressReaction,
            reactionWhiteCheckMark = wcmReaction
        )
    }

    return reversedListOfMessages
}


fun messagesForSecondCsv(mapForEmailAndTeamName: MutableMap<String, MutableList<String>>): List<TeamCsvDTO> {
    val listOfProjects = mapForEmailAndTeamName.flatMap { message ->
        val projects = message.value
        projects.asSequence().filter { it != "null" }
            .map {
                TeamCsvDTO(
                    project = it
                )
            }
    }

    return listOfProjects
}


fun messagesForThirdCsv(
    slackAPI: SlackAPI,
    messageWithUser: List<MessageWithUser>,
): List<YouTrackTicketCsvDTO> {

    val pattern = ("[A-Z]{2,}\\-[\\d]{1,}").toRegex()

    val getMessagesWithYTLink = { messagesInThread: ConversationsRepliesResponse ->
        messagesInThread.messages.asSequence()
            .filter { it.text.contains("https://youtrack.jetbrains.com/issue/") }
            .map {
                val foundTicket = pattern.findAll(it.text)
                val names = foundTicket.joinToString { it.value }
                names.substringBefore(",")
            }.toSet()
            .filter { it != "null" } // are we sure it is possible?
    }

    val result = messageWithUser.asSequence()
        .filter { it.msg.reactions != null }
        .flatMap {
            val messagesInThread = slackAPI.getThreadsFromMsg(it.msg)
            val ytReaction = it.msg.reactions.find { it.name == "youtrack" }
            if (ytReaction != null) {
                getMessagesWithYTLink(messagesInThread)
            } else
                setOf()
        }
        .map { YouTrackTicketCsvDTO(issueID = it) }
        .toList()

    return result
}
