package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.CulturalRuleEngine
import com.example.ui.components.AvatarCanvas
import com.example.ui.components.BodyMetricsController
import com.example.ui.components.CulturalCheckBanner
import com.example.ui.components.StyleModeSelector
import com.example.ui.theme.*

@Composable
fun OutfitBuilderScreen(
    currentOutfit: ActiveOutfit,
    onOutfitUpdated: (ActiveOutfit) -> Unit,
    allGarments: List<GarmentItem>,
    allInnerGarments: List<GarmentItem>,
    allBottoms: List<GarmentItem>,
    allFootwear: List<GarmentItem>,
    allHeadwear: List<GarmentItem>,
    allAccessories: List<GarmentItem>,
    colorPalettes: List<ColorOption>,
    onViewLookCard: () -> Unit,
    onSaveLook: () -> Unit,
    onOpenCompare: () -> Unit,
    onRotateAngle: () -> Unit,
    onOpenAiPrompt: () -> Unit,
    onToggleGender: () -> Unit,
    onOpenScanCamera: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedLayerTab by remember { mutableStateOf(GarmentLayerType.MAIN_GARMENT) }

    // Evaluate live Cultural Check & Score
    val (checkResult, score) = remember(currentOutfit) {
        CulturalRuleEngine.evaluate(currentOutfit)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IvoryBase)
    ) {
        // 1. TOP HEADER & QUICK ACTION BAR (Thu gọn tối đa khoảng trắng, đưa khung nhân vật lên cao)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = "Studio Phối Đồ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalPrimary,
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = "Dịp: ${currentOutfit.occasion.title}",
                    fontSize = 11.sp,
                    color = VermilionRed,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    softWrap = false
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Các nút hành động cuộn ngang mượt mà, không bao giờ bị bóp ép hay kéo dài thành dải dọc
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = onOpenScanCamera,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = CreamSurface),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DocumentScanner,
                        contentDescription = "Quét",
                        tint = VermilionRed,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Quét", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VermilionRed, maxLines = 1, softWrap = false)
                }

                FilledTonalButton(
                    onClick = onOpenAiPrompt,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = CreamSurface),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "Prompt AI",
                        tint = JadeTeal,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("AI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = JadeTeal, maxLines = 1, softWrap = false)
                }

                FilledTonalButton(
                    onClick = onOpenCompare,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = CreamSurface),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = "So sánh",
                        tint = CharcoalPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("So sánh", fontSize = 10.sp, color = CharcoalPrimary, maxLines = 1, softWrap = false)
                }

                Button(
                    onClick = onViewLookCard,
                    colors = ButtonDefaults.buttonColors(containerColor = VermilionRed),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Xem Look Card",
                        tint = PureWhite,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Look Card", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PureWhite, maxLines = 1, softWrap = false)
                }
            }
        }

        // 2. PINNED HERO MODEL SECTION (Đưa khung nhân vật lên cao, tận dụng diện tích)
        AvatarCanvas(
            outfit = currentOutfit,
            onRotateAngle = onRotateAngle,
            onToggleGender = onToggleGender,
            onMetricsChanged = { newH, newW ->
                onOutfitUpdated(currentOutfit.copy(heightCm = newH, weightKg = newW))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(horizontal = 14.dp, vertical = 2.dp)
        )

        // 3. SCROLLABLE CUSTOMIZATION PANE (Khoảng cách gọn gàng, thoáng đãng)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
        ) {
            // 3-Mode Selector (Nét Gốc | Giao Thoa | Remix Gen Z)
            item {
                StyleModeSelector(
                    selectedMode = currentOutfit.mode,
                    onModeSelected = { newMode ->
                        onOutfitUpdated(currentOutfit.copy(mode = newMode))
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Realtime Cultural Check Banner (4-state)
            item {
                CulturalCheckBanner(
                    checkResult = checkResult,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Vóc Dáng & Số Đo 3D (Chiều cao & Cân nặng)
            item {
                BodyMetricsController(
                    outfit = currentOutfit,
                    onMetricsChanged = { newH, newW ->
                        onOutfitUpdated(currentOutfit.copy(heightCm = newH, weightKg = newW))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Color Palette Selector
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Màu chủ đạo (Áo chính)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalPrimary
                        )
                        Text(
                            text = currentOutfit.primaryColor.name,
                            fontSize = 11.sp,
                            color = VermilionRed,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(colorPalettes) { palette ->
                            val isSelected = palette.id == currentOutfit.primaryColor.id
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(palette.color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) CharcoalPrimary else BorderCream,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onOutfitUpdated(currentOutfit.copy(primaryColor = palette))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Đã chọn",
                                        tint = if (palette.id == "kem") CharcoalPrimary else PureWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Màu phối phụ (Quần / Váy)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalPrimary
                        )
                        Text(
                            text = currentOutfit.secondaryColor.name,
                            fontSize = 11.sp,
                            color = JadeTeal,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(colorPalettes) { palette ->
                            val isSelected = palette.id == currentOutfit.secondaryColor.id
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(palette.color)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) JadeTeal else BorderCream,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onOutfitUpdated(currentOutfit.copy(secondaryColor = palette))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Đã chọn",
                                        tint = if (palette.id == "kem") CharcoalPrimary else PureWhite,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Layer Tabs Header
            item {
                Text(
                    text = "Tùy chọn từng lớp trang phục (Layers)",
                    style = MaterialTheme.typography.titleMedium,
                    color = CharcoalPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                ScrollableTabRow(
                    selectedTabIndex = selectedLayerTab.ordinal,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    GarmentLayerType.entries.filter { it != GarmentLayerType.HAIR }.forEach { layerType ->
                        Tab(
                            selected = selectedLayerTab == layerType,
                            onClick = { selectedLayerTab = layerType },
                            text = {
                                Text(
                                    text = layerType.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedLayerTab == layerType) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedLayerTab == layerType) VermilionRed else CharcoalSecondary
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Items for selected layer
            val currentItemList = when (selectedLayerTab) {
                GarmentLayerType.MAIN_GARMENT -> allGarments
                GarmentLayerType.INNER_GARMENT -> allInnerGarments
                GarmentLayerType.BOTTOM -> allBottoms
                GarmentLayerType.FOOTWEAR -> allFootwear
                GarmentLayerType.HEADWEAR -> allHeadwear
                GarmentLayerType.ACCESSORY -> allAccessories
                else -> emptyList()
            }

            items(currentItemList) { item ->
                val isCurrentSelected = when (selectedLayerTab) {
                    GarmentLayerType.MAIN_GARMENT -> currentOutfit.mainGarment.id == item.id
                    GarmentLayerType.INNER_GARMENT -> currentOutfit.innerGarment?.id == item.id
                    GarmentLayerType.BOTTOM -> currentOutfit.bottom.id == item.id
                    GarmentLayerType.FOOTWEAR -> currentOutfit.footwear.id == item.id
                    GarmentLayerType.HEADWEAR -> currentOutfit.headwear?.id == item.id
                    GarmentLayerType.ACCESSORY -> currentOutfit.accessory?.id == item.id
                    else -> false
                }

                GarmentSelectableItem(
                    garment = item,
                    isSelected = isCurrentSelected,
                    onClick = {
                        val updated = when (selectedLayerTab) {
                            GarmentLayerType.MAIN_GARMENT -> currentOutfit.copy(mainGarment = item)
                            GarmentLayerType.INNER_GARMENT -> currentOutfit.copy(innerGarment = if (isCurrentSelected) null else item)
                            GarmentLayerType.BOTTOM -> currentOutfit.copy(bottom = item)
                            GarmentLayerType.FOOTWEAR -> currentOutfit.copy(footwear = item)
                            GarmentLayerType.HEADWEAR -> currentOutfit.copy(headwear = if (isCurrentSelected) null else item)
                            GarmentLayerType.ACCESSORY -> currentOutfit.copy(accessory = if (isCurrentSelected) null else item)
                            else -> currentOutfit
                        }
                        onOutfitUpdated(updated)
                    },
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            // Save & Explore Action Button
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onSaveLook,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VermilionRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = null,
                        tint = PureWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lưu Look vào Lookbook của tôi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
            }
        }
    }
}

@Composable
private fun GarmentSelectableItem(
    garment: GarmentItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) VermilionRed else BorderCream,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PureWhite else CreamSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = VermilionRed)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = garment.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalPrimary,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (garment.isHistoricalPiece) JadeTeal.copy(alpha = 0.15f) else ImperialGold.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (garment.isHistoricalPiece) "Truyền thống" else "Hiện đại",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (garment.isHistoricalPiece) JadeTeal else ImperialGold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = garment.shortStory,
                    fontSize = 11.sp,
                    color = CharcoalSecondary,
                    lineHeight = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
