package com.example.bookshelfapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import coil.load
import com.example.bookshelfapp.R
import com.example.bookshelfapp.data.model.Book
import com.example.bookshelfapp.databinding.BottomSheetBookDetailsBinding
import com.example.bookshelfapp.util.ensureHttpsImageUrl
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

class BookDetailsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetBookDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetBookDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Book>(ARG_BOOK)?.let { book ->
            setupBookDetails(book)
        }
    }

    private fun setupBookDetails(book: Book) {
        val secureBook = book.ensureHttpsImageUrl()
        with(binding) {
            bookDetailTitleTextView.text = book.volumeInfo.title
            bookDetailAuthorsTextView.text = book.volumeInfo.authors?.joinToString(", ")
            
            // Publisher and date
            val publisherText = buildString {
                book.volumeInfo.publisher?.let { append(it) }
                book.volumeInfo.publishedDate?.let {
                    if (isNotEmpty()) append(" • ")
                    append(it.split("-")[0]) // Show only the year
                }
            }
            bookDetailPublisherTextView.text = publisherText
            bookDetailPublisherTextView.isVisible = publisherText.isNotEmpty()

            // Description
            bookDetailDescriptionTextView.text = book.volumeInfo.description
            bookDetailDescriptionLabel.isVisible = !book.volumeInfo.description.isNullOrEmpty()
            bookDetailDescriptionTextView.isVisible = !book.volumeInfo.description.isNullOrEmpty()

            // Categories as chips
            categoriesChipGroup.removeAllViews()
            book.volumeInfo.categories?.forEach { category ->
                val chip = Chip(requireContext()).apply {
                    text = category
                    setChipBackgroundColorResource(R.color.material_on_surface_stroke)
                    setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Chip)
                }
                categoriesChipGroup.addView(chip)
            }
            categoriesChipGroup.isVisible = !book.volumeInfo.categories.isNullOrEmpty()

            // Book cover
            bookDetailCoverImageView.load(secureBook.volumeInfo.imageLinks?.thumbnail) {
                crossfade(true)
                placeholder(R.drawable.ic_book_placeholder)
                error(R.drawable.ic_book_placeholder)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_BOOK = "book"

        fun newInstance(book: Book) = BookDetailsBottomSheet().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_BOOK, book)
            }
        }
    }
}
