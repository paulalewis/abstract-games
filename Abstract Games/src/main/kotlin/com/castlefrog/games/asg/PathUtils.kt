package com.castlefrog.games.asg

import android.graphics.*

val HEXAGON_RADIUS = 1f
val HEXAGON_HALF_RADIUS = HEXAGON_RADIUS * 0.5f
val HEXAGON_SHORT_RADIUS = HEXAGON_HALF_RADIUS * Math.sqrt(3.0).toFloat()

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
private fun isLeftOfEdge(p0x: Float, p0y: Float, p1x: Float, p1y: Float, p2x: Float, p2y: Float): Boolean {
    return ((p1x - p0x) * (p2y - p0y) - (p2x - p0x) * (p1y - p0y)) > 0
}

private fun isRightOfEdge(p0x: Float, p0y: Float, p1x: Float, p1y: Float, p2x: Float, p2y: Float): Boolean {
    return ((p1x - p0x) * (p2y - p0y) - (p2x -  p0x) * (p1y - p0y)) < 0
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
 * @param pointX x coordinate of a point to check if inside polygon
 * @param pointY y coordinate of a point to check if inside polygon
 * @param vertices the vertices of the polygon in (x,y) coordinate pairs
 * @return the winding number, is 0 when point is outside polygon
 */
fun windingNumberTest(pointX: Float, pointY: Float, vertices: Array<Float>): Int {
    if (vertices.size % 2 == 1) {
        throw IllegalArgumentException("Vertices array must have even number of values")
    }
    var windingNumber = 0
    // loop through all edges of the polygon
    for (i in 0..vertices.size - 2 step 2) {
        val vix = vertices[i]
        val viy = vertices[i + 1]
        val vfx = vertices[if (i == vertices.size - 2) 0 else i + 2]
        val vfy = vertices[if (i == vertices.size - 2) 1 else i + 3]
        if (viy <= pointY) { // start y <= P.y
            if (vfy  > pointY) // an upward crossing
                if (isLeftOfEdge(vix, viy, vfx, vfy, pointX, pointY))
                    windingNumber += 1 // have  a valid up intersect
        } else { // start y > P.y (no test needed)
            if (vfy  <= pointY)     // a downward crossing
                if (isRightOfEdge(vix, viy, vfx, vfy, pointX, pointY))
                    windingNumber -= 1 // have  a valid down intersect
        }
    }
    return windingNumber
}
