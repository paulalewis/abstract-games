package com.castlefrog.games.asg

import com.google.common.truth.Truth
import org.junit.Test

class PathUtilsTest {

    @Test
    fun testWindingNumberTestPathEmpty() {
        val pointX = 0f
        val pointY = 0f
        val path = Array(0, { 0f })
        Truth.assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test
    fun testWindingNumberTestPathSinglePoint() {
        val pointX = 0f
        val pointY = 0f
        val path = Array(2, { 0f })
        Truth.assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testWindingNumberTestPathInvalidSize1() {
        val pointX = 0f
        val pointY = 0f
        val path = Array(1, { 0f })
        Truth.assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testWindingNumberTestPathInvalidSize7() {
        val pointX = 0f
        val pointY = 0f
        val path = arrayOf(1f, 1f, 1f, -1f, -1f, -1f, -1f)
        Truth.assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(0)
    }

    @Test
    fun testWindingNumberTestPathSquare() {
        val pointX = 0f
        val pointY = 0f
        val path = arrayOf(1f, 1f, -1f, 1f, -1f, -1f, 1f, -1f)
        Truth.assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(1)
    }

    @Test
    fun testWindingNumberTestPathSquareReverse() {
        val pointX = 0f
        val pointY = 0f
        val path = arrayOf(1f, 1f, 1f, -1f, -1f, -1f, -1f, 1f)
        Truth.assertThat(windingNumberTest(pointX, pointY, path)).isEqualTo(-1)
    }
}
