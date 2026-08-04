package com.quangkhai.getgo_application.presentation.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.presentation.ui.shared.AuthField
import com.quangkhai.getgo_application.presentation.ui.shared.PillButton

private val GreenBackground = Color(0xFF1B7426)

@Composable
fun RegisterScreen(
    onSubmit: (name: String, username: String, password: String) -> Unit,
    onGoLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenBackground)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Register", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        AuthField(name, { name = it }, "Name")
        Spacer(Modifier.height(12.dp))
        AuthField(username, { username = it }, "Username")
        Spacer(Modifier.height(12.dp))
        AuthField(password, { password = it }, "Password", isPassword = true)
        Spacer(Modifier.height(18.dp))
        PillButton("Submit", onClick = { onSubmit(name, username, password) })
        Spacer(Modifier.height(14.dp))
        Text(
            "Have account? Login",
            color = Color.White,
            fontSize = 13.sp,
            modifier = Modifier.clickable { onGoLogin() }
        )
    }
}
