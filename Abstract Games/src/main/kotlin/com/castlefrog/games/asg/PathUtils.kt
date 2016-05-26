package com.castlefrog.games.asg

import android.graphics.Matrix
import android.graphics.Path

class PathUtils {
    companion object {
        val HEXAGON_RADIUS: Float = 1f
        val HEXAGON_HALF_RADIUS: Float = HEXAGON_RADIUS * 0.5f
        val HEXAGON_SHORT_RADIUS: Float = HEXAGON_HALF_RADIUS * Math.sqrt(3.0).toFloat()

        /**
         * @param radius of the circumscribed circle
         * @return a Path object in the shape of a hexagon
         */
        fun getHexagon(radius: Float): Path {
            return getRegularPolygon(6, radius)
        }

        /**
         * Get a path in the shape of a regular polygon.
         * @param nSides number of sides to polygon
         * @param radius radius of the circumscribed circle
         * @return a Path object in the shape of a regular polygon
         */
        fun getRegularPolygon(nSides: Int, radius: Float): Path {
            val path = Path()
            if (nSides < 3) {
                return path
            }
            val a = (Math.PI * 2) / nSides
            path.moveTo(radius, 0f)
            for (i in 1..nSides - 1) {
                path.lineTo((radius * Math.cos(a * i)).toFloat(), (radius * Math.sin(a * i)).toFloat())
            }
            path.close()
            val matrix = Matrix()
            matrix.postTranslate(radius, radius)
            path.transform(matrix)
            return path
        }
    }
}
