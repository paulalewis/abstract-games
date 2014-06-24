package com.castlefrog.games.asg;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public final class HavannahView extends View {
    private static final int SCALE = 100;

    private Vector<Vector<PointF>> locations_;
    private ShapeDrawable hexagon;
    /** distance from center to corner */
    private float hexagonRadius_;
    private float hexagonCRadius_;
    private float boardWidth_;
    private float boardHeight_;
    private Point selectedHex;

    private int boardSize;
    private byte[][] locationColors;
    private List<Integer> paletteColors = new ArrayList<>();
    private int boardOutlineColor = Color.WHITE;
    private int lineColor = Color.BLACK;

    private Paint paint_ = new Paint();
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private int contentWidth;
    private int contentHeight;

    private HexBoardStyle boardStyle = HexBoardStyle.HEXAGONS;
    private SelectActionListener selectActionListener;

    public interface SelectActionListener {
        void onActionSelected(int x, int y);
    }

    private class DummySelectActionListener implements SelectActionListener {
        public void onActionSelected(int x, int y) {}
    }

    public HavannahView(Context context) {
        super(context);
        init(null, 0);
    }

    public HavannahView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public HavannahView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HexView, defStyle, 0);

        boardSize = a.getInt(R.styleable.HexView_boardSize, boardSize);
        int boardBackgroundColor = a.getColor(R.styleable.HexView_boardBackgroundColor, Color.GRAY);
        int agent1Color = a.getColor(R.styleable.HexView_agent1Color, Color.RED);
        int agent2Color = a.getColor(R.styleable.HexView_agent2Color, Color.BLUE);
        int connectionColor = a.getColor(R.styleable.HexView_connectionColor, Color.WHITE);
        boardOutlineColor = a.getColor(R.styleable.HexView_boardOutlineColor, boardOutlineColor);
        lineColor = a.getColor(R.styleable.HexView_lineColor, lineColor);

        a.recycle();

        paletteColors.add(boardBackgroundColor);
        paletteColors.add(agent1Color);
        paletteColors.add(agent2Color);
        paletteColors.add(connectionColor);

        hexagon = new ShapeDrawable(new PathShape(Utils.getHexagon(SCALE), 2 * SCALE, 2 * SCALE * Utils.HEXAGON_SHORT_RADIUS));
        hexagon.getPaint().setAntiAlias(true);
        hexagon.getPaint().setStrokeWidth(0.1f * SCALE);

        locations_ = new Vector<>();
        locations_.setSize(boardSize);
        int sideLength = (boardSize + 1) / 2;
        for (int i = 0; i < sideLength; i += 1) {
            locations_.set(i, new Vector<PointF>());
            locations_.get(i).setSize(sideLength + i);
        }
        for (int i = sideLength; i < boardSize; i += 1) {
            locations_.set(i, new Vector<PointF>());
            locations_.get(i).setSize(boardSize + sideLength - i - 1);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isEnabled()) {
            float x = ev.getX();
            float y = ev.getY();
            switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                Point newSelected = null;
                for (int i = 0; i < locations_.size(); i += 1) {
                    for (int j = 0; j < locations_.get(i).size(); j += 1) {
                        PointF point = locations_.get(i).get(j);
                        float dx = point.x - x;
                        float dy = point.y - y;
                        if (Math.hypot(dx, dy) < hexagonCRadius_) {
                            int dj = j;
                            if (i > locations_.size() / 2) {
                                dj += i - locations_.size() / 2;
                            }
                            newSelected = new Point(i, dj);
                        }
                    }
                }
                if (selectedHex == null || !selectedHex.equals(newSelected)) {
                    selectedHex = null;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (selectedHex != null) {
                    selectActionListener.onActionSelected(selectedHex.x, selectedHex.y);
                    selectedHex = null;
                    invalidate();
                }
                break;
            }
        }
        return true;
    }

    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
        contentWidth = width - paddingLeft - paddingRight;
        contentHeight = height - paddingTop - paddingBottom;

        boardWidth_ = Math.min(contentWidth, contentHeight);
        boardHeight_ = boardWidth_ * (2 * Utils.HEXAGON_SHORT_RADIUS * boardSize) / (Utils.HEXAGON_RADIUS * (1.5f * boardSize + 0.5f));
        hexagonCRadius_ = boardHeight_ / (2 * boardSize);
        hexagonRadius_ = hexagonCRadius_ / Utils.HEXAGON_SHORT_RADIUS;
        float xPadding = (width - boardWidth_) / 2;
        float yPadding = (height - boardHeight_) / 2;

        float x0 = xPadding + boardWidth_ / 2;
        float y0 = yPadding + hexagonCRadius_;

        for (int i = 0; i < boardSize / 2 + 1; i += 1) {
            for (int j = 0; j < locations_.get(i).size(); j += 1) {
                locations_.get(i).set(j, new PointF(x0 + (j - i) * 1.5f * hexagonRadius_, y0 + (i + j) * hexagonCRadius_));
            }
        }
        float x02 = x0 - (boardSize / 2) * hexagonRadius_ * 3 / 2;
        float y02 = y0 + (boardSize / 2) * hexagonCRadius_;
        for (int i = boardSize / 2 + 1; i < boardSize; i += 1) {
            for (int j = 0; j < locations_.get(i).size(); j += 1) {
                locations_.get(i).set(j, new PointF(x02 + j * 1.5f * hexagonRadius_, y02 + (2 * (i - boardSize / 2) + j) * hexagonCRadius_));
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw board
        for (int i = 0; i < locations_.size(); i += 1) {
            for (int j = 0; j < locations_.get(i).size(); j += 1) {
                hexagon.getPaint().setStyle(Paint.Style.FILL);
                PointF point = locations_.get(i).get(j);
                hexagon.setBounds((int) (point.x - hexagonRadius_),
                                  (int) (point.y - hexagonCRadius_),
                                  (int) (point.x + hexagonRadius_),
                                  (int) (point.y + hexagonCRadius_));
                if (i > locations_.size() / 2) {
                    int dj = j + i - locations_.size() / 2;
                    //hexagon.getPaint().setColor(paletteColors.get(state.getLocation(i, dj)));
                } else {
                    //hexagon.getPaint().setColor(paletteColors.get(state.getLocation(i, j)));
                }
                hexagon.draw(canvas);
                //draw board outline
                hexagon.getPaint().setStyle(Paint.Style.STROKE);
                hexagon.getPaint().setColor(boardOutlineColor);
                hexagon.getPaint().setStrokeWidth(0.1f * SCALE);
                hexagon.draw(canvas);
            }
        }
        //draw selectedHex lines
        if (selectedHex != null) {
            int y = selectedHex.x;
            int x = selectedHex.y;
            paint_.setStyle(Paint.Style.STROKE);
            paint_.setColor(Color.BLACK);
            paint_.setStrokeWidth(4f);
            PointF p1i = locations_.get(x).get(0);
            PointF p1f = locations_.get(x).get(locations_.get(x).size() - 1);
            PointF p2i = locations_.get(Math.max(0, y - locations_.size() / 2)).get(y);
            int nx = Math.min(locations_.size() - 1, y + locations_.size() / 2);
            int ny = Math.max(0, y - locations_.size() / 2);
            PointF p2f = locations_.get(nx).get(ny);
            canvas.drawLine(p1i.x, p1i.y, p1f.x, p1f.y, paint_);
            canvas.drawLine(p2i.x, p2i.y, p2f.x, p2f.y, paint_);
        }
    }
}
