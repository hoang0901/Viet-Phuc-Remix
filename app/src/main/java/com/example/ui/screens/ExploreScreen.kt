package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.model.ActiveOutfit
import com.example.data.model.OccasionEvent
import com.example.data.model.OutfitBadgeType
import com.example.ui.components.OutfitBadge
import com.example.ui.theme.*

@Composable
fun ExploreScreen(
    selectedOccasion: OccasionEvent,
    onOccasionSelected: (OccasionEvent) -> Unit,
    onStartMixMatch: () -> Unit,
    onSelectPresetOutfit: (ActiveOutfit) -> Unit,
    onReadArticle: (String) -> Unit,
    onOpenScanCamera: () -> Unit,
    onOpenHeritageMap: () -> Unit,
    presetLooks: List<ActiveOutfit>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(IvoryBase)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
    ) {
        // App Header & Brand Philosophy
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "VIỆT PHỤC",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = VermilionRed
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = VermilionRed
                            ) {
                                Text(
                                    text = "REMIX",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PureWhite,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Mặc chất riêng, hiểu chất Việt",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = CharcoalSecondary
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = CardHighlight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream)
                    ) {
                        IconButton(onClick = onStartMixMatch, modifier = Modifier.size(38.dp)) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Bắt đầu phối đồ",
                                tint = ImperialGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hero Banner (Tối ưu padding và khoảng trắng)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = VermilionRed)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(VermilionDark, VermilionRed, Color(0xFFD64545))
                                )
                            )
                            .padding(14.dp)
                    ) {
                        Column {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = PureWhite.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Triết lý: Remix phong cách, không remix lịch sử",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PureWhite,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Hôm nay bạn muốn diện Việt phục ở đâu?",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Trợ lý phối đồ có hiểu biết văn hóa, giúp bạn tự tin trong các dịp lễ tết, tốt nghiệp và dạo phố.",
                                fontSize = 12.sp,
                                color = PureWhite.copy(alpha = 0.9f),
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = onStartMixMatch,
                                    colors = ButtonDefaults.buttonColors(containerColor = PureWhite),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Phối đồ 3D",
                                        color = VermilionRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = VermilionRed,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                OutlinedButton(
                                    onClick = onOpenScanCamera,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PureWhite.copy(alpha = 0.8f)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DocumentScanner,
                                        contentDescription = null,
                                        tint = PureWhite,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Quét Camera",
                                        color = PureWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Camera Scan Feature Card (Gọn gàng, tỉ lệ cân đối)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onOpenScanCamera),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, ImperialGold.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = VermilionRed.copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DocumentScanner,
                                contentDescription = null,
                                tint = VermilionRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Quét Việt phục bằng Camera",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalPrimary,
                                modifier = Modifier.weight(1f, fill = false),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            OutfitBadge(badgeType = OutfitBadgeType.AI_ANALYZED)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Chụp 5 góc để AI đối chiếu điểm neo và đưa vào 3D Studio",
                            fontSize = 11.sp,
                            color = CharcoalSecondary,
                            lineHeight = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = VermilionRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Vietnam Heritage Map Feature Card (Gọn gàng)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onOpenHeritageMap),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, JadeTeal.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = JadeTeal.copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = JadeTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Bản đồ Di sản 34 Tỉnh/Thành",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalPrimary,
                                modifier = Modifier.weight(1f, fill = false),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ImperialGold.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "SVG GỐC",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ImperialGold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Khám phá trang phục truyền thống và hoa văn bản địa trên bản đồ vector tương tác",
                            fontSize = 11.sp,
                            color = CharcoalSecondary,
                            lineHeight = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = JadeTeal,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Section: Chọn sự kiện / Bối cảnh
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bối cảnh & Dịp diện đồ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalPrimary
                )
                Text(
                    text = "6 sự kiện",
                    fontSize = 11.sp,
                    color = CharcoalMuted
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(OccasionEvent.entries) { occasion ->
                    val isSelected = occasion == selectedOccasion
                    OccasionCard(
                        occasion = occasion,
                        isSelected = isSelected,
                        onClick = { onOccasionSelected(occasion) }
                    )
                }
            }
        }

        // Section: Gợi ý Outfit chuẩn theo sự kiện đã chọn (Không để 'Tự do sáng tạo' bị ép dọc)
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Gợi ý Look: ${selectedOccasion.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = selectedOccasion.subtitle,
                        fontSize = 11.sp,
                        color = CharcoalSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = onStartMixMatch,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Tự do sáng tạo",
                        color = VermilionRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(presetLooks) { look ->
            FeaturedLookCard(
                outfit = look,
                onSelect = { onSelectPresetOutfit(look) },
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        // Section: "60 giây hiểu về..." Micro Knowledge Card
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, BorderCream, RoundedCornerShape(14.dp))
                    .clickable { onReadArticle("ao_ngu_than_1744") },
                colors = CardDefaults.cardColors(containerColor = CreamSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = JadeTeal.copy(alpha = 0.15f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = JadeTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "60 GIÂY HIỂU VỀ",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = JadeTeal
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = JadeTeal.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Đã được ghi nhận",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = JadeTeal,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                        Text(
                            text = "Vì sao áo ngũ thân có đúng 5 thân & 5 cúc cài?",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalPrimary
                        )
                        Text(
                            text = "Khám phá ý nghĩa 'Tứ thân phụ mẫu' và 'Ngũ thường' từ tư liệu chính thống.",
                            fontSize = 11.sp,
                            color = CharcoalSecondary,
                            maxLines = 2,
                            lineHeight = 15.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Đọc thêm",
                        tint = CharcoalMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OccasionCard(
    occasion: OccasionEvent,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (icon, badgeColor) = when (occasion) {
        OccasionEvent.TET -> Pair(Icons.Default.Celebration, VermilionRed)
        OccasionEvent.GRADUATION -> Pair(Icons.Default.School, RoyalNavy)
        OccasionEvent.HERITAGE_TOUR -> Pair(Icons.Default.AccountBalance, JadeTeal)
        OccasionEvent.FESTIVAL -> Pair(Icons.Default.Festival, ImperialGold)
        OccasionEvent.PHOTO_SHOOT -> Pair(Icons.Default.CameraAlt, LotusPink)
        OccasionEvent.CASUAL -> Pair(Icons.Default.DirectionsWalk, ForestGreen)
    }

    Card(
        modifier = Modifier
            .width(148.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) badgeColor else BorderCream,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PureWhite else CreamSurface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = badgeColor.copy(alpha = if (isSelected) 0.2f else 0.1f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = occasion.tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = occasion.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = occasion.subtitle,
                fontSize = 11.sp,
                color = CharcoalMuted,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun FeaturedLookCard(
    outfit: ActiveOutfit,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderCream, RoundedCornerShape(16.dp))
            .clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(containerColor = PureWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual badge with outfit's primary color
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(outfit.primaryColor.color)
                    .border(1.5.dp, outfit.secondaryColor.color, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Checkroom,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = outfit.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalPrimary,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (outfit.mode) {
                            com.example.data.model.CulturalMode.HERITAGE -> VermilionRed.copy(alpha = 0.15f)
                            com.example.data.model.CulturalMode.FUSION -> JadeTeal.copy(alpha = 0.15f)
                            com.example.data.model.CulturalMode.REMIX_GEN_Z -> ImperialGold.copy(alpha = 0.2f)
                        }
                    ) {
                        Text(
                            text = outfit.mode.title,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (outfit.mode) {
                                com.example.data.model.CulturalMode.HERITAGE -> VermilionRed
                                com.example.data.model.CulturalMode.FUSION -> JadeTeal
                                com.example.data.model.CulturalMode.REMIX_GEN_Z -> ImperialGold
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${outfit.mainGarment.name} · ${outfit.bottom.name} · ${outfit.footwear.name}",
                    fontSize = 11.sp,
                    color = CharcoalSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = outfit.customNotes,
                    fontSize = 10.sp,
                    color = CharcoalMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onSelect, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Xem look này",
                    tint = VermilionRed,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
