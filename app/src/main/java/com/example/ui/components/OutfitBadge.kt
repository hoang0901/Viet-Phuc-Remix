package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OutfitBadgeType
import com.example.ui.theme.*

private data class BadgeVisual(
    val bgColor: Color,
    val textColor: Color,
    val borderColor: Color,
    val iconVector: ImageVector
)

@Composable
fun OutfitBadge(
    badgeType: OutfitBadgeType,
    modifier: Modifier = Modifier
) {
    val visual = when (badgeType) {
        OutfitBadgeType.THREE_D_OUTFIT -> BadgeVisual(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            Color(0xFFA5D6A7),
            Icons.Default.ViewInAr
        )
        OutfitBadgeType.REAL_PHOTO -> BadgeVisual(
            Color(0xFFE3F2FD),
            Color(0xFF1565C0),
            Color(0xFF90CAF9),
            Icons.Default.CameraAlt
        )
        OutfitBadgeType.AI_ANALYZED -> BadgeVisual(
            Color(0xFFFFF3E0),
            Color(0xFFE65100),
            Color(0xFFFFCC80),
            Icons.Default.DocumentScanner
        )
        OutfitBadgeType.AI_ILLUSTRATION_UNVERIFIED -> BadgeVisual(
            Color(0xFFFBE9E7),
            Color(0xFFD84315),
            Color(0xFFFFAB91),
            Icons.Default.WarningAmber
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = visual.bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, visual.borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = visual.iconVector,
                contentDescription = null,
                tint = visual.textColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = badgeType.label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = visual.textColor
            )
        }
    }
}
