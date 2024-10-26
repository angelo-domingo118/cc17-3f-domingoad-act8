package com.example.bookshelfapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bookshelfapp.R
import com.example.bookshelfapp.data.model.Book
import com.example.bookshelfapp.databinding.ItemBookBinding
import com.example.bookshelfapp.util.ensureHttpsImageUrl

class BookAdapter : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BookViewHolder(private val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.bookTitleTextView.text = book.volumeInfo.title
            // Use the ensureHttpsImageUrl extension
            val secureBook = book.ensureHttpsImageUrl()
            binding.bookCoverImageView.load(secureBook.volumeInfo.imageLinks?.thumbnail) {
                crossfade(true)
                placeholder(R.drawable.ic_book_placeholder)
                error(R.drawable.ic_book_placeholder)
            }
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}
