package com.akmere.iddog.ui.dogfeed

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ImageButton
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akmere.iddog.R
import com.akmere.iddog.ui.ExpandedImageView
import com.akmere.iddog.ui.login.LoginActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class DogFeedActivity : AppCompatActivity() {

    private val dogListViewModel by viewModel(DogFeedViewModel::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setupActionBar()

        dogListViewModel.dogFeedState.observe(this, Observer {
            when (it) {
                is DogFeedState.Success -> handleSuccessState(it.dogImages)
                is DogFeedState.LoginRequired -> redirectToLogin()
                is DogFeedState.Loading -> updateUiVisibility(
                    showLoading = true,
                    showDogList = false,
                    showError = false
                )
                is DogFeedState.Failure -> handleErrorState(it.errorMessage)
            }
        })
    }

    private fun setupActionBar() {
        supportActionBar?.let {
            it.setCustomView(R.layout.action_bar_view)
            val logoutButton =
                it.customView?.findViewById<ImageButton>(R.id.logout_button)


            it.displayOptions = (ActionBar.DISPLAY_SHOW_CUSTOM
                    or ActionBar.DISPLAY_SHOW_HOME)

            logoutButton?.setOnClickListener { view ->
                view.isEnabled = false
                AlertDialog.Builder(this)
                    .setTitle(R.string.logout_title)
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(R.string.confirm_logout) { dialog, _ ->
                        dialog.dismiss()
                        dogListViewModel.logout()
                        redirectToLogin()
                    }
                    .setOnDismissListener {
                        view.isEnabled = true
                    }
                    .show()
            }

            setupSpinner(it)
        }
    }

    private fun setupSpinner(actionBar: ActionBar) {
        val dogCategories = resources.getStringArray(R.array.dog_categories)

        val spinner = actionBar.customView?.findViewById<Spinner>(R.id.spinner)

        spinner?.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            dogCategories
        )
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val expandedImageContainer =
                    findViewById<ExpandedImageView>(R.id.expanded_image_container)

                if (expandedImageContainer.isVisible) expandedImageContainer.visibility =
                    View.GONE

                dogListViewModel.loadDogFeed(dogCategories[position])
            }
        }
    }

    private fun redirectToLogin() {
        LoginActivity.start(this)
        finish()
    }

    private fun updateUiVisibility(
        showLoading: Boolean,
        showDogList: Boolean,
        showError: Boolean
    ) {
        val dogListView = findViewById<RecyclerView>(R.id.recyclerview_dog_list)
        val loadingView = findViewById<ProgressBar>(R.id.loading)
        val errorView = findViewById<ConstraintLayout>(R.id.error)
        dogListView.visibility = if (showDogList) View.VISIBLE else View.GONE
        loadingView.visibility = if (showLoading) View.VISIBLE else View.GONE
        errorView.visibility = if (showError) View.VISIBLE else View.GONE
    }

    private fun handleSuccessState(dogImages: List<String>) {
        val dogListView = findViewById<RecyclerView>(R.id.recyclerview_dog_list)
        dogListView.layoutManager = GridLayoutManager(this, 3)

        dogListView.adapter = DogFeedAdapter(dogImages, ::expandDogImage)
        updateUiVisibility(showLoading = false, showDogList = true, showError = false)
    }

    private fun handleErrorState(errorMessage: String) {
        val errorMessageView = findViewById<TextView>(R.id.error_message)
        errorMessageView.text = errorMessage
        updateUiVisibility(showLoading = false, showDogList = false, showError = true)

    }

    private fun expandDogImage(image: Drawable?, view: View) {
        val dogListView = findViewById<RecyclerView>(R.id.recyclerview_dog_list)
        val expandedImageContainer = findViewById<ExpandedImageView>(R.id.expanded_image_container)
        val closeImageButton = findViewById<ImageButton>(R.id.close_image)
        val expandedImage = findViewById<AppCompatImageView>(R.id.expanded_image)

        image?.let {
            expandedImage.setImageDrawable(it)
            expandedImageContainer.visibility = View.VISIBLE
            dogListView.suppressLayout(true)
            supportActionBar?.hide()
            view.isEnabled = false

            closeImageButton.setOnClickListener {
                expandedImageContainer.visibility = View.GONE
                dogListView.suppressLayout(false)
                view.isEnabled = true
                supportActionBar?.show()
            }
        }
    }


}