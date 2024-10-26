package com.example.bookshelfapp.util

import com.example.bookshelfapp.data.model.Book

fun Book.ensureHttpsImageUrl(): Book {
    val httpsImageUrl = this.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")
    return this.copy(
        volumeInfo = this.volumeInfo.copy(
            imageLinks = this.volumeInfo.imageLinks?.copy(
                thumbnail = httpsImageUrl
            )
        )
    )
}
