package com.castlefrog.games.asg

import android.graphics.Matrix
import android.graphics.Path
import android.graphics.Point
import android.graphics.PointF

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

/**
 * tests if a point is Left|On|Right of an infinite line
 * @return >0 for p2 left of the line through p0 and p1
 *         =0 for p2 on the line
 *         <0 for p2 right of the line
 */
private fun isLeftOfEdge(p0: PointF, p1: PointF, p2: PointF): Boolean {
    return ((p1.x - p0.x) * (p2.y - p0.y) - (p2.x -  p0.x) * (p1.y - p0.y)) > 0
}

private fun isRightOfEdge(p0: PointF, p1: PointF, p2: PointF): Boolean {
    return ((p1.x - p0.x) * (p2.y - p0.y) - (p2.x -  p0.x) * (p1.y - p0.y)) < 0
}

/**
 * crossing number test for a point in a polygon
 * @point a point that could be inside or outside polygon
 * @vertices vertex points of a polygon V[n+1] with V[n]=V[0]
 * @return 0 = outside, 1 = inside
 */
fun crossingNumberTest(point: Point, vertices: List<Point>): Int {
    var crossingNumber: Int = 0

    // loop through all edges of the polygon
    for (i in 0..vertices.size - 1) {
        val vi = vertices[i]
        val vf = vertices[if (i == vertices.size - 1) 0 else i + 1]
        if (((vi.y <= point.y) && (vf.y > point.y)) // an upward crossing
                || ((vi.y > point.y) && (vf.y <=  point.y))) { // a downward crossing
            // compute  the actual edge-ray intersect x-coordinate
            val vt: Float = (point.y  - vi.y) / (vf.y - vi.y).toFloat()
            if (point.x < vi.x + vt * (vf.x - vi.x)) // P.x < intersect
                crossingNumber += 1 // a valid crossing of y=P.y right of P.x
        }
    }
    return crossingNumber and 1
}

/**
 * winding number test for a point in a polygon
 * @param point a point to check if inside polygon
 * @param vertices the vertices of the polygon
 * @return the winding number, is 0 when point is outside polygon
 */
fun windingNumberTest(point: PointF, vertices: List<PointF>): Int {
    var windingNumber = 0
    // loop through all edges of the polygon
    for (i in 0..vertices.size - 1) {
        val vi = vertices[i]
        val vf = vertices[if (i == vertices.size - 1) 0 else i + 1]
        if (vi.y <= point.y) { // start y <= P.y
            if (vf.y  > point.y) // an upward crossing
                if (isLeftOfEdge(vi, vf, point))
                    windingNumber += 1 // have  a valid up intersect
        } else { // start y > P.y (no test needed)
            if (vf.y  <= point.y)     // a downward crossing
                if (isRightOfEdge(vi, vf, point))
                    windingNumber -= 1 // have  a valid down intersect
        }
    }
    return windingNumber
}
