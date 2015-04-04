package com.castlefrog.games.asg

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.castlefrog.agl.domains.havannah.HavannahAction
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.Vector

public class HexGridView : View {

    companion object {
        private val MIN_BOARD_SIZE = 1
        private val DEFAULT_LINE_WIDTH_RATIO = 0.1f
        private val DEFAULT_OUTLINE_COLOR = Color.WHITE
        private val DEFAULT_BACKGROUND_COLOR = Color.GRAY
    }

    /**
     * Get a touch event on a specific hex
     */
    public trait HexTouchListener {
        public fun onHexTouchEvent(x: Int, y: Int, mv: MotionEvent)
    }

    /**
     * Dummy listener to use instead of null
     */
    private class DummyHexTouchListener : HexTouchListener {
        override fun onHexTouchEvent(x: Int, y: Int, mv: MotionEvent) {}
    }

    /** Size of the hex grid */
    public var boardSize: Int = MIN_BOARD_SIZE
        set(value) {
            if (boardSize != value) {
                $boardSize = Math.max(MIN_BOARD_SIZE, value)
                locations.clear()
                for (i in 1..boardSize) {
                    val temp = ArrayList<PointF>()
                    for (j in 1..boardSize) {
                        temp.add(PointF())
                    }
                    locations.add(temp)
                }
                locationColors = Array(boardSize, { ByteArray(boardSize) })
                invalidate()
            }
        }

    /** Ratio of line width to hex width. Must be value between 0 and 1 */
    public var lineWidthRatio: Float = DEFAULT_LINE_WIDTH_RATIO
        set(value) {
            lineWidthRatio = if (value >= 0 && value <= 1) value else lineWidthRatio
            invalidate()
        }

    public var locationColors: Array<ByteArray> = Array(boardSize, { ByteArray(boardSize) })
    public val paletteColors: MutableMap<Byte, Int> = HashMap()
    public var boardOutlineColor: Int = DEFAULT_OUTLINE_COLOR
    public var boardBackgroundColor: Int = DEFAULT_BACKGROUND_COLOR

    public var hexTouchListener: HexTouchListener = DummyHexTouchListener()

    private val locations: MutableList<List<PointF>> = ArrayList()
    private var hexagon = Path()
    /** distance from center to corner  */
    private var hexagonRadius = 0f
    private var hexagonCRadius = 0f

    private val paint = Paint()
    private var lineWidth = 0f

    public constructor(context: Context) : super(context) {
        init(null, 0)
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    public constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.HexView, defStyle, 0)

        boardSize = a.getInt(R.styleable.HexView_boardSize, boardSize)
        boardBackgroundColor = a.getColor(R.styleable.HexView_boardBackgroundColor, boardBackgroundColor)
        boardOutlineColor = a.getColor(R.styleable.HexView_boardOutlineColor, boardOutlineColor)

        a.recycle()
    }

    override fun onTouchEvent(mv: MotionEvent): Boolean {
        if (isEnabled()) {
            val x = mv.getX()
            val y = mv.getY()
            for (i in 0..boardSize - 1) {
                for (j in 0..boardSize - 1) {
                    val point = locations[i][j]
                    // TODO - get all points in each hex rather than just inscribed circle
                    if (Math.hypot((point.x - x).toDouble(), (point.y - y).toDouble()) < hexagonCRadius) {
                        hexTouchListener.onHexTouchEvent(i, j, mv)
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        val paddingLeft = getPaddingLeft()
        val paddingTop = getPaddingTop()
        val paddingRight = getPaddingRight()
        val paddingBottom = getPaddingBottom()
        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        val maxHexWidth = contentWidth / (0.75f * boardSize + 0.25f)
        val maxHexHeight = ((2 * contentHeight) / (3 * boardSize - 1)).toFloat()
        hexagonRadius = Math.min(maxHexWidth, maxHexHeight / PathUtils.HEXAGON_SHORT_RADIUS) / 2
        hexagonCRadius = PathUtils.HEXAGON_SHORT_RADIUS * hexagonRadius
        val boardWidth = hexagonRadius * (1.5f * boardSize + 0.5f)
        val boardHeight = hexagonCRadius * (3 * boardSize - 1)
        val xPadding = (width.toFloat() - boardWidth) / 2
        val yPadding = (height.toFloat() - boardHeight) / 2
        val yAdjust = hexagonCRadius * (boardSize - 1)
        hexagon = PathUtils.getHexagon(hexagonRadius)
        lineWidth = hexagonRadius * lineWidthRatio

        val x0 = xPadding + hexagonRadius
        val y0 = yAdjust + yPadding + (PathUtils.HEXAGON_SHORT_RADIUS * hexagonRadius)
        for (i in 0..boardSize - 1) {
            for (j in 0..boardSize - 1) {
                locations[i][j].x = x0 + i * hexagonRadius * 1.5f
                locations[i][j].y = y0 - i * hexagonCRadius + j * 2 * hexagonCRadius
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //draw board
        val matrix = Matrix()
        paint.setStyle(Paint.Style.FILL)
        for (i in 0..boardSize - 1) {
            for (j in 0..boardSize - 1) {
                paint.setColor(paletteColors.get(locationColors[i][j].toInt()) ?: boardBackgroundColor)
                val temp = Path()
                val point = locations[i][j]
                matrix.reset()
                matrix.postTranslate(point.x - hexagonRadius, point.y - hexagonCRadius)
                hexagon.transform(matrix, temp)
                canvas.drawPath(temp, paint)
            }
        }
        // draw board inner edges
        paint.setStyle(Paint.Style.STROKE)
        paint.setColor(boardOutlineColor)
        paint.setStrokeWidth(lineWidth)
        for (row in locations) {
            for (point in row) {
                val temp = Path()
                matrix.reset()
                matrix.postTranslate(point.x - hexagonRadius, point.y - hexagonCRadius)
                hexagon.transform(matrix, temp)
                canvas.drawPath(temp, paint)
            }
        }
    }
}