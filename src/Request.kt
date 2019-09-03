package com.example

interface Request<T> {
    fun prepare()
    suspend fun execute(): T
}