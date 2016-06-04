package com.castlefrog.games.asg

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.castlefrog.agl.Arbiter
import com.castlefrog.agl.domains.havannah.HavannahAction
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.Vector

class HexGridView : View {

    companion object {
        private val MIN_SIZE = 1
        private val DEFAULT_LINE_WIDTH_RATIO = 0.1f
        private val DEFAULT_OUTLINE_COLOR = Color.WHITE
        private val DEFAULT_BACKGROUND_COLOR = Color.GRAY
    }

    /** Size of the hex grid */
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

    /** Ratio of line width to hex width. Must be value between 0 and 1 */
    var lineWidthRatio: Float = DEFAULT_LINE_WIDTH_RATIO
        set(value) {
            lineWidthRatio = if (value >= 0 && value <= 1) value else lineWidthRatio
            invalidate()
        }

    var locationColors: Array<ByteArray> = Array(size, { ByteArray(size) })
    val paletteColors: MutableMap<Byte, Int> = HashMap()
    var boardOutlineColor: Int = DEFAULT_OUTLINE_COLOR
    var boardBackgroundColor: Int = DEFAULT_BACKGROUND_COLOR

    var touchListener: (x: Int, y: Int, mv: MotionEvent) -> Unit = { x, y, mv -> }

    private val locations: MutableList<List<PointF>> = ArrayList()
    private var hexagon = Path()
    /** distance from center to corner  */
    private var hexagonRadius = 0f
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

        a.recycle()
    }

    override fun onTouchEvent(mv: MotionEvent): Boolean {
        if (isEnabled) {
            for (i in 0..size - 1) {
                for (j in 0..size - 1) {
                    val point = locations[i][j]
                    // TODO - get all points in each hex rather than just inscribed circle
                    if (Math.hypot((point.x - mv.x).toDouble(), (point.y - mv.y).toDouble()) < hexagonCRadius) {
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
        hexagonRadius = Math.min(maxHexWidth, maxHexHeight / PathUtils.HEXAGON_SHORT_RADIUS) / 2
        lineWidth = hexagonRadius * lineWidthRatio

        maxHexWidth = contentWidth / (0.75f * size + 0.25f)
        maxHexHeight = (2 * (contentHeight - lineWidth)) / (3 * size - 1)
        hexagonRadius = Math.min(maxHexWidth, maxHexHeight / PathUtils.HEXAGON_SHORT_RADIUS) / 2

        hexagonCRadius = PathUtils.HEXAGON_SHORT_RADIUS * hexagonRadius
        hexagon = PathUtils.getHexagon(hexagonRadius)
        val boardWidth = hexagonRadius * (1.5f * size + 0.5f)
        val boardHeight = hexagonCRadius * (3 * size - 1)
        val xPadding = (width - boardWidth) / 2
        val yPadding = (height - boardHeight) / 2
        val yAdjust = hexagonCRadius * (size - 1)

        val x0 = xPadding + hexagonRadius
        val y0 = yAdjust + yPadding + (PathUtils.HEXAGON_SHORT_RADIUS * hexagonRadius) - 1.5f * lineWidth
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
                paint.color = paletteColors[locationColors[i][j]] ?: boardBackgroundColor
                val point = locations[i][j]
                drawMatrix.reset()
                drawMatrix.postTranslate(point.x - hexagonRadius, point.y - hexagonCRadius)
                drawPath.reset()
                hexagon.transform(drawMatrix, drawPath)
                canvas.drawPath(drawPath, paint)
            }
        }
        // draw board inner edges
        paint.style = Paint.Style.STROKE
        paint.color = boardOutlineColor
        paint.strokeWidth = lineWidth
        for (row in locations) {
            for (point in row) {
                drawMatrix.reset()
                drawMatrix.postTranslate(point.x - hexagonRadius, point.y - hexagonCRadius)
                drawPath.reset()
                hexagon.transform(drawMatrix, drawPath)
                canvas.drawPath(drawPath, paint)
            }
        }
    }

    fun setLocationColor(x: Int, y: Int, colorIndex: Int) {
        locationColors[x][y] = colorIndex.toByte()
        invalidate()
    }
}