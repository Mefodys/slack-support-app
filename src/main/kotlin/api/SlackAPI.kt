package api

import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.model.Message
import types.MessageWithUser
import types.User
import kotlin.collections.filter
import kotlin.collections.mutableListOf
import kotlin.collections.set

class SlackAPI (
    private val token: String,
    private val client: MethodsClient = Slack.getInstance().methods()
) {
    fun fetchSlackHistory(channelID: String?, oldest: String?, latest: String?): List<com.slack.api.model.Message> {
        val allRawMessages = client
            .conversationsHistory {
                it
                    .token(token)
                    .channel(channelID)
                    .limit(500)
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


    fun obtainTwoMapsWithUserIDUserNameEmail(rawSlackMessages: List<com.slack.api.model.Message>, ): Pair<List<User>, List<MessageWithUser>>  {

        val userRealNamesDict = mutableMapOf<String, String>()
        val userEmailsDict = mutableMapOf<String, String>()

        for (message in rawSlackMessages) {
            //Mef comment: Additional check if the usedID not null (if null then there will NPE during client.userInfo request)
            if (message.user != null) {
                val userInfo = client.usersInfo {
                    it
                        .token(token)
                        .user(message.user)
                }

                //Mef comment: filling of two maps with userdata
                userRealNamesDict[message.user] = userInfo.user.realName

                //Filling the second map (but skip the profile with no email)
                if (userInfo.user.profile.email == null) continue
                userEmailsDict[message.user] = userInfo.user.profile.email


            }
        }

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
}



