package serialization

import types.CsvSerializable
import types.serialize
import java.io.FileOutputStream
import java.io.PrintWriter
import kotlin.collections.forEach

fun createCsv(fileName: String, header: String, messages: List<CsvSerializable>) =
    PrintWriter(FileOutputStream(fileName)).use { w ->
        w.println(header)
        messages.forEach {
            w.println(it.serialize())
        }
    }
