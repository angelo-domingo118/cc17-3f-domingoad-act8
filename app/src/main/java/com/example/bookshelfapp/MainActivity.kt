package com.example.bookshelfapp

import GridSpacingItemDecoration
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import android.widget.EditText
import androidx.core.view.ViewCompat
import com.example.bookshelfapp.data.model.Book
import com.example.bookshelfapp.data.repository.BooksRepository
import com.example.bookshelfapp.databinding.ActivityMainBinding
import com.example.bookshelfapp.ui.BookAdapter
import com.example.bookshelfapp.ui.BookDetailsBottomSheet
import com.example.bookshelfapp.ui.BookshelfViewModel
import com.example.bookshelfapp.ui.BookshelfViewModelFactory
import com.google.android.material.snackbar.Snackbar
import android.content.Context
import android.widget.TextView
import android.content.res.Configuration
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: BookshelfViewModel by viewModels { 
        BookshelfViewModelFactory(BooksRepository()) 
    }
    private val bookAdapter = BookAdapter { book ->
        showBookDetails(book)
    }

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
            // Adjust grid columns based on screen size and orientation
            val spanCount = when {
                resources.configuration.screenWidthDp >= 840 -> 4 // Large tablets
                resources.configuration.screenWidthDp >= 600 -> 3 // Tablets
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE -> 3
                else -> 2 // Phones portrait
            }
            
            layoutManager = GridLayoutManager(this@MainActivity, spanCount)
            adapter = bookAdapter
            
            // Add item decoration for consistent spacing
            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
                        val position = parent.getChildAdapterPosition(view)
                        val column = position % spanCount

                        outRect.left = spacing - column * spacing / spanCount
                        outRect.right = (column + 1) * spacing / spanCount
                        if (position < spanCount) outRect.top = spacing
                        outRect.bottom = spacing
                    }
                })
            }
        }
    }

    private fun setupSearchInput() {
        binding.searchEditText?.apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun performSearch() {
        binding.searchEditText?.text?.toString()?.let { query ->
            if (query.isNotBlank()) {
                viewModel.searchBooks(query)
                hideKeyboard()
            }
        }
    }

    private fun hideKeyboard() {
        binding.searchEditText?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun observeViewModel() {
        viewModel.books.observe(this) { books ->
            bookAdapter.submitList(books)
            if (books.isEmpty() && !viewModel.isLoading.value!!) {
                showMessage(getString(R.string.no_results))
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.networkStatusCard?.apply {
                isVisible = isLoading
                if (isLoading) {
                    animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(300)
                        .start()
                } else {
                    animate()
                        .alpha(0f)
                        .translationY(-50f)
                        .setDuration(200)
                        .start()
                }
            }
            
            // Disable search while loading
            binding.searchEditText?.isEnabled = !isLoading
        }

        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                binding.networkStatusCard?.isVisible = false
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAction("Retry") { performSearch() }
                    .show()
            }
        }
    }

    private fun showBookDetails(book: Book) {
        BookDetailsBottomSheet.newInstance(book)
            .show(supportFragmentManager, "BookDetailsBottomSheet")
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    // Handle configuration changes
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setupRecyclerView() // Readjust the grid
    }
}
