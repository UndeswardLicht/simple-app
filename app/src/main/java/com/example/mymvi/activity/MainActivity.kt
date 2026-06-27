package com.example.mymvi.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.mymvi.R
import com.example.mymvi.adapter.ClientAdapter
import com.example.mymvi.intent.ClientIntent
import com.example.mymvi.model.ClientViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: ClientViewModel by viewModels()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_clients -> {
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_categories -> {
                    val intent = Intent(this, CategoryListActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_clients)

        val adapter = ClientAdapter(
            onItemClick = { id -> viewModel.processIntent(ClientIntent.NavigateToEdit(id)) },
            onDelete = { id -> viewModel.processIntent(ClientIntent.DeleteClient(id)) }
        )

        recyclerView.adapter = adapter

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.clients)

                    state.navigateToEdit?.let { id ->
                        val intent = Intent(this@MainActivity, EditClientActivity::class.java)
                            .putExtra(EditClientActivity.EXTRA_CLIENT_ID, id)
                        startActivity(intent)
                        viewModel.processIntent(ClientIntent.NavigatedToEdit)
                    }
                }
            }
        }

        findViewById<Button>(R.id.button_add_client)?.setOnClickListener {
            viewModel.processIntent(ClientIntent.AddClient)
        }
    }
}