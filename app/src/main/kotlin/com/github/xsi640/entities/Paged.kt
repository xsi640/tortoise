package com.github.xsi640.entities

class Paged<T>(
    val total: Long,
    val page: Long,
    val size: Long,
    val data: List<T>
)