package com.castlefrog.games.asg

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.castlefrog.agl.domains.hex.HexAction
import java.util.ArrayList

public class HexView : View {

    companion object {
        private val DEFAULT_BOARD_SIZE = 5
        private val LINE_WIDTH_RATIO = 0.1f

        public val BACKGROUND_COLOR_VALUE: Int = 0
        public val AGENT1_COLOR_VALUE: Int = 1
        public val AGENT2_COLOR_VALUE: Int = 2
        public val CONNECTION_COLOR_VALUE: Int = 3
    }

    private var locations: MutableList<MutableList<PointF>> = ArrayList()
    private var hexagon: Path? = null
    /** Distance from center to corner  */
    private var hexagonRadius: Float = 0f
    private var hexagonCRadius: Float = 0f
    private var lineWidth: Float = 0f

    private var locationColors: Array<ByteArray> = Array(0, { ByteArray(0) })
    private val paletteColors = ArrayList<Int>()
    private var boardOutlineColor: Int = Color.WHITE
    private var lineColor = Color.BLACK

    private var contentWidth: Int = 0
    private var contentHeight: Int = 0
    private val paint = Paint()
    private val path = Path()

    public var boardSize: Int = 0
        set(value) {
            $boardSize = value
            invalidate()
        }
    public var boardBackgroundColor: Int
        get() = paletteColors.get(BACKGROUND_COLOR_VALUE)
        set(value) {
            paletteColors.set(BACKGROUND_COLOR_VALUE, value)
            invalidate()
        }
    public var agent1Color: Int
        get() = paletteColors.get(AGENT1_COLOR_VALUE)
        set(value) {
            paletteColors.set(AGENT1_COLOR_VALUE, value)
            invalidate()
        }
    public var agent2Color: Int
        get() = paletteColors.get(AGENT2_COLOR_VALUE)
        set(value) {
            paletteColors.set(AGENT2_COLOR_VALUE, value)
            invalidate()
        }
    public var connectionColor: Int
        get() = paletteColors.get(CONNECTION_COLOR_VALUE)
        set(value) {
            paletteColors.set(CONNECTION_COLOR_VALUE, connectionColor)
            invalidate()
        }

