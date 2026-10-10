package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import com.example.data.model.HeritageArticle
import com.example.data.model.KnowledgeTaxonomy
import com.example.ui.CultureHubMode
import com.example.ui.theme.*

@Composable
fun CultureHubScreen(
    articles: List<HeritageArticle>,
    initialArticleId: String? = null,
    currentMode: CultureHubMode = CultureHubMode.MAP,
    onModeChanged: (CultureHubMode) -> Unit = {},
    onNavigateToOutfit: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedTaxonomyFilter by remember { mutableStateOf<KnowledgeTaxonomy?>(null) }
    var expandedArticleId by remember(initialArticleId) { mutableStateOf(initialArticleId) }

    val filteredArticles = remember(selectedTaxonomyFilter, articles) {
        if (selectedTaxonomyFilter == null) articles
        else articles.filter { it.taxonomy == selectedTaxonomyFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IvoryBase)
    ) {
        // Top Tab Switcher: [Bản Đồ 34 Tỉnh] vs [Thư Viện Bài Viết]
        TabRow(
            selectedTabIndex = if (currentMode == CultureHubMode.MAP) 0 else 1,
            containerColor = PureWhite,
            contentColor = VermilionRed
        ) {
            Tab(
                selected = currentMode == CultureHubMode.MAP,
                onClick = { onModeChanged(CultureHubMode.MAP) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Bản Đồ 34 Tỉnh",
                            fontWeight = if (currentMode == CultureHubMode.MAP) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                },
                selectedContentColor = VermilionRed,
                unselectedContentColor = CharcoalSecondary
            )
            Tab(
                selected = currentMode == CultureHubMode.ARTICLES,
                onClick = { onModeChanged(CultureHubMode.ARTICLES) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Thư Viện Bài Viết",
                            fontWeight = if (currentMode == CultureHubMode.ARTICLES) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                },
                selectedContentColor = VermilionRed,
                unselectedContentColor = CharcoalSecondary
            )
        }

        when (currentMode) {
            CultureHubMode.MAP -> {
                VietnamHeritageMapScreen(
                    onNavigateToArticle = { artId ->
                        expandedArticleId = artId
                        onModeChanged(CultureHubMode.ARTICLES)
                    },
                    onNavigateToOutfit = onNavigateToOutfit
                )
            }

            CultureHubMode.ARTICLES -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
                ) {
                    // Header
                    item {
                        Column {
                            Text(
                                text = "Thư Viện Di Sản & Văn Hóa",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalPrimary
                            )
                            Text(
                                text = "Dữ liệu văn hóa chuẩn hóa, minh bạch nguồn gốc và độ tin cậy",
                                fontSize = 12.sp,
                                color = CharcoalSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

        // Philosophy Card: "Không che giấu điểm chưa chắc chắn"
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderCream, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CreamSurface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = JadeTeal.copy(alpha = 0.15f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = JadeTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Tiêu chuẩn dữ liệu di sản (UNESCO & Bộ VHTTDL)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalPrimary
                        )
                        Text(
                            text = "Hệ thống phân định rạch ròi giữa 'Hiện vật đã ghi nhận', 'Quan điểm học thuật' và 'Diễn giải biểu tượng'.",
                            fontSize = 11.sp,
                            color = CharcoalSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Filter chips row
        item {
            Text(
                text = "Bộ lọc cấp độ thông tin:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedTaxonomyFilter == null,
                        onClick = { selectedTaxonomyFilter = null },
                        label = { Text("Tất cả (${articles.size})", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VermilionRed,
                            selectedLabelColor = PureWhite
                        )
                    )
                }

                items(KnowledgeTaxonomy.entries) { taxonomy ->
                    val isSelected = selectedTaxonomyFilter == taxonomy
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedTaxonomyFilter = if (isSelected) null else taxonomy
                        },
                        label = { Text(taxonomy.label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(taxonomy.badgeColorHex),
                            selectedLabelColor = PureWhite
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Article cards list
        items(filteredArticles) { article ->
            val isExpanded = expandedArticleId == article.id

            HeritageArticleCard(
                article = article,
                isExpanded = isExpanded,
                onToggleExpand = {
                    expandedArticleId = if (isExpanded) null else article.id
                },
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}
    }
}
}

@Composable
private fun HeritageArticleCard(
    article: HeritageArticle,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderCream, RoundedCornerShape(16.dp))
            .clickable(onClick = onToggleExpand),
        colors = CardDefaults.cardColors(containerColor = PureWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Taxonomy tag & confidence badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(article.taxonomy.badgeColorHex).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = article.taxonomy.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(article.taxonomy.badgeColorHex),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "Độ tin cậy: ${article.confidenceLevel}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = CharcoalMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CharcoalPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 60-second summary
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CreamSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Tóm tắt 60 giây:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = VermilionRed
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = article.summary60s,
                        fontSize = 12.sp,
                        color = CharcoalPrimary,
                        lineHeight = 16.sp
                    )
                }
            }

            // Expanded Full Content
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = BorderCream)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = article.fullContent,
                        fontSize = 13.sp,
                        color = CharcoalPrimary,
                        lineHeight = 19.sp
                    )

                    if (article.debateNotes != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardHighlight),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Ghi chú học thuật & Tranh luận:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ImperialGold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = article.debateNotes,
                                    fontSize = 11.sp,
                                    color = CharcoalSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Citation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = JadeTeal,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Nguồn: ${article.sourceName} (${article.publicationYear})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = JadeTeal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Expand / Collapse affordance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Thu gọn" else "Xem chi tiết & nguồn dẫn",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VermilionRed
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = VermilionRed,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
