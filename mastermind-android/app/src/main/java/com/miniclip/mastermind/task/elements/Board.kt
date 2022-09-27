package com.miniclip.mastermind.task.elements

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import com.miniclip.mastermind.task.R
import com.miniclip.mastermind.task.defines.GameSettings
import com.miniclip.mastermind.task.types.BallColor
import com.miniclip.mastermind.task.types.ClueType
import com.miniclip.mastermind.task.types.GameState
import com.miniclip.mastermind.task.views.ClickableBallSlot
import com.miniclip.mastermind.task.views.ClueSlot
import com.miniclip.mastermind.task.views.LayoutUtils

class Board {
    private var rowCount:Int = 0
    private var colCount:Int = 0
    private lateinit var balls:Array<Array<Ball?>>
    private lateinit var clues:ArrayList<Clue>

    var state:GameState = GameState.PLAYING
    var currentRow:Int = 0

    init {
        rowCount = GameSettings.rows + 1
        bootStrapGame()
    }

    /**
     * Sets initial board state
     */
    private fun bootStrapGame () {
        colCount = GameSettings.columns
        currentRow = GameSettings.rows

        balls = Array(rowCount) { Array(colCount) { null } }
        clues = ArrayList()

        generateSolutionRow()
    }

    /**
     * Fills the top-most row with a random 4 color solution (all balls will have different colors)
     */
    private fun generateSolutionRow() {
        balls[0][0] = Ball(BallColor.getRandomBallColor())

        for (col in 1 until colCount) {
            do {
                balls[0][col] = Ball(BallColor.getRandomBallColor())
            } while (!isColorUniqueInSolution(col))
        }
    }


    /**
     * Ensures that for the solution row, no colors are repeated
     */
    private fun isColorUniqueInSolution(col:Int) : Boolean {
        for (i in 0 until col) {
            if (balls[0][col]!!.ballColor == balls[0][i]!!.ballColor) {
                return false
            }
        }

        return true
    }

    private fun isColorIsRepeatedForRow(row:Int, ballColor: BallColor) : Boolean {
        for(i in 0 until colCount) {
            val ball: Ball = balls[row][i] ?: return false
            if (ball.ballColor == ballColor) {
                return true
            }
        }

        return false
    }

    /**
     * Iterate through input (guess) row and output (solution) row and checks:
     * First, how many balls have the correct color
     * Second, how many positions are correct
     */
    private fun countClues() {
        // Iterate through colors
        for(solutionCol in 0 until colCount) {
            for(guessCol in 0 until colCount) {
                // Compare each of the guess row colors with all of the solution row colors
                if (balls[0][solutionCol]!!.ballColor == balls[currentRow][guessCol]!!.ballColor) {
                    clues.add(Clue(currentRow, ClueType.COLOR))
                    // If we already match a ball, we don't need to keep searching we can move on to the next
                    break
                }
            }
        }

        // Iterate through positions
        // If, for the same position, colors match, then the location is correct
        for (col in 0 until colCount) {
            if (balls[0][col]!!.ballColor == balls[currentRow][col]!!.ballColor) {
                clues.add(Clue(currentRow, ClueType.LOCATION))
            }
        }
    }

    /**
     * Standard win condition check - if there is a ball with a different color from the solution
     * and row is not 0, then we are still playing
     */
    private fun updateGameState() {
        state = GameState.WON
        for (col in 0 until colCount) {
            if (balls[0][col]!!.ballColor != balls[currentRow][col]!!.ballColor) {
                state = if (currentRow - 1 > 0) GameState.PLAYING else GameState.LOST
                break
            }
        }
    }

    /**
     * Check if the row has balls in every slot
     * @return true if all slots are filled, false otherwise
     */
    private fun isRowFilled() : Boolean {
        for (col in 0 until colCount) {
            if (balls[currentRow][col] == null) {
                return false
            }
        }
        return true
    }

    /**
     * Accessor for the balls array
     */
    private fun getBallAt(row:Int, col: Int) : Ball? {
        return balls[row][col]
    }

