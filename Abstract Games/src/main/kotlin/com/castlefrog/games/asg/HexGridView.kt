package com.castlefrog.games.asg

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewManager
import org.jetbrains.anko.custom.ankoView
import java.util.ArrayList
import java.util.HashMap

class HexGridView : View {

    companion object {
        private val MIN_SIZE = 1
        private val DEFAULT_LINE_WIDTH_RATIO = 0.1f
        private val DEFAULT_OUTLINE_COLOR = Color.WHITE
        private val DEFAULT_BACKGROUND_COLOR = Color.GRAY
        val HEX_HIDDEN: Byte = -1
    }

    /** size of the hex grid */
    var size: Int = MIN_SIZE
        set(value) {
            if (size != value) {
                field = Math.max(MIN_SIZE, value)
                locations.clear()
                for (i in 1..size) {
                    val temp = ArrayList<PointF>()
                    for (j in 1..size) {
                        temp.add(PointF())
                    }
                    locations.add(temp)
                }
                locationColors = Array(size, { ByteArray(size) })
                invalidate()
            }
        }

    /** ratio of line width to hex width */
    var lineWidthRatio: Float = DEFAULT_LINE_WIDTH_RATIO
        set(value) {
            lineWidthRatio = when {
                value < 0 -> 0f
                value > 1 -> 1f
                else -> value
            }
            invalidate()
        }

    val paletteColors: MutableMap<Byte, Int> = HashMap()
    var boardOutlineColor: Int = DEFAULT_OUTLINE_COLOR
    var boardBackgroundColor: Int = DEFAULT_BACKGROUND_COLOR
    var touchListener: (x: Int, y: Int, me: MotionEvent) -> Unit = { x, y, me -> }

    private var locationColors: Array<ByteArray> = Array(size, { ByteArray(size) })
    private val locations: MutableList<List<PointF>> = ArrayList()
    private var hexagon = Path()
    /** distance from center to corner */
    private var hexagonRadius = 0f
    /** distance from center to edge */
    private var hexagonCRadius = 0f

    private val paint = Paint()
    private val drawMatrix = Matrix()
    private val drawPath = Path()
    private var lineWidth = 0f

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.HexGridView, defStyle, 0)

        size = a.getInt(R.styleable.HexGridView_boardSize, size)
        boardBackgroundColor = a.getColor(R.styleable.HexGridView_boardBackgroundColor, boardBackgroundColor)
        boardOutlineColor = a.getColor(R.styleable.HexGridView_boardOutlineColor, boardOutlineColor)

        paint.isAntiAlias = true

        a.recycle()
    }

    override fun onTouchEvent(mv: MotionEvent): Boolean {
        if (isEnabled) {
            for (i in 0..size - 1) {
                for (j in 0..size - 1) {
                    val point = locations[i][j]
                    if (windingNumberTest(mv.x.toDouble(), mv.y.toDouble(),
                            getHexVertices(point.x.toDouble(), point.y.toDouble(),
                                    hexagonRadius.toDouble())) != 0) {
                        touchListener(i, j, mv)
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        // pre-calc line width
        var maxHexWidth = contentWidth / (0.75f * size + 0.25f)
        var maxHexHeight = ((2 * contentHeight) / (3 * size - 1)).toFloat()
        hexagonRadius = Math.min(maxHexWidth, maxHexHeight / HEXAGON_SHORT_RADIUS) / 2
        lineWidth = hexagonRadius * lineWidthRatio

        maxHexWidth = contentWidth / (0.75f * size + 0.25f)
        maxHexHeight = (2 * (contentHeight - lineWidth)) / (3 * size - 1)
        hexagonRadius = Math.min(maxHexWidth, maxHexHeight / HEXAGON_SHORT_RADIUS) / 2

        hexagonCRadius = HEXAGON_SHORT_RADIUS * hexagonRadius
        hexagon = getHexagon(hexagonRadius)
        val boardWidth = hexagonRadius * (1.5f * size + 0.5f)
        val boardHeight = hexagonCRadius * (3 * size - 1)
        val xPadding = (width - boardWidth) / 2
        val yPadding = (height - boardHeight) / 2
        val yAdjust = hexagonCRadius * (size - 1)

        val x0 = xPadding + hexagonRadius
        val y0 = yAdjust + yPadding + (HEXAGON_SHORT_RADIUS * hexagonRadius) - 1.5f * lineWidth
        for (i in 0..size - 1) {
            for (j in 0..size - 1) {
                locations[i][j].x = x0 + i * hexagonRadius * 1.5f
                locations[i][j].y = y0 - i * hexagonCRadius + j * 2 * hexagonCRadius
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //draw board
        paint.style = Paint.Style.FILL
        for (i in 0..size - 1) {
            for (j in 0..size - 1) {
                if (locationColors[i][j] != HEX_HIDDEN) {
                    paint.color = paletteColors[locationColors[i][j]] ?: boardBackgroundColor
                    val point = locations[i][j]
                    drawMatrix.reset()
                    drawMatrix.postTranslate(point.x - hexagonRadius, point.y - hexagonCRadius)
                    drawPath.reset()
                    hexagon.transform(drawMatrix, drawPath)
                    canvas.drawPath(drawPath, paint)
                }
            }
        }
        // draw board inner edges
        paint.style = Paint.Style.STROKE
        paint.color = boardOutlineColor
        paint.strokeWidth = lineWidth
        for (i in 0..size - 1) {
            for (j in 0..size - 1) {
                if (locationColors[i][j] != HEX_HIDDEN) {
                    val point = locations[i][j]
                    drawMatrix.reset()
                    drawMatrix.postTranslate(point.x - hexagonRadius, point.y - hexagonCRadius)
                    drawPath.reset()
                    hexagon.transform(drawMatrix, drawPath)
                    canvas.drawPath(drawPath, paint)
                }
            }
        }
    }

    fun setLocationColor(x: Int, y: Int, colorIndex: Int) {
        locationColors[x][y] = colorIndex.toByte()
        invalidate()
    }

    fun setLocationsColor(locations: List<Pair<Int, Int>>, colorIndex: Int) {
        locations.forEach { pair -> locationColors[pair.first][pair.second] = colorIndex.toByte() }
        invalidate()
    }

    fun setLocationHidden(x: Int, y: Int) {
        locationColors[x][y] = HEX_HIDDEN
        invalidate()
    }

    fun setLocationsHidden(locations: List<Pair<Int, Int>>) {
        locations.forEach { pair -> locationColors[pair.first][pair.second] = HEX_HIDDEN }
        invalidate()
    }

}

inline fun ViewManager.hexGridView(theme: Int = 0) = hexGridView(theme) {}
inline fun ViewManager.hexGridView(theme: Int = 0, init: HexGridView.() -> Unit) = ankoView(::HexGridView, theme, init)
