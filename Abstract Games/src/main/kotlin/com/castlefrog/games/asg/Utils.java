package com.castlefrog.games.asg;

import android.graphics.Path;

public class Utils {
    public static final float HEXAGON_RADIUS = 1;
    public static final float HEXAGON_HALF_RADIUS = HEXAGON_RADIUS * 0.5f;
    public static final float HEXAGON_SHORT_RADIUS = HEXAGON_HALF_RADIUS * (float) Math.sqrt(3);

    public static Path getHexagon(int scale) {
        Path path = new Path();
        path.moveTo(HEXAGON_HALF_RADIUS * scale, 0);
        path.rLineTo(HEXAGON_RADIUS * scale, 0);
        path.rLineTo(HEXAGON_HALF_RADIUS * scale, HEXAGON_SHORT_RADIUS * scale);
        path.rLineTo(-HEXAGON_HALF_RADIUS * scale, HEXAGON_SHORT_RADIUS * scale);
        path.rLineTo(-HEXAGON_RADIUS * scale, 0);
        path.rLineTo(-HEXAGON_HALF_RADIUS * scale, -HEXAGON_SHORT_RADIUS * scale);
        path.close();
        return path;
    }
}
