package com.example.mymvi.activity

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mymvi.R
import com.example.mymvi.data.Category
import com.example.mymvi.model.EditClientViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class EditClientActivity : AppCompatActivity() {

    private val viewModel: EditClientViewModel by viewModels {
        EditClientViewModel.Factory(intent.getIntExtra(EXTRA_CLIENT_ID, -1))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_client)

        val rootLayout = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nameEditText = findViewById<EditText>(R.id.edit_text_name)
        val categorySpinner = findViewById<Spinner>(R.id.spinner_category)
        val saveButton = findViewById<Button>(R.id.button_save)
        val backButton = findViewById<ImageButton>(R.id.button_back)
        val cancelButton = findViewById<Button>(R.id.button_cancel)

        var categoriesList: List<Category> = emptyList()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(viewModel.client, viewModel.categories) { client, categories ->
                    client to categories
                }.collect { (client, categories) ->
                    categoriesList = categories

                    val adapter = ArrayAdapter(
                        this@EditClientActivity,
                        android.R.layout.simple_spinner_item,
                        categories.map { it.title }
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    categorySpinner.adapter = adapter

                    if (client != null) {
                        if (nameEditText.text.isEmpty()) {
                            nameEditText.setText(client.name)
                        }

                        val selectionIndex = categories.indexOfFirst { it.id == client.categoryId }
                        if (selectionIndex != -1) {
                            categorySpinner.setSelection(selectionIndex)
                        }
                    }
                }
            }
        }

        saveButton.setOnClickListener {
            val selectedPosition = categorySpinner.selectedItemPosition
            val categoryId = if (selectedPosition != Spinner.INVALID_POSITION && categoriesList.isNotEmpty()) {
                categoriesList[selectedPosition].id
            } else {
                null
            }
            viewModel.saveClient(nameEditText.text.toString(), categoryId)
            finish()
        }

        backButton.setOnClickListener {
            finish()
        }

        cancelButton?.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val EXTRA_CLIENT_ID = "extra_client_id"
    }
}