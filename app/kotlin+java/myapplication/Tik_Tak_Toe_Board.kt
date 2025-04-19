package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min
import kotlin.random.Random
import android.widget.TextView
import android.os.Handler
import android.os.Looper

class Tik_Tak_Toe_Board(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // Zīmēšana līnijās parametriem Tik_Tak_Toe_Board
    private val paint = Paint().apply {
        strokeWidth = 8f
        isAntiAlias = true
        color = Color.BLACK
    }
    //Zīmēšanas tekstam parametriem (X un O)
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 100f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    // spēles laukums
    private val board = Array(3) { Array(3) { "" } }
    // Minimax algoritms botam
    private val minimax = Minimax(board)
    // Kurš spēlētājs šobrīd spēlē un par ko spēlē(X vai O)
    private var currentPlayer = if (Random.nextBoolean()) "X" else "O"
    // Spēlēs beiguma raditajs
    private var gameFinished = false
    // Spēlēs režima raditajs, tieši viņš rada vai cilvēks spēlē pret botu
    private var isPlayingWithBot = false
    // Spēlētāju krāsas
    private val player1Color = Color.RED
    private val player2Color = Color.BLUE
    // Spēlētāju vārdi
    private var player1Name: String? = null
    private var player2Name: String? = null
    // Koordinātas uzvaras līnijai
    private var winStartX = -1f
    private var winStartY = -1f
    private var winEndX = -1f
    private var winEndY = -1f
    // Teksta lauki spēlētāju un uzvarētāju rādīšanai
    private var currentPlayerTextView: TextView? = null
    private var winnerTextView: TextView? = null
    // Vai šī ir bota spēle (papildu pārbaude), jo testēšanas laikā radās kļūda, kad bots veica divus gājienus pēc kārtas, no problēmas izdevās izvairīties ar papildpārbaudi
    private var isBotGame: Boolean = false
    // Handler animācijām un atlikšanai
    private val handler = Handler(Looper.getMainLooper())
    // Norāda, vai bots domā savu gājienu, kad spēlē pret Bota režīmu
    private var botIsThinking = false

    // Atjauno spēlētāja kārtas tekstu
    private fun updateCurrentPlayerTextView() {
        if (isBotGame) {
            if (currentPlayer == "X") {
                currentPlayerTextView?.text = "Your turn: ${player1Name ?: "Player"}"
            } else {
                currentPlayerTextView?.text = "Bot turn..."
            }
        } else {
            val playerName = if (currentPlayer == "X") player1Name else player2Name
            currentPlayerTextView?.text = "Turn: ${playerName ?: currentPlayer}"
        }
    }
    // Iestata spēlētāja kārtas teksta lauku
    fun setCurrentPlayerTextView(textView: TextView) {
        currentPlayerTextView = textView
    }
    // Iestata pašreizējo spēlētāju,ja spēle vēl nav beigusies, tas ir izmantots lai butu redzams kurš tagat gaens ir
    fun setCurrentPlayer(player: String) {
        if (!gameFinished) {
            currentPlayer = player
            updateCurrentPlayerTextView()
        }
    }
    // Paslēpj spēlētāja teksta lauku
    private fun hideCurrentPlayerTextView() {
        currentPlayerTextView?.visibility = View.GONE
    }
    // Parāda spēlētāja teksta lauku
    private fun showCurrentPlayerTextView() {
        currentPlayerTextView?.visibility = View.VISIBLE
        updateCurrentPlayerTextView()
    }
    // Zīmē spēles laukumu un uzvaras līniju
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = min(width, height).toFloat()
        val cellSize = size / 3f
        val offsetX = (width - size) / 2f
        val offsetY = (height - size) / 2f
        // Zīmē režģi
        paint.color = Color.BLACK
        paint.strokeWidth = 8f
        for (i in 1..2) {
            canvas.drawLine(offsetX + i * cellSize, offsetY, offsetX + i * cellSize, offsetY + size, paint)
            canvas.drawLine(offsetX, offsetY + i * cellSize, offsetX + size, offsetY + i * cellSize, paint)
        }
        // Zīmē simbolus X un O
        for (i in 0..2) {
            for (j in 0..2) {
                val value = board[i][j]
                if (value.isNotEmpty()) {
                    val x = offsetX + j * cellSize + cellSize / 2
                    val y = offsetY + i * cellSize + cellSize / 2 + textPaint.textSize / 3
                    textPaint.color = if (value == "X") player1Color else player2Color

                    canvas.drawText(value, x, y, textPaint)
                }
            }
        }
        // Ja ir uzvarētājs, zīmē zaļo līniju
        if (winStartX >= 0 && winEndX >= 0) {
            paint.color = Color.GREEN
            paint.strokeWidth = 12f
            canvas.drawLine(offsetX + winStartX, offsetY + winStartY, offsetX + winEndX, offsetY + winEndY, paint)
        }
    }
    // Lietotāja gājiens
    override fun onTouchEvent(event: MotionEvent): Boolean {
        Achievements.unlock(context, "achievement_play")
        if (event.action == MotionEvent.ACTION_DOWN && winStartX < 0 && !gameFinished) {
            val size = min(width, height).toFloat()
            val cellSize = size / 3f
            val offsetX = (width - size) / 2f
            val offsetY = (height - size) / 2f
            val col = ((event.x - offsetX) / cellSize).toInt()
            val row = ((event.y - offsetY) / cellSize).toInt()
            // Gājiens cilvēkam vai botam atkarībā no spēlētāja
            if (row in 0..2 && col in 0..2 && board[row][col] == "" && currentPlayer == "X") {
                board[row][col] = currentPlayer
                checkWin()
                if (!gameFinished) {
                    currentPlayer = "O"
                    updateCurrentPlayerTextView()
                    invalidate()
                    // Ja spēle ar botu, izsauc bota gājienu ar nelielu pauzi, neliela pauze vajadziga lai programa spetu pieladaties un bots ne taisija gajenu uz reiz, momentali, jo bija problema ka pec uzpiešanas uz PLAY AGAIN bots taisija savu gaenu ne skatot uz to ka ir cilvēka gajens
                    if (isPlayingWithBot && currentPlayer == "O" && !gameFinished && !botIsThinking) {
                        botIsThinking = true
                        handler.removeCallbacksAndMessages(null)
                        handler.postDelayed({
                            botMove()
                            botIsThinking = false
                        }, 500)
                    }
                } else {
                    invalidate()
                }
            }
            // Otrā spēlētāja gājiens, kad spēle ar draugu
            else if (row in 0..2 && col in 0..2 && board[row][col] == "" && currentPlayer == "O") {
                board[row][col] = currentPlayer
                checkWin()
                if (!gameFinished) {
                    currentPlayer = "X"
                    updateCurrentPlayerTextView()
                    invalidate()
                }
            }
        }
        return true
    }
    // Iestata uzvarētāja teksta lauku un spēles režīmu
    fun setWinnerTextView(textView: TextView, isBotGame: Boolean) {
        this.winnerTextView = textView
        this.isBotGame = isBotGame
    }
    // Iestata spēlētāju vārdus
    fun setPlayerNames(p1: String?, p2: String?) {
        player1Name = p1
        player2Name = p2
    }
    // Pārbauda visus vareantus uz Borda vai kāds ir uzvarējis un šeit ari pievienoti Achievement dabušānā
    private fun checkWin() {
        val size = min(width, height).toFloat()
        val cellSize = size / 3f
        for (i in 0..2) {
            if (board[i][0] != "" && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                val winner: String
                Achievements.unlock(context, "achievement_play")
                if (isBotGame) {
                    Achievements.unlock(context, "achievement_bot_play")

                    if (board[i][0] == "X") {
                        winner = "YOU WIN"
                        Achievements.unlock(context, "achievement_win_bot")
                    } else {
                        winner = "BOT WINS"
                        Achievements.unlock(context, "achievement_lose_bot")
                    }
                } else {
                    Achievements.unlock(context, "achievement_friend")
                    winner = if (board[i][0] == "X") player1Name ?: "Player 1" else player2Name ?: "Player 2"
                }
                winnerTextView?.text = "Winner: $winner"
                winStartX = 0f
                winStartY = i * cellSize + cellSize / 2
                winEndX = size
                winEndY = winStartY
                invalidate()
                finishGame()
                return
            }
        }
        for (j in 0..2) {
            if (board[0][j] != "" && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                val winner: String
                Achievements.unlock(context, "achievement_play")
                if (isBotGame) {
                    Achievements.unlock(context, "achievement_bot_play")

                    if (board[0][j] == "X") {
                        winner = "YOU WIN"
                        Achievements.unlock(context, "achievement_win_bot")
                    } else {
                        winner = "BOT WINS"
                        Achievements.unlock(context, "achievement_lose_bot")
                    }
                } else {
                    Achievements.unlock(context, "achievement_friend")
                    winner = if (board[0][j] == "X") player1Name ?: "Player 1" else player2Name ?: "Player 2"
                }
                winnerTextView?.text = "Winner: $winner"
                winStartX = j * cellSize + cellSize / 2
                winEndX = winStartX
                winStartY = 0f
                winEndY = size
                invalidate()
                finishGame()
                return
            }
        }
        if (board[0][0] != "" && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            val winner: String
            Achievements.unlock(context, "achievement_play")
            if (isBotGame) {
                Achievements.unlock(context, "achievement_bot_play")
                if (board[0][0] == "X") {
                    winner = "YOU WIN"
                    Achievements.unlock(context, "achievement_win_bot")
                } else {
                    winner = "BOT WINS"
                    Achievements.unlock(context, "achievement_lose_bot")
                }
            } else {
                Achievements.unlock(context, "achievement_friend")
                winner = if (board[0][0] == "X") player1Name ?: "Player 1" else player2Name ?: "Player 2"
            }
            winnerTextView?.text = "Winner: $winner"
            winStartX = 0f
            winStartY = 0f
            winEndX = size
            winEndY = size
            invalidate()
            finishGame()
            return
        }
        if (board[0][2] != "" && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            val winner: String
            Achievements.unlock(context, "achievement_play")
            if (isBotGame) {
                Achievements.unlock(context, "achievement_bot_play")
                if (board[0][2] == "X") {
                    winner = "YOU WIN"
                    Achievements.unlock(context, "achievement_win_bot")
                } else {
                    winner = "BOT WINS"
                    Achievements.unlock(context, "achievement_lose_bot")
                }
            } else {
                Achievements.unlock(context, "achievement_friend")
                winner = if (board[0][2] == "X") player1Name ?: "Player 1" else player2Name ?: "Player 2"
            }
            winnerTextView?.text = "Winner: $winner"
            winStartX = size
            winStartY = 0f
            winEndX = 0f
            winEndY = size
            invalidate()
            finishGame()
            return
        }
        if (!gameFinished && board.all { row -> row.all { it.isNotEmpty() } }) {
            Achievements.unlock(context, "achievement_play")
            if (isBotGame) Achievements.unlock(context, "achievement_bot_play")
            else Achievements.unlock(context, "achievement_friend")

            winnerTextView?.text = "Draw!"
            gameFinished = true
            hideCurrentPlayerTextView()
            invalidate()
        }
    }
    // Atiestata spēli sākumā, izdzēs bordu, kurš spēlē, kurš un par ko spēlēja ka ari atjauno bota, ja spēlē bija ar botu
    fun resetGame() {
        botIsThinking = false
        gameFinished = false
        currentPlayer = ""
        showCurrentPlayerTextView()
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j] = ""
            }
        }
        winStartX = -1f
        winStartY = -1f
        winEndX = -1f
        winEndY = -1f
        winnerTextView?.text = ""
        currentPlayer = if (Random.nextBoolean()) "X" else "O"
        handler.removeCallbacksAndMessages(null)
        if (isPlayingWithBot && currentPlayer == "O") {
            handler.postDelayed({ botMove() }, 500)
        }
        invalidate()
    }
    // Indikators, ka spēle ir beigusies, tiek izveidots atsevišķi no resetGame(), jo radās problēmas ar hideCurrentPlayerTextView() realizāciju un šādi tas bija vienkāršāk
    private fun finishGame() {
        gameFinished = true
        hideCurrentPlayerTextView()
    }
    // Iestata spēles režīmu – ar botu vai draugu
    fun setGameMode(isPlayingWithBot: Boolean) {
        this.isPlayingWithBot = isPlayingWithBot
        if (isPlayingWithBot) {
            Achievements.unlock(context, "achievement_bot_play")
        } else {
            Achievements.unlock(context, "achievement_friend")
        }
    }
    // Bota gājiens, izmantojot Minimax algoritmu
    fun botMove() {
        if (gameFinished || currentPlayer != "O") return

        val move = minimax.findBestMove()
        if (move.first != -1 && move.second != -1) {
            board[move.first][move.second] = "O"
            checkWin()
            if (!gameFinished) {
                currentPlayer = "X"
                updateCurrentPlayerTextView()
            }
        }
        invalidate()
    }
}
// Minimax algoritms tika atrasts internetā. Tā kā "Tic-Tac-Toe" ir ļoti izplatīta spēle, pieejamas daudzas vienkāršas un dažādas šī algoritma realizācijas.
class Minimax(private val board: Array<Array<String>>) {
    private fun heuristic_evaluation(): Int {
        for (i in 0..2) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                if (board[i][0] == "X") return -10
                if (board[i][0] == "O") return 10
            }
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                if (board[0][i] == "X") return -10
                if (board[0][i] == "O") return 10
            }
        }
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            if (board[0][0] == "X") return -10
            if (board[0][0] == "O") return 10
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[0][2] == "X") return -10
            if (board[0][2] == "O") return 10
        }
        return 0
    }
    fun minimax(depth: Int, isMaximizing: Boolean): Int {
        val score = heuristic_evaluation()
        if (score == 10 || score == -10) return score

        if (isMovesLeft().not()) return 0

        if (isMaximizing) {
            var best = Int.MIN_VALUE
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == "") {
                        board[i][j] = "O"
                        best = maxOf(best, minimax(depth + 1, false))
                        board[i][j] = ""
                    }
                }
            }
            return best
        } else {
            var best = Int.MAX_VALUE
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == "") {
                        board[i][j] = "X"
                        best = minOf(best, minimax(depth + 1, true))
                        board[i][j] = ""
                    }
                }
            }
            return best
        }
    }
    private fun isMovesLeft(): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == "") return true
            }
        }
        return false
    }
    fun findBestMove(): Pair<Int, Int> {
        var bestVal = Int.MIN_VALUE
        var bestMove = Pair(-1, -1)

        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == "") {
                    board[i][j] = "O"
                    val moveVal = minimax(0, false)
                    board[i][j] = ""

                    if (moveVal > bestVal) {
                        bestMove = Pair(i, j)
                        bestVal = moveVal
                    }
                }
            }
        }
        return bestMove
    }
}