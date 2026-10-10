package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveOutfit
import com.example.data.model.AvatarAngle
import com.example.data.model.AvatarGender
import com.example.data.model.CulturalMode
import com.example.data.model.OutfitBadgeType
import com.example.ui.theme.*
import kotlin.math.roundToInt

/**
 * Model phối đồ 3D Perspective Visualizer phản ánh cấu trúc chuẩn xác của Việt Phục:
 * - Trục xoay 3D (3D Orbit) với hiệu ứng chiều sâu phối cảnh (camera distance & rotationY)
 * - Tương tác chạm vuốt xoay tự do (360° Drag Gesture) hoặc bấm nút xoay góc
 * - Phản ánh đúng cấu trúc giải phẫu trang phục di sản:
 *   * Áo Ngũ Thân: 5 thân, cổ đứng ôm khít lót trắng, vạt cong chéo sang nách phải, ngũ khuy vàng
 *   * Áo Tứ Thân: Yếm đào, 4 thân, dải thắt lưng xanh buông xẻ, sống lưng may nối
 *   * Áo Dài: Cổ cao thanh thoát, xẻ tà hông cao đến cạp quần lụa, tà rủ mềm mại
 * - Đổ bóng khối 3D (Ambient Occlusion & Specular Silk Sheen) dưới ánh sáng studio đa hướng
 */
