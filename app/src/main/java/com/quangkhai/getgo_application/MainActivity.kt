package com.quangkhai.getgo_application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.quangkhai.getgo_application.presentation.ui.UserTestScreen
import com.quangkhai.getgo_application.ui.theme.GetGo_ApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GetGo_ApplicationTheme {
                UserTestScreen()
            }
        }
    }
}
