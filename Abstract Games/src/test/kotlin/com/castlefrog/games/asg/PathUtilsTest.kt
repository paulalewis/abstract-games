package com.castlefrog.games.asg

import com.google.common.truth.Truth
import org.junit.Test

class PathUtilsTest {

    @Test
    fun testGetHexagonVertices() {
        val vertices = getHexVertices(0.0, 0.0, 1.0)
        Truth.assertThat(vertices).hasValuesWithin(0.0001).of(-1.0, 0.0, -0.5, -0.866, 0.5,
                -0.866, 1.0, 0.0, 0.5, 0.866, -0.5, 0.866)
    }

    @Test
    fun testWindingNumberTestPathEmpty() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf()
        Truth.assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test
    fun testWindingNumberTestPathSinglePoint() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf(0.0, 0.0)
        Truth.assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testWindingNumberTestPathInvalidSize1() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf(0.0)
        Truth.assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testWindingNumberTestPathInvalidSize7() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf(1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0)
        Truth.assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test
    fun testWindingNumberTestPathSquare() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf(1.0, 1.0, -1.0, 1.0, -1.0, -1.0, 1.0, -1.0)
        Truth.assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(1)
    }

    @Test
    fun testWindingNumberTestPathSquareReverse() {
        val pointX = 0.0
        val pointY = 0.0
        val path = doubleArrayOf(1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0, 1.0)
        Truth.assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(-1)
    }
}
