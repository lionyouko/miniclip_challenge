package com.miniclip.mastermind.task.types

import com.miniclip.mastermind.task.elements.Ball

enum class BallColor(val colorLight:String, val colorDark:String) {
    ORANGE      ("#FDC830", "#F37335"),
    DARK_RED    ("#ED213A", "#93291E"),
    BLUE        ("#56CCF2", "#2F80ED"),
    GREEN       ("#F953C6", "#B91D73"),
    LIGHT_BLUE  ("#005C97", "#363795"),
    RED         ("#A8E063", "#56AB2f"),
    VIOLET      ("#5A175E", "#380038"),
    YELLOW      ("#825421", "#69140E");

    companion object BallColorHelper {
        fun getRandomBallColor() : BallColor {
            return values()[(Math.random() * values().size).toInt()]
        }


        fun getNextBallColor(ball:Ball?) : BallColor {
            if (ball == null || ball.ballColor.ordinal == values().size - 1) {
                return values()[0]
            }

            return values()[ball.ballColor.ordinal + 1]
        }

        fun getNextBallColor(ballColor: BallColor) : BallColor {
            if (ballColor.ordinal == values().size - 1) {
                return values()[0]
            }

            return values()[ballColor.ordinal + 1]
        }
    }
}