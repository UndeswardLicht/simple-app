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
import com.example.mymvi.intent.EditClientIntent
import com.example.mymvi.model.EditClientViewModel
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

        val spinnerAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            mutableListOf()
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = it
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect { state ->
                    if (state.isDone) {
                        finish()
                        return@collect
                    }
                    spinnerAdapter.clear()
                    spinnerAdapter.addAll(state.categories.map { it.title })

                    state.client?.let { client ->
                        if (nameEditText.text.isEmpty()) {
                            nameEditText.setText(client.name)
                        }
                        val index = state.categories.indexOfFirst { it.id == client.categoryId }
                        if (index != -1) categorySpinner.setSelection(index)
                    }
                }
            }
        }

        saveButton.setOnClickListener {
            val pos = categorySpinner.selectedItemPosition
            val categoryId = viewModel.uiState.value.categories
                .getOrNull(pos)?.id
            viewModel.processIntent(EditClientIntent.Save(
                name = nameEditText.text.toString(),
                categoryId = categoryId
            ))
        }

        backButton.setOnClickListener {
            viewModel.processIntent(EditClientIntent.Cancel)
        }

        cancelButton?.setOnClickListener {
            viewModel.processIntent(EditClientIntent.Cancel)
        }
    }

    companion object {
        const val EXTRA_CLIENT_ID = "extra_client_id"
    }
}