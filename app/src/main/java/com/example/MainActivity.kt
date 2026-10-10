package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.local.VietPhucDatabase
import com.example.data.repository.VietPhucRepository
import com.example.ui.VietPhucApp
import com.example.ui.VietPhucViewModel
import com.example.ui.VietPhucViewModelFactory
import com.example.ui.theme.VietPhucRemixTheme

class MainActivity : ComponentActivity() {

    private val viewModel: VietPhucViewModel by viewModels {
        val database = VietPhucDatabase.getInstance(applicationContext)
        val repository = VietPhucRepository(database.savedLookDao())
        VietPhucViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VietPhucRemixTheme {
                VietPhucApp(viewModel = viewModel)
            }
        }
    }
}
