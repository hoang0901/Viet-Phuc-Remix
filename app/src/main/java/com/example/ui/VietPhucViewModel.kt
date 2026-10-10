package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.SavedLookEntity
import com.example.data.model.*
import com.example.data.repository.CulturalRuleEngine
import com.example.data.repository.VietPhucRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppDestination(val label: String, val iconVectorName: String) {
    EXPLORE("Khám phá", "explore"),
    BUILDER("Phối đồ", "checkroom"),
    LOOKBOOK("Lookbook", "collections"),
    CULTURE("Di sản", "menu_book"),
    PROFILE("Hồ sơ", "person")
}

enum class CultureHubMode {
    MAP,
    ARTICLES
}

class VietPhucViewModel(private val repository: VietPhucRepository) : ViewModel() {

    // Current Navigation Tab
    private val _currentDestination = MutableStateFlow(AppDestination.EXPLORE)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    // Culture Hub active section (Map or Articles)
    private val _cultureHubMode = MutableStateFlow(CultureHubMode.MAP)
    val cultureHubMode: StateFlow<CultureHubMode> = _cultureHubMode.asStateFlow()

    // Selected Occasion on Home
    private val _selectedOccasion = MutableStateFlow(OccasionEvent.TET)
    val selectedOccasion: StateFlow<OccasionEvent> = _selectedOccasion.asStateFlow()

    // Current Working Outfit in Builder
    private val _currentOutfit = MutableStateFlow(repository.getDefaultOutfit(OccasionEvent.TET))
    val currentOutfit: StateFlow<ActiveOutfit> = _currentOutfit.asStateFlow()

    // Modal dialog states
    private val _showLookCardModal = MutableStateFlow(false)
    val showLookCardModal: StateFlow<Boolean> = _showLookCardModal.asStateFlow()

    private val _showPosterDialog = MutableStateFlow(false)
    val showPosterDialog: StateFlow<Boolean> = _showPosterDialog.asStateFlow()

    private val _showAiPromptModal = MutableStateFlow(false)
    val showAiPromptModal: StateFlow<Boolean> = _showAiPromptModal.asStateFlow()

    private val _showCameraScanModal = MutableStateFlow(false)
    val showCameraScanModal: StateFlow<Boolean> = _showCameraScanModal.asStateFlow()

    // Split-screen Compare state
    private val _compareLookA = MutableStateFlow<ActiveOutfit?>(null)
    val compareLookA: StateFlow<ActiveOutfit?> = _compareLookA.asStateFlow()

    private val _compareLookB = MutableStateFlow<ActiveOutfit?>(null)
    val compareLookB: StateFlow<ActiveOutfit?> = _compareLookB.asStateFlow()

    private val _isComparing = MutableStateFlow(false)
    val isComparing: StateFlow<Boolean> = _isComparing.asStateFlow()

    // Targeted knowledge article id for deep linking
    private val _targetArticleId = MutableStateFlow<String?>(null)
    val targetArticleId: StateFlow<String?> = _targetArticleId.asStateFlow()

    // Snackbar notifications
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    // Room Database persistent saved looks
    val savedLooks: StateFlow<List<SavedLookEntity>> = repository.getAllSavedLooks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allGarments = repository.garments
    val allInnerGarments = repository.innerGarments
    val allBottoms = repository.bottoms
    val allFootwear = repository.footwear
    val allHeadwear = repository.headwear
    val allAccessories = repository.accessories
    val colorPalettes = repository.colorPalettes
    val heritageArticles = repository.heritageArticles

    val presetLooks = listOf(
        repository.getDefaultOutfit(OccasionEvent.GRADUATION),
        repository.getHueMinimalOutfit(),
        repository.getTetHeritageOutfit()
    )

    init {
        // Pre-populate with initial looks if database is empty on first launch
        viewModelScope.launch {
            savedLooks.collect { list ->
                if (list.isEmpty()) {
                    val defaultLook = repository.getHueMinimalOutfit()
                    val (_, score) = CulturalRuleEngine.evaluate(defaultLook)
                    repository.saveOutfit(defaultLook, score)

                    val gradLook = repository.getDefaultOutfit(OccasionEvent.GRADUATION)
                    val (_, scoreGrad) = CulturalRuleEngine.evaluate(gradLook)
                    repository.saveOutfit(gradLook, scoreGrad)
                }
            }
        }
    }