@Composable
fun AvatarCanvas(
    outfit: ActiveOutfit,
    onRotateAngle: (() -> Unit)? = null,
    onToggleGender: (() -> Unit)? = null,
    onMetricsChanged: ((heightCm: Int, weightKg: Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density
    var dragRotation by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var showAnchorsOverlay by remember { mutableStateOf(false) }
    var showMetricsOverlay by remember { mutableStateOf(false) }
    var studioLightingMode by remember { mutableIntStateOf(0) }

    // Target base rotation from outfit angle
    val baseAngleDegrees = when (outfit.viewAngle) {
        AvatarAngle.FRONT -> 0f
        AvatarAngle.THREE_QUARTER -> 35f
        AvatarAngle.BACK -> 180f
    }

    val effectiveRotation = if (isDragging) dragRotation else baseAngleDegrees
    val animatedRotation by animateFloatAsState(
        targetValue = effectiveRotation,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f),
        label = "3dRotationY"
    )
    val animatedZoom by animateFloatAsState(
        targetValue = zoomScale,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f),
        label = "zoomScale"
    )

    val animatedPrimaryColor by animateColorAsState(
        targetValue = outfit.primaryColor.color,
        animationSpec = tween(400),
        label = "primaryColor"
    )
    val animatedSecondaryColor by animateColorAsState(
        targetValue = outfit.secondaryColor.color,
        animationSpec = tween(400),
        label = "secondaryColor"
    )

    val boxModifier = Modifier
        .fillMaxWidth()
        .then(modifier)
        .clip(RoundedCornerShape(22.dp))
        .background(Color(0xFFFFFDF5))
        .border(1.5.dp, ImperialGold.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
        .pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragStart = {
                    isDragging = true
                    dragRotation = animatedRotation
                },
                onDragEnd = {
                    val norm = (dragRotation % 360f + 360f) % 360f
                    dragRotation = when {
                        norm < 20f || norm > 340f -> 0f
                        norm in 20f..65f -> 35f
                        norm in 65f..120f -> 90f
                        norm in 120f..240f -> 180f
                        else -> 35f
                    }
                    isDragging = false
                },
                onHorizontalDrag = { change, dragAmount ->
                    change.consume()
                    dragRotation = (dragRotation + dragAmount * 0.75f)
                }
            )
        }

    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center
    ) {
        // 3D Perspective Container with dynamic zoom and camera rotation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = animatedRotation
                    scaleX = animatedZoom
                    scaleY = animatedZoom
                    cameraDistance = 18f * density
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp, bottom = 8.dp)
            ) {
                val canvasW = size.width
                val canvasH = size.height
                val centerX = canvasW / 2f
                val isMale = outfit.gender == AvatarGender.MALE

                // Normalized angle for layer visibility
                val normRot = ((animatedRotation % 360f) + 360f) % 360f
                val isBackView = normRot in 120f..240f
                val isThreeQuarter = normRot in 20f..120f || normRot in 240f..340f

                // 1. STUDIO LIGHTING & RADIANT AMBIENT GLOW
                val bgColors = when (studioLightingMode) {
                    1 -> listOf(Color(0xFFFFFBEA), Color(0xFFF5E6CA)) // Hoàng Kim Cung Đình
                    2 -> listOf(Color(0xFFFAF7F0), Color(0xFFE8E0D2)) // Dạ Yến Hội An
                    else -> listOf(Color(0xFFFFFDF5), Color(0xFFF6F0E4)) // Studio Nhật Nguyệt (Tự nhiên)
                }
                drawRect(
                    brush = Brush.radialGradient(
                        colors = bgColors,
                        center = Offset(centerX, canvasH * 0.42f),
                        radius = canvasW * 0.65f
                    )
                )

                // 3D Cultural Spotlight Arch behind figure
                val haloColors = when (studioLightingMode) {
                    1 -> listOf(Color(0x45BA2D2C), Color(0x30E5B552), Color(0x00000000))
                    2 -> listOf(Color(0x45D97706), Color(0x221E2A44), Color(0x00000000))
                    else -> listOf(Color(0x38E5B552), Color(0x18BA2D2C), Color(0x00000000))
                }
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = haloColors,
                        center = Offset(centerX, canvasH * 0.38f),
                        radius = canvasW * 0.48f
                    ),
                    radius = canvasW * 0.48f,
                    center = Offset(centerX, canvasH * 0.38f)
                )

                // Heritage geometric halo pattern
                drawArc(
                    color = Color(0x28C98A3C),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(centerX - canvasW * 0.36f, canvasH * 0.08f),
                    size = Size(canvasW * 0.72f, canvasH * 0.62f),
                    style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
                )

                // 2. SOFT 3D PEDESTAL DROP SHADOW (Tùy biến theo chiều cao & cân nặng)
                val bmiScale = (outfit.bmi / 20.5f).coerceIn(0.82f, 1.28f)
                val heightScale = (outfit.heightCm / 165f).coerceIn(0.88f, 1.15f)
                val feetY = (canvasH * (0.83f + (heightScale - 1f) * 0.06f)).coerceIn(canvasH * 0.80f, canvasH * 0.86f)

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x38000000), Color(0x00000000)),
                        center = Offset(centerX, feetY + 4.dp.toPx()),
                        radius = canvasW * 0.34f * bmiScale
                    ),
                    topLeft = Offset(centerX - (canvasW * 0.30f * bmiScale), feetY - 4.dp.toPx()),
                    size = Size(canvasW * 0.60f * bmiScale, 18.dp.toPx())
                )

                // 3. HUMAN ANATOMY (Tỷ lệ cân đối, không bị che khuất đầu hoặc chân)
                val headRadius = if (isMale) 18.5.dp.toPx() else 17.5.dp.toPx()
                val headCenterY = canvasH * (0.18f - (heightScale - 1f) * 0.02f)
                val skinTone = Color(0xFFF9DCBF)
                val skinShadow = Color(0xFFE5BBA1)
                val skinDetail = Color(0xFFC48661)

                // 4. LOWER GARMENT (3D Cylindrical silk flow tỷ lệ theo số đo)
                val bottomTopY = canvasH * (0.48f + (heightScale - 1f) * 0.04f)
                drawLowerGarment3D(
                    centerX = centerX,
                    topY = bottomTopY,
                    bottomY = feetY,
                    bottomId = outfit.bottom.id,
                    color = animatedSecondaryColor,
                    isBackView = isBackView,
                    isThreeQuarter = isThreeQuarter,
                    isMale = isMale,
                    bodyScale = bmiScale
                )

                // 5. INNER GARMENT (Yếm đào / Áo lót trắng)
                val garmentTopY = canvasH * (0.24f - (heightScale - 1f) * 0.03f)
                if (outfit.innerGarment != null && !isBackView) {
                    if (outfit.innerGarment.id == "yem_dao") {
                        drawYemDao3D(centerX, garmentTopY)
                    } else if (outfit.innerGarment.id == "ao_lot_trang") {
                        val neckW = (if (isMale) 22.dp.toPx() else 18.dp.toPx()) * bmiScale
                        drawRect(Color(0xFFFFFDF5), Offset(centerX - neckW / 2, garmentTopY - 12.dp.toPx()), Size(neckW, 14.dp.toPx()))
                    }
                }

                // 6. HEAD & EDITORIAL MANNEQUIN PROFILE
                drawEditorialHead3D(
                    centerX = centerX,
                    centerY = headCenterY,
                    radius = headRadius,
                    skinTone = skinTone,
                    skinShadow = skinShadow,
                    skinDetail = skinDetail,
                    isBackView = isBackView,
                    isThreeQuarter = isThreeQuarter,
                    isMale = isMale
                )

                // 7. HAIR & HEADWEAR (Khăn vấn chữ Nhân, Nón quai thao, Beret)
                drawHeadwearAndHair3D(
                    centerX = centerX,
                    headCenterY = headCenterY,
                    headRadius = headRadius,
                    headwear = outfit.headwear?.id,
                    isBackView = isBackView,
                    isThreeQuarter = isThreeQuarter,
                    isMale = isMale
                )

                // 8. AUTHENTIC 3D MAIN GARMENT
                val garmentHemY = (if (outfit.mainGarment.id == "ao_dai_cach_tan") canvasH * 0.70f else canvasH * 0.80f) + (heightScale - 1f) * (canvasH * 0.12f)

                when (outfit.mainGarment.category) {
                    "ao_ngu_than" -> {
                        drawAoNguThan3D(
                            centerX = centerX,
                            topY = garmentTopY,
                            hemY = garmentHemY,
                            primaryColor = animatedPrimaryColor,
                            isTayThung = outfit.mainGarment.id == "ao_ngu_than_tay_thung",
                            isBackView = isBackView,
                            isThreeQuarter = isThreeQuarter,
                            isMale = isMale,
                            bodyScale = bmiScale,
                            heightScale = heightScale
                        )
                    }
                    "ao_dai" -> {
                        drawAoDai3D(
                            centerX = centerX,
                            topY = garmentTopY,
                            hemY = garmentHemY,
                            primaryColor = animatedPrimaryColor,
                            isBackView = isBackView,
                            isThreeQuarter = isThreeQuarter,
                            isMale = isMale,
                            bodyScale = bmiScale
                        )
                    }
                    "ao_tu_than" -> {
                        drawAoTuThan3D(
                            centerX = centerX,
                            topY = garmentTopY,
                            hemY = garmentHemY,
                            primaryColor = animatedPrimaryColor,
                            isBackView = isBackView,
                            bodyScale = bmiScale
                        )
                    }
                }

                // 9. HANDS EMERGING NATURALLY FROM SLEEVE CUFFS (Drawn on top!)
                if (!isBackView) {
                    drawHands3D(
                        centerX = centerX,
                        topY = garmentTopY,
                        skinTone = skinTone,
                        skinShadow = skinShadow,
                        isTayThung = outfit.mainGarment.id == "ao_ngu_than_tay_thung",
                        isThreeQuarter = isThreeQuarter,
                        isMale = isMale,
                        bodyScale = bmiScale,
                        heightScale = heightScale
                    )
                }

                // 10. OUTER CLOAK (Áo Cử Nhân)
                if (outfit.innerGarment != null && outfit.innerGarment.id == "ao_cu_nhan") {
                    drawGraduationGown3D(centerX, garmentTopY, garmentHemY + 10.dp.toPx(), isBackView, isMale)
                }

                // 11. FOOTWEAR (Guốc mộc, Hài thêu sen, Loafer, Sneaker)
                drawFootwear3D(centerX, feetY, outfit.footwear.id, isBackView)

                // 12. ACCESSORIES
                if (outfit.accessory != null && !isBackView) {
                    drawAccessory3D(centerX, canvasH, outfit.accessory.id, garmentTopY)
                }
            }
        }

        // Top Control Bar: Cuộn ngang mượt mà, phân nhóm rõ ràng, không bao giờ chồng lấn trên màn hình hẹp
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mode & 3D Angle Indicator Pill
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PureWhite.copy(alpha = 0.94f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(
                                when (outfit.mode) {
                                    CulturalMode.HERITAGE -> VermilionRed
                                    CulturalMode.FUSION -> JadeTeal
                                    CulturalMode.REMIX_GEN_Z -> ImperialGold
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${outfit.mode.title} · 3D ${effectiveRotation.toInt()}°",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalPrimary
                    )
                }
            }

            // Body Metrics Pill (Chiều cao & Cân nặng - Bấm để mở Thước Tấc)
            Surface(
                modifier = Modifier
                    .clickable { showMetricsOverlay = !showMetricsOverlay },
                shape = RoundedCornerShape(8.dp),
                color = PureWhite.copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (showMetricsOverlay) VermilionRed else BorderCream),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📏 ${outfit.heightCm}cm · ${outfit.weightKg}kg ▾",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = VermilionRed
                    )
                }
            }

            // Badge 3D Outfit & Xoay 360 Hint
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OutfitBadge(badgeType = OutfitBadgeType.THREE_D_OUTFIT)

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PureWhite.copy(alpha = 0.90f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = VermilionRed,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "360°",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalPrimary
                        )
                    }
                }
            }
        }

        // Quick 3D Body Metrics & Thước Tấc Floating Card
        if (showMetricsOverlay) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.94f)
                    .padding(8.dp),
                shape = RoundedCornerShape(18.dp),
                color = PureWhite.copy(alpha = 0.98f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ImperialGold),
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessibilityNew, contentDescription = null, tint = VermilionRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Vóc Dáng & Thước Tấc 3D",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalPrimary
                            )
                        }
                        IconButton(
                            onClick = { showMetricsOverlay = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng", tint = CharcoalPrimary, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Height & Weight Quick Steppers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Chiều cao stepper
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = CreamSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Chiều cao", fontSize = 10.sp, color = CharcoalSecondary)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            val newH = (outfit.heightCm - 2).coerceAtLeast(145)
                                            onMetricsChanged?.invoke(newH, outfit.weightKg)
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) { Text("−", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VermilionRed) }
                                    Text("${outfit.heightCm} cm", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VermilionRed)
                                    IconButton(
                                        onClick = {
                                            val newH = (outfit.heightCm + 2).coerceAtMost(195)
                                            onMetricsChanged?.invoke(newH, outfit.weightKg)
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) { Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VermilionRed) }
                                }
                            }
                        }

                        // Cân nặng stepper
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = CreamSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Cân nặng", fontSize = 10.sp, color = CharcoalSecondary)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            val newW = (outfit.weightKg - 1).coerceAtLeast(38)
                                            onMetricsChanged?.invoke(outfit.heightCm, newW)
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) { Text("−", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VermilionRed) }
                                    Text("${outfit.weightKg} kg", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VermilionRed)
                                    IconButton(
                                        onClick = {
                                            val newW = (outfit.weightKg + 1).coerceAtMost(95)
                                            onMetricsChanged?.invoke(outfit.heightCm, newW)
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) { Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VermilionRed) }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset Chips Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val quickPresets = listOf(
                            Triple("Nữ chuẩn", 162, 50),
                            Triple("Nữ nhỏ nhắn", 155, 45),
                            Triple("Nam đĩnh đạc", 172, 64),
                            Triple("Nam cao ráo", 178, 72),
                            Triple("Đầy đặn", 165, 66)
                        )
                        items(quickPresets) { (lbl, h, w) ->
                            val isSel = outfit.heightCm == h && outfit.weightKg == w
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) VermilionRed else PureWhite,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) VermilionRed else BorderCream),
                                modifier = Modifier.clickable { onMetricsChanged?.invoke(h, w) }
                            ) {
                                Text(
                                    text = lbl,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) PureWhite else CharcoalPrimary,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Thước Tấc Cổ Truyền Banner
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ImperialGold.copy(alpha = 0.10f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "📐 ${outfit.bodyTypeDesc} • BMI ${(outfit.bmi * 10f).roundToInt() / 10f}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VermilionRed
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = outfit.traditionalMeasurements,
                                fontSize = 10.sp,
                                color = CharcoalPrimary
                            )
                        }
                    }
                }
            }
        }

        // Structural Anchors Floating Overlay Card (Khi người dùng bật soi điểm neo)
        if (showAnchorsOverlay) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.92f)
                    .padding(12.dp),
                shape = RoundedCornerShape(16.dp),
                color = PureWhite.copy(alpha = 0.97f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ImperialGold),
                shadowElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Điểm Neo Cấu Trúc (Bảo tàng Lịch sử)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = VermilionRed
                        )
                        IconButton(
                            onClick = { showAnchorsOverlay = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng", tint = CharcoalPrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("✓ Cổ đứng tròn ôm khít 2.5 - 3.5cm có viền lót trắng", fontSize = 11.sp, color = CharcoalPrimary)
                    Text("✓ Ngũ khuy: Đúng 5 cúc cài hoàng tộc (Ngũ thường)", fontSize = 11.sp, color = CharcoalPrimary)
                    Text("✓ Vạt đắp cong sang nách phải phủ lên vạt con", fontSize = 11.sp, color = CharcoalPrimary)
                    Text("✓ Sống áo may nối thẳng tắp ở giữa lưng", fontSize = 11.sp, color = CharcoalPrimary)
                    Text("✓ Tà áo buông rủ cánh cung trên quần lụa rộng", fontSize = 11.sp, color = CharcoalPrimary)
                }
            }
        }

        // Bottom Controls: Phân nhóm điều khiển và cuộn ngang linh hoạt trên điện thoại
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Gender Toggle
            if (onToggleGender != null) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream),
                    color = PureWhite.copy(alpha = 0.95f),
                    modifier = Modifier
                        .height(32.dp)
                        .clickable { onToggleGender() },
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (outfit.gender == AvatarGender.FEMALE) Icons.Default.Female else Icons.Default.Male,
                            contentDescription = "Đổi giới tính model",
                            tint = if (outfit.gender == AvatarGender.FEMALE) LotusPink else RoyalNavy,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = outfit.gender.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalPrimary
                        )
                    }
                }
            }

            // Center: Preset Views (0° Front, 35° 3/4, 90° Side, 180° Back)
            Surface(
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream),
                color = PureWhite.copy(alpha = 0.95f),
                modifier = Modifier.height(32.dp),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val presets = listOf(
                        Pair("0°", 0f),
                        Pair("3/4", 35f),
                        Pair("90°", 90f),
                        Pair("180°", 180f)
                    )
                    presets.forEach { (lbl, deg) ->
                        val isCurr = (effectiveRotation % 360f).toInt() == deg.toInt()
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCurr) VermilionRed else Color.Transparent,
                            modifier = Modifier.clickable {
                                dragRotation = deg
                            }
                        ) {
                            Text(
                                text = lbl,
                                fontSize = 10.sp,
                                fontWeight = if (isCurr) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCurr) PureWhite else CharcoalPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Right: Zoom (+ / -) & Anchors Toggle
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Zoom In / Out
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream),
                    color = PureWhite.copy(alpha = 0.95f),
                    modifier = Modifier.height(32.dp),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { zoomScale = (zoomScale - 0.2f).coerceAtLeast(0.85f) },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Text("−", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CharcoalPrimary)
                        }
                        Text(
                            text = "${(zoomScale * 100).toInt()}%",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalSecondary
                        )
                        IconButton(
                            onClick = { zoomScale = (zoomScale + 0.2f).coerceAtMost(1.6f) },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Text("+", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CharcoalPrimary)
                        }
                    }
                }

                // Anchors Checklist Button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (showAnchorsOverlay) VermilionRed else BorderCream),
                    color = if (showAnchorsOverlay) VermilionRed else PureWhite.copy(alpha = 0.95f),
                    modifier = Modifier
                        .height(32.dp)
                        .clickable { showAnchorsOverlay = !showAnchorsOverlay },
                    shadowElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FactCheck,
                            contentDescription = "Điểm neo",
                            tint = if (showAnchorsOverlay) PureWhite else ImperialGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Studio Lighting Mode Button (Tự nhiên / Hoàng Kim / Dạ Yến)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream),
                    color = PureWhite.copy(alpha = 0.95f),
                    modifier = Modifier
                        .height(32.dp)
                        .clickable { studioLightingMode = (studioLightingMode + 1) % 3 },
                    shadowElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Ánh sáng Studio",
                            tint = when (studioLightingMode) {
                                1 -> ImperialGold
                                2 -> Color(0xFFD97706)
                                else -> JadeTeal
                            },
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// 3D DRAWING IMPLEMENTATION: Phản ánh cấu trúc thực thụ của Việt phục
// -------------------------------------------------------------------------------------

