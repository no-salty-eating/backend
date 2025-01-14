package com.study.history.infrastructure.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun String.toLocalDate(format: String): LocalDate {
    return LocalDate.parse(this.filter { it.isDigit() }, DateTimeFormatter.ofPattern(format))
}
