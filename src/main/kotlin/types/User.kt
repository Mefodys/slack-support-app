package types

import com.slack.api.model.Message

class User (
    val id: String,
    val name: String,
    val email: String
)

class MessageWithUser(val msg: Message, val user: User)