private fun DrawScope.drawEditorialHead3D(
    centerX: Float,
    centerY: Float,
    radius: Float,
    skinTone: Color,
    skinShadow: Color,
    skinDetail: Color,
    isBackView: Boolean,
    isThreeQuarter: Boolean,
    isMale: Boolean
) {
    val neckW = if (isMale) 18.dp.toPx() else 14.dp.toPx()
    val neckH = 22.dp.toPx()

    // 3D Cylindrical neck gradient
    drawRect(
        brush = Brush.horizontalGradient(
            listOf(skinShadow, skinTone, skinShadow.copy(alpha = 0.9f))
        ),
        topLeft = Offset(centerX - neckW / 2, centerY + 8.dp.toPx()),
        size = Size(neckW, neckH)
    )

    // Volumetric 3D head oval
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(skinTone, skinShadow),
            center = Offset(centerX - 3.dp.toPx(), centerY - 2.dp.toPx()),
            radius = radius * 1.1f
        ),
        radius = radius,
        center = Offset(centerX, centerY)
    )

    if (!isBackView) {
        val shiftX = if (isThreeQuarter) 4.dp.toPx() else 0f
        // Delicate brows
        drawLine(skinDetail, Offset(centerX - 8.dp.toPx() + shiftX, centerY - 2.dp.toPx()), Offset(centerX - 2.dp.toPx() + shiftX, centerY - 3.dp.toPx()), 1.2.dp.toPx())
        drawLine(skinDetail, Offset(centerX + 2.dp.toPx() + shiftX, centerY - 3.dp.toPx()), Offset(centerX + 8.dp.toPx() + shiftX, centerY - 2.dp.toPx()), 1.2.dp.toPx())

        // Serene eyes
        drawLine(Color(0xFF2C221E), Offset(centerX - 7.dp.toPx() + shiftX, centerY + 2.dp.toPx()), Offset(centerX - 3.dp.toPx() + shiftX, centerY + 2.dp.toPx()), 1.2.dp.toPx())
        drawLine(Color(0xFF2C221E), Offset(centerX + 3.dp.toPx() + shiftX, centerY + 2.dp.toPx()), Offset(centerX + 7.dp.toPx() + shiftX, centerY + 2.dp.toPx()), 1.2.dp.toPx())

        // Lips with 3D highlight
        drawOval(LotusPink, Offset(centerX - 3.dp.toPx() + shiftX, centerY + 9.dp.toPx()), Size(6.dp.toPx(), 2.5.dp.toPx()))

        // Ears with depth
        drawCircle(skinShadow, 3.5.dp.toPx(), Offset(centerX - radius + 1.dp.toPx(), centerY + 2.dp.toPx()))
        drawCircle(skinShadow, 3.5.dp.toPx(), Offset(centerX + radius - 1.dp.toPx(), centerY + 2.dp.toPx()))
    }
}

