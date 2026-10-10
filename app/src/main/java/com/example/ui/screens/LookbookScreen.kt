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
import com.example.data.local.SavedLookEntity
import com.example.data.model.*
import com.example.data.repository.VietPhucRepository
import com.example.ui.components.AvatarCanvas
import com.example.ui.components.OutfitBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LookbookScreen(
    savedLooks: List<SavedLookEntity>,
    onSelectLookToWear: (SavedLookEntity) -> Unit,
    onDeleteLook: (Long) -> Unit,
    onOpenPosterDialog: () -> Unit,
    onCompareSavedLooks: (SavedLookEntity, SavedLookEntity) -> Unit,
    onNavigateToBuilder: () -> Unit,
    onSelectPresetOutfitToStudio: ((ActiveOutfit) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedForCompareA by remember { mutableStateOf<SavedLookEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IvoryBase)
            .padding(horizontal = 16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Lookbook & Di Sản",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalPrimary
                )
                Text(
                    text = "Kho lưu trữ cá nhân & AI Lookbook đa góc",
                    fontSize = 12.sp,
                    color = CharcoalSecondary
                )
            }

            if (selectedTab == 0 && savedLooks.isNotEmpty()) {
                Button(
                    onClick = onOpenPosterDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = VermilionRed),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Collections,
                        contentDescription = "Tạo Poster",
                        tint = PureWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tạo Poster", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }
        }

        // Two Primary Tabs (Đặc tả trang 1 & 2): Bộ sưu tập cá nhân vs AI Lookbook đa góc
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = VermilionRed,
            divider = {}
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "Bộ Sưu Tập Của Tôi (${savedLooks.size})",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 0) VermilionRed else CharcoalSecondary
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = "AI Lookbook Đa Góc",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 1) VermilionRed else CharcoalSecondary
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            // TAB 1: SAVED LOOKS LIST
            SavedLooksView(
                savedLooks = savedLooks,
                selectedForCompareA = selectedForCompareA,
                onCancelCompare = { selectedForCompareA = null },
                onSelectLookToWear = onSelectLookToWear,
                onDeleteLook = onDeleteLook,
                onSelectForCompare = { look ->
                    if (selectedForCompareA == null) {
                        selectedForCompareA = look
                    } else if (selectedForCompareA?.id != look.id) {
                        onCompareSavedLooks(selectedForCompareA!!, look)
                        selectedForCompareA = null
                    }
                },
                onNavigateToBuilder = onNavigateToBuilder
            )
        } else {
            // TAB 2: MULTI-ANGLE AI LOOKBOOK SHOWCASE (Hero 3/4, Front, Back, Detail)
            MultiAngleLookbookShowcase(
                onSelectLookToWear = { outfit ->
                    onSelectPresetOutfitToStudio?.invoke(outfit) ?: onNavigateToBuilder()
                }
            )
        }
    }
}

