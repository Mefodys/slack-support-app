package serialization

import types.CsvMessage1
import types.CsvMessage2
import types.CsvMessage3
import java.io.OutputStream
import kotlin.collections.forEach
import kotlin.io.bufferedWriter

fun OutputStream.writeCsv1(messages: List<CsvMessage1>) {
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

fun OutputStream.writeCsv2(messages: List<CsvMessage2>) {
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

fun OutputStream.writeCsv3(messages: List<CsvMessage3>) {
    val writer = bufferedWriter()
    writer.write("""TicketID, Type, Subsystem, State""")
    writer.newLine()
    messages.forEach {
        writer.write("${it.issueID}, ${it.issueType}, ${it.subsystem}, ${it.stateInYT}")
        writer.newLine()
    }
    writer.flush()
    println("YouTrack tickets csv was generated")
}