private fun DrawScope.drawHeadwearAndHair3D(
    centerX: Float,
    headCenterY: Float,
    headRadius: Float,
    headwear: String?,
    isBackView: Boolean,
    isThreeQuarter: Boolean,
    isMale: Boolean
) {
    val hairColor = Color(0xFF1B1917)

    // Base volumetric hair
    drawArc(
        brush = Brush.verticalGradient(listOf(Color(0xFF332C27), hairColor)),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(centerX - headRadius * 1.05f, headCenterY - headRadius * 1.15f),
        size = Size(headRadius * 2.1f, headRadius * 1.9f)
    )

    when (headwear) {
        "khan_van" -> {
            // Multi-layered wrapped turban with 3D bevel folds
            drawArc(
                brush = Brush.verticalGradient(listOf(Color(0xFF455047), Color(0xFF1D221F))),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(centerX - headRadius * 1.25f, headCenterY - headRadius * 1.45f),
                size = Size(headRadius * 2.5f, headRadius * 1.8f)
            )

            if (!isBackView) {
                val shiftX = if (isThreeQuarter) 5.dp.toPx() else 0f
                val vFold = Path().apply {
                    moveTo(centerX - 9.dp.toPx() + shiftX, headCenterY - headRadius * 0.45f)
                    lineTo(centerX + shiftX, headCenterY - headRadius * 0.1f)
                    lineTo(centerX + 9.dp.toPx() + shiftX, headCenterY - headRadius * 0.45f)
                }
                drawPath(vFold, Color(0xFF55655A), style = Stroke(width = 2.5.dp.toPx()))
            } else {
                drawLine(Color(0xFF3B463E), Offset(centerX - headRadius, headCenterY - headRadius * 0.5f), Offset(centerX + headRadius, headCenterY - headRadius * 0.5f), 2.dp.toPx())
                drawLine(Color(0xFF3B463E), Offset(centerX - headRadius * 0.9f, headCenterY - headRadius * 0.8f), Offset(centerX + headRadius * 0.9f, headCenterY - headRadius * 0.8f), 2.dp.toPx())
            }
        }
        "non_quai_thao" -> {
            if (isBackView) {
                drawCircle(Color(0xFFD4A373), headRadius * 2.2f, Offset(centerX, headCenterY + headRadius * 1.5f))
                drawCircle(Color(0xFFBC8A5F), headRadius * 2.2f, Offset(centerX, headCenterY + headRadius * 1.5f), style = Stroke(2.dp.toPx()))
            } else {
                val hatW = 90.dp.toPx()
                val hatH = 16.dp.toPx()
                drawOval(Color(0xFFD4A373), Offset(centerX - hatW / 2, headCenterY - headRadius * 1.05f), Size(hatW, hatH))
                drawOval(Color(0xFFBC8A5F), Offset(centerX - hatW / 2, headCenterY - headRadius * 1.05f), Size(hatW, hatH), style = Stroke(2.dp.toPx()))
                // Dải quai thao lụa đào buông rủ 3D
                drawLine(LotusPink, Offset(centerX - hatW * 0.35f, headCenterY - headRadius * 0.5f), Offset(centerX - 14.dp.toPx(), headCenterY + headRadius * 2.0f), 3.dp.toPx())
                drawLine(LotusPink, Offset(centerX + hatW * 0.35f, headCenterY - headRadius * 0.5f), Offset(centerX + 14.dp.toPx(), headCenterY + headRadius * 2.0f), 3.dp.toPx())
            }
        }
        "khan_mo_qua" -> {
            val moQua = Path().apply {
                moveTo(centerX - headRadius * 1.15f, headCenterY - headRadius * 0.2f)
                lineTo(centerX, headCenterY - headRadius * 1.3f)
                lineTo(centerX + headRadius * 1.15f, headCenterY - headRadius * 0.2f)
                lineTo(centerX, headCenterY + 1.dp.toPx())
                close()
            }
            drawPath(moQua, hairColor)
        }
        "beret_gen_z" -> {
            val beret = Path().apply {
                moveTo(centerX - headRadius * 1.25f, headCenterY - headRadius * 0.5f)
                cubicTo(
                    centerX - headRadius * 0.8f, headCenterY - headRadius * 1.55f,
                    centerX + headRadius * 0.8f, headCenterY - headRadius * 1.45f,
                    centerX + headRadius * 1.35f, headCenterY - headRadius * 0.35f
                )
                lineTo(centerX + headRadius * 0.85f, headCenterY - headRadius * 0.15f)
                lineTo(centerX - headRadius * 1.05f, headCenterY - headRadius * 0.35f)
                close()
            }
            drawPath(beret, Color(0xFF2C3E50))
        }
        else -> {
            if (isMale) {
                drawArc(hairColor, 180f, 180f, true, Offset(centerX - headRadius * 1.05f, headCenterY - headRadius * 1.2f), Size(headRadius * 2.1f, headRadius * 1.8f))
            } else {
                if (isBackView) {
                    drawCircle(hairColor, headRadius * 0.65f, Offset(centerX, headCenterY + headRadius * 0.65f))
                    drawLine(ImperialGold, Offset(centerX - 16.dp.toPx(), headCenterY + headRadius * 0.65f), Offset(centerX + 16.dp.toPx(), headCenterY + headRadius * 0.65f), 2.5.dp.toPx())
                    drawCircle(JadeTeal, 2.5.dp.toPx(), Offset(centerX + 16.dp.toPx(), headCenterY + headRadius * 0.65f))
                } else {
                    drawOval(hairColor, Offset(centerX - headRadius * 0.95f, headCenterY - headRadius * 0.2f), Size(headRadius * 1.9f, headRadius * 0.8f))
                }
            }
        }
    }
}

