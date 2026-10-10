package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    savedLooksCount: Int,
    articlesCount: Int,
    modifier: Modifier = Modifier
) {
    val genZStyles = listOf(
        "Tối giản (Minimalist)",
        "Thanh lịch (Elegant)",
        "Vintage Hoài Cổ",
        "Streetwear Năng Động",
        "Pastel Ngọt Ngào",
        "Cá Tính Đột Phá"
    )
    var selectedStyle by remember { mutableStateOf(genZStyles[0]) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(IvoryBase)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // User Identity Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderCream, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = PureWhite)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(VermilionRed)
                            .border(3.dp, ImperialGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "VP",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Gen Z Yêu Việt Phục",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Mặc chất riêng, hiểu chất Việt",
                        fontSize = 12.sp,
                        color = VermilionRed,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CreamSurface)
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$savedLooksCount",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = VermilionRed
                            )
                            Text(
                                text = "Look đã lưu",
                                fontSize = 11.sp,
                                color = CharcoalSecondary
                            )
                        }

                        VerticalDivider(
                            color = BorderCream,
                            modifier = Modifier.height(32.dp)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$articlesCount",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = JadeTeal
                            )
                            Text(
                                text = "Bài học di sản",
                                fontSize = 11.sp,
                                color = CharcoalSecondary
                            )
                        }

                        VerticalDivider(
                            color = BorderCream,
                            modifier = Modifier.height(32.dp)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Cấp 3",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = ImperialGold
                            )
                            Text(
                                text = "Am hiểu di sản",
                                fontSize = 11.sp,
                                color = CharcoalSecondary
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Personal Style Selector
        item {
            Text(
                text = "Phong cách cá nhân của bạn",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CharcoalPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PureWhite)
                    .border(1.dp, BorderCream, RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                genZStyles.forEach { style ->
                    val isSelected = style == selectedStyle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CardHighlight else Color.Transparent)
                            .clickable { selectedStyle = style }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = style,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) VermilionRed else CharcoalPrimary
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Đã chọn",
                                tint = VermilionRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Privacy Policy & Legal compliance (Luật 91/2025/QH15 - Page 18 of PDF)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderCream, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CreamSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = JadeTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bảo vệ dữ liệu cá nhân & Riêng tư",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tuân thủ nghiêm ngặt Luật Bảo vệ dữ liệu cá nhân số 91/2025/QH15 và Nghị định hướng dẫn 356/2025/NĐ-CP. Mọi outfit, phối màu và ghi chú được lưu trữ an toàn trong bộ nhớ máy cục bộ (Room Local DB) và không chia sẻ cho bên thứ ba khi chưa có sự đồng thuận.",
                        fontSize = 11.sp,
                        color = CharcoalSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section: About App
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderCream, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = PureWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Về Việt Phục Remix",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Phiên bản: 1.0.0 (MVP 2026-2027)",
                        fontSize = 11.sp,
                        color = CharcoalMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "“Việt Phục Remix không cố đóng khung người trẻ trong quá khứ. Ứng dụng tạo một khoảng không để họ sáng tạo với di sản, đồng thời luôn biết ranh giới giữa điều được kế thừa, điều được cách tân và điều do chính họ thêm vào.”",
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = CharcoalSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}
