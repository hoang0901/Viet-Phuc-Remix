package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CulturalMode
import com.example.ui.theme.*

@Composable
fun StyleModeSelector(
    selectedMode: CulturalMode,
    onModeSelected: (CulturalMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chế độ phối đồ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalPrimary
            )
            Text(
                text = "Mức tự do: ${selectedMode.freedomLevel}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = when (selectedMode) {
                    CulturalMode.HERITAGE -> VermilionRed
                    CulturalMode.FUSION -> JadeTeal
                    CulturalMode.REMIX_GEN_Z -> ImperialGold
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CreamSurface)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CulturalMode.entries.forEach { mode ->
                val isSelected = mode == selectedMode
                val activeColor = when (mode) {
                    CulturalMode.HERITAGE -> VermilionRed
                    CulturalMode.FUSION -> JadeTeal
                    CulturalMode.REMIX_GEN_Z -> ImperialGold
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) activeColor else CreamSurface)
                        .clickable { onModeSelected(mode) }
                        .padding(vertical = 8.dp, horizontal = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = mode.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) PureWhite else CharcoalPrimary,
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = mode.subtitle,
                            fontSize = 9.sp,
                            color = if (isSelected) PureWhite.copy(alpha = 0.85f) else CharcoalMuted,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = selectedMode.description,
            fontSize = 11.sp,
            color = CharcoalSecondary,
            lineHeight = 15.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