private fun DrawScope.drawLowerGarment3D(
    centerX: Float,
    topY: Float,
    bottomY: Float,
    bottomId: String,
    color: Color,
    isBackView: Boolean,
    isThreeQuarter: Boolean,
    isMale: Boolean,
    bodyScale: Float = 1.0f
) {
    val legW = (if (isMale) 26.dp.toPx() else 23.dp.toPx()) * bodyScale
    val angleShift = if (isThreeQuarter) 7.dp.toPx() else 0f

    when (bottomId) {
        "quan_lua_rong", "quan_au" -> {
            // Left leg 3D cylinder
            val leftLeg = Path().apply {
                moveTo(centerX - 4.dp.toPx() + angleShift, topY)
                lineTo(centerX - (30.dp.toPx() * bodyScale) + angleShift, bottomY)
                lineTo(centerX - (30.dp.toPx() * bodyScale) + legW + angleShift, bottomY)
                lineTo(centerX - 2.dp.toPx() + angleShift, topY + 24.dp.toPx())
                close()
            }
            drawPath(
                leftLeg,
                brush = Brush.horizontalGradient(
                    listOf(color.copy(alpha = 0.82f), color, color.copy(alpha = 0.95f))
                )
            )
            // 3D silk crease & shadow fold
            drawLine(color.copy(alpha = 0.65f), Offset(centerX - (19.dp.toPx() * bodyScale) + angleShift, topY + 20.dp.toPx()), Offset(centerX - (19.dp.toPx() * bodyScale) + angleShift, bottomY), 1.5.dp.toPx())

            // Right leg 3D cylinder
            val rightLeg = Path().apply {
                moveTo(centerX + 4.dp.toPx() + angleShift, topY)
                lineTo(centerX + (30.dp.toPx() * bodyScale) + angleShift, bottomY)
                lineTo(centerX + (30.dp.toPx() * bodyScale) - legW + angleShift, bottomY)
                lineTo(centerX + 2.dp.toPx() + angleShift, topY + 24.dp.toPx())
                close()
            }
            drawPath(
                rightLeg,
                brush = Brush.horizontalGradient(
                    listOf(color.copy(alpha = 0.95f), color, color.copy(alpha = 0.82f))
                )
            )
            drawLine(color.copy(alpha = 0.65f), Offset(centerX + (19.dp.toPx() * bodyScale) + angleShift, topY + 20.dp.toPx()), Offset(centerX + (19.dp.toPx() * bodyScale) + angleShift, bottomY), 1.5.dp.toPx())
        }
        "vay_xep_ly" -> {
            val skirt = Path().apply {
                moveTo(centerX - (26.dp.toPx() * bodyScale), topY)
                lineTo(centerX + (26.dp.toPx() * bodyScale), topY)
                lineTo(centerX + (42.dp.toPx() * bodyScale), bottomY)
                lineTo(centerX - (42.dp.toPx() * bodyScale), bottomY)
                close()
            }
            drawPath(
                skirt,
                brush = Brush.verticalGradient(
                    listOf(color.copy(alpha = 0.9f), color, color.copy(alpha = 0.75f))
                )
            )
            for (i in -4..4) {
                val startX = centerX + (i * 6.dp.toPx() * bodyScale)
                val endX = centerX + (i * 10.dp.toPx() * bodyScale)
                drawLine(Color(0x28000000), Offset(startX, topY + 6.dp.toPx()), Offset(endX, bottomY), 1.2.dp.toPx())
            }
        }
        else -> {
            drawRect(Color(0xFF334E68), Offset(centerX - (24.dp.toPx() * bodyScale), topY), Size(48.dp.toPx() * bodyScale, bottomY - topY))
        }
    }
}

/**
 * Cấu trúc chuẩn xác của ÁO NGŨ THÂN trong không gian 3D:
 * 1. Năm thân ghép lại: Thân ngoài đắp cong qua ngực sang nách phải.
 * 2. Cổ đứng cao 2.5 - 3.5cm, ôm tròn khít cổ, có lót cổ trắng bên trong.
 * 3. Ngũ khuy: Đúng 5 chiếc cúc cài biểu trưng cho Ngũ thường.
 * 4. Mặt sau lưng: Đường may nối sống áo thẳng tắp ở giữa lưng.
 */
