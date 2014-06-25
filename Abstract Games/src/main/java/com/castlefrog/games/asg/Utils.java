package com.castlefrog.games.asg;

import android.graphics.Path;

public class Utils {
    public static final float HEXAGON_RADIUS = 1;
    public static final float HEXAGON_HALF_RADIUS = HEXAGON_RADIUS * 0.5f;
    public static final float HEXAGON_SHORT_RADIUS = HEXAGON_HALF_RADIUS * (float) Math.sqrt(3);

    /**
     * @param radius
     * @return a Path object in the shape of a hexagon
     */
    public static Path getHexagon(float radius) {
        Path path = new Path();
        path.moveTo(HEXAGON_HALF_RADIUS * radius, 0);
        path.rLineTo(HEXAGON_RADIUS * radius, 0);
        path.rLineTo(HEXAGON_HALF_RADIUS * radius, HEXAGON_SHORT_RADIUS * radius);
        path.rLineTo(-HEXAGON_HALF_RADIUS * radius, HEXAGON_SHORT_RADIUS * radius);
        path.rLineTo(-HEXAGON_RADIUS * radius, 0);
        path.rLineTo(-HEXAGON_HALF_RADIUS * radius, -HEXAGON_SHORT_RADIUS * radius);
        path.close();
        return path;
    }
}
