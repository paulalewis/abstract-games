package com.castlefrog.games.asg.view;

import java.util.List;
import java.util.Vector;

import android.app.Activity;
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
import com.castlefrog.agl.domains.havannah.HavannahAction;
import com.castlefrog.agl.domains.havannah.HavannahState;
import com.castlefrog.agl.domains.havannah.HavannahSimulator;
import com.castlefrog.games.asg.GameActivity;
import com.castlefrog.games.asg.R;
import com.castlefrog.games.asg.Utils;

public final class HavannahView extends View {
    private static final int WIN_COLOR = 0xffffffff;
    private static final int PADDING = 10;
    private static final int SCALE = 100;

    private HavannahAction selected_;
    private Vector<Vector<PointF>> locations_;
    private ShapeDrawable hexagon_;
    //private ShapeDrawable circle_;
    //private ShapeDrawable board_;
    /** distance from center to corner */
    private float hexagonRadius_;
    private float hexagonCRadius_;
    private float boardWidth_;
    private float boardHeight_;
    private int padding_;

    private List<GameActivity.AgentColor> agentColors_;
    private int[] boardColors_;

    private Paint paint_ = new Paint();

    private Arbiter<?, ?> arbiter_;

    //private HexBoardStyle boardStyle_ = HexBoardStyle.HEXAGONS;

    public HavannahView(Context context) {
        super(context);

        final float scale = getResources().getDisplayMetrics().density;
        padding_ = (int) (PADDING * scale);
        arbiter_ = ((GameActivity) context).getArbiter();

        //Use an xml resource to recall selected board style and board color
        //SharedPreferences settings = context.getSharedPreferences("havannah", Context.MODE_PRIVATE);
        boardColors_ = new int[] {0xffffffff, 0xffaaaaaa};
        agentColors_ = ((GameActivity) context).getAgentColors();

        int size = ((HavannahSimulator) arbiter_.getWorld()).getState().getSize();
        hexagon_ = new ShapeDrawable(new PathShape(Utils.getHexagon(SCALE), 2 * SCALE, 2 * SCALE * Utils.HEXAGON_SHORT_RADIUS));
        //circle_ = new ShapeDrawable(new OvalShape());

        locations_ = new Vector<Vector<PointF>>();
        locations_.setSize(size);
        int sideLength = (size + 1) / 2;
        for (int i = 0; i < sideLength; i += 1) {
            locations_.set(i, new Vector<PointF>());
            locations_.get(i).setSize(sideLength + i);
        }
        for (int i = sideLength; i < size; i += 1) {
            locations_.set(i, new Vector<PointF>());
            locations_.get(i).setSize(size + sideLength - i - 1);
        }
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
                for (int i = 0; i < locations_.size(); i += 1) {
                    for (int j = 0; j < locations_.get(i).size(); j += 1) {
                        PointF point = locations_.get(i).get(j);
                        float dx = point.x - x;
                        float dy = point.y - y;
                        if (Math.hypot(dx, dy) < hexagonCRadius_) {
                            HavannahSimulator simulator = (HavannahSimulator) arbiter_.getWorld();
                            HavannahState state = simulator.getState();
                            int dj = j;
                            if (i > locations_.size() / 2) {
                                dj += i - locations_.size() / 2;
                            }
                            HavannahAction action = HavannahAction.valueOf(i , dj);
                            int agentTurn = state.getAgentTurn();
                            List<HavannahAction> legalActions = simulator.getLegalActions(agentTurn);
                            List<Agent> agents = arbiter_.getAgents();
                            if (agents.get(agentTurn) instanceof ExternalAgent && legalActions.contains(action)) {
                                selected_ = action;
                                invalidate();
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

        HavannahSimulator simulator = (HavannahSimulator) arbiter_.getWorld();

        int size = simulator.getState().getSize();
        boardWidth_ = Math.min(paddedWidth, paddedHeight);
        boardHeight_ = boardWidth_ * (2 * Utils.HEXAGON_SHORT_RADIUS * size) / (Utils.HEXAGON_RADIUS * (1.5f * size + 0.5f));
        hexagonCRadius_ = boardHeight_ / (2 * size);
        hexagonRadius_ = hexagonCRadius_ / Utils.HEXAGON_SHORT_RADIUS;
        float xPadding = (width - boardWidth_) / 2;
        float yPadding = (height - boardHeight_) / 2;

        float x0 = xPadding + boardWidth_ / 2;
        float y0 = yPadding + hexagonCRadius_;

        for (int i = 0; i < size / 2 + 1; i += 1) {
            for (int j = 0; j < locations_.get(i).size(); j += 1) {
                locations_.get(i).set(j, new PointF(x0 + (j - i) * 1.5f * hexagonRadius_, y0 + (i + j) * hexagonCRadius_));
            }
        }
        float x02 = x0 - (size / 2) * hexagonRadius_ * 3 / 2;
        float y02 = y0 + (size / 2) * hexagonCRadius_;
        for (int i = size / 2 + 1; i < size; i += 1) {
            for (int j = 0; j < locations_.get(i).size(); j += 1) {
                locations_.get(i).set(j,
                        new PointF(x02 + j * 1.5f * hexagonRadius_, y02 + (2 * (i - size / 2) + j) * hexagonCRadius_));
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        HavannahSimulator simulator = (HavannahSimulator) arbiter_.getWorld();
        HavannahState state = simulator.getState();
        //draw board
        for (int i = 0; i < locations_.size(); i += 1) {
            for (int j = 0; j < locations_.get(i).size(); j += 1) {
                hexagon_.getPaint().setStyle(Paint.Style.FILL);
                PointF point = locations_.get(i).get(j);
                hexagon_.setBounds((int) (point.x - hexagonRadius_),
                                   (int) (point.y - hexagonCRadius_),
                                   (int) (point.x + hexagonRadius_),
                                   (int) (point.y + hexagonCRadius_));
                if (i > locations_.size() / 2) {
                    int dj = j + i - locations_.size() / 2;
                    //draw winning connection
                    if (state.getLocation(i, dj) != 0) {
                        hexagon_.getPaint().setColor(agentColors_.get(state.getLocation(i, dj) - 1).intValue());
                    } else {
                        hexagon_.getPaint().setColor(boardColors_[1]);
                    }
                } else {
                    //draw winning connection
                    if (state.getLocation(i, j) != 0) {
                        hexagon_.getPaint().setColor(agentColors_.get(state.getLocation(i, j) - 1).intValue());
                    } else {
                        hexagon_.getPaint().setColor(boardColors_[1]);
                    }
                }
                hexagon_.draw(canvas);
                //draw board outline
                hexagon_.getPaint().setStyle(Paint.Style.STROKE);
                hexagon_.getPaint().setColor(boardColors_[0]);
                hexagon_.getPaint().setStrokeWidth(0.1f * SCALE);
                hexagon_.draw(canvas);
            }
        }
        // draw winning connection
        List<HavannahAction> connection = simulator.getWinningConnection();
        for (HavannahAction location : connection) {
            hexagon_.getPaint().setStyle(Paint.Style.FILL);
            hexagon_.getPaint().setColor(WIN_COLOR);
            PointF point = locations_.get(location.getX()).get(location.getY());
            hexagon_.setBounds((int) (point.x - hexagonRadius_),
                               (int) (point.y - hexagonCRadius_),
                               (int) (point.x + hexagonRadius_),
                               (int) (point.y + hexagonCRadius_));
            hexagon_.draw(canvas);
        }
        //draw selected lines
        if (selected_ != null) {
            int y = selected_.getY();
            int x = selected_.getX();
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
