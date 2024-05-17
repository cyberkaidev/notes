package com.cyberkaidev.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cyberkaidev.notes.model.HomePageModel
import com.cyberkaidev.notes.ui.theme.NotesTheme
import com.cyberkaidev.notes.ui.view.pages.HomePage
import com.cyberkaidev.notes.ui.view.pages.NewNotePage
import com.cyberkaidev.notes.viewmodel.NotesViewModel
import com.google.android.material.color.DynamicColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DynamicColors.applyToActivitiesIfAvailable(this.application)
        val viewModel by viewModels<NotesViewModel>()
        val notes = viewModel.notes.value

        setContent {
            NotesTheme {
                Surface {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomePage(HomePageModel(navController, notes))
                        }
                        composable("new-note") {
                            NewNotePage(navController, viewModel)
                        }
                    }
                }
            }
        }
    }
}