private fun DrawScope.drawAoNguThan3D(
    centerX: Float,
    topY: Float,
    hemY: Float,
    primaryColor: Color,
    isTayThung: Boolean,
    isBackView: Boolean,
    isThreeQuarter: Boolean,
    isMale: Boolean,
    bodyScale: Float = 1.0f,
    heightScale: Float = 1.0f
) {
    val bodyW = (if (isMale) 40.dp.toPx() else 35.dp.toPx()) * bodyScale
    val hemW = (if (isMale) 54.dp.toPx() else 50.dp.toPx()) * bodyScale

    // 3D silk multi-stop gradient highlighting volume and luster
    val silkShader = Brush.horizontalGradient(
        listOf(
            primaryColor.copy(alpha = 0.85f),
            primaryColor,
            primaryColor.copy(alpha = 1.0f),
            primaryColor.copy(alpha = 0.90f)
        )
    )

    if (isBackView) {
        // CẤU TRÚC MẶT SAU: 2 thân sau ghép sống áo ở giữa
        val backPanel = Path().apply {
            moveTo(centerX - bodyW / 2, topY)
            lineTo(centerX + bodyW / 2, topY)
            lineTo(centerX + hemW / 2, hemY)
            lineTo(centerX - hemW / 2, hemY)
            close()
        }
        drawPath(backPanel, silkShader)

        // Đường sống áo sau lưng (Di sản nhận diện cốt lõi)
        drawLine(Color(0x55000000), Offset(centerX, topY), Offset(centerX, hemY), 2.2.dp.toPx())
        drawLine(Color(0x30FFFFFF), Offset(centerX + 1.dp.toPx(), topY), Offset(centerX + 1.dp.toPx(), hemY), 0.8.dp.toPx())

        // 3D Drapery side shadows
        drawLine(Color(0x22000000), Offset(centerX - 12.dp.toPx() * bodyScale, topY + 20.dp.toPx()), Offset(centerX - 16.dp.toPx() * bodyScale, hemY), 1.2.dp.toPx())
        drawLine(Color(0x22000000), Offset(centerX + 12.dp.toPx() * bodyScale, topY + 20.dp.toPx()), Offset(centerX + 16.dp.toPx() * bodyScale, hemY), 1.2.dp.toPx())
    } else {
        val angleShift = if (isThreeQuarter) 6.dp.toPx() else 0f

        // CẤU TRÚC MẶT TRƯỚC: Thân trước bên trái + Thân con lót bên trong
        val frontPanel = Path().apply {
            moveTo(centerX - bodyW / 2, topY)
            lineTo(centerX + bodyW / 2, topY)
            cubicTo(
                centerX + bodyW / 2 + 4.dp.toPx(), topY + 28.dp.toPx() * heightScale,
                centerX + hemW / 2 - 2.dp.toPx(), hemY - 20.dp.toPx() * heightScale,
                centerX + hemW / 2, hemY
            )
            lineTo(centerX - hemW / 2, hemY)
            cubicTo(
                centerX - hemW / 2 + 2.dp.toPx(), hemY - 20.dp.toPx() * heightScale,
                centerX - bodyW / 2 - 4.dp.toPx(), topY + 28.dp.toPx() * heightScale,
                centerX - bodyW / 2, topY
            )
            close()
        }
        drawPath(frontPanel, silkShader)

        // Sleeves (Tay Chẽn hoặc Tay Thụng Áo Tấc)
        val sleeveColor = primaryColor.copy(alpha = 0.94f)
        if (isTayThung) {
            val sleeveSpread = 34.dp.toPx() * bodyScale
            val sleeveDrop = 70.dp.toPx() * heightScale
            val leftSleeve = Path().apply {
                moveTo(centerX - bodyW / 2, topY + 3.dp.toPx())
                lineTo(centerX - bodyW / 2 - sleeveSpread, topY + 40.dp.toPx() * heightScale)
                lineTo(centerX - bodyW / 2 - (sleeveSpread * 0.7f), topY + sleeveDrop)
                lineTo(centerX - bodyW / 2, topY + 50.dp.toPx() * heightScale)
                close()
            }
            drawPath(leftSleeve, sleeveColor)

            val rightSleeve = Path().apply {
                moveTo(centerX + bodyW / 2, topY + 3.dp.toPx())
                lineTo(centerX + bodyW / 2 + sleeveSpread, topY + 40.dp.toPx() * heightScale)
                lineTo(centerX + bodyW / 2 + (sleeveSpread * 0.7f), topY + sleeveDrop)
                lineTo(centerX + bodyW / 2, topY + 50.dp.toPx() * heightScale)
                close()
            }
            drawPath(rightSleeve, sleeveColor)
        } else {
            val sleeveSpread = 16.dp.toPx() * bodyScale
            val leftSleeve = Path().apply {
                moveTo(centerX - bodyW / 2, topY + 3.dp.toPx())
                lineTo(centerX - bodyW / 2 - sleeveSpread, topY + 44.dp.toPx() * heightScale)
                lineTo(centerX - bodyW / 2 - (sleeveSpread * 0.4f), topY + 46.dp.toPx() * heightScale)
                lineTo(centerX - bodyW / 2 + 2.dp.toPx(), topY + 32.dp.toPx() * heightScale)
                close()
            }
            drawPath(leftSleeve, sleeveColor)

            val rightSleeve = Path().apply {
                moveTo(centerX + bodyW / 2, topY + 3.dp.toPx())
                lineTo(centerX + bodyW / 2 + sleeveSpread, topY + 44.dp.toPx() * heightScale)
                lineTo(centerX + bodyW / 2 + (sleeveSpread * 0.4f), topY + 46.dp.toPx() * heightScale)
                lineTo(centerX + bodyW / 2 - 2.dp.toPx(), topY + 32.dp.toPx() * heightScale)
                close()
            }
            drawPath(rightSleeve, sleeveColor)
        }

        // CỔ ĐỨNG (Mandarin Collar) tròn ôm sát cổ với dải lót trắng bên trong
        val collarW = if (isMale) 20.dp.toPx() else 18.dp.toPx()
        val collarH = 9.dp.toPx()
        drawRect(Color(0xFFFFFDF5), Offset(centerX - collarW / 2 + angleShift, topY - collarH), Size(collarW, collarH))
        drawRect(primaryColor, Offset(centerX - collarW / 2 + angleShift, topY - collarH + 2.dp.toPx()), Size(collarW, collarH - 2.dp.toPx()))

        // ĐƯỜNG NẸP VẠT CONG đắp chéo sang nách phải (Đặc trưng Áo Ngũ Thân)
        val lapelPath = Path().apply {
            moveTo(centerX + angleShift, topY)
            cubicTo(
                centerX + 10.dp.toPx() + angleShift, topY + 7.dp.toPx(),
                centerX + 16.dp.toPx() + angleShift, topY + 16.dp.toPx(),
                centerX + bodyW / 2 + angleShift, topY + 26.dp.toPx()
            )
        }
        drawPath(lapelPath, Color(0xFFFFFDF5).copy(alpha = 0.65f), style = Stroke(width = 2.2.dp.toPx()))

        // 5 CÚC CÀI (Ngũ khuy) bằng đồng/vàng hoàng gia nổi khối 3D
        val goldBtn = Color(0xFFE5B552)
        val goldHighlight = Color(0xFFFFF3CC)
        val buttonPos = listOf(
            Offset(centerX + angleShift, topY - 1.dp.toPx()),
            Offset(centerX + 5.dp.toPx() + angleShift, topY + 6.dp.toPx()),
            Offset(centerX + 10.dp.toPx() + angleShift, topY + 14.dp.toPx()),
            Offset(centerX + 15.dp.toPx() + angleShift, topY + 22.dp.toPx()),
            Offset(centerX + 17.dp.toPx() + angleShift, topY + 32.dp.toPx())
        )
        buttonPos.forEach { pos ->
            drawCircle(Color(0xFF78561C), 2.8.dp.toPx(), pos)
            drawCircle(goldBtn, 2.3.dp.toPx(), pos)
            drawCircle(goldHighlight, 1.dp.toPx(), Offset(pos.x - 0.7.dp.toPx(), pos.y - 0.7.dp.toPx()))
        }
    }
}

