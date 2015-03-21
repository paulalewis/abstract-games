package com.castlefrog.games.asg;

import android.graphics.Matrix;
import android.graphics.Path;

public class PathUtils {
    public static final float HEXAGON_RADIUS = 1;
    public static final float HEXAGON_HALF_RADIUS = HEXAGON_RADIUS * 0.5f;
    public static final float HEXAGON_SHORT_RADIUS = HEXAGON_HALF_RADIUS * (float) Math.sqrt(3);

    /**
     * @param radius of the circumscribed circle
     * @return a Path object in the shape of a hexagon
     */
    public static Path getHexagon(float radius) {
        return getRegularPolygon(6, radius);
    }

    /**
     * Get a path in the shape of a regular polygon.
     * @param nSides number of sides to polygon
     * @param radius radius of the circumscribed circle
     * @return a Path object in the shape of a regular polygon
     */
    public static Path getRegularPolygon(int nSides, float radius) {
        Path path = new Path();
        if (nSides < 3) {
            return path;
        }
        float a = (float) (Math.PI * 2) / nSides;
        path.moveTo(radius, 0);
        for (int i = 1; i < nSides; i++) {
            path.lineTo((float)(radius*Math.cos(a*i)), (float)(radius*Math.sin(a*i)));
        }
        path.close();
        Matrix matrix = new Matrix();
        matrix.postTranslate(radius, radius);
        path.transform(matrix);
        return path;
    }
}
