package com.example.ui.screens

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.data.repository.CameraScanEngine
import com.example.ui.components.OutfitBadge
import com.example.ui.theme.*

@Composable
fun CameraScanModal(
    baseOutfit: ActiveOutfit,
    onApplyTo3dStudio: (ActiveOutfit) -> Unit,
    onDismiss: () -> Unit
) {
    // 1. Consent State (Luật 91/2025/QH15 về Bảo vệ dữ liệu cá nhân)
    var isConsentAgreed by remember { mutableStateOf(false) }

    // 2. Request permission when first opening multi-angle scan
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        permissionLauncher.launch(perm)
    }

    // 3. Scan & Gallery Selection State
    val capturedAngles = remember { mutableStateListOf<CameraScanAngle>() }
    var currentSelectingAngle by remember { mutableStateOf(CameraScanAngle.FRONT) }
    var scanAnalysisResult by remember { mutableStateOf<CapturedOutfitAnalysis?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            if (!capturedAngles.contains(currentSelectingAngle)) {
                capturedAngles.add(currentSelectingAngle)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(IvoryBase),
            color = IvoryBase
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = VermilionRed.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = VermilionRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Quét Việt Phục Đa Góc",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalPrimary
                            )
                            Text(
                                text = "Thư viện Ảnh → AI Analysis → 3D Model",
                                fontSize = 11.sp,
                                color = CharcoalSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = CharcoalPrimary
                        )
                    }
                }

                if (!isConsentAgreed) {
                    // CONSENT SCREEN (Luật Bảo vệ dữ liệu cá nhân 91/2025/QH15)
                    PrivacyConsentView(
                        onAccept = { isConsentAgreed = true },
                        onDecline = onDismiss
                    )
                } else {
                    // MAIN SCANNING WORKFLOW
                    if (scanAnalysisResult == null) {
                        MultiAngleCaptureView(
                            capturedAngles = capturedAngles,
                            currentAngle = currentSelectingAngle,
                            onSelectAngle = { currentSelectingAngle = it },
                            onSelectFromGallery = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            onTriggerAnalysis = {
                                isAnalyzing = true
                                val analysis = CameraScanEngine.analyzeScan(
                                    capturedAngles = capturedAngles.toSet(),
                                    baseOutfit = baseOutfit
                                )
                                isAnalyzing = false
                                scanAnalysisResult = analysis
                            },
                            isAnalyzing = isAnalyzing
                        )
                    } else {
                        // STRUCTURED ANALYSIS RESULT VIEW
                        ScanAnalysisResultView(
                            analysis = scanAnalysisResult!!,
                            onRetake = {
                                scanAnalysisResult = null
                            },
                            onApply = {
                                onApplyTo3dStudio(scanAnalysisResult!!.mappedOutfitConfig)
                                onDismiss()
                            },
                            onDeleteData = {
                                capturedAngles.clear()
                                scanAnalysisResult = null
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Màn hình thỏa thuận quyền riêng tư (Luật 91/2025/QH15 & Nghị định bảo vệ dữ liệu cá nhân)
 */
@Composable
private fun PrivacyConsentView(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ImperialGold.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = VermilionRed.copy(alpha = 0.12f),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "QUYỀN RIÊNG TƯ & LUẬT 91/2025/QH15",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VermilionRed,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Text(
                text = "Thỏa thuận quyền riêng tư trước khi quét",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Tính năng quét đa góc từ thư viện được thiết kế tuân thủ nghiêm ngặt Luật Bảo vệ dữ liệu cá nhân số 91/2025/QH15 và điều khoản dịch vụ Google AI Studio:",
                fontSize = 13.sp,
                color = CharcoalSecondary,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            PrivacyBullet(
                icon = Icons.Default.CheckCircle,
                title = "Chỉ phân tích cấu trúc trang phục",
                content = "AI chỉ nhận diện nếp vải, cổ áo, hàng khuy, tà áo và màu sắc. Tuyệt đối không nhận diện khuôn mặt hay danh tính cá nhân."
            )
            Spacer(modifier = Modifier.height(8.dp))

            PrivacyBullet(
                icon = Icons.Default.Lock,
                title = "Chế độ riêng tư mặc định (Private-by-default)",
                content = "Hình ảnh chỉ xử lý phục vụ phiên quét hiện tại, không lưu trữ vĩnh viễn và không công khai cho người khác."
            )
            Spacer(modifier = Modifier.height(8.dp))

            PrivacyBullet(
                icon = Icons.Default.DeleteOutline,
                title = "Quyền xóa dữ liệu bất kỳ lúc nào",
                content = "Bạn có toàn quyền hủy bỏ hoặc xóa dữ liệu quét ngay lập tức sau khi hoàn thành."
            )
            Spacer(modifier = Modifier.height(8.dp))

            PrivacyBullet(
                icon = Icons.Default.Person,
                title = "Quy định độ tuổi tester",
                content = "Theo điều khoản Gemini API, người dùng cần từ 18 tuổi trở lên khi trải nghiệm dịch vụ phân tích hình ảnh thực tế."
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Từ chối", color = CharcoalSecondary)
                }

                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1.4f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VermilionRed)
                ) {
                    Text("Tôi đồng ý", fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }
        }
    }
}

@Composable
private fun PrivacyBullet(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = JadeTeal,
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalPrimary
            )
            Text(
                text = content,
                fontSize = 11.sp,
                color = CharcoalSecondary,
                lineHeight = 15.sp
            )
        }
    }
}