    fun navigateTo(destination: AppDestination) {
        _currentDestination.value = destination
    }

    fun setOccasion(occasion: OccasionEvent) {
        _selectedOccasion.value = occasion
        _currentOutfit.value = _currentOutfit.value.copy(occasion = occasion)
    }

    fun updateCurrentOutfit(outfit: ActiveOutfit) {
        _currentOutfit.value = outfit
    }

    fun selectPresetOutfit(outfit: ActiveOutfit) {
        _currentOutfit.value = outfit
        _currentDestination.value = AppDestination.BUILDER
    }

    fun openLookCardModal() {
        _showLookCardModal.value = true
    }

    fun closeLookCardModal() {
        _showLookCardModal.value = false
    }

    fun openPosterDialog() {
        _showPosterDialog.value = true
    }

    fun closePosterDialog() {
        _showPosterDialog.value = false
    }

    fun openAiPromptModal() {
        _showAiPromptModal.value = true
    }

    fun closeAiPromptModal() {
        _showAiPromptModal.value = false
    }

    fun openCameraScanModal() {
        _showCameraScanModal.value = true
    }

    fun closeCameraScanModal() {
        _showCameraScanModal.value = false
    }

    fun applyScannedOutfit(scannedOutfit: ActiveOutfit) {
        _currentOutfit.value = scannedOutfit
        _showCameraScanModal.value = false
        _currentDestination.value = AppDestination.BUILDER
        viewModelScope.launch {
            _snackbarMessage.emit("Đã nạp thành công trang phục từ Camera Scan vào 3D Studio!")
        }
    }

    fun selectGarmentAndNavigateToBuilder(costumeId: String) {
        val matchedGarment = allGarments.firstOrNull { it.id == costumeId || it.category == costumeId }
        if (matchedGarment != null) {
            _currentOutfit.value = _currentOutfit.value.copy(
                mainGarment = matchedGarment,
                name = "Trang phục ${matchedGarment.name}"
            )
            _currentDestination.value = AppDestination.BUILDER
            viewModelScope.launch {
                _snackbarMessage.emit("Đã nạp '${matchedGarment.name}' vào Phòng Thiết Kế!")
            }
        } else {
            _currentDestination.value = AppDestination.BUILDER
            viewModelScope.launch {
                _snackbarMessage.emit("Đang chuyển đến Phòng Thiết Kế Việt Phục!")
            }
        }
    }

    fun rotateAvatarAngle() {
        val currentAngle = _currentOutfit.value.viewAngle
        val nextAngle = when (currentAngle) {
            AvatarAngle.FRONT -> AvatarAngle.THREE_QUARTER
            AvatarAngle.THREE_QUARTER -> AvatarAngle.BACK
            AvatarAngle.BACK -> AvatarAngle.FRONT
        }
        _currentOutfit.value = _currentOutfit.value.copy(viewAngle = nextAngle)
        viewModelScope.launch {
            _snackbarMessage.emit("Đã chuyển sang góc nhìn: ${nextAngle.label} (${nextAngle.rotationDesc})")
        }
    }

    fun toggleAvatarGender() {
        val currentGender = _currentOutfit.value.gender
        val nextGender = if (currentGender == AvatarGender.FEMALE) AvatarGender.MALE else AvatarGender.FEMALE
        _currentOutfit.value = _currentOutfit.value.copy(gender = nextGender)
        viewModelScope.launch {
            _snackbarMessage.emit("Đã chuyển sang model: ${nextGender.label} (${nextGender.symbol})")
        }
    }

    fun updateBodyMetrics(heightCm: Int, weightKg: Int) {
        val safeH = heightCm.coerceIn(140, 205)
        val safeW = weightKg.coerceIn(35, 110)
        _currentOutfit.value = _currentOutfit.value.copy(heightCm = safeH, weightKg = safeW)
    }

    fun showToast(msg: String) {
        viewModelScope.launch {
            _snackbarMessage.emit(msg)
        }
    }

    fun saveCurrentLook() {
        viewModelScope.launch {
            val outfit = _currentOutfit.value
            val (_, score) = CulturalRuleEngine.evaluate(outfit)
            repository.saveOutfit(outfit, score)
            _snackbarMessage.emit("Đã lưu '${outfit.name}' vào Lookbook thành công!")
        }
    }

