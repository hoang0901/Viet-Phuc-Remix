package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ActiveOutfit
import com.example.ui.theme.*

/**
 * Bộ tạo Prompt AI 2 lớp theo đặc tả trang 1 & 3 của hồ sơ:
 * Lớp 1: Cultural Constraints (Bất biến, bảo vệ bản sắc di sản)
 * Lớp 2: Visual Rendering (Chất liệu, tỷ lệ 1:7.5, ánh sáng studio, 2.5D)
 * Negative Prompt: Chống nhầm sang Hán phục, Kimono, Hanbok hoặc fantasy.
 */
@Composable
fun AiPromptGeneratorModal(
    outfit: ActiveOutfit,
    onDismiss: () -> Unit,
    onCopied: (String) -> Unit
) {
    val context = LocalContext.current

    val culturalConstraints = """
[LAYER 1: CULTURAL CONSTRAINTS - DO NOT ALTER]
- Core Vietnamese Garment: ${outfit.mainGarment.name} (${outfit.mainGarment.category})
- Cultural Space & Era: ${outfit.mainGarment.historyLayer.culturalSpace}, ${outfit.mainGarment.historyLayer.originEra}
- Required Identifying Anatomy:
  * High standing mandarin collar (Cổ đứng dựng cao khép kín 2.5-3.5cm)
  * Curved wrap-around lapel fastening under the right armpit (Vạt đắp chéo sườn phải)
  * Exactly 5 knotted buttons (Ngũ khuy ngũ thường: Nhân, Lễ, Nghĩa, Trí, Tín)
  * Lower garment: ${outfit.bottom.name} (${if (outfit.bottom.isHistoricalPiece) "Traditional flowing silk" else "Contemporary tailored cut"})
  * Footwear: ${outfit.footwear.name}
  * Mode: ${outfit.mode.title} (Freedom level: ${outfit.mode.freedomLevel})
- Authenticity Reference: Luật Di sản văn hóa (Văn bản hợp nhất số 51/VBHN-VPQH), Bảo tàng Phụ nữ Việt Nam
    """.trimIndent()

    val visualRendering = """
[LAYER 2: VISUAL RENDERING & AESTHETICS]
- Style: Vietnamese heritage fashion illustration, semi-realistic 2.5D, modern editorial fashion lookbook
- Body Anatomy & Proportions: 1:7.2 realistic human figure, natural shoulder slope, organic hands gently holding ${outfit.accessory?.name ?: "folded silk"}
- Palette: Primary tone ${outfit.primaryColor.name} (${outfit.primaryColor.visualDesc}), secondary tone ${outfit.secondaryColor.name}
- Fabric Texture & Drapery: Natural silk luster, subtle brocade weave, visible realistic cloth folds and gentle gravitational drapery
- Lighting: Soft studio diffused daylight, high key, gentle ambient occlusion
- Environment: Solid #FFFDF5 ivory cream background with soft ground contact shadow, clean editorial composition
    """.trimIndent()

    val negativePrompt = """
[NEGATIVE PROMPT - ZERO TOLERANCE]
hanfu, kimono, yukata, hanbok, qipao, cheongsam, chinese historical drama costume, exaggerated anime eyes, 3d plastic doll, deformed hands, fantasy armor, mismatched buttons, synthetic fluorescent colors, low resolution, inaccurate collar closure.
    """.trimIndent()

    val fullPrompt = "$culturalConstraints\n\n$visualRendering\n\n$negativePrompt"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, BorderCream, RoundedCornerShape(24.dp)),
            color = IvoryBase
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = JadeTeal.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = JadeTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Prompt AI Hai Lớp (GenAI)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalPrimary
                            )
                            Text(
                                text = "Cultural Constraints + Visual Rendering",
                                fontSize = 11.sp,
                                color = JadeTeal
                            )
                        }
                    }

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

                // Philosophy Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CreamSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "“AI tạo minh họa và gợi ý; AI không tự quyết định sự thật văn hóa.”",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = VermilionRed
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Mô hình hai lớp khóa chặt phần bất biến của Việt phục, ngăn chặn AI biến dạng thành trang phục ngoại lai.",
                            fontSize = 11.sp,
                            color = CharcoalSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // LAYER 1: Cultural Constraints
                Text(
                    text = "Lớp 1: Cultural Constraints (Khóa bản sắc)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PureWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = culturalConstraints,
                        fontSize = 11.sp,
                        color = CharcoalPrimary,
                        modifier = Modifier.padding(12.dp),
                        lineHeight = 16.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // LAYER 2: Visual Rendering
                Text(
                    text = "Lớp 2: Visual Rendering (Thẩm mỹ 2.5D)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PureWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = visualRendering,
                        fontSize = 11.sp,
                        color = CharcoalPrimary,
                        modifier = Modifier.padding(12.dp),
                        lineHeight = 16.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Negative Prompt
                Text(
                    text = "Negative Prompt (Chống nhầm lẫn Đông Á & Fantasy)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = RecheckRed
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = RecheckRedBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, RecheckRed.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = negativePrompt,
                        fontSize = 11.sp,
                        color = RecheckRed,
                        modifier = Modifier.padding(12.dp),
                        lineHeight = 16.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Copy Button
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("VietPhucPrompt", fullPrompt)
                        clipboard.setPrimaryClip(clip)
                        onCopied("Đã sao chép Prompt AI hai lớp vào bộ nhớ tạm!")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VermilionRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = PureWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sao chép trọn bộ Prompt AI 2 lớp",
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
            }
        }
    }
}