/**
 * Quy trình chọn ảnh 5 góc từ thư viện: Front -> 3/4 -> Side -> Back -> Close-up
 */
@Composable
private fun MultiAngleCaptureView(
    capturedAngles: List<CameraScanAngle>,
    currentAngle: CameraScanAngle,
    onSelectAngle: (CameraScanAngle) -> Unit,
    onSelectFromGallery: () -> Unit,
    onTriggerAnalysis: () -> Unit,
    isAnalyzing: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Multi-angle Step Selector Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CreamSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Quy trình 5 góc chọn ảnh từ thư viện máy (Front → 3/4 → Side → Back → Close-up):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(CameraScanAngle.entries) { angle ->
                            val isSelected = angle == currentAngle
                            val isCaptured = capturedAngles.contains(angle)

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = when {
                                    isSelected -> VermilionRed
                                    isCaptured -> JadeTeal.copy(alpha = 0.15f)
                                    else -> PureWhite
                                },
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when {
                                        isSelected -> VermilionRed
                                        isCaptured -> JadeTeal
                                        else -> BorderCream
                                    }
                                ),
                                modifier = Modifier.clickable { onSelectAngle(angle) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isCaptured) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = if (isSelected) PureWhite else JadeTeal,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = angle.label + if (angle.isMandatory) " *" else "",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) PureWhite else CharcoalPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Gallery Selection Guide View
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2321)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ImperialGold)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top info badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x99000000)
                        ) {
                            Text(
                                text = "Góc đang chọn: ${currentAngle.label} (${currentAngle.subTitle})",
                                fontSize = 11.sp,
                                color = ImperialGold,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        // Central selection target box
                        Box(
                            modifier = Modifier
                                .size(width = 170.dp, height = 180.dp)
                                .border(
                                    width = 1.5.dp,
                                    color = if (capturedAngles.contains(currentAngle)) JadeTeal else PureWhite.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (capturedAngles.contains(currentAngle)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = JadeTeal,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Đã chọn ảnh ${currentAngle.label}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite
                                    )
                                }
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoLibrary,
                                        contentDescription = null,
                                        tint = PureWhite.copy(alpha = 0.6f),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Chọn ảnh thư viện cho ${currentAngle.label}\n(${currentAngle.guideDescription})",
                                        fontSize = 10.sp,
                                        color = PureWhite.copy(alpha = 0.85f),
                                        textAlign = TextAlign.Center,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }

                        // Bottom Gallery Picker Button
                        Button(
                            onClick = onSelectFromGallery,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (capturedAngles.contains(currentAngle)) JadeTeal else PureWhite
                            )
                        ) {
                            Icon(
                                imageVector = if (capturedAngles.contains(currentAngle)) Icons.Default.Check else Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = if (capturedAngles.contains(currentAngle)) PureWhite else CharcoalPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (capturedAngles.contains(currentAngle)) "Chọn lại ảnh ${currentAngle.label}" else "Chọn ảnh từ thư viện máy",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (capturedAngles.contains(currentAngle)) PureWhite else CharcoalPrimary
                            )
                        }
                    }
                }
            }
        }

        // Captured status and trigger button
        item {
            val mandatoryMissing = CameraScanAngle.entries.filter { it.isMandatory && !capturedAngles.contains(it) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tiến độ chọn ảnh thư viện:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalPrimary
                        )
                        Text(
                            text = "${capturedAngles.size} / 5 góc (${if (mandatoryMissing.isEmpty()) "Đủ góc chính" else "Thiếu ${mandatoryMissing.size} góc bắt buộc"})",
                            fontSize = 11.sp,
                            color = if (mandatoryMissing.isEmpty()) JadeTeal else VermilionRed,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onTriggerAnalysis,
                        enabled = capturedAngles.isNotEmpty() && !isAnalyzing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VermilionRed)
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Đang phân tích điểm neo...", color = PureWhite)
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = ImperialGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Phân Tích Cấu Trúc Bằng AI (Gemini Vision)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Màn hình kết quả phân tích có cấu trúc JSON Structured Output
 */
@Composable
private fun ScanAnalysisResultView(
    analysis: CapturedOutfitAnalysis,
    onRetake: () -> Unit,
    onApply: () -> Unit,
    onDeleteData: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Card with Result & Confidence Band
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ImperialGold)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutfitBadge(badgeType = OutfitBadgeType.AI_ANALYZED)

                        // Confidence Band Pill
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (analysis.confidenceBand) {
                                ConfidenceBand.HIGH -> JadeTeal.copy(alpha = 0.15f)
                                ConfidenceBand.MEDIUM -> ImperialGold.copy(alpha = 0.2f)
                                ConfidenceBand.LOW -> VermilionRed.copy(alpha = 0.15f)
                            }
                        ) {
                            Text(
                                text = "${analysis.confidenceBand.label} (${(analysis.confidence * 100).toInt()}%)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (analysis.confidenceBand) {
                                    ConfidenceBand.HIGH -> JadeTeal
                                    ConfidenceBand.MEDIUM -> Color(0xFFB57C1E)
                                    ConfidenceBand.LOW -> VermilionRed
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = analysis.namingResolution,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = analysis.summary,
                        fontSize = 12.sp,
                        color = CharcoalSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Structural Anchors Checklist
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CreamSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Điểm Neo Cấu Trúc (Bảo tàng Lịch sử Quốc gia):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    analysis.detectedAnchors.forEach { anchor ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = if (anchor.isFound) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (anchor.isFound) JadeTeal else VermilionRed,
                                modifier = Modifier.size(16.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = anchor.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalPrimary
                                    )
                                    if (anchor.isMandatory) {
                                        Text(
                                            text = " (Bắt buộc)",
                                            fontSize = 10.sp,
                                            color = VermilionRed,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                Text(
                                    text = anchor.note,
                                    fontSize = 11.sp,
                                    color = CharcoalSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Missing Anchors Warning (nếu có)
        if (analysis.missingAnchors.isNotEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF3CD),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEEBA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF856404),
                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Điểm neo chưa ghi nhận đầy đủ:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF856404)
                            )
                            Text(
                                text = "${analysis.missingAnchors.joinToString(", ")}. Để giữ tính chuẩn mực, trang phục được định danh là 'Lấy cảm hứng từ…' thay vì nguyên bản.",
                                fontSize = 11.sp,
                                color = Color(0xFF856404),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons: Apply to 3D Character, Retake, Delete
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onApply,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VermilionRed)
                ) {
                    Icon(imageVector = Icons.Default.ViewInAr, contentDescription = null, tint = PureWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đưa vào Studio 3D Character",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onRetake,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chọn bổ sung", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onDeleteData,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VermilionRed)
                    ) {
                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Xóa ảnh ngay", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
