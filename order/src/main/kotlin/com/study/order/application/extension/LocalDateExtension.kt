package com.study.order.application.extension

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun String.toLocalDate(format: String): LocalDate {
    return LocalDate.parse(this.filter { it.isDigit() }, DateTimeFormatter.ofPattern(format))
}

fun LocalDate.toString(format: String): String {
    return this.format(DateTimeFormatter.ofPattern(format))
}