package com.example.modul2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.modul2.navigation.NavigationItem
import com.example.modul2.navigation.Screen
import com.example.modul2.screen.MatkulScreen
import com.example.modul2.screen.ProfileScreen
import com.example.modul2.screen.TugasScreen
import com.example.modul2.ui.theme.Modul2Theme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        checkAuthAndShowAppropriateScreen()
    }

    private fun checkAuthAndShowAppropriateScreen() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            showMainScreen()
        } else {
            startAuthActivity()
        }
    }

    private fun showMainScreen() {
        setContent {
            Modul2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainActivityContent(
                        onLogout = {
                            auth.signOut()
                            checkAuthAndShowAppropriateScreen()
                        }
                    )
                }
            }
        }
    }

    private fun startAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun MainActivityContent(
    onLogout: () -> Unit,
    navController: NavHostController = rememberNavController(),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = { BottomAppBar(navController) },
        modifier = modifier
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Matkul.route
            ) {
                composable(Screen.Matkul.route) {
                    MatkulScreen(
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                        onLogout = onLogout
                    )
                }
                composable(Screen.Tugas.route) {
                    TugasScreen(onNavigateBack = { navController.navigateUp() })
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(onNavigateBack = { navController.navigateUp() })
                }
            }
        }
    }
}

@Composable
private fun BottomAppBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        val navigationItems = listOf(
            NavigationItem("Matkul", Screen.Matkul.icon, Screen.Matkul),
            NavigationItem("Tugas", Screen.Tugas.icon, Screen.Tugas),
            NavigationItem("Profile", Screen.Profile.icon, Screen.Profile)
        )
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.title) },
                selected = false,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}