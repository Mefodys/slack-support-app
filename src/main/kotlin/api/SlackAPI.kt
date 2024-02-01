package api

import Settings.channelToFetch
import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.response.conversations.ConversationsRepliesResponse
import com.slack.api.model.Message
import types.MessageWithUser
import types.User
import kotlin.collections.filter
import kotlin.collections.mutableListOf

class SlackAPI (
    private val token: String,
) {
    private val client: MethodsClient = Slack.getInstance().methods()

    private fun fetchSlackHistory(channelID: String, oldest: String, latest: String, limit: Int): List<Message> {
        val allRawMessages = client
            .conversationsHistory {
                it
                    .token(token)
                    .channel(channelID)
                    .limit(limit)
                    .oldest(oldest)
                    .latest(latest)
            }.also {
                if (!it.isOk) {
                    throw Exception("Slack connection error: ${it.error}")
                }
            }.messages

        val messagesWithText = allRawMessages
            .filter {
                it.subtype != "channel_join" && it.subtype != "channel_leave"
            } //!no warning from IDEA plugin - KTIJ-27938

        return messagesWithText
    }

    private fun deserializeData(rawSlackMessages: List<Message>): Pair<List<User>, List<MessageWithUser>>  {
        val messageWithUsers = mutableListOf<MessageWithUser>()

        val listOfUsers = rawSlackMessages
            .asSequence()
            .filter {
                it.user != null

            }
            .map { msg ->
                val userInfo = client.usersInfo {
                    it
                        .token(token)
                        .user(msg.user)
                }

                val user = User(
                    id = msg.user,
                    name = userInfo.user.realName,
                    email = userInfo.user.profile?.email ?: ""
                )
                messageWithUsers.add(MessageWithUser(msg, user))
                user
            }.toList()

        return Pair(listOfUsers, messageWithUsers)
    }

    fun getData(channelID: String, oldest: String, latest: String, limit: Int): Pair<List<User>, List<MessageWithUser>> =
         fetchSlackHistory(channelID, oldest, latest, limit).let { deserializeData(it) }


    fun getThreadsFromMsg(message: Message): ConversationsRepliesResponse {
        return client.conversationsReplies {
            it
                .token(token)
                .channel(channelToFetch)
                .ts(message.ts)
        }
    }
}



