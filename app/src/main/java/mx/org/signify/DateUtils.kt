package mx.org.signify

import java.text.SimpleDateFormat
import java.util.*

fun String.toDate(format: String): Date {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.parse(this) ?: Date()
}