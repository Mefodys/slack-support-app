import java.io.OutputStream

fun OutputStream.writeCsv1(messages: List<Message1>) {
    val writer = bufferedWriter()
    writer.write("""DateTime, SlackLink, RealName, Email, ReactionYT, ReactionInProgress, ReactionWhiteCheckMark""")
    writer.newLine()
    messages.forEach {
        writer.write("${it.dateTime}, ${it.slackLink}, ${it.realName}, ${it.slackEmail}, ${it.reactionYT}, ${it.reactionInProgress}, ${it.reactionWhiteCheckMark}")
        writer.newLine()
    }
    writer.flush()
    println("The main csv was generated")
}

fun OutputStream.writeCsv2(messages: List<Message2>) {
    val writer = bufferedWriter()
    writer.write("""Team""")
    writer.newLine()
    messages.forEach {
        writer.write("${it.project}")
        writer.newLine()
    }
    writer.flush()
    println("Teams csv was generated")
}