@Composable
private fun SavedLooksView(
    savedLooks: List<SavedLookEntity>,
    selectedForCompareA: SavedLookEntity?,
    onCancelCompare: () -> Unit,
    onSelectLookToWear: (SavedLookEntity) -> Unit,
    onDeleteLook: (Long) -> Unit,
    onSelectForCompare: (SavedLookEntity) -> Unit,
    onNavigateToBuilder: () -> Unit
) {
    if (selectedForCompareA != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            colors = CardDefaults.cardColors(containerColor = CardHighlight),
            border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Đã chọn: ${selectedForCompareA.lookName}. Hãy chọn thêm Look thứ hai để so sánh!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = CharcoalPrimary,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onCancelCompare) {
                    Text("Hủy", color = VermilionRed, fontSize = 11.sp)
                }
            }
        }
    }

    if (savedLooks.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 96.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = CreamSurface,
                    modifier = Modifier.size(80.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Style,
                            contentDescription = null,
                            tint = ImperialGold,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Chưa có look nào trong bộ sưu tập",
                    style = MaterialTheme.typography.titleMedium,
                    color = CharcoalPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Hãy vào tab 'Phối đồ' để thử nghiệm các lớp trang phục và lưu những set đồ yêu thích của bạn vào đây.",
                    fontSize = 13.sp,
                    color = CharcoalMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onNavigateToBuilder,
                    colors = ButtonDefaults.buttonColors(containerColor = VermilionRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = PureWhite
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Bắt đầu phối look đầu tiên", fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            items(savedLooks, key = { it.id }) { look ->
                SavedLookCardItem(
                    look = look,
                    onOpen = { onSelectLookToWear(look) },
                    onDelete = { onDeleteLook(look.id) },
                    onSelectForCompare = { onSelectForCompare(look) },
                    isComparingA = selectedForCompareA?.id == look.id,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }
}

/**
 * AI Lookbook Showcase nhiều góc: Hero 3/4, Front, Back/Side, Detail
 * Kèm phân định Badge chuẩn xác theo đặc tả trang 1 & 2
 */
@Composable
private fun MultiAngleLookbookShowcase(
    onSelectLookToWear: (ActiveOutfit) -> Unit
) {
    val showcaseLooks: List<ActiveOutfit> = listOf(
        VietPhucRepository.getHueMinimalOutfit().copy(name = "Áo Ngũ Thân Huế Hoàng Triều"),
        VietPhucRepository.getDefaultOutfit(OccasionEvent.FESTIVAL).copy(name = "Áo Tứ Thân Bắc Bộ Hội Lim"),
        VietPhucRepository.getTetHeritageOutfit().copy(name = "Áo Dài Truyền Thống Đón Xuân")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Notice banner on AI illustration transparency
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PureWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = JadeTeal,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Quy định minh bạch PRD 2026: AI Lookbook là lớp minh họa trực quan sinh bởi AI. Ảnh có biến đổi cấu trúc được gắn badge [AI minh họa – cần kiểm tra cấu trúc] để phân biệt rõ với [3D Outfit] và [Ảnh chụp người thật].",
                        fontSize = 11.sp,
                        color = CharcoalSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        items(showcaseLooks) { outfit ->
            MultiAngleLookItemCard(
                outfit = outfit,
                onWearInStudio = { onSelectLookToWear(outfit) }
            )
        }
    }
}

@Composable
private fun MultiAngleLookItemCard(
    outfit: ActiveOutfit,
    onWearInStudio: () -> Unit
) {
    var selectedAngle by remember { mutableStateOf(AvatarAngle.THREE_QUARTER) }
    var currentBadge by remember {
        mutableStateOf(
            if (outfit.mainGarment.id == "ao_tu_than") OutfitBadgeType.REAL_PHOTO
            else OutfitBadgeType.THREE_D_OUTFIT
        )
    }

    val displayOutfit = remember(outfit, selectedAngle) {
        outfit.copy(viewAngle = selectedAngle)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with title and Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = outfit.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalPrimary
                    )
                    Text(
                        text = "Bối cảnh: ${outfit.occasion.title} • ${outfit.mode.title}",
                        fontSize = 11.sp,
                        color = VermilionRed
                    )
                }
                OutfitBadge(badgeType = currentBadge)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Multi-angle selector pills (Hero 3/4, Front, Back, Detail)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    AnglePill(
                        label = "Hero 3/4 (Nghiêng)",
                        isSelected = selectedAngle == AvatarAngle.THREE_QUARTER,
                        onClick = {
                            selectedAngle = AvatarAngle.THREE_QUARTER
                        }
                    )
                }
                item {
                    AnglePill(
                        label = "Chính diện (0°)",
                        isSelected = selectedAngle == AvatarAngle.FRONT,
                        onClick = {
                            selectedAngle = AvatarAngle.FRONT
                        }
                    )
                }
                item {
                    AnglePill(
                        label = "Mặt sau (180°)",
                        isSelected = selectedAngle == AvatarAngle.BACK,
                        onClick = {
                            selectedAngle = AvatarAngle.BACK
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3D Canvas visualizer for the selected angle
            AvatarCanvas(
                outfit = displayOutfit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Heritage Anchor note & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Điểm neo: ${outfit.mainGarment.historyLayer.originEra}",
                        fontSize = 11.sp,
                        color = CharcoalMuted,
                        maxLines = 1
                    )
                }

                Button(
                    onClick = onWearInStudio,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VermilionRed),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.ViewInAr, contentDescription = null, tint = PureWhite, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Thử Lên 3D", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }
        }
    }
}

@Composable
private fun AnglePill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) VermilionRed else CreamSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) VermilionRed else BorderCream),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) PureWhite else CharcoalPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun SavedLookCardItem(
    look: SavedLookEntity,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
    onSelectForCompare: () -> Unit,
    isComparingA: Boolean,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val formattedDate = remember(look.timestamp) { dateFormat.format(Date(look.timestamp)) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isComparingA) 2.dp else 1.dp,
                color = if (isComparingA) VermilionRed else BorderCream,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = PureWhite)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = look.lookName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalPrimary
                    )
                    Text(
                        text = "Lưu ngày $formattedDate",
                        fontSize = 11.sp,
                        color = CharcoalMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = VermilionRed.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = look.culturalStatus,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = VermilionRed,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Score metric pill
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Điểm tổng thể:",
                    fontSize = 11.sp,
                    color = CharcoalMuted
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${look.totalScore}/100",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = VermilionRed
                )
            }

            if (look.userNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = look.userNotes,
                    fontSize = 12.sp,
                    color = CharcoalSecondary,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BorderCream)
            Spacer(modifier = Modifier.height(6.dp))

            // Actions row: So sánh, Mở lại, Xóa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onSelectForCompare,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = null,
                        tint = if (isComparingA) VermilionRed else CharcoalSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isComparingA) "Đang chọn..." else "Chọn để so sánh",
                        fontSize = 11.sp,
                        color = if (isComparingA) VermilionRed else CharcoalSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Xóa look",
                            tint = CharcoalMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    FilledTonalButton(
                        onClick = onOpen,
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = CreamSurface),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Mở Look", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VermilionRed)
                    }
                }
            }
        }
    }
}
