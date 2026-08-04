package com.quangkhai.getgo_application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.quangkhai.getgo_application.presentation.navigation.GetGoNavGraph
import com.quangkhai.getgo_application.ui.theme.GetGo_ApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GetGo_ApplicationTheme {
                val navController = rememberNavController()
                GetGoNavGraph(navController = navController)
            }
        }
    }
}
