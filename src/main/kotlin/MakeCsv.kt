import java.io.OutputStream

fun OutputStream.writeCsv(messages: List<Message>) {
    val writer = bufferedWriter()
    writer.write("""DateTime, SlackLink, RealName, Email, ReactionYT, ReactionInProgress, ReactionWhiteCheckMark""")
    writer.newLine()
    messages.forEach {
        writer.write("${it.dateTime}, ${it.slackLink}, ${it.realName}, ${it.slackEmail}, ${it.reactionYT}, ${it.reactionInProgress}, ${it.reactionWhiteCheckMark}")
        writer.newLine()
    }
    writer.flush()
    println("The first csv was generated")
}