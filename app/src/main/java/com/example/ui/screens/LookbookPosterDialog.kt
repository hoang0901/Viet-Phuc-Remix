package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.SavedLookEntity
import com.example.ui.theme.*

@Composable
fun LookbookPosterDialog(
    savedLooks: List<SavedLookEntity>,
    onDismiss: () -> Unit,
    onExportPoster: () -> Unit,
    onSharePoster: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, BorderCream, RoundedCornerShape(24.dp)),
            color = IvoryBase
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Poster Lookbook",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalPrimary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CreamSurface)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = CharcoalPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // THE POSTER FRAME (Editorial Heritage Look)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, BorderCream, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = PureWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Poster Header
                        Text(
                            text = "MY VIỆT PHỤC LOOKBOOK",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalPrimary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Mặc chất riêng · Hiểu chất Việt",
                            fontSize = 12.sp,
                            color = VermilionRed,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = BorderCream, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Selected Looks in Poster
                        val displayLooks = savedLooks.take(3)
                        displayLooks.forEachIndexed { index, look ->
                            val lookLetter = when (index) {
                                0 -> "LOOK A"
                                1 -> "LOOK B"
                                else -> "LOOK C"
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CreamSurface)
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = VermilionRed
                                ) {
                                    Text(
                                        text = lookLetter,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = look.lookName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalPrimary
                                    )
                                    Text(
                                        text = "Văn hóa: ${look.culturalStatus}",
                                        fontSize = 11.sp,
                                        color = JadeTeal
                                    )
                                }
                                Text(
                                    text = "${look.totalScore} pts",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VermilionRed
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = BorderCream, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Poster Footer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Việt Phục Remix • Gen Z Heritage",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CharcoalMuted
                            )
                            Text(
                                text = "Năm 2026 - 2027",
                                fontSize = 10.sp,
                                color = CharcoalMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Export & Share buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onExportPoster,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, CharcoalPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = CharcoalPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Xuất ảnh", color = CharcoalPrimary, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onSharePoster,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VermilionRed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = PureWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chia sẻ", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
