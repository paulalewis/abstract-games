package com.castlefrog.games.asg

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.Test

class PathUtilsTest {

    @Test
    fun testGetHexagonVertices() {
        val vertices = getHexVertices(0.0, 0.0, 1.0)
        assertThat(vertices).contains(doubleArrayOf(-1.0, 0.0, -0.5, -0.866, 0.5,
                -0.866, 1.0, 0.0, 0.5, 0.866, -0.5, 0.866), Offset.offset(0.0001))
    }

    @Test
    fun testGetHexagonVerticesZeroRadius() {
        val vertices = getHexVertices(1.0, 0.0, 0.0)
        assertThat(vertices).contains(doubleArrayOf(1.0, 0.0, 1.0, 0.0, 1.0,
                0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0), Offset.offset(0.0001))
    }

    @Test
    fun testCrossingNumberTestVerticesEmpty() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf()
        assertThat(crossingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test
    fun testCrossingNumberTestPathSinglePoint() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf(0.0, 0.0)
        assertThat(crossingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCrossingNumberTestPathInvalidSize1() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf(0.0)
        assertThat(crossingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCrossingNumberTestPathInvalidSize7() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf(1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0)
        assertThat(crossingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test
    fun testCrossingNumberTestPathSquare() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf(1.0, 1.0, -1.0, 1.0, -1.0, -1.0, 1.0, -1.0)
        assertThat(crossingNumberTest(pointX, pointY, path)).isEqualTo(1)
    }

    @Test
    fun testCrossingNumberTestPathSquareReverse() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf(1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0, 1.0)
        assertThat(crossingNumberTest(pointX, pointY, path)).isEqualTo(1)
    }

    @Test
    fun testWindingNumberTestVerticesEmpty() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf()
        assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test
    fun testWindingNumberTestPathSinglePoint() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf(0.0, 0.0)
        assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testWindingNumberTestPathInvalidSize1() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf(0.0)
        assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testWindingNumberTestPathInvalidSize7() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf(1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0)
        assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test
    fun testWindingNumberTestPathSquare() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf(1.0, 1.0, -1.0, 1.0, -1.0, -1.0, 1.0, -1.0)
        assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(1)
    }

    @Test
    fun testWindingNumberTestPathSquareReverse() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf(1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0, 1.0)
        assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(-1)
    }
}
