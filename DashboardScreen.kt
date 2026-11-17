package com.schoolmanagement.app.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.schoolmanagement.app.R
import com.schoolmanagement.app.domain.security.Permission
import com.schoolmanagement.app.domain.security.Permissions
import com.schoolmanagement.app.domain.security.SessionUser

private data class Module(@StringRes val title: Int, val permission: Permission)

private val modules = listOf(
    Module(R.string.mod_students, Permission.VIEW_STUDENTS),
    Module(R.string.mod_classes, Permission.MANAGE_CLASSES),
    Module(R.string.mod_attendance, Permission.MARK_ATTENDANCE),
    Module(R.string.mod_fees, Permission.VIEW_FEES),
    Module(R.string.mod_exams, Permission.VIEW_RESULTS),
    Module(R.string.mod_staff, Permission.MANAGE_STAFF),
    Module(R.string.mod_reports, Permission.VIEW_REPORTS),
    Module(R.string.mod_backup, Permission.BACKUP_RESTORE),
    Module(R.string.mod_settings, Permission.MANAGE_SETTINGS),
    Module(R.string.mod_users, Permission.MANAGE_USERS)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(user: SessionUser, onLogout: () -> Unit) {
    var showSoon by remember { mutableStateOf(false) }
    val allowed = Permissions.of(user.role)
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(R.string.dashboard)) },
            actions = { TextButton(onClick = onLogout) { Text(stringResource(R.string.logout)) } }
        )
    }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            Text(stringResource(R.string.welcome, user.fullName), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(modules.filter { it.permission in allowed }) { m ->
                    ElevatedCard(onClick = { showSoon = true }, modifier = Modifier.fillMaxWidth().height(96.dp)) {
                        Box(Modifier.fillMaxSize().padding(12.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            Text(stringResource(m.title), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
    if (showSoon) AlertDialog(
        onDismissRequest = { showSoon = false },
        confirmButton = { TextButton(onClick = { showSoon = false }) { Text("OK") } },
        text = { Text(stringResource(R.string.coming_soon)) }
    )
}