private fun DrawScope.drawAoDai3D(
    centerX: Float,
    topY: Float,
    hemY: Float,
    primaryColor: Color,
    isBackView: Boolean,
    isThreeQuarter: Boolean,
    isMale: Boolean,
    bodyScale: Float = 1.0f
) {
    val bodyW = (if (isMale) 36.dp.toPx() else 32.dp.toPx()) * bodyScale
    val hemW = (if (isMale) 50.dp.toPx() else 46.dp.toPx()) * bodyScale

    val panel = Path().apply {
        moveTo(centerX - bodyW / 2, topY)
        lineTo(centerX + bodyW / 2, topY)
        lineTo(centerX + hemW / 2, hemY)
        lineTo(centerX - hemW / 2, hemY)
        close()
    }
    drawPath(
        panel,
        brush = Brush.verticalGradient(
            listOf(primaryColor, primaryColor.copy(alpha = 0.95f), primaryColor.copy(alpha = 0.85f))
        )
    )

    // Xẻ tà hông cao đến cạp quần lụa
    drawLine(Color(0x35000000), Offset(centerX, topY + 32.dp.toPx()), Offset(centerX, hemY), 1.2.dp.toPx())
    // Cổ đứng thanh thoát
    drawRect(primaryColor, Offset(centerX - 9.dp.toPx(), topY - 8.dp.toPx()), Size(18.dp.toPx(), 8.dp.toPx()))

    // Sleeves
    val leftSleeve = Path().apply {
        moveTo(centerX - bodyW / 2, topY + 3.dp.toPx())
        lineTo(centerX - bodyW / 2 - (14.dp.toPx() * bodyScale), topY + 44.dp.toPx())
        lineTo(centerX - bodyW / 2 - (4.dp.toPx() * bodyScale), topY + 46.dp.toPx())
        close()
    }
    drawPath(leftSleeve, primaryColor.copy(alpha = 0.9f))

    val rightSleeve = Path().apply {
        moveTo(centerX + bodyW / 2, topY + 3.dp.toPx())
        lineTo(centerX + bodyW / 2 + (14.dp.toPx() * bodyScale), topY + 44.dp.toPx())
        lineTo(centerX + bodyW / 2 + (4.dp.toPx() * bodyScale), topY + 46.dp.toPx())
        close()
    }
    drawPath(rightSleeve, primaryColor.copy(alpha = 0.9f))
}

private fun DrawScope.drawAoTuThan3D(
    centerX: Float,
    topY: Float,
    hemY: Float,
    primaryColor: Color,
    isBackView: Boolean,
    bodyScale: Float = 1.0f
) {
    val bodyW = 36.dp.toPx() * bodyScale
    val hemW = 48.dp.toPx() * bodyScale

    if (isBackView) {
        val backRobe = Path().apply {
            moveTo(centerX - bodyW / 2, topY)
            lineTo(centerX + bodyW / 2, topY)
            lineTo(centerX + hemW / 2, hemY)
            lineTo(centerX - hemW / 2, hemY)
            close()
        }
        drawPath(backRobe, primaryColor)
        // Đường sống may sau lưng
        drawLine(Color(0x45000000), Offset(centerX, topY), Offset(centerX, hemY), 2.dp.toPx())
    } else {
        val robe = Path().apply {
            moveTo(centerX - bodyW / 2, topY)
            lineTo(centerX + bodyW / 2, topY)
            lineTo(centerX + hemW / 2, hemY)
            lineTo(centerX - hemW / 2, hemY)
            close()
        }
        drawPath(robe, primaryColor)

        // Open front để lộ yếm đào
        val openV = Path().apply {
            moveTo(centerX - 10.dp.toPx(), topY)
            lineTo(centerX, topY + 32.dp.toPx())
            lineTo(centerX + 10.dp.toPx(), topY)
            close()
        }
        drawPath(openV, LotusPink)

        // Silk belt dải thắt lưng xẻ tà
        val beltY = topY + 34.dp.toPx()
        drawRect(JadeTeal, Offset(centerX - (18.dp.toPx() * bodyScale), beltY), Size(36.dp.toPx() * bodyScale, 6.dp.toPx()))
        drawLine(JadeTeal, Offset(centerX - 4.dp.toPx(), beltY + 6.dp.toPx()), Offset(centerX - 7.dp.toPx(), beltY + 34.dp.toPx()), 3.5.dp.toPx())
        drawLine(LotusPink, Offset(centerX + 4.dp.toPx(), beltY + 6.dp.toPx()), Offset(centerX + 6.dp.toPx(), beltY + 40.dp.toPx()), 3.dp.toPx())
    }
}

private fun DrawScope.drawHands3D(
    centerX: Float,
    topY: Float,
    skinTone: Color,
    skinShadow: Color,
    isTayThung: Boolean,
    isThreeQuarter: Boolean,
    isMale: Boolean,
    bodyScale: Float = 1.0f,
    heightScale: Float = 1.0f
) {
    val wristOffset = (if (isTayThung) 70.dp.toPx() else 46.dp.toPx()) * heightScale
    val handRadius = (if (isMale) 6.dp.toPx() else 5.5.dp.toPx()) * (bodyScale * 0.4f + 0.6f)

    val leftHandPos = Offset(centerX - (24.dp.toPx() * bodyScale), topY + wristOffset)
    drawCircle(skinShadow, handRadius + 1.dp.toPx(), leftHandPos)
    drawCircle(skinTone, handRadius, leftHandPos)

    val rightHandPos = Offset(centerX + (24.dp.toPx() * bodyScale), topY + wristOffset)
    drawCircle(skinShadow, handRadius + 1.dp.toPx(), rightHandPos)
    drawCircle(skinTone, handRadius, rightHandPos)
}

