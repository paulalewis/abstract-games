package com.castlefrog.games.asg

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*

class HavannahView : View {

    companion object {
        private val DEFAULT_BOARD_SIZE = 5
        private val LINE_WIDTH_RATIO = 0.1f
    }

    private val locations: Vector<Vector<PointF>> = Vector()
    private var hexagon: Path = Path()
    /** distance from center to corner  */
    private var hexagonRadius: Float = 0f
    private var hexagonCRadius: Float = 0f
    private var boardWidth: Float = 0f
    private var boardHeight: Float = 0f
    private var selectedHex: Point? = null

    private var boardSize: Int = 0
    private var locationColors: Array<ByteArray> = Array(0, { ByteArray(0) })
    private val paletteColors: MutableMap<Byte, Int> = HashMap()
    private var boardOutlineColor = Color.WHITE
    private var lineColor = Color.BLACK

    private val paint = Paint()
    private var contentWidth: Int = 0
    private var contentHeight: Int = 0
    private var lineWidth: Float = 0f

    //public var selectActionListener: SelectActionListener<HavannahAction> = DummySelectActionListener()

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
        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.HexGridView, defStyle, 0)

        boardSize = a.getInt(R.styleable.HexGridView_boardSize, DEFAULT_BOARD_SIZE)
        val boardBackgroundColor = a.getColor(R.styleable.HexGridView_boardBackgroundColor, Color.GRAY)
        val agent1Color = a.getColor(R.styleable.HexGridView_agent1Color, Color.RED)
        val agent2Color = a.getColor(R.styleable.HexGridView_agent2Color, Color.BLUE)
        val connectionColor = a.getColor(R.styleable.HexGridView_connectionColor, Color.WHITE)
        boardOutlineColor = a.getColor(R.styleable.HexGridView_boardOutlineColor, boardOutlineColor)
        lineColor = a.getColor(R.styleable.HexGridView_lineColor, lineColor)

        a.recycle()

        paletteColors.put(0, boardBackgroundColor)
        paletteColors.put(1, agent1Color)
        paletteColors.put(2, agent2Color)
        paletteColors.put(3, lineColor)
        paletteColors.put(4, connectionColor)

        locations.setSize(boardSize)
        val sideLength = (boardSize + 1) / 2
        for (i in 0..sideLength - 1) {
            locations[i] = Vector<PointF>()
            locations[i].setSize(sideLength + i)
        }
        for (i in sideLength..boardSize - 1) {
            locations[i] = Vector<PointF>()
            locations[i].setSize(boardSize + sideLength - i - 1)
        }

        locationColors = Array(boardSize, { ByteArray(boardSize) })
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (isEnabled) {
            val x = ev.x
            val y = ev.y
            when (ev.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    var newSelected: Point? = null
                    for (i in 0..locations.size - 1) {
                        for (j in 0..locations[i].size - 1) {
                            val point = locations[i][j]
                            val dx = point.x - x
                            val dy = point.y - y
                            if (Math.hypot(dx.toDouble(), dy.toDouble()) < hexagonCRadius) {
                                var dj = j
                                if (i > locations.size / 2) {
                                    dj += i - locations.size / 2
                                }
                                newSelected = Point(i, dj)
                            }
                        }
                    }
                    if (selectedHex == null || selectedHex != newSelected) {
                        selectedHex = null
                        invalidate()
                    }
                }
                MotionEvent.ACTION_UP -> if (selectedHex != null) {
                    //selectActionListener.onActionSelected(HavannahAction.valueOf(selectedHex!!.x, selectedHex!!.y))
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
        contentWidth = width - (paddingLeft + paddingRight)
        contentHeight = height - (paddingTop + paddingBottom)

        // pre-calc for line width
        boardWidth = Math.min(contentWidth, contentHeight).toFloat()
        boardHeight = boardWidth * (2 * PathUtils.HEXAGON_SHORT_RADIUS * boardSize) / (PathUtils.HEXAGON_RADIUS * (1.5f * boardSize + 0.5f))
        hexagonCRadius = boardHeight / (2 * boardSize)
        hexagonRadius = hexagonCRadius / PathUtils.HEXAGON_SHORT_RADIUS
        lineWidth = hexagonRadius * LINE_WIDTH_RATIO

        // re-calc values with known line width
        boardWidth = Math.min(contentWidth, contentHeight).toFloat() - lineWidth
        boardHeight = boardWidth * (2 * PathUtils.HEXAGON_SHORT_RADIUS * boardSize) / (PathUtils.HEXAGON_RADIUS * (1.5f * boardSize + 0.5f))
        hexagonCRadius = boardHeight / (2 * boardSize)
        hexagonRadius = hexagonCRadius / PathUtils.HEXAGON_SHORT_RADIUS

        hexagon = PathUtils.getHexagon(hexagonRadius)

        val xPadding = paddingLeft + (width - boardWidth) / 2
        val yPadding = paddingTop + (height - boardHeight) / 2

        val x0 = xPadding + boardWidth / 2
        val y0 = yPadding + hexagonCRadius

        for (i in 0..boardSize / 2) {
            for (j in 0..locations[i].size - 1) {
                locations[i][j] = PointF(x0 + (j - i) * 1.5f * hexagonRadius, y0 + (i + j) * hexagonCRadius)
            }
        }

        val x02 = x0 - (boardSize / 2).toFloat() * hexagonRadius * 3 / 2
        val y02 = y0 + (boardSize / 2).toFloat() * hexagonCRadius
        for (i in boardSize / 2 + 1..boardSize - 1) {
            for (j in 0..locations[i].size - 1) {
                locations[i][j] = PointF(x02 + j * 1.5f * hexagonRadius, y02 + (2 * (i - boardSize / 2) + j) * hexagonCRadius)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //draw board
        val matrix = Matrix()
        for (i in 0..locations.size - 1) {
            for (j in 0..locations[i].size - 1) {
                paint.style = Paint.Style.FILL
                val point = locations[i][j]
                val temp = Path()
                matrix.reset()
                matrix.postTranslate(point.x - hexagonRadius, point.y - hexagonCRadius)
                hexagon.transform(matrix, temp)
                if (i > locations.size / 2) {
                    val dj = j + i - locations.size / 2
                    paint.color = paletteColors[locationColors[i][dj]] ?: 0
                } else {
                    paint.color = paletteColors[locationColors[i][j]] ?: 0
                }
                canvas.drawPath(temp, paint)
                //draw board outline
                paint.style = Paint.Style.STROKE
                paint.color = boardOutlineColor
                paint.strokeWidth = lineWidth
                paint.strokeJoin = Paint.Join.ROUND
                canvas.drawPath(temp, paint)
            }
        }
        //draw selectedHex lines
        if (selectedHex != null) {
            val y = selectedHex!!.x
            val x = selectedHex!!.y
            paint.style = Paint.Style.STROKE
            paint.color = Color.BLACK
            paint.strokeWidth = 4f
            val p1i = locations[x][0]
            val p1f = locations[x][locations[x].size - 1]
            val p2i = locations[Math.max(0, y - locations.size / 2)][y]
            val nx = Math.min(locations.size - 1, y + locations.size / 2)
            val ny = Math.max(0, y - locations.size / 2)
            val p2f = locations[nx][ny]
            canvas.drawLine(p1i.x, p1i.y, p1f.x, p1f.y, paint)
            canvas.drawLine(p2i.x, p2i.y, p2f.x, p2f.y, paint)
        }
    }
}