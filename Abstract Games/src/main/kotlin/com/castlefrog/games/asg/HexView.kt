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

    private var locationColors: Array<ByteArray>? = null
    private val paletteColors = ArrayList<Int>()
    public var myBoardOutlineColor: Int = Color.WHITE
        private set
    private var lineColor = Color.BLACK

    private var myPaddingLeft: Int = 0
    private var myPaddingTop: Int = 0
    private var myPaddingRight: Int = 0
    private var myPaddingBottom: Int = 0
    private var contentWidth: Int = 0
    private var contentHeight: Int = 0
    private val paint = Paint()
    private val path = Path()

    public var myBoardSize: Int = 0
        private set
    private val boardStyle = HexBoardStyle.HEXAGONS
    private var selectedHex: Point? = null
    private val myMatrix = Matrix()
    var selectActionListener: SelectActionListener<HexAction> = DummySelectActionListener()

    private inner class DummySelectActionListener : SelectActionListener<HexAction> {
        override fun onActionSelected(action: HexAction) {}
    }

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

        myBoardSize = a.getInt(R.styleable.HexView_boardSize, DEFAULT_BOARD_SIZE)
        val boardBackgroundColor = a.getColor(R.styleable.HexView_boardBackgroundColor, Color.GRAY)
        val agent1Color = a.getColor(R.styleable.HexView_agent1Color, Color.RED)
        val agent2Color = a.getColor(R.styleable.HexView_agent2Color, Color.BLUE)
        val connectionColor = a.getColor(R.styleable.HexView_connectionColor, Color.WHITE)
        myBoardOutlineColor = a.getColor(R.styleable.HexView_boardOutlineColor, myBoardOutlineColor)
        lineColor = a.getColor(R.styleable.HexView_lineColor, lineColor)

        a.recycle()

        paletteColors.add(boardBackgroundColor)
        paletteColors.add(agent1Color)
        paletteColors.add(agent2Color)
        paletteColors.add(connectionColor)

        for (i in 1..myBoardSize) {
            val temp = ArrayList<PointF>()
            for (j in 1..myBoardSize) {
                temp.add(null)
            }
            locations.add(temp)
        }
        locationColors = Array(myBoardSize, { ByteArray(myBoardSize) })
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (isEnabled()) {
            val x = ev.getX()
            val y = ev.getY()
            when (ev.getAction()) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    var newSelected: Point? = null
                    run {
                        var i = 0
                        while (i < myBoardSize) {
                            run {
                                var j = 0
                                while (j < myBoardSize) {
                                    val point = locations!![i][j]
                                    if (Math.hypot((point.x - x).toDouble(), (point.y - y).toDouble()) < hexagonCRadius) {
                                        newSelected = Point(i, j)
                                    }
                                    j += 1
                                }
                            }
                            i += 1
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
        }
        return true
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        myPaddingLeft = getPaddingLeft()
        myPaddingTop = getPaddingTop()
        myPaddingRight = getPaddingRight()
        myPaddingBottom = getPaddingBottom()
        contentWidth = width - myPaddingLeft - myPaddingRight
        contentHeight = height - myPaddingTop - myPaddingBottom

        val maxHexWidth = contentWidth.toFloat() / (0.75.toFloat() * myBoardSize.toFloat() + 0.25.toFloat())
        val maxHexHeight = ((2 * contentHeight) / (3 * myBoardSize - 1)).toFloat()
        hexagonRadius = Math.min(maxHexWidth, maxHexHeight / PathUtils.HEXAGON_SHORT_RADIUS) / 2
        hexagonCRadius = PathUtils.HEXAGON_SHORT_RADIUS * hexagonRadius
        val boardWidth = hexagonRadius * (1.5.toFloat() * myBoardSize.toFloat() + 0.5.toFloat())
        val boardHeight = hexagonCRadius * (3 * myBoardSize - 1).toFloat()
        val xPadding = (width.toFloat() - boardWidth) / 2
        val yPadding = (height.toFloat() - boardHeight) / 2
        val yAdjust = hexagonCRadius * (myBoardSize - 1).toFloat()
        hexagon = PathUtils.getHexagon(hexagonRadius)
        lineWidth = hexagonRadius * LINE_WIDTH_RATIO

        val x0 = xPadding + hexagonRadius
        val y0 = yAdjust + yPadding + (PathUtils.HEXAGON_SHORT_RADIUS * hexagonRadius)
        run {
            var i = 0
            while (i < myBoardSize) {
                run {
                    var j = 0
                    while (j < myBoardSize) {
                        locations[i][j] = PointF(x0 + i.toFloat() * hexagonRadius * 1.5.toFloat(), y0 - i.toFloat() * hexagonCRadius + j.toFloat() * 2 * hexagonCRadius)
                        j += 1
                    }
                }
                i += 1
            }
        }
    }

    private fun getCorner(location: PointF, xChange: Int, yChange: Int): PointF {
        return PointF((location.x + xChange.toFloat() * PathUtils.HEXAGON_HALF_RADIUS * hexagonRadius), (location.y + yChange.toFloat() * PathUtils.HEXAGON_SHORT_RADIUS * hexagonRadius))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //draw board outer edges
        paint.setStyle(Paint.Style.FILL)
        paint.setStrokeWidth(lineWidth)
        paint.setColor(paletteColors.get(AGENT1_COLOR_VALUE))
        var p1 = getCorner(locations!![0][0], -2, 0)
        var p2 = locations!![0][myBoardSize - 1]
        canvas.drawRect(p1.x, p1.y, p2.x, p2.y, paint)
        p1 = getCorner(locations!![myBoardSize - 1][0], 2, 0)
        p2 = locations!![myBoardSize - 1][myBoardSize - 1]
        canvas.drawRect(p2.x, p1.y, p1.x, p2.y, paint)

        paint.setColor(paletteColors.get(AGENT2_COLOR_VALUE))
        p1 = getCorner(locations!![0][0], -1, -1)
        p2 = getCorner(locations!![myBoardSize - 1][0], -1, -1)
        var p3 = locations!![myBoardSize - 1][0]
        var p4 = locations!![0][0]
        path.reset()
        path.moveTo(p1.x, p1.y)
        path.lineTo(p2.x, p2.y)
        path.lineTo(p3.x, p3.y)
        path.lineTo(p4.x, p4.y)
        path.close()
        canvas.drawPath(path, paint)
        p1 = getCorner(locations!![0][myBoardSize - 1], 1, 1)
        p2 = getCorner(locations!![myBoardSize - 1][myBoardSize - 1], 1, 1)
        p3 = locations!![myBoardSize - 1][myBoardSize - 1]
        p4 = locations!![0][myBoardSize - 1]
        path.reset()
        path.moveTo(p1.x, p1.y)
        path.lineTo(p2.x, p2.y)
        path.lineTo(p3.x, p3.y)
        path.lineTo(p4.x, p4.y)
        path.close()
        canvas.drawPath(path, paint)

        // draw board background
        paint.setStyle(Paint.Style.FILL)
        run {
            var i = 0
            while (i < myBoardSize) {
                run {
                    var j = 0
                    while (j < myBoardSize) {
                        paint.setColor(paletteColors.get(locationColors!![i][j].toInt()))
                        val temp = Path()
                        val point = locations!![i][j]
                        myMatrix.reset()
                        myMatrix.postTranslate(point.x - hexagonRadius, point.y - hexagonCRadius)
                        hexagon!!.transform(myMatrix, temp)
                        canvas.drawPath(temp, paint)
                        j += 1
                    }
                }
                i += 1
            }
        }
        // draw board inner edges
        paint.setStyle(Paint.Style.STROKE)
        paint.setColor(myBoardOutlineColor)
        //paint.setStrokeWidth(lineWidth);
        for (row in locations!!) {
            for (point in row) {
                val temp = Path()
                myMatrix.reset()
                myMatrix.postTranslate(point.x - hexagonRadius, point.y - hexagonCRadius)
                hexagon!!.transform(myMatrix, temp)
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
            val p1i = locations!![x][0]
            val p1f = locations!![x][locations!!.size() - 1]
            val p2i = locations!![0][y]
            val p2f = locations!![locations!!.size() - 1][y]
            canvas.drawLine(p1i.x, p1i.y, p1f.x, p1f.y, paint)
            canvas.drawLine(p2i.x, p2i.y, p2f.x, p2f.y, paint)
        }
    }

    public fun setBoardSize(boardSize: Int) {
        this.myBoardSize = boardSize
        invalidate()
    }

    public fun getBoardBackgroundColor(): Int {
        return paletteColors.get(BACKGROUND_COLOR_VALUE)
    }

    public fun setBoardBackgroundColor(boardBackgroundColor: Int) {
        paletteColors.set(BACKGROUND_COLOR_VALUE, boardBackgroundColor)
        invalidate()
    }

    public fun getAgent1Color(): Int {
        return paletteColors.get(AGENT1_COLOR_VALUE)
    }

    public fun setAgent1Color(agent1Color: Int) {
        paletteColors.set(AGENT1_COLOR_VALUE, agent1Color)
        invalidate()
    }

    public fun getAgent2Color(): Int {
        return paletteColors.get(AGENT2_COLOR_VALUE)
    }

    public fun setAgent2Color(agent2Color: Int) {
        paletteColors.set(AGENT2_COLOR_VALUE, agent2Color)
        invalidate()
    }

    public fun getConnectionColor(): Int {
        return paletteColors.get(CONNECTION_COLOR_VALUE)
    }

    public fun setConnectionColor(connectionColor: Int) {
        paletteColors.set(CONNECTION_COLOR_VALUE, connectionColor)
        invalidate()
    }

    public fun setBoardOutlineColor(boardOutlineColor: Int) {
        this.myBoardOutlineColor = boardOutlineColor
        invalidate()
    }

}