private fun DrawScope.drawGraduationGown3D(centerX: Float, topY: Float, hemY: Float, isBackView: Boolean, isMale: Boolean) {
    val gownColor = Color(0xEE1E2A44)
    val goldStole = Color(0xFFC98A3C)
    val gownW = if (isMale) 26.dp.toPx() else 23.dp.toPx()

    if (isBackView) {
        val backGown = Path().apply {
            moveTo(centerX - gownW, topY)
            lineTo(centerX + gownW, topY)
            lineTo(centerX + 44.dp.toPx(), hemY)
            lineTo(centerX - 44.dp.toPx(), hemY)
            close()
        }
        drawPath(backGown, gownColor)
    } else {
        val leftCloak = Path().apply {
            moveTo(centerX - gownW, topY)
            lineTo(centerX - 42.dp.toPx(), hemY)
            lineTo(centerX - 16.dp.toPx(), hemY)
            lineTo(centerX - 12.dp.toPx(), topY + 28.dp.toPx())
            close()
        }
        drawPath(leftCloak, gownColor)

        val rightCloak = Path().apply {
            moveTo(centerX + gownW, topY)
            lineTo(centerX + 42.dp.toPx(), hemY)
            lineTo(centerX + 16.dp.toPx(), hemY)
            lineTo(centerX + 11.dp.toPx(), topY + 28.dp.toPx())
            close()
        }
        drawPath(rightCloak, gownColor)

        drawRect(goldStole, Offset(centerX - 18.dp.toPx(), topY + 3.dp.toPx()), Size(5.dp.toPx(), 42.dp.toPx()))
        drawRect(goldStole, Offset(centerX + 13.dp.toPx(), topY + 3.dp.toPx()), Size(5.dp.toPx(), 42.dp.toPx()))
    }
}

private fun DrawScope.drawYemDao3D(centerX: Float, topY: Float) {
    val yem = Path().apply {
        moveTo(centerX - 9.dp.toPx(), topY + 5.dp.toPx())
        lineTo(centerX, topY + 20.dp.toPx())
        lineTo(centerX + 9.dp.toPx(), topY + 5.dp.toPx())
        close()
    }
    drawPath(yem, LotusPink)
}

private fun DrawScope.drawFootwear3D(centerX: Float, feetY: Float, footwearId: String, isBackView: Boolean) {
    when (footwearId) {
        "sneaker" -> {
            drawRoundRect(PureWhite, Offset(centerX - 30.dp.toPx(), feetY), Size(18.dp.toPx(), 9.dp.toPx()), CornerRadius(3.dp.toPx()))
            drawRoundRect(PureWhite, Offset(centerX + 12.dp.toPx(), feetY), Size(18.dp.toPx(), 9.dp.toPx()), CornerRadius(3.dp.toPx()))
            drawRoundRect(Color(0xFFCCC5B9), Offset(centerX - 30.dp.toPx(), feetY + 6.dp.toPx()), Size(18.dp.toPx(), 3.dp.toPx()), CornerRadius(1.5.dp.toPx()))
            drawRoundRect(Color(0xFFCCC5B9), Offset(centerX + 12.dp.toPx(), feetY + 6.dp.toPx()), Size(18.dp.toPx(), 3.dp.toPx()), CornerRadius(1.5.dp.toPx()))
        }
        "loafer" -> {
            val loaferColor = Color(0xFF382A21)
            drawRoundRect(loaferColor, Offset(centerX - 28.dp.toPx(), feetY + 1.dp.toPx()), Size(16.dp.toPx(), 7.dp.toPx()), CornerRadius(3.dp.toPx()))
            drawRoundRect(loaferColor, Offset(centerX + 12.dp.toPx(), feetY + 1.dp.toPx()), Size(16.dp.toPx(), 7.dp.toPx()), CornerRadius(3.dp.toPx()))
        }
        "guoc_moc" -> {
            val woodColor = Color(0xFF9C6644)
            drawRect(woodColor, Offset(centerX - 26.dp.toPx(), feetY + 2.dp.toPx()), Size(14.dp.toPx(), 6.dp.toPx()))
            drawRect(VermilionRed, Offset(centerX - 23.dp.toPx(), feetY), Size(8.dp.toPx(), 3.dp.toPx()))
            drawRect(woodColor, Offset(centerX + 12.dp.toPx(), feetY + 2.dp.toPx()), Size(14.dp.toPx(), 6.dp.toPx()))
            drawRect(VermilionRed, Offset(centerX + 15.dp.toPx(), feetY), Size(8.dp.toPx(), 3.dp.toPx()))
        }
        "hai_theu" -> {
            drawRoundRect(VermilionRed, Offset(centerX - 26.dp.toPx(), feetY), Size(16.dp.toPx(), 7.dp.toPx()), CornerRadius(3.dp.toPx()))
            drawRoundRect(VermilionRed, Offset(centerX + 10.dp.toPx(), feetY), Size(16.dp.toPx(), 7.dp.toPx()), CornerRadius(3.dp.toPx()))
            drawCircle(ImperialGold, 2.dp.toPx(), Offset(centerX - 26.dp.toPx(), feetY + 1.dp.toPx()))
            drawCircle(ImperialGold, 2.dp.toPx(), Offset(centerX + 26.dp.toPx(), feetY + 1.dp.toPx()))
        }
    }
}

private fun DrawScope.drawAccessory3D(centerX: Float, canvasH: Float, accId: String, garmentTopY: Float) {
    when (accId) {
        "the_bai" -> {
            drawRect(Color(0xFF4A3525), Offset(centerX + 6.dp.toPx(), garmentTopY + 30.dp.toPx()), Size(7.dp.toPx(), 16.dp.toPx()))
            drawLine(VermilionRed, Offset(centerX + 9.5.dp.toPx(), garmentTopY + 46.dp.toPx()), Offset(centerX + 9.5.dp.toPx(), garmentTopY + 54.dp.toPx()), 2.dp.toPx())
        }
        "quat_lua" -> {
            val fx = centerX - 24.dp.toPx()
            val fy = canvasH * 0.52f
            val fan = Path().apply {
                moveTo(fx, fy + 14.dp.toPx())
                lineTo(fx - 14.dp.toPx(), fy - 8.dp.toPx())
                arcTo(Rect(fx - 14.dp.toPx(), fy - 16.dp.toPx(), fx + 14.dp.toPx(), fy), 180f, 180f, false)
                close()
            }
            drawPath(fan, Color(0xFFE8D5B5))
            drawPath(fan, ImperialGold, style = Stroke(1.dp.toPx()))
        }
        "tui_mini_deo_cheo" -> {
            drawLine(Color(0xFF2C3E50), Offset(centerX + 24.dp.toPx(), garmentTopY - 10.dp.toPx()), Offset(centerX - 2.dp.toPx(), canvasH * 0.52f), 1.5.dp.toPx())
            drawRoundRect(Color(0xFF2C3E50), Offset(centerX - 36.dp.toPx(), canvasH * 0.50f), Size(18.dp.toPx(), 15.dp.toPx()), CornerRadius(3.dp.toPx()))
        }
        "chuoi_ngoc_trai" -> {
            drawArc(Color(0xFFFAF0E6), 0f, 180f, false, Offset(centerX - 10.dp.toPx(), garmentTopY + 8.dp.toPx()), Size(20.dp.toPx(), 16.dp.toPx()), style = Stroke(2.2.dp.toPx()))
        }
    }
}
