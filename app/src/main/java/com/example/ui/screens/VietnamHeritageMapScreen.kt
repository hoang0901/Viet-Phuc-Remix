package com.example.ui.screens

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

/**
 * Màn hình BẢN ĐỒ VIỆT NAM tập trung chuyên sâu cho ứng dụng Việt Phục.
 *
 * Sử dụng 100% dữ liệu gốc từ file `vietnam-34-provinces.svg`:
 * - 34 tỉnh/thành phố với đường biên vector chính xác, không tự vẽ lại.
 * - Phân loại cấu trúc: 22 tỉnh/thành hợp nhất (province-merged) và 12 tỉnh/thành chưa hợp nhất (province-unmerged).
 * - 4 thực thể đảo/quần đảo chủ quyền: Hoàng Sa, Trường Sa, Phú Quốc, Côn Đảo.
 * - Hệ thống kinh vĩ tuyến (grid-line) và nhãn hải phận Biển Đông, Vịnh Thái Lan, Vịnh Bắc Bộ.
 * - Minh bạch dữ liệu: Tuyệt đối không bịa đặt số liệu dân số, diện tích hay văn hóa nếu nguồn không cung cấp.
 */
@Composable
fun VietnamHeritageMapScreen(
    onNavigateToArticle: ((String) -> Unit)? = null,
    onNavigateToOutfit: ((String) -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSvgPresent by remember { mutableStateOf(VietnamSvgMapParser.isSvgAssetAvailable(context)) }
    var parsedMap by remember { mutableStateOf<ParsedVietnamMap?>(null) }
    var parseError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Preload master dataset from provinces_master.json
    val masterProvinces = remember { ProvincesMasterRepository.loadProvinces(context) }

    var selectedProvinceGroup by remember { mutableStateOf<ParsedProvinceGroup?>(null) }
    var hoveredProvinceGroup by remember { mutableStateOf<ParsedProvinceGroup?>(null) }

    // Selected Province master model
    val selectedMasterProvince: MasterProvinceItem? = remember(selectedProvinceGroup) {
        selectedProvinceGroup?.let { group ->
            ProvincesMasterRepository.getProvinceById(context, group.id)
        }
    }

    var searchQuery by remember { mutableStateOf("") }

    // View Options
    var showProvinceLabels by remember { mutableStateOf(true) }
    var showGridLines by remember { mutableStateOf(true) }

    // Map Transform State (Pinch-to-zoom & Pan)
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    // Reload function
    val loadMap: () -> Unit = {
        isLoading = true
        parseError = null
        val available = VietnamSvgMapParser.isSvgAssetAvailable(context)
        isSvgPresent = available

        if (available) {
            try {
                context.assets.open(VietnamSvgMapParser.ASSET_FILE_NAME).use { stream ->
                    val result = VietnamSvgMapParser.parse(stream)
                    result.onSuccess { map ->
                        parsedMap = map
                        parseError = null
                    }.onFailure { err ->
                        parseError = "Lỗi khi đọc file SVG: ${err.localizedMessage}"
                    }
                }
            } catch (e: Exception) {
                parseError = "Không thể mở file SVG: ${e.localizedMessage}"
            }
        }
        isLoading = false
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            loadMap()
        }
    }

    // Selected Province Data model
    val selectedProvinceData: VietnamProvinceData? = remember(selectedProvinceGroup) {
        selectedProvinceGroup?.let { group ->
            VietnamMapRegistry.findById(group.id) ?: VietnamMapRegistry.findById(group.name)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF081226)),
        topBar = {
            val matchingProvinces = remember(parsedMap?.provinces, searchQuery) {
                if (searchQuery.isBlank()) emptyList()
                else parsedMap?.provinces?.filter { p ->
                    p.name.contains(searchQuery, ignoreCase = true) ||
                    p.id.contains(searchQuery, ignoreCase = true)
                } ?: emptyList()
            }

            VietnamMapTopBar(
                onBack = onBack,
                onResetView = {
                    zoomScale = 1f
                    panOffset = Offset.Zero
                },
                searchQuery = searchQuery,
                onSearchQueryChange = { newQuery ->
                    searchQuery = newQuery
                    // Tự động chọn tỉnh nếu người dùng nhập trùng tên
                    val matched = parsedMap?.provinces?.firstOrNull {
                        it.name.equals(newQuery.trim(), ignoreCase = true) ||
                        it.id.equals(newQuery.trim().replace(' ', '_'), ignoreCase = true)
                    }
                    if (matched != null) {
                        selectedProvinceGroup = matched
                    }
                },
                matchingProvinces = matchingProvinces,
                onSelectProvince = { province ->
                    selectedProvinceGroup = province
                },
                showLabels = showProvinceLabels,
                onToggleLabels = { showProvinceLabels = !showProvinceLabels },
                showGrid = showGridLines,
                onToggleGrid = { showGridLines = !showGridLines },
                totalProvinces = parsedMap?.provinces?.size ?: 34
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF081226))
        ) {
            if (!isSvgPresent) {
                MissingSvgGuide(onCheckAgain = { loadMap() })
            } else if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF60A5FA))
                }
            } else if (parseError != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Lỗi nạp bản đồ SVG gốc",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = parseError ?: "Không xác định",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { loadMap() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                            ) {
                                Text("Thử lại", color = Color.White)
                            }
                        }
                    }
                }
            } else if (parsedMap != null) {
                val map = parsedMap!!

                // Filtered provinces list based on search query
                val displayedProvinces = remember(map.provinces, searchQuery) {
                    map.provinces.filter { p ->
                        searchQuery.isBlank() ||
                                p.name.contains(searchQuery, ignoreCase = true) ||
                                p.id.contains(searchQuery, ignoreCase = true)
                    }
                }

                // Main Interactive Canvas
                Box(modifier = Modifier.fillMaxSize()) {
                    InteractiveVietnamSvgCanvas(
                        parsedMap = map,
                        selectedProvinceId = selectedProvinceGroup?.id,
                        hoveredProvinceId = hoveredProvinceGroup?.id,
                        selectedStatusFilter = null,
                        highlightedProvinceIds = displayedProvinces.map { it.id }.toSet(),
                        showLabels = showProvinceLabels,
                        showGrid = showGridLines,
                        scale = zoomScale,
                        offset = panOffset,
                        onTransformChanged = { s, o ->
                            zoomScale = max(0.6f, min(s, 6.0f))
                            panOffset = o
                        },
                        onProvinceTapped = { tapped ->
                            selectedProvinceGroup = if (selectedProvinceGroup?.id == tapped?.id) null else tapped
                        },
                        onProvinceHovered = { hovered ->
                            hoveredProvinceGroup = hovered
                        }
                    )

                    // Top Left Province Badge Indicator (when hovered or selected)
                    val activeGroup = hoveredProvinceGroup ?: selectedProvinceGroup
                    val activeMasterItem = remember(activeGroup?.id) {
                        activeGroup?.let { ProvincesMasterRepository.getProvinceById(context, it.id) }
                    }
                    if (activeGroup != null) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(start = 16.dp, top = 12.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F172A).copy(alpha = 0.92f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            shadowElevation = 8.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF60A5FA),
                                    modifier = Modifier.size(10.dp)
                                ) {}
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = activeGroup.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    val reg = activeMasterItem?.regionName
                                    if (!reg.isNullOrBlank()) {
                                        Text(
                                            text = reg,
                                            fontSize = 11.sp,
                                            color = Color(0xFF93C5FD)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Map View Controls (Zoom In, Zoom Out, Reset, Fit)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 12.dp, bottom = if (selectedProvinceGroup != null) 240.dp else 75.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SmallFloatingActionButton(
                            onClick = { zoomScale = min(zoomScale * 1.3f, 6.0f) },
                            containerColor = Color(0xFF1E293B),
                            contentColor = Color.White
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Phóng to", modifier = Modifier.size(18.dp))
                        }
                        SmallFloatingActionButton(
                            onClick = { zoomScale = max(zoomScale / 1.3f, 0.6f) },
                            containerColor = Color(0xFF1E293B),
                            contentColor = Color.White
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Thu nhỏ", modifier = Modifier.size(18.dp))
                        }
                        SmallFloatingActionButton(
                            onClick = {
                                zoomScale = 1f
                                panOffset = Offset.Zero
                            },
                            containerColor = Color(0xFF1E293B),
                            contentColor = Color.White
                        ) {
                            Icon(Icons.Default.CenterFocusStrong, contentDescription = "Đặt lại góc nhìn", modifier = Modifier.size(18.dp))
                        }
                    }

                    // Bottom Province Quick-Selector Bar (when no province is selected)
                    if (selectedProvinceGroup == null) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0F172A).copy(alpha = 0.95f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "DANH SÁCH 34 TỈNH/THÀNH (${displayedProvinces.size})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = "Chạm để chọn & xem chi tiết",
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    contentPadding = PaddingValues(horizontal = 2.dp)
                                ) {
                                    items(displayedProvinces) { province ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (province.isMerged) Color(0xFF1E3A8A).copy(alpha = 0.6f) else Color(0xFF78350F).copy(alpha = 0.6f),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (province.isMerged) Color(0xFF60A5FA).copy(alpha = 0.5f) else Color(0xFFF59E0B).copy(alpha = 0.5f)
                                            ),
                                            modifier = Modifier.clickable {
                                                selectedProvinceGroup = province
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = if (province.isMerged) Color(0xFF60A5FA) else Color(0xFFF59E0B),
                                                    modifier = Modifier.size(6.dp)
                                                ) {}
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = province.name,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Selected Province Detail Sheet / Card
                    AnimatedVisibility(
                        visible = selectedProvinceGroup != null,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        val group = selectedProvinceGroup
                        val masterItem = selectedMasterProvince
                        if (masterItem != null) {
                            ProvinceHeritageDetailSheet(
                                province = masterItem,
                                onClose = { selectedProvinceGroup = null },
                                onNavigateToOutfit = onNavigateToOutfit,
                                onNavigateToArticle = onNavigateToArticle
                            )
                        } else if (group != null) {
                            ProvinceDataVerificationCard(
                                provinceGroup = group,
                                provinceData = selectedProvinceData,
                                onClose = { selectedProvinceGroup = null }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Top bar header with dedicated Province Search bar and view toggles
 * Thay khu vực các nút "Hợp nhất" và "Chưa hợp nhất" bằng thanh tìm kiếm tỉnh/thành chuyên dụng,
 * tối ưu diện tích và hiển thị kết quả gợi ý tức thì.
 */
@Composable
private fun VietnamMapTopBar(
    onBack: (() -> Unit)?,
    onResetView: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    matchingProvinces: List<ParsedProvinceGroup>,
    onSelectProvince: (ParsedProvinceGroup) -> Unit,
    showLabels: Boolean,
    onToggleLabels: () -> Unit,
    showGrid: Boolean,
    onToggleGrid: () -> Unit,
    totalProvinces: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A))
            .border(width = 0.5.dp, color = Color(0xFF1E293B))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        // Hàng duy nhất: Dòng chữ "BẢN ĐỒ VIỆT NAM" và Thanh tìm kiếm tỉnh/thành ngang hàng nhau
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            // Tiêu đề BẢN ĐỒ VIỆT NAM
            Text(
                text = "BẢN ĐỒ VIỆT NAM",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF1F5F9),
                maxLines = 1,
                softWrap = false
            )

            // Thanh tìm kiếm tỉnh/thành hiển thị rõ ràng, nằm cùng hàng ngang với BẢN ĐỒ VIỆT NAM
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (searchQuery.isNotEmpty()) Color(0xFF60A5FA) else Color(0xFF334155)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Tìm kiếm",
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Tìm tỉnh/thành (vd: Hà Nội)...",
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = SolidColor(Color(0xFF60A5FA)),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Xóa tìm kiếm",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Quick Toggle Buttons (Nhãn & Đặt lại góc nhìn)
            IconButton(
                onClick = onToggleLabels,
                modifier = Modifier.size(26.dp)
            ) {
                Icon(
                    imageVector = if (showLabels) Icons.Default.Label else Icons.Default.LabelOff,
                    contentDescription = "Nhãn địa danh",
                    tint = if (showLabels) Color(0xFF60A5FA) else Color(0xFF64748B),
                    modifier = Modifier.size(15.dp)
                )
            }
            IconButton(
                onClick = onResetView,
                modifier = Modifier.size(26.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CenterFocusStrong,
                    contentDescription = "Đặt lại góc nhìn",
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        // Gợi ý kết quả tìm kiếm tức thì
        if (searchQuery.isNotBlank() && matchingProvinces.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(matchingProvinces) { province ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E3A8A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF60A5FA)),
                        modifier = Modifier.clickable { onSelectProvince(province) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF93C5FD), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = province.name, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Canvas Renderer: renders original SVG vector shapes with hit testing, zoom, pan, and text.
 */
@Composable
private fun InteractiveVietnamSvgCanvas(
    parsedMap: ParsedVietnamMap,
    selectedProvinceId: String?,
    hoveredProvinceId: String?,
    selectedStatusFilter: ProvinceMergeStatus?,
    highlightedProvinceIds: Set<String>,
    showLabels: Boolean,
    showGrid: Boolean,
    scale: Float,
    offset: Offset,
    onTransformChanged: (Float, Offset) -> Unit,
    onProvinceTapped: (ParsedProvinceGroup?) -> Unit,
    onProvinceHovered: (ParsedProvinceGroup?) -> Unit,
    modifier: Modifier = Modifier
) {
    val vb = parsedMap.viewBox
    var canvasSize by remember { mutableStateOf(Offset.Zero) }

    // Fit SVG aspect ratio (2400 x 3600) into Canvas
    val baseScale = if (canvasSize.x > 0 && canvasSize.y > 0 && vb.width > 0 && vb.height > 0) {
        min(canvasSize.x / vb.width, canvasSize.y / vb.height) * 0.98f
    } else 1f

    val finalScale = baseScale * scale
    val baseTranslationX = (canvasSize.x - vb.width * finalScale) / 2f - vb.left * finalScale
    val baseTranslationY = (canvasSize.y - vb.height * finalScale) / 2f - vb.top * finalScale
    val totalOffsetX = baseTranslationX + offset.x
    val totalOffsetY = baseTranslationY + offset.y

    // Paint for labels
    val labelPaint = remember {
        Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.WHITE
            textSize = 26f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setShadowLayer(6f, 0f, 2f, android.graphics.Color.BLACK)
        }
    }

    val seaPaint = remember {
        Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#1e3a5f")
            textSize = 64f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.25f
        }
    }

    val islandPaint = remember {
        Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#fef08a")
            textSize = 24f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setShadowLayer(8f, 0f, 2f, android.graphics.Color.BLACK)
        }
    }

    val islandSubPaint = remember {
        Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.WHITE
            textSize = 16f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setShadowLayer(6f, 0f, 2f, android.graphics.Color.BLACK)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(parsedMap, finalScale, totalOffsetX, totalOffsetY) {
                detectTransformGestures { _, pan, zoom, _ ->
                    onTransformChanged(scale * zoom, offset + pan)
                }
            }
            .pointerInput(parsedMap, finalScale, totalOffsetX, totalOffsetY) {
                detectTapGestures(
                    onTap = { tapPos ->
                        val svgX = (tapPos.x - totalOffsetX) / finalScale
                        val svgY = (tapPos.y - totalOffsetY) / finalScale
                        val matched = parsedMap.findProvinceAt(svgX, svgY)
                        onProvinceTapped(matched)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            canvasSize = Offset(size.width, size.height)

            // 1. Draw SVG Background Ocean rect (#081226)
            drawRect(color = Color(0xFF081226))

            withTransform({
                translate(left = totalOffsetX, top = totalOffsetY)
                scale(scaleX = finalScale, scaleY = finalScale, pivot = Offset.Zero)
            }) {
                // 2. Draw Grid Lines if enabled
                if (showGrid) {
                    parsedMap.gridLines.forEach { line ->
                        drawLine(
                            color = Color(0xFF1E293B),
                            start = Offset(line.x1, line.y1),
                            end = Offset(line.x2, line.y2),
                            strokeWidth = 1f
                        )
                    }
                }

                // 3. Draw Sea Labels (Biển Đông, Vịnh Thái Lan)
                parsedMap.seaLabels.forEach { sea ->
                    drawContext.canvas.nativeCanvas.drawText(
                        sea.text,
                        sea.position.x,
                        sea.position.y,
                        seaPaint
                    )
                }

                // 4. Draw Province Fills (Màu sắc gốc theo class SVG)
                parsedMap.provinces.forEach { group ->
                    val isSelected = group.id == selectedProvinceId
                    val isHovered = group.id == hoveredProvinceId
                    val isDimmed = !highlightedProvinceIds.contains(group.id)

                    val fillColor = when {
                        isSelected -> Color(0xFFDC2626) // Crimson Red selection
                        isHovered -> if (group.isMerged) Color(0xFF2563EB) else Color(0xFFB45309)
                        isDimmed -> if (group.isMerged) Color(0xFF1E3A8A).copy(alpha = 0.25f) else Color(0xFF78350F).copy(alpha = 0.25f)
                        group.isMerged -> Color(0xFF1E3A8A) // province-merged
                        else -> Color(0xFF78350F) // province-unmerged
                    }

                    group.subPaths.forEach { subPath ->
                        drawPath(
                            path = subPath.composePath,
                            color = fillColor
                        )
                    }
                }

                // 5. Draw Province Borders (Đường ranh giới theo SVG gốc)
                parsedMap.provinces.forEach { group ->
                    val isSelected = group.id == selectedProvinceId
                    val isHovered = group.id == hoveredProvinceId
                    val isDimmed = !highlightedProvinceIds.contains(group.id)

                    val strokeColor = when {
                        isSelected -> Color.White
                        isHovered -> Color(0xFFFBBF24)
                        isDimmed -> Color(0xFF334155).copy(alpha = 0.4f)
                        group.isMerged -> Color(0xFF60A5FA)
                        else -> Color(0xFFF59E0B)
                    }

                    val strokeWidth = when {
                        isSelected -> 3.5f
                        isHovered -> 2.5f
                        else -> 1.2f
                    }

                    group.subPaths.forEach { subPath ->
                        drawPath(
                            path = subPath.composePath,
                            color = strokeColor,
                            style = Stroke(width = strokeWidth)
                        )
                    }
                }

                // 6. Draw Island / Archipelago Markers (Hoàng Sa, Trường Sa, Phú Quốc, Côn Đảo)
                parsedMap.islandMarkers.forEach { island ->
                    val pos = island.position
                    if (island.isArchipelago) {
                        // Concentric circles for archipelago
                        drawCircle(
                            color = Color(0xFFEF4444),
                            radius = 18f,
                            center = pos
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 18f,
                            center = pos,
                            style = Stroke(width = 3f)
                        )
                        drawCircle(
                            color = Color(0xFFEF4444),
                            radius = 32f,
                            center = pos,
                            style = Stroke(width = 2f)
                        )

                        // Texts
                        drawContext.canvas.nativeCanvas.drawText(
                            island.name,
                            pos.x,
                            pos.y + 48f,
                            islandPaint
                        )
                        if (island.subTitle != null) {
                            drawContext.canvas.nativeCanvas.drawText(
                                island.subTitle,
                                pos.x,
                                pos.y + 74f,
                                islandSubPaint
                            )
                        }
                    } else {
                        // Single island marker
                        drawCircle(
                            color = Color(0xFF10B981),
                            radius = 10f,
                            center = pos
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 10f,
                            center = pos,
                            style = Stroke(width = 2f)
                        )
                        drawContext.canvas.nativeCanvas.drawText(
                            island.name,
                            pos.x,
                            pos.y - 18f,
                            islandSubPaint
                        )
                    }
                }

                // 7. Draw Province Labels from SVG <text class="label-text">
                if (showLabels) {
                    parsedMap.provinces.forEach { group ->
                        val pos = group.labelPos
                        if (pos != null) {
                            drawContext.canvas.nativeCanvas.drawText(
                                group.name,
                                pos.x,
                                pos.y,
                                labelPaint
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card hiển thị chi tiết và kiểm chứng dữ liệu cho tỉnh/thành được chọn.
 * Minh bạch 100% giữa dữ liệu có sẵn trong SVG và dữ liệu chưa có (chờ bổ sung).
 */
@Composable
private fun ProvinceDataVerificationCard(
    provinceGroup: ParsedProvinceGroup,
    provinceData: VietnamProvinceData?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(14.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF334155)),
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row: Tên tỉnh + Badge phân loại + Nút đóng
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF60A5FA),
                        modifier = Modifier.size(12.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = provinceGroup.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION 1: DỮ LIỆU ĐÃ XÁC THỰC TỪ FILE SVG GỐC
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DỮ LIỆU XÁC THỰC TỪ FILE SVG GỐC",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    DataRowItem(label = "Mã định danh dữ liệu (data-province-id)", value = provinceGroup.id)
                    DataRowItem(label = "ID phần tử vector SVG (id)", value = provinceGroup.svgId)
                    DataRowItem(label = "Tên chính thức trong thẻ <title>", value = provinceGroup.name)
                    DataRowItem(
                        label = "Tọa độ Bounding Box (viewBox 2400x3600)",
                        value = "X: [${provinceGroup.overallBounds.left.toInt()} -> ${provinceGroup.overallBounds.right.toInt()}], Y: [${provinceGroup.overallBounds.top.toInt()} -> ${provinceGroup.overallBounds.bottom.toInt()}]"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SECTION 2: MINH BẠCH VỀ DỮ LIỆU CHƯA CÓ TRONG NGUỒN GỐC
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "THÔNG TIN NGOÀI PHẠM VI SVG GỐC (CHỜ BỔ SUNG)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFBBF24)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Theo nguyên tắc bảo tồn dữ liệu địa lý chính xác, ứng dụng không tự suy đoán hoặc thêm số liệu nếu file gốc không cung cấp:",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    PendingDataItem(label = "Dân số địa phương", note = provinceData?.populationNote ?: "Chưa có trong file SVG gốc")
                    PendingDataItem(label = "Diện tích tự nhiên", note = provinceData?.areaNote ?: "Chưa có trong file SVG gốc")
                    PendingDataItem(label = "Chi tiết y phục & hoa văn di sản", note = provinceData?.historicalCultureNote ?: "Cần liên kết cơ sở dữ liệu di sản sau")
                    PendingDataItem(label = "Địa giới hành chính cấp xã/phường", note = provinceData?.administrativeBoundaryNote ?: "Chưa có trong file SVG gốc")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Button: Deselect
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Deselect, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Bỏ chọn vùng này", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun DataRowItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = Color(0xFF94A3B8))
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFF1F5F9),
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun PendingDataItem(label: String, note: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "• $label", fontSize = 11.sp, color = Color(0xFFCBD5E1))
        Text(
            text = note,
            fontSize = 10.sp,
            color = Color(0xFFFCD34D),
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Hướng dẫn khi file SVG chưa được đặt đúng vị trí
 */
@Composable
private fun MissingSvgGuide(
    onCheckAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1E3A8A).copy(alpha = 0.3f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Không tìm thấy file vietnam-34-provinces.svg",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Vui lòng đặt file bản đồ gốc vào đúng đường dẫn sau:",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Text(
                        text = "app/src/main/assets/vietnam-34-provinces.svg",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF60A5FA),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onCheckAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Kiểm tra lại file SVG", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
