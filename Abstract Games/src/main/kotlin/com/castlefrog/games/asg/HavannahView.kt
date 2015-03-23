package com.castlefrog.games.asg

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.castlefrog.agl.domains.havannah.HavannahAction
import java.util.ArrayList
import java.util.Vector

public class HavannahView : View {

    companion object {
        private val DEFAULT_BOARD_SIZE = 5
        private val LINE_WIDTH_RATIO = 0.1f
    }

    private var locations: Vector<Vector<PointF>>? = null
    private var hexagon: Path? = null
    /** distance from center to corner  */
    private var hexagonRadius: Float = 0f
    private var hexagonCRadius: Float = 0f
    private var boardWidth: Float = 0f
    private var boardHeight: Float = 0f
    private var selectedHex: Point? = null

    private var boardSize: Int = 0
    //private Vector<Vector<PointF>> locationsColors;
    private var locationColors: Array<ByteArray>? = null
    private val paletteColors = ArrayList<Int>()
    private var boardOutlineColor = Color.WHITE
    private var lineColor = Color.BLACK

    private val paint = Paint()
    private val myMatrix = Matrix()
    private var myPaddingLeft: Int = 0
    private var myPaddingTop: Int = 0
    private var myPaddingRight: Int = 0
    private var myPaddingBottom: Int = 0
    private var contentWidth: Int = 0
    private var contentHeight: Int = 0
    private var lineWidth: Float = 0f

    private val boardStyle = HexBoardStyle.HEXAGONS
    private var selectActionListener: SelectActionListener<HavannahAction> = DummySelectActionListener()

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

        locations = Vector<Vector<PointF>>()
        locations!!.setSize(boardSize)
        val sideLength = (boardSize + 1) / 2
        run {
            var i = 0
            while (i < sideLength) {
                locations!!.set(i, Vector<PointF>())
                locations!!.get(i).setSize(sideLength + i)
                i += 1
            }
        }
        run {
            var i = sideLength
            while (i < boardSize) {
                locations!!.set(i, Vector<PointF>())
                locations!!.get(i).setSize(boardSize + sideLength - i - 1)
                i += 1
            }
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
                    run {
                        var i = 0
                        while (i < locations!!.size()) {
                            run {
                                var j = 0
                                while (j < locations!!.get(i).size()) {
                                    val point = locations!!.get(i).get(j)
                                    val dx = point.x - x
                                    val dy = point.y - y
                                    if (Math.hypot(dx.toDouble(), dy.toDouble()) < hexagonCRadius) {
                                        var dj = j
                                        if (i > locations!!.size() / 2) {
                                            dj += i - locations!!.size() / 2
                                        }
                                        newSelected = Point(i, dj)
                                    }
                                    j += 1
                                }
                            }
                            i += 1
                        }
                    }
                    if (selectedHex == null || selectedHex != newSelected) {
                        selectedHex = null
                        invalidate()
                    }
                }
                MotionEvent.ACTION_UP -> if (selectedHex != null) {
                    selectActionListener.onActionSelected(HavannahAction.valueOf(selectedHex!!.x, selectedHex!!.y))
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

        boardWidth = Math.min(contentWidth, contentHeight).toFloat()
        boardHeight = boardWidth * (2 * PathUtils.HEXAGON_SHORT_RADIUS * boardSize.toFloat()) / (PathUtils.HEXAGON_RADIUS * (1.5.toFloat() * boardSize.toFloat() + 0.5.toFloat()))
        hexagonCRadius = boardHeight / (2 * boardSize).toFloat()
        hexagonRadius = hexagonCRadius / PathUtils.HEXAGON_SHORT_RADIUS
        hexagon = PathUtils.getHexagon(hexagonRadius)
        lineWidth = hexagonRadius * LINE_WIDTH_RATIO
        val xPadding = (width.toFloat() - boardWidth) / 2
        val yPadding = (height.toFloat() - boardHeight) / 2

        val x0 = xPadding + boardWidth / 2
        val y0 = yPadding + hexagonCRadius

        run {
            var i = 0
            while (i < boardSize / 2 + 1) {
                run {
                    var j = 0
                    while (j < locations!!.get(i).size()) {
                        locations!!.get(i).set(j, PointF(x0 + (j - i).toFloat() * 1.5.toFloat() * hexagonRadius, y0 + (i + j).toFloat() * hexagonCRadius))
                        j += 1
                    }
                }
                i += 1
            }
        }
        val x02 = x0 - (boardSize / 2).toFloat() * hexagonRadius * 3 / 2
        val y02 = y0 + (boardSize / 2).toFloat() * hexagonCRadius
        run {
            var i = boardSize / 2 + 1
            while (i < boardSize) {
                run {
                    var j = 0
                    while (j < locations!!.get(i).size()) {
                        locations!!.get(i).set(j, PointF(x02 + j.toFloat() * 1.5.toFloat() * hexagonRadius, y02 + (2 * (i - boardSize / 2) + j).toFloat() * hexagonCRadius))
                        j += 1
                    }
                }
                i += 1
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //draw board
        run {
            var i = 0
            while (i < locations!!.size()) {
                run {
                    var j = 0
                    while (j < locations!!.get(i).size()) {
                        paint.setStyle(Paint.Style.FILL)
                        val point = locations!!.get(i).get(j)
                        val temp = Path()
                        myMatrix.reset()
                        myMatrix.postTranslate(point.x - hexagonRadius, point.y - hexagonCRadius)
                        hexagon!!.transform(myMatrix, temp)
                        if (i > locations!!.size() / 2) {
                            val dj = j + i - locations!!.size() / 2
                            paint.setColor(paletteColors.get(locationColors!![i][dj].toInt()))
                        } else {
                            paint.setColor(paletteColors.get(locationColors!![i][j].toInt()))
                        }
                        canvas.drawPath(temp, paint)
                        //draw board outline
                        paint.setStyle(Paint.Style.STROKE)
                        paint.setColor(boardOutlineColor)
                        paint.setStrokeWidth(lineWidth)
                        canvas.drawPath(temp, paint)
                        j += 1
                    }
                }
                i += 1
            }
        }
        //draw selectedHex lines
        if (selectedHex != null) {
            val y = selectedHex!!.x
            val x = selectedHex!!.y
            paint.setStyle(Paint.Style.STROKE)
            paint.setColor(Color.BLACK)
            paint.setStrokeWidth(4.toFloat())
            val p1i = locations!!.get(x).get(0)
            val p1f = locations!!.get(x).get(locations!!.get(x).size() - 1)
            val p2i = locations!!.get(Math.max(0, y - locations!!.size() / 2)).get(y)
            val nx = Math.min(locations!!.size() - 1, y + locations!!.size() / 2)
            val ny = Math.max(0, y - locations!!.size() / 2)
            val p2f = locations!!.get(nx).get(ny)
            canvas.drawLine(p1i.x, p1i.y, p1f.x, p1f.y, paint)
            canvas.drawLine(p2i.x, p2i.y, p2f.x, p2f.y, paint)
        }
    }
}