    fun deleteSavedLook(id: Long) {
        viewModelScope.launch {
            repository.deleteLook(id)
            _snackbarMessage.emit("Đã xóa look khỏi Lookbook.")
        }
    }

    fun loadSavedLookIntoBuilder(entity: SavedLookEntity) {
        val main = allGarments.firstOrNull { it.id == entity.mainGarmentId } ?: allGarments[0]
        val inner = allInnerGarments.firstOrNull { it.id == entity.innerGarmentId }
        val bottom = allBottoms.firstOrNull { it.id == entity.bottomId } ?: allBottoms[0]
        val foot = allFootwear.firstOrNull { it.id == entity.footwearId } ?: allFootwear[0]
        val head = allHeadwear.firstOrNull { it.id == entity.headwearId }
        val acc = allAccessories.firstOrNull { it.id == entity.accessoryId }
        val pColor = colorPalettes.firstOrNull { it.id == entity.primaryColorId } ?: colorPalettes[0]
        val sColor = colorPalettes.firstOrNull { it.id == entity.secondaryColorId } ?: colorPalettes[1]

        _currentOutfit.value = ActiveOutfit(
            id = "saved_${entity.id}",
            name = entity.lookName,
            occasion = OccasionEvent.fromId(entity.occasionId),
            mode = CulturalMode.fromId(entity.modeId),
            mainGarment = main,
            innerGarment = inner,
            bottom = bottom,
            footwear = foot,
            headwear = head,
            accessory = acc,
            hairstyle = null,
            primaryColor = pColor,
            secondaryColor = sColor,
            customNotes = entity.userNotes
        )
        _currentDestination.value = AppDestination.BUILDER
    }

    fun compareCurrentWithPreset() {
        _compareLookA.value = _currentOutfit.value
        _compareLookB.value = repository.getHueMinimalOutfit()
        _isComparing.value = true
    }

    fun compareTwoSavedLooks(entityA: SavedLookEntity, entityB: SavedLookEntity) {
        val lookA = entityToActiveOutfit(entityA)
        val lookB = entityToActiveOutfit(entityB)
        _compareLookA.value = lookA
        _compareLookB.value = lookB
        _isComparing.value = true
    }

    fun closeComparison() {
        _isComparing.value = false
    }

    fun openHeritageMap() {
        _cultureHubMode.value = CultureHubMode.MAP
        _currentDestination.value = AppDestination.CULTURE
    }

    fun setCultureHubMode(mode: CultureHubMode) {
        _cultureHubMode.value = mode
    }

    fun navigateToArticle(articleId: String) {
        _targetArticleId.value = articleId
        _cultureHubMode.value = CultureHubMode.ARTICLES
        _currentDestination.value = AppDestination.CULTURE
    }

    private fun entityToActiveOutfit(entity: SavedLookEntity): ActiveOutfit {
        val main = allGarments.firstOrNull { it.id == entity.mainGarmentId } ?: allGarments[0]
        val inner = allInnerGarments.firstOrNull { it.id == entity.innerGarmentId }
        val bottom = allBottoms.firstOrNull { it.id == entity.bottomId } ?: allBottoms[0]
        val foot = allFootwear.firstOrNull { it.id == entity.footwearId } ?: allFootwear[0]
        val head = allHeadwear.firstOrNull { it.id == entity.headwearId }
        val acc = allAccessories.firstOrNull { it.id == entity.accessoryId }
        val pColor = colorPalettes.firstOrNull { it.id == entity.primaryColorId } ?: colorPalettes[0]
        val sColor = colorPalettes.firstOrNull { it.id == entity.secondaryColorId } ?: colorPalettes[1]

        return ActiveOutfit(
            id = "look_${entity.id}",
            name = entity.lookName,
            occasion = OccasionEvent.fromId(entity.occasionId),
            mode = CulturalMode.fromId(entity.modeId),
            mainGarment = main,
            innerGarment = inner,
            bottom = bottom,
            footwear = foot,
            headwear = head,
            accessory = acc,
            hairstyle = null,
            primaryColor = pColor,
            secondaryColor = sColor,
            customNotes = entity.userNotes
        )
    }
}

class VietPhucViewModelFactory(private val repository: VietPhucRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VietPhucViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VietPhucViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
