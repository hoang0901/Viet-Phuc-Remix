package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.SavedLookEntity
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun VietPhucApp(viewModel: VietPhucViewModel) {
    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val selectedOccasion by viewModel.selectedOccasion.collectAsStateWithLifecycle()
    val currentOutfit by viewModel.currentOutfit.collectAsStateWithLifecycle()
    val savedLooks by viewModel.savedLooks.collectAsStateWithLifecycle()
    val showLookCardModal by viewModel.showLookCardModal.collectAsStateWithLifecycle()
    val showPosterDialog by viewModel.showPosterDialog.collectAsStateWithLifecycle()
    val showAiPromptModal by viewModel.showAiPromptModal.collectAsStateWithLifecycle()
    val showCameraScanModal by viewModel.showCameraScanModal.collectAsStateWithLifecycle()
    val isComparing by viewModel.isComparing.collectAsStateWithLifecycle()
    val compareLookA by viewModel.compareLookA.collectAsStateWithLifecycle()
    val compareLookB by viewModel.compareLookB.collectAsStateWithLifecycle()
    val targetArticleId by viewModel.targetArticleId.collectAsStateWithLifecycle()
    val cultureHubMode by viewModel.cultureHubMode.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Back handling for comparison screen and sub-destinations
    BackHandler(enabled = isComparing || currentDestination != AppDestination.EXPLORE) {
        if (isComparing) {
            viewModel.closeComparison()
        } else {
            viewModel.navigateTo(AppDestination.EXPLORE)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!isComparing) {
                NavigationBar(
                    containerColor = PureWhite,
                    contentColor = CharcoalPrimary,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    AppDestination.entries.forEach { destination ->
                        val isSelected = destination == currentDestination
                        val iconVector: ImageVector = when (destination) {
                            AppDestination.EXPLORE -> Icons.Default.Explore
                            AppDestination.BUILDER -> Icons.Default.Checkroom
                            AppDestination.LOOKBOOK -> Icons.Default.CollectionsBookmark
                            AppDestination.CULTURE -> Icons.Default.MenuBook
                            AppDestination.PROFILE -> Icons.Default.Person
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(destination) },
                            icon = {
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = destination.label
                                )
                            },
                            label = {
                                Text(
                                    text = destination.label,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PureWhite,
                                selectedTextColor = VermilionRed,
                                indicatorColor = VermilionRed,
                                unselectedIconColor = CharcoalMuted,
                                unselectedTextColor = CharcoalMuted
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(IvoryBase)
        ) {
            if (isComparing && compareLookA != null && compareLookB != null) {
                ComparisonScreen(
                    lookA = compareLookA!!,
                    lookB = compareLookB!!,
                    onBack = { viewModel.closeComparison() }
                )
            } else {
                AnimatedContent(
                    targetState = currentDestination,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "DestinationTransition"
                ) { destination ->
                    when (destination) {
                        AppDestination.EXPLORE -> {
                            ExploreScreen(
                                selectedOccasion = selectedOccasion,
                                onOccasionSelected = { viewModel.setOccasion(it) },
                                onStartMixMatch = { viewModel.navigateTo(AppDestination.BUILDER) },
                                onSelectPresetOutfit = { viewModel.selectPresetOutfit(it) },
                                onReadArticle = { viewModel.navigateToArticle(it) },
                                onOpenScanCamera = { viewModel.openCameraScanModal() },
                                onOpenHeritageMap = { viewModel.openHeritageMap() },
                                presetLooks = viewModel.presetLooks
                            )
                        }

                        AppDestination.BUILDER -> {
                            OutfitBuilderScreen(
                                currentOutfit = currentOutfit,
                                onOutfitUpdated = { viewModel.updateCurrentOutfit(it) },
                                allGarments = viewModel.allGarments,
                                allInnerGarments = viewModel.allInnerGarments,
                                allBottoms = viewModel.allBottoms,
                                allFootwear = viewModel.allFootwear,
                                allHeadwear = viewModel.allHeadwear,
                                allAccessories = viewModel.allAccessories,
                                colorPalettes = viewModel.colorPalettes,
                                onViewLookCard = { viewModel.openLookCardModal() },
                                onSaveLook = { viewModel.saveCurrentLook() },
                                onOpenCompare = { viewModel.compareCurrentWithPreset() },
                                onRotateAngle = { viewModel.rotateAvatarAngle() },
                                onOpenAiPrompt = { viewModel.openAiPromptModal() },
                                onToggleGender = { viewModel.toggleAvatarGender() },
                                onOpenScanCamera = { viewModel.openCameraScanModal() }
                            )
                        }

                        AppDestination.LOOKBOOK -> {
                            LookbookScreen(
                                savedLooks = savedLooks,
                                onSelectLookToWear = { viewModel.loadSavedLookIntoBuilder(it) },
                                onDeleteLook = { viewModel.deleteSavedLook(it) },
                                onOpenPosterDialog = { viewModel.openPosterDialog() },
                                onCompareSavedLooks = { a, b -> viewModel.compareTwoSavedLooks(a, b) },
                                onNavigateToBuilder = { viewModel.navigateTo(AppDestination.BUILDER) }
                            )
                        }

                        AppDestination.CULTURE -> {
                            CultureHubScreen(
                                articles = viewModel.heritageArticles,
                                initialArticleId = targetArticleId,
                                currentMode = cultureHubMode,
                                onModeChanged = { viewModel.setCultureHubMode(it) },
                                onNavigateToOutfit = { viewModel.selectGarmentAndNavigateToBuilder(it) }
                            )
                        }

                        AppDestination.PROFILE -> {
                            ProfileScreen(
                                savedLooksCount = savedLooks.size,
                                articlesCount = viewModel.heritageArticles.size
                            )
                        }
                    }
                }
            }

            // Look Card Modal Dialog
            if (showLookCardModal) {
                LookCardModal(
                    outfit = currentOutfit,
                    onDismiss = { viewModel.closeLookCardModal() },
                    onSaveLook = {
                        viewModel.saveCurrentLook()
                        viewModel.closeLookCardModal()
                    },
                    onCompare = {
                        viewModel.closeLookCardModal()
                        viewModel.compareCurrentWithPreset()
                    },
                    onReadStory = {
                        viewModel.closeLookCardModal()
                        viewModel.navigateToArticle("ao_ngu_than_1744")
                    },
                    onShare = {
                        viewModel.closeLookCardModal()
                        viewModel.openPosterDialog()
                    }
                )
            }

            // Lookbook Poster Generator Dialog
            if (showPosterDialog) {
                LookbookPosterDialog(
                    savedLooks = savedLooks,
                    onDismiss = { viewModel.closePosterDialog() },
                    onExportPoster = {
                        viewModel.closePosterDialog()
                    },
                    onSharePoster = {
                        viewModel.closePosterDialog()
                    }
                )
            }

            // Two-Layer AI Prompt Generator Dialog
            if (showAiPromptModal) {
                AiPromptGeneratorModal(
                    outfit = currentOutfit,
                    onDismiss = { viewModel.closeAiPromptModal() },
                    onCopied = { msg ->
                        viewModel.showToast(msg)
                        viewModel.closeAiPromptModal()
                    }
                )
            }

            // Multi-Angle Camera Scan Flow (Luật 91/2025/QH15 & Gemini Vision)
            if (showCameraScanModal) {
                CameraScanModal(
                    baseOutfit = currentOutfit,
                    onApplyTo3dStudio = { scannedOutfit ->
                        viewModel.applyScannedOutfit(scannedOutfit)
                    },
                    onDismiss = { viewModel.closeCameraScanModal() }
                )
            }
        }
    }
}