    private var selectedHex: Point? = null
    var selectActionListener: SelectActionListener<HexAction> = DummySelectActionListener()

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
        // Load attributes
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.HexView, defStyle, 0)

        boardSize = a.getInt(R.styleable.HexView_boardSize, DEFAULT_BOARD_SIZE)
        val boardBackgroundColor = a.getColor(R.styleable.HexView_boardBackgroundColor, Color.GRAY)
        val agent1Color = a.getColor(R.styleable.HexView_agent1Color, Color.RED)
        val agent2Color = a.getColor(R.styleable.HexView_agent2Color, Color.BLUE)
        val connectionColor = a.getColor(R.styleable.HexView_connectionColor, Color.WHITE)
        boardOutlineColor = a.getColor(R.styleable.HexView_boardOutlineColor, boardOutlineColor)
        lineColor = a.getColor(R.styleable.HexView_lineColor, lineColor)

        a.recycle()

        paletteColors.add(boardBackgroundColor)
        paletteColors.add(agent1Color)
        paletteColors.add(agent2Color)
        paletteColors.add(connectionColor)

        for (i in 1..boardSize) {
            val temp = ArrayList<PointF>()
            for (j in 1..boardSize) {
                temp.add(null)
            }
            locations.add(temp)
        }
        locationColors = Array(boardSize, { ByteArray(boardSize) })
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (isEnabled()) {
            val x = ev.getX()
            val y = ev.getY()
            when (ev.getAction()) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    var newSelected: Point? = null
                    for (i in 0..boardSize - 1) {
                        for (j in 0..boardSize - 1) {
                            val point = locations[i][j]
                            if (Math.hypot((point.x - x).toDouble(), (point.y - y).toDouble()) < hexagonCRadius) {
                                newSelected = Point(i, j)
                            }
                        }
                    }
                    if (selectedHex == null || selectedHex != newSelected) {
                        selectedHex = newSelected
                        invalidate()
                    }
                }
                MotionEvent.ACTION_UP -> if (selectedHex != null) {
                    selectActionListener.onActionSelected(HexAction.valueOf(selectedHex!!.x, selectedHex!!.y))
                    selectedHex = null
                    invalidate()
                }
            }
            return true
        }
        return false
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        val paddingLeft = getPaddingLeft()
        val paddingTop = getPaddingTop()
        val paddingRight = getPaddingRight()
        val paddingBottom = getPaddingBottom()
        contentWidth = width - paddingLeft - paddingRight
        contentHeight = height - paddingTop - paddingBottom

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
        lineWidth = hexagonRadius * LINE_WIDTH_RATIO

        val x0 = xPadding + hexagonRadius
        val y0 = yAdjust + yPadding + (PathUtils.HEXAGON_SHORT_RADIUS * hexagonRadius)
        for (i in 0..boardSize - 1) {
            for (j in 0..boardSize - 1) {
                locations[i][j] = PointF(x0 + i * hexagonRadius * 1.5f, y0 - i * hexagonCRadius + j * 2 * hexagonCRadius)
            }
        }
    }

    private fun getCorner(location: PointF, xChange: Int, yChange: Int): PointF {
        return PointF((location.x + xChange * PathUtils.HEXAGON_HALF_RADIUS * hexagonRadius), (location.y + yChange * PathUtils.HEXAGON_SHORT_RADIUS * hexagonRadius))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val matrix = Matrix()
        //draw board outer edges
        paint.setStyle(Paint.Style.FILL)
        paint.setStrokeWidth(lineWidth)
        paint.setColor(paletteColors.get(AGENT1_COLOR_VALUE))
        var p1 = getCorner(locations[0][0], -2, 0)
        var p2 = locations[0][boardSize - 1]
        canvas.drawRect(p1.x, p1.y, p2.x, p2.y, paint)
        p1 = getCorner(locations[boardSize - 1][0], 2, 0)
        p2 = locations[boardSize - 1][boardSize - 1]
        canvas.drawRect(p2.x, p1.y, p1.x, p2.y, paint)

        paint.setColor(paletteColors.get(AGENT2_COLOR_VALUE))
        p1 = getCorner(locations[0][0], -1, -1)
        p2 = getCorner(locations[boardSize - 1][0], -1, -1)
        var p3 = locations[boardSize - 1][0]
        var p4 = locations[0][0]
        path.reset()
        path.moveTo(p1.x, p1.y)
        path.lineTo(p2.x, p2.y)
        path.lineTo(p3.x, p3.y)
        path.lineTo(p4.x, p4.y)
        path.close()
        canvas.drawPath(path, paint)
        p1 = getCorner(locations[0][boardSize - 1], 1, 1)
        p2 = getCorner(locations[boardSize - 1][boardSize - 1], 1, 1)
        p3 = locations[boardSize - 1][boardSize - 1]
        p4 = locations[0][boardSize - 1]
        path.reset()
        path.moveTo(p1.x, p1.y)
        path.lineTo(p2.x, p2.y)
        path.lineTo(p3.x, p3.y)
        path.lineTo(p4.x, p4.y)
        path.close()
        canvas.drawPath(path, paint)

        // draw board background
        paint.setStyle(Paint.Style.FILL)
        for (i in 0..boardSize - 1) {
            for (j in 0..boardSize - 1) {
                paint.setColor(paletteColors.get(locationColors[i][j].toInt()))
                val temp = Path()
                val point = locations[i][j]
                matrix.reset()
                matrix.postTranslate(point.x - hexagonRadius, point.y - hexagonCRadius)
                hexagon!!.transform(matrix, temp)
                canvas.drawPath(temp, paint)
            }
        }
        // draw board inner edges
        paint.setStyle(Paint.Style.STROKE)
        paint.setColor(boardOutlineColor)
        //paint.setStrokeWidth(lineWidth);
        for (row in locations!!) {
            for (point in row) {
                val temp = Path()
                matrix.reset()
                matrix.postTranslate(point.x - hexagonRadius, point.y - hexagonCRadius)
                hexagon!!.transform(matrix, temp)
                canvas.drawPath(temp, paint)
            }
        }
        // draw selection lines
        if (selectedHex != null) {
            val y = selectedHex!!.y
            val x = selectedHex!!.x
            paint.setStyle(Paint.Style.STROKE)
            paint.setColor(lineColor)
            paint.setStrokeWidth(4.toFloat())
            val p1i = locations[x][0]
            val p1f = locations[x][locations.size() - 1]
            val p2i = locations[0][y]
            val p2f = locations[locations.size() - 1][y]
            canvas.drawLine(p1i.x, p1i.y, p1f.x, p1f.y, paint)
            canvas.drawLine(p2i.x, p2i.y, p2f.x, p2f.y, paint)
        }
    }
}