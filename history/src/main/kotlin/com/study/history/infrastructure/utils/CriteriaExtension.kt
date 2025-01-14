package com.study.history.infrastructure.utils

import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.elasticsearch.core.query.Criteria
import kotlin.reflect.KProperty

val KProperty<*>.criteria: Criteria
    get() {
        return Criteria(this.name)
    }

fun KProperty<*>.sort(direction: Direction = Direction.ASC): Sort {
    return Sort.by(direction, this.name)
}
