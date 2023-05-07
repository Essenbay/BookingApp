package com.example.bookingapp

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun main() {
        val rectangle = Rectangle(10.0,20.0)
        val square = Square(10.0)
        rectangle.width = 30.0
        rectangle.height= 40.0
        println(rectangle)

        println("Square first: $square")
        square.value= 20.0
        println("Square second: $square")
    }
}

interface Figure {
    val width: Double
    val height: Double
    val area: Double
}

class Rectangle(override var width: Double, override var height: Double): Figure {
    override val area = width * height
}

class Square(var value: Double):Figure {
    override val width: Double = value
    override val height: Double = value
    override val area: Double = value
}

