package com.example.bookshelfapp.data.repository

import com.example.bookshelfapp.data.api.RetrofitInstance
import com.example.bookshelfapp.data.model.Book
import com.example.bookshelfapp.util.ensureHttpsImageUrl // Add this import
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class BooksRepository {
    private val service = RetrofitInstance.googleBooksService

    suspend fun searchBooks(query: String): List<Book> = withContext(Dispatchers.IO) {
        val response = service.getBooks(query)
        val bookIds = response.items?.map { it.id } ?: emptyList()

        bookIds.map { id ->
            async {
                try {
                    service.getBookDetails(id).ensureHttpsImageUrl()
                } catch (e: Exception) {
                    null
                }
            }
        }.mapNotNull { it.await() }
    }
}
