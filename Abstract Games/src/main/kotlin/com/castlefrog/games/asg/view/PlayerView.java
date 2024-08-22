package com.castlefrog.games.asg.view;

import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.castlefrog.agl.Simulator;
import com.castlefrog.games.asg.GameActivity;

public final class PlayerView extends View {
    public static final float RING_PADDING = 0.3f;
    public static final float PADDING = 0.2f;

    private boolean human_;
    private int number_;
    private Paint paint_;

    public PlayerView(Context context) {
        this(context, null);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint_ = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_.setStrokeWidth(4.0f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(height, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        if (human_) {
            paint_.setStyle(Paint.Style.FILL);
            canvas.drawCircle(width / 2, height / 2, width * (1 - RING_PADDING - PADDING) / 2, paint_);
        } else {
            paint_.setStyle(Paint.Style.FILL);
            canvas.drawCircle(width / 2, height / 2, width * (1 - RING_PADDING - PADDING) / 2, paint_);
        }
        if (isPlayerTurn()) {
            paint_.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(width / 2, height / 2, width * (1 - PADDING) / 2, paint_);
        }
    }

    private boolean isPlayerTurn() {
        GameActivity game = (GameActivity) getContext();
        Simulator<?, ?> simulator = game.getArbiter().getWorld();
        return simulator.hasLegalActions(number_ - 1);
    }

    public void setColor(int color) {
        paint_.setColor(color);
    }

    public void setHuman(boolean human) {
        human_ = human;
    }

    public void setNumber(int number) {
        number_ = number;
    }
}
