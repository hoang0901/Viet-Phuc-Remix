package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.VietPhucDatabase
import com.example.data.repository.VietPhucRepository
import com.example.ui.VietPhucApp
import com.example.ui.VietPhucViewModel
import com.example.ui.VietPhucViewModelFactory
import com.example.ui.theme.*
import kotlinx.coroutines.delay

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
                var showSplash by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(1300)
                    showSplash = false
                }

                AnimatedVisibility(
                    visible = showSplash,
                    exit = fadeOut() + scaleOut(targetScale = 1.1f)
                ) {
                    SplashScreen()
                }

                if (!showSplash) {
                    VietPhucApp(viewModel = viewModel)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Tự động save khi thoát app
        viewModel.saveCurrentLook()
    }
}

@Composable
fun SplashScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = IvoryBase
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(VermilionRed.copy(alpha = 0.15f), IvoryBase, ImperialGold.copy(alpha = 0.1f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = VermilionRed,
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(96.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ImperialGold,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Việt Phục Remix",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Khám phá & Bảo tồn di sản văn hóa truyền thống",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CharcoalSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                CircularProgressIndicator(
                    color = VermilionRed,
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 3.dp
                )
            }
        }
    }
}