    /**
     * Modifier for the balls array.
     */
    private fun setBall(colCount:Int, color:BallColor) {
        balls[currentRow][colCount - 1] = Ball(color)
    }

    /**
     * For each @param row, count how many clues exist for given @param type
     */
    fun getClueAmountForRowAndType(row:Int, type:ClueType) : Int {
        var clueAmount = 0

        for(clue in clues) {
            if (clue.row == row && clue.type == type) {
                clueAmount++
            }
        }

        return clueAmount
    }

    /**
     * Construct the board in the view
     */
    fun populateGame(table: TableLayout, context: Context) {
        table.removeAllViews()

        for(row in 0 .. GameSettings.rows) {
            val ballRow = TableRow(context)
            table.addView(ballRow)

            val placesClueButton = ClueSlot(context, ClueType.LOCATION, getClueAmountForRowAndType(row, ClueType.LOCATION))
            ballRow.addView(placesClueButton)

            // Create tile buttons
            for (column in 0 until GameSettings.columns) {
                val ball = getBallAt(row, column)

                // Colors for empty tiles
                var ballColorLight = LayoutUtils.getColorSafe(context, R.color.freeTileColor)
                var ballColorDark = LayoutUtils.getColorSafe(context, R.color.freeTileColor)

                if (row == 0) {
                    // Paint top tiles purple to keep them hidden during play time
                    if (state == GameState.PLAYING) {
                        ballColorLight = LayoutUtils.getColorSafe(context, R.color.colorPrimary)
                        ballColorDark = LayoutUtils.getColorSafe(context, R.color.colorPrimaryDark)
                    } else {
                        // We can reveal them when game is over (WON or LOST)
                        if (ball != null) {
                            ballColorLight = Color.parseColor(ball.ballColor.colorLight)
                            ballColorDark = Color.parseColor(ball.ballColor.colorDark)
                        }
                    }
                } else if (ball != null) {
                    ballColorLight = Color.parseColor(ball.ballColor.colorLight)
                    ballColorDark = Color.parseColor(ball.ballColor.colorDark)
                }

                val ballButton = ClickableBallSlot(context, ballColorLight, ballColorDark)
                ballRow.addView(ballButton)

                // Add the '?' to the solution tiles
                if (row == 0) {
                    ballButton.setTextColor(LayoutUtils.getColorSafe(context, R.color.darkBackgroundText))
                    ballButton.setText(R.string.hidden_ball)
                }

                // OnClick behaviour to rotate balls
                if (currentRow == row) {
                    ballButton.setOnClickListener {
                        if (state == GameState.PLAYING) {
                            val currentBall = getBallAt(row, column)
                            var nextColor:BallColor = BallColor.getNextBallColor(currentBall)

                            // Since there only are unique solutions, we only allow for balls of
                            // different colors to be selected
                            while(isColorIsRepeatedForRow(row, nextColor)) {
                                nextColor = BallColor.getNextBallColor(nextColor)
                            }

                            setBall(column + 1, nextColor)
                            populateGame(table, context)
                        }
                    }
                }
            }

            val colorsClueButton = ClueSlot(context, ClueType.COLOR, getClueAmountForRowAndType(row, ClueType.COLOR))
            ballRow.addView(colorsClueButton)

            // Hide clues for previous rows
            if (row <= currentRow) {
                placesClueButton.visibility = View.INVISIBLE
                colorsClueButton.visibility = View.INVISIBLE
            }
        }
    }

    fun resetBoard() {
        state = GameState.PLAYING
        bootStrapGame()
    }


    /**
     * Step to the next (top) row
     */
    fun nextRow() { currentRow-- }


    /**
     * Check row state
     */
    fun checkRow() : Boolean {
        return if (isRowFilled()) {
            updateGameState()
            countClues()
            true
        } else {
            false
        }
    }

    /**
     * Clean row
     */
    fun clearCurrentRow() {
        for (col in 0 until colCount) {
            balls[currentRow][col] = null
        }
    }
}