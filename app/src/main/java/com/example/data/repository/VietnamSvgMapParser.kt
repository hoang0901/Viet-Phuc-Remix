package com.example.data.repository

import android.content.Context
import android.graphics.Path as AndroidPath
import android.graphics.RectF
import android.graphics.Region
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path as ComposePath
import androidx.compose.ui.graphics.asComposePath
import androidx.core.graphics.PathParser
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

/**
 * Native SVG parser for vietnam-34-provinces.svg.
 * Preserves 100% of the original vector coordinates, IDs, boundary paths,
 * and geographical structure without redrawing or altering geography.
 */
data class ParsedProvincePath(
    val id: String,                 // e.g. "an_giang"
    val svgId: String,              // e.g. "svg_an_giang"
    val name: String,               // Official Vietnamese name from <title>
    val isMerged: Boolean,          // true if province-merged, false if province-unmerged
    val androidPath: AndroidPath,
    val composePath: ComposePath,
    val bounds: RectF,
    val centerPoint: Offset,
    val hitRegion: Region,
    var labelPos: Offset? = null
)

data class ParsedIslandMarker(
    val name: String,
    val subTitle: String?,
    val position: Offset,
    val isArchipelago: Boolean
)

data class ParsedGridLine(
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float,
    val label: String?,
    val labelPos: Offset?
)

data class ParsedSeaLabel(
    val text: String,
    val position: Offset,
    val fontSize: Float
)

data class ParsedProvinceGroup(
    val id: String,                 // e.g. "an_giang"
    val svgId: String,              // e.g. "svg_an_giang"
    val name: String,               // e.g. "An Giang"
    val isMerged: Boolean,
    val subPaths: List<ParsedProvincePath>,
    val overallBounds: RectF,
    val centerPoint: Offset,
    val labelPos: Offset?
)

data class ParsedVietnamMap(
    val viewBox: Rect,
    val provinces: List<ParsedProvinceGroup>,
    val islandMarkers: List<ParsedIslandMarker>,
    val gridLines: List<ParsedGridLine>,
    val seaLabels: List<ParsedSeaLabel>
) {
    /**
     * Hit tests a point in SVG coordinates against all province regions.
     * Returns the matched province group, or null if touching ocean/outside.
     */
    fun findProvinceAt(svgX: Float, svgY: Float): ParsedProvinceGroup? {
        val x = svgX.toInt()
        val y = svgY.toInt()
        return provinces.firstOrNull { group ->
            group.subPaths.any { sub ->
                sub.bounds.contains(svgX, svgY) && sub.hitRegion.contains(x, y)
            }
        }
    }
}

object VietnamSvgMapParser {

    const val ASSET_FILE_NAME = "vietnam-34-provinces.svg"

