package com.schoolmanagement.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.schoolmanagement.app.R
import com.schoolmanagement.app.presentation.auth.AuthState

@Composable
fun AuthScreen(state: AuthState, setupMode: Boolean, onSubmit: (String, String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().padding(24.dp).widthIn(max = 480.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        if (setupMode) {
            Text(stringResource(R.string.setup_title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.setup_hint), style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(16.dp))
        if (setupMode) {
            OutlinedTextField(fullName, { fullName = it }, label = { Text(stringResource(R.string.full_name)) },
                singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
        }
        OutlinedTextField(username, { username = it }, label = { Text(stringResource(R.string.username)) },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(password, { password = it }, label = { Text(stringResource(R.string.password)) },
            singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        state.errorRes?.let {
            Spacer(Modifier.height(8.dp))
            Text(stringResource(it), color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { onSubmit(username, fullName, password) },
            enabled = !state.busy,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.busy) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            else Text(stringResource(if (setupMode) R.string.create_account else R.string.login))
        }
    }
}
