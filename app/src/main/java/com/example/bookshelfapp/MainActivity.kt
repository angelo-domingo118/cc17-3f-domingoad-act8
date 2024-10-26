package com.example.bookshelfapp

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bookshelfapp.data.repository.BooksRepository
import com.example.bookshelfapp.databinding.ActivityMainBinding
import com.example.bookshelfapp.ui.BookAdapter
import com.example.bookshelfapp.ui.BookshelfViewModel
import com.example.bookshelfapp.ui.BookshelfViewModelFactory
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: BookshelfViewModel by viewModels { 
        BookshelfViewModelFactory(BooksRepository()) 
    }
    private val bookAdapter = BookAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchInput()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.booksRecyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = bookAdapter
        }
    }

    private fun setupSearchInput() {
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun performSearch() {
        val query = binding.searchEditText.text.toString().trim()
        if (query.isNotEmpty()) {
            viewModel.searchBooks(query)
        }
    }

    private fun observeViewModel() {
        viewModel.books.observe(this) { books ->
            bookAdapter.submitList(books)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.loadingProgressBar.isVisible = isLoading
        }

        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
