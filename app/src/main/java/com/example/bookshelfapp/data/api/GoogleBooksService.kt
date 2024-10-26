package com.example.bookshelfapp.data.api

import com.example.bookshelfapp.data.model.Book
import com.example.bookshelfapp.data.model.BooksResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksService {
    @GET("volumes")
    suspend fun getBooks(@Query("q") query: String): BooksResponse

    @GET("volumes/{volumeId}")
    suspend fun getBookDetails(@Path("volumeId") volumeId: String): Book
}
