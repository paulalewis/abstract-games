package com.castlefrog.games.asg;

import java.util.List;

import android.app.Activity;
import android.graphics.Path;
import android.view.View;
import android.view.MotionEvent;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.PointF;

import com.castlefrog.agl.Arbiter;
import com.castlefrog.agl.Agent;
import com.castlefrog.agl.agents.ExternalAgent;
import com.castlefrog.agl.domains.hex.HexAction;
import com.castlefrog.agl.domains.hex.HexState;
import com.castlefrog.agl.domains.hex.HexSimulator;

public final class HexView extends View {
    private static final int WIN_COLOR = 0xffffffff;
    private static final int PADDING = 10;
    private static final int SCALE = 100;

    private PointF[][] locations_;
    private ShapeDrawable hexagon_;
    /** Distance from center to corner */
    private float hexagonRadius_;
    private float hexagonCRadius_;
    private HexAction selected_;
    private int padding_;

    private List<GameActivity.AgentColor> agentColors_;
    private int[] boardColors_;

    private Paint paint_ = new Paint();
    private Path path_ = new Path();

    private Arbiter<?, ?> arbiter_;

    public HexView(Context context) {
        super(context);

        final float scale = getResources().getDisplayMetrics().density;
        padding_ = (int) (PADDING * scale);
        arbiter_ = ((GameActivity) context).getArbiter();

        //Use an xml resource to recall selected board style and board color
        //SharedPreferences settings = context.getSharedPreferences("hex", Context.MODE_PRIVATE);
        boardColors_ = new int[] {0xffffffff, 0xffaaaaaa};
        agentColors_ = ((GameActivity) context).getAgentColors();

        hexagon_ = new ShapeDrawable(new PathShape(Utils.getHexagon(SCALE), 2 * SCALE, 2 * SCALE * Utils.HEXAGON_SHORT_RADIUS));
        hexagon_.getPaint().setAntiAlias(true);
        hexagon_.getPaint().setStrokeWidth(0.1f * SCALE);

        int size = ((HexSimulator) arbiter_.getWorld()).getState().getBoardSize();
        locations_ = new PointF[size][size];
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        View menuWindow = ((Activity) getContext()).findViewById(R.id.menuWindow);

        if (menuWindow.getVisibility() != View.VISIBLE) {
            switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < locations_.length; i += 1) {
                    for (int j = 0; j < locations_.length; j += 1) {
                        PointF point = locations_[i][j];
                        if (Math.hypot(point.x - x, point.y - y) < hexagonCRadius_) {
                            HexSimulator simulator = (HexSimulator) arbiter_.getWorld();
                            HexState state = simulator.getState();
                            HexAction action = HexAction.valueOf(i , j);
                            int agentTurn = state.getAgentTurn();
                            List<HexAction> legalActions = simulator.getLegalActions(agentTurn);
                            List<Agent> agents = arbiter_.getAgents();
                            if (agents.get(agentTurn) instanceof ExternalAgent && legalActions.contains(action)) {
                                if (selected_ == null || !selected_.equals(action)) {
                                    selected_ = action;
                                    invalidate();
                                }
                                return true;
                            }
                        }
                    }
                }
                if (selected_ != null) {
                    selected_ = null;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (selected_ != null) {
                    ((GameActivity) getContext()).inputAction(selected_);
                    selected_ = null;
                    invalidate();
                }
                break;
            default:
                break;
            }
        }
        return true;
    }

    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        int paddedWidth = width - padding_;
        int paddedHeight = height - padding_;

        HexSimulator simulator = (HexSimulator) arbiter_.getWorld();
        int boardSize = simulator.getState().getBoardSize();

        float maxHexWidth = paddedWidth / (0.75f * boardSize + 0.25f);
        float maxHexHeight = (2 * paddedHeight) / (3 * boardSize - 1);
        hexagonRadius_ = Math.min(maxHexWidth, maxHexHeight / Utils.HEXAGON_SHORT_RADIUS) / 2;
        hexagonCRadius_ = Utils.HEXAGON_SHORT_RADIUS * hexagonRadius_;
        float boardWidth = hexagonRadius_ * (1.5f * boardSize + 0.5f);
        float boardHeight = hexagonCRadius_ * (3 * boardSize - 1);
        float xPadding = (width - boardWidth) / 2;
        float yPadding = (height - boardHeight) / 2;
        float yAdjust = hexagonCRadius_ * (boardSize - 1);

        float x0 = xPadding + hexagonRadius_;
        float y0 = yAdjust + yPadding + (Utils.HEXAGON_SHORT_RADIUS * hexagonRadius_);
        for (int i = 0; i < boardSize; i += 1) {
            for (int j = 0; j < boardSize; j += 1) {
                locations_[i][j] = new PointF(x0 + i * hexagonRadius_ * 1.5f,
                                         y0 - i * hexagonCRadius_ + j * 2 * hexagonCRadius_);
            }
        }
    }

    private PointF getCorner(PointF location,
                             int xChange,
                             int yChange) {
        return new PointF((location.x + xChange * Utils.HEXAGON_HALF_RADIUS * hexagonRadius_),
                          (location.y + yChange * Utils.HEXAGON_SHORT_RADIUS * hexagonRadius_));
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        HexSimulator simulator = (HexSimulator) arbiter_.getWorld();
        HexState state = simulator.getState();
        int boardSize = state.getBoardSize();
        byte[][] locations = state.getLocations();

        //draw board outer edges
        paint_.setStyle(Paint.Style.FILL);
        paint_.setColor(agentColors_.get(0).intValue());
        PointF p1 = getCorner(locations_[0][0], -2, 0);
        PointF p2 = locations_[0][boardSize - 1];
        canvas.drawRect(p1.x, p1.y, p2.x, p2.y, paint_);
        p1 = getCorner(locations_[boardSize - 1][0], 2, 0);
        p2 = locations_[boardSize - 1][boardSize - 1];
        canvas.drawRect(p1.x, p1.y, p2.x, p2.y, paint_);

        paint_.setColor(agentColors_.get(1).intValue());
        p1 = getCorner(locations_[0][0], -1, -1);
        p2 = getCorner(locations_[boardSize - 1][0], -1, -1);
        PointF p3 = locations_[boardSize - 1][0];
        PointF p4 = locations_[0][0];
        path_.reset();
        path_.moveTo(p1.x, p1.y);
        path_.lineTo(p2.x, p2.y);
        path_.lineTo(p3.x, p3.y);
        path_.lineTo(p4.x, p4.y);
        path_.close();
        canvas.drawPath(path_, paint_);
        p1 = getCorner(locations_[0][boardSize - 1], 1, 1);
        p2 = getCorner(locations_[boardSize - 1][boardSize - 1], 1, 1);
        p3 = locations_[boardSize - 1][boardSize - 1];
        p4 = locations_[0][boardSize - 1];
        path_.reset();
        path_.moveTo(p1.x, p1.y);
        path_.lineTo(p2.x, p2.y);
        path_.lineTo(p3.x, p3.y);
        path_.lineTo(p4.x, p4.y);
        path_.close();
        canvas.drawPath(path_, paint_);

        //draw board background
        for (int i = 0; i < locations.length; i += 1) {
            for (int j = 0; j < locations[i].length; j += 1) {
                hexagon_.getPaint().setStyle(Paint.Style.FILL);
                PointF point = locations_[i][j];
                hexagon_.setBounds((int) (point.x - hexagonRadius_),
                                   (int) (point.y - hexagonCRadius_),
                                   (int) (point.x + hexagonRadius_),
                                   (int) (point.y + hexagonCRadius_));
                if (locations[i][j] != 0) {
                    hexagon_.getPaint().setColor(agentColors_.get(locations[i][j] - 1).intValue());
                } else {
                    hexagon_.getPaint().setColor(boardColors_[1]);
                }
                hexagon_.draw(canvas);
            }
        }
        //draw winning connection
        List<HexAction> connection = simulator.getWinningConnection();
        for (HexAction location : connection) {
            hexagon_.getPaint().setStyle(Paint.Style.FILL);
            hexagon_.getPaint().setColor(WIN_COLOR);
            PointF point = locations_[location.getX()][location.getY()];
            hexagon_.setBounds((int) (point.x - hexagonRadius_),
                    (int) (point.y - hexagonCRadius_),
                    (int) (point.x + hexagonRadius_),
                    (int) (point.y + hexagonCRadius_));
            hexagon_.draw(canvas);
        }
        //Draw board inner edges
        for (PointF[] row: locations_) {
            for (PointF point: row) {
                hexagon_.getPaint().setStyle(Paint.Style.STROKE);
                hexagon_.getPaint().setColor(boardColors_[0]);
                hexagon_.setBounds((int) (point.x - hexagonRadius_),
                                   (int) (point.y - hexagonCRadius_),
                                   (int) (point.x + hexagonRadius_),
                                   (int) (point.y + hexagonCRadius_));
                hexagon_.draw(canvas);
            }
        }
        //draw selected lines
        if (selected_ != null) {
            int y = selected_.getY();
            int x = selected_.getX();
            paint_.setStyle(Paint.Style.STROKE);
            paint_.setColor(Color.BLACK);
            paint_.setStrokeWidth(4f);
            PointF p1i = locations_[x][0];
            PointF p1f = locations_[x][locations_.length - 1];
            PointF p2i = locations_[0][y];
            PointF p2f = locations_[locations_.length - 1][y];
            canvas.drawLine(p1i.x, p1i.y, p1f.x, p1f.y, paint_);
            canvas.drawLine(p2i.x, p2i.y, p2f.x, p2f.y, paint_);
        }
    }
}