    /**
     * Checks whether the user-provided SVG map file is present in app assets and non-empty.
     */
    fun isSvgAssetAvailable(context: Context): Boolean {
        return try {
            val assetList = context.assets.list("") ?: emptyArray()
            if (assetList.contains(ASSET_FILE_NAME)) {
                context.assets.open(ASSET_FILE_NAME).use { stream ->
                    stream.available() > 0
                }
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Parses the SVG input stream.
     * Preserves exact geometry, contours, titles, classes, and native SVG metadata.
     */
    fun parse(inputStream: InputStream): Result<ParsedVietnamMap> {
        return runCatching {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, "UTF-8")

            var viewBox = Rect(0f, 0f, 2400f, 3600f)

            val rawPaths = mutableListOf<ParsedProvincePath>()
            val labelsMap = mutableMapOf<String, Offset>()
            val islands = mutableListOf<ParsedIslandMarker>()
            val gridLines = mutableListOf<ParsedGridLine>()
            val seaLabels = mutableListOf<ParsedSeaLabel>()

            var inPath = false
            var pendingD: String? = null
            var pendingId: String? = null
            var pendingSvgId: String? = null
            var pendingClass: String? = null
            var pendingTitle: String? = null

            var inGroup = false
            var currentGroupTransform: String? = null
            val currentGroupTexts = mutableListOf<String>()

            var eventType = parser.eventType

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tagName = parser.name?.lowercase() ?: ""
                        when (tagName) {
                            "svg" -> {
                                val vbAttr = parser.getAttributeValue(null, "viewBox")
                                    ?: parser.getAttributeValue("", "viewBox")
                                if (vbAttr != null) {
                                    val parts = vbAttr.trim().split(Regex("[\\s,]+")).mapNotNull { it.toFloatOrNull() }
                                    if (parts.size >= 4) {
                                        viewBox = Rect(parts[0], parts[1], parts[0] + parts[2], parts[1] + parts[3])
                                    }
                                }
                            }

                            "line" -> {
                                val x1 = parser.getAttributeValue(null, "x1")?.toFloatOrNull() ?: 0f
                                val y1 = parser.getAttributeValue(null, "y1")?.toFloatOrNull() ?: 0f
                                val x2 = parser.getAttributeValue(null, "x2")?.toFloatOrNull() ?: 0f
                                val y2 = parser.getAttributeValue(null, "y2")?.toFloatOrNull() ?: 0f
                                gridLines.add(ParsedGridLine(x1, y1, x2, y2, null, null))
                            }

                            "g" -> {
                                inGroup = true
                                currentGroupTransform = parser.getAttributeValue(null, "transform")
                                currentGroupTexts.clear()
                            }

                            "path" -> {
                                inPath = true
                                pendingD = parser.getAttributeValue(null, "d") ?: parser.getAttributeValue("", "d")
                                pendingSvgId = parser.getAttributeValue(null, "id") ?: parser.getAttributeValue("", "id")
                                pendingId = parser.getAttributeValue(null, "data-province-id")
                                    ?: parser.getAttributeValue("", "data-province-id")
                                    ?: pendingSvgId
                                pendingClass = parser.getAttributeValue(null, "class") ?: parser.getAttributeValue("", "class")
                                pendingTitle = null
                            }

                            "title" -> {
                                if (inPath) {
                                    val titleText = parser.nextText()
                                    pendingTitle = titleText.trim()
                                }
                            }

                            "text" -> {
                                val cls = parser.getAttributeValue(null, "class") ?: ""
                                val x = parser.getAttributeValue(null, "x")?.toFloatOrNull() ?: 0f
                                val y = parser.getAttributeValue(null, "y")?.toFloatOrNull() ?: 0f
                                val fontSize = parser.getAttributeValue(null, "font-size")?.toFloatOrNull() ?: 24f
                                val textVal = parser.nextText().trim()

                                if (cls.contains("label-text")) {
                                    labelsMap[textVal] = Offset(x, y)
                                } else if (inGroup) {
                                    currentGroupTexts.add(textVal)
                                } else if (textVal.contains("BIỂN") || textVal.contains("VỊNH")) {
                                    seaLabels.add(ParsedSeaLabel(textVal, Offset(x, y), fontSize))
                                }
                            }
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name?.lowercase() ?: ""
                        when (tagName) {
                            "path" -> {
                                inPath = false
                                val d = pendingD
                                if (!d.isNullOrBlank()) {
                                    val androidPath = try {
                                        PathParser.createPathFromPathData(d)
                                    } catch (_: Exception) {
                                        null
                                    }

                                    if (androidPath != null) {
                                        val bounds = RectF()
                                        androidPath.computeBounds(bounds, true)
                                        if (bounds.width() > 0 && bounds.height() > 0) {
                                            val region = Region()
                                            val clip = Region(
                                                (bounds.left - 1).toInt(),
                                                (bounds.top - 1).toInt(),
                                                (bounds.right + 1).toInt(),
                                                (bounds.bottom + 1).toInt()
                                            )
                                            region.setPath(androidPath, clip)

                                            val composePath = androidPath.asComposePath()
                                            val center = Offset(bounds.centerX(), bounds.centerY())
                                            val id = pendingId ?: "unknown"
                                            val svgId = pendingSvgId ?: id
                                            val name = pendingTitle ?: id
                                            val isMerged = pendingClass?.contains("province-merged") == true

                                            rawPaths.add(
                                                ParsedProvincePath(
                                                    id = id,
                                                    svgId = svgId,
                                                    name = name,
                                                    isMerged = isMerged,
                                                    androidPath = androidPath,
                                                    composePath = composePath,
                                                    bounds = bounds,
                                                    centerPoint = center,
                                                    hitRegion = region
                                                )
                                            )
                                        }
                                    }
                                }
                                pendingD = null
                                pendingId = null
                                pendingSvgId = null
                                pendingClass = null
                                pendingTitle = null
                            }

                            "g" -> {
                                if (inGroup) {
                                    inGroup = false
                                    // Parse transform="translate(x,y)"
                                    val tr = currentGroupTransform ?: ""
                                    val trMatch = Regex("translate\\(([0-9.]+),([0-9.]+)\\)").find(tr)
                                    val pos = if (trMatch != null) {
                                        val (gx, gy) = trMatch.destructured
                                        Offset(gx.toFloat(), gy.toFloat())
                                    } else {
                                        Offset.Zero
                                    }

                                    if (currentGroupTexts.isNotEmpty()) {
                                        val islandName = currentGroupTexts[0]
                                        val subTitle = currentGroupTexts.getOrNull(1)
                                        val isArchi = islandName.contains("QUẦN ĐẢO", ignoreCase = true)
                                        islands.add(
                                            ParsedIslandMarker(
                                                name = islandName,
                                                subTitle = subTitle,
                                                position = pos,
                                                isArchipelago = isArchi
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                eventType = parser.next()
            }

            // Assign label positions to paths from labelsMap
            rawPaths.forEach { p ->
                val matchedPos = labelsMap[p.name]
                if (matchedPos != null) {
                    p.labelPos = matchedPos
                }
            }

            // Group paths by data_id
            val groupedProvinces = rawPaths.groupBy { it.id }.map { (id, paths) ->
                val overallBounds = RectF()
                paths.forEach { p -> overallBounds.union(p.bounds) }
                val first = paths.first()
                val labelPos = paths.firstNotNullOfOrNull { it.labelPos } ?: Offset(overallBounds.centerX(), overallBounds.centerY())

                ParsedProvinceGroup(
                    id = id,
                    svgId = first.svgId,
                    name = first.name,
                    isMerged = first.isMerged,
                    subPaths = paths,
                    overallBounds = overallBounds,
                    centerPoint = Offset(overallBounds.centerX(), overallBounds.centerY()),
                    labelPos = labelPos
                )
            }.sortedBy { it.name }

            ParsedVietnamMap(
                viewBox = viewBox,
                provinces = groupedProvinces,
                islandMarkers = islands,
                gridLines = gridLines,
                seaLabels = seaLabels
            )
        }
    }
}
