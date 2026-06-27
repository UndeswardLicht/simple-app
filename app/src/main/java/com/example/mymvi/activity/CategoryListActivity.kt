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
import com.example.mymvi.adapter.CategoryAdapter
import com.example.mymvi.activity.MainActivity
import com.example.mymvi.R
import com.example.mymvi.intent.CategoryIntent
import com.example.mymvi.model.CategoryViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class CategoryListActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private val viewModel: CategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category_list)

        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
                    //navigate to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_categories -> {
                    //Already on categories view, just close the drawer
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_categories)
        val adapter = CategoryAdapter { id, title, cashBack ->
            viewModel.processIntent(CategoryIntent.UpdateField(id, title, cashBack))
        }

        recyclerView.adapter = adapter

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.mergedCategories())
                }
            }
        }

        findViewById<Button>(R.id.button_save_all).setOnClickListener {
           viewModel.processIntent(CategoryIntent.SaveAll)
        }
    }
}