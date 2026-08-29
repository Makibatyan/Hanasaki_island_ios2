package com.example.dialectkeyboard.android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.dialectkeyboard.FlickDirection
import com.example.dialectkeyboard.KeyActionListener
import com.example.dialectkeyboard.KeyModel
import com.example.dialectkeyboard.KeyType
import com.example.dialectkeyboard.KeyboardMode
import kotlin.math.abs

class FlickKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var actionListener: KeyActionListener? = null
    var isComposing: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    private var currentMode: KeyboardMode = KeyboardMode.KANA_TENKEY

    private var touchDownX = 0f
    private var touchDownY = 0f
    private var activeKey: KeyModel? = null
    private var currentDirection: FlickDirection = FlickDirection.CENTER

    // 背景 & キー色設定（画像準拠）
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#DDE2E5")
    }
    private val keyBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val keySideBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CFD8DC") // 左右の機能キー
        style = Paint.Style.FILL
    }
    private val keyEnterBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1976D2") // 右下の確定青ボタン
        style = Paint.Style.FILL
    }
    private val keyActiveBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#BBDEFB") // タップ時ハイライト
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        textSize = 42f
    }
    private val whiteTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 42f
    }
    private val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#78909C")
        textAlign = Paint.Align.CENTER
        textSize = 22f
    }

    // 5列 × 4行 のキー配列
    private val kanaKeys = listOf(
        // Row 0
        KeyModel(0, 0, type = KeyType.CURSOR_RIGHT, centerText = "→"),
        KeyModel(0, 1, type = KeyType.CHAR, centerText = "あ", leftText = "い", upText = "う", rightText = "え", downText = "お"),
        KeyModel(0, 2, type = KeyType.CHAR, centerText = "か", leftText = "き", upText = "く", rightText = "け", downText = "こ"),
        KeyModel(0, 3, type = KeyType.CHAR, centerText = "さ", leftText = "し", upText = "す", rightText = "せ", downText = "そ"),
        KeyModel(0, 4, type = KeyType.DELETE, centerText = "⌫"),

        // Row 1
        KeyModel(1, 0, type = KeyType.UNDO, centerText = "↶"),
        KeyModel(1, 1, type = KeyType.CHAR, centerText = "た", leftText = "ち", upText = "つ", rightText = "て", downText = "と"),
        KeyModel(1, 2, type = KeyType.CHAR, centerText = "な", leftText = "に", upText = "ぬ", rightText = "ね", downText = "の"),
        KeyModel(1, 3, type = KeyType.CHAR, centerText = "は", leftText = "ひ", upText = "ふ", rightText = "へ", downText = "ほ"),
        KeyModel(1, 4, type = KeyType.SPACE_OR_NEXT, centerText = "空白"),

        // Row 2
        KeyModel(2, 0, type = KeyType.SWITCH_EN, centerText = "ABC"),
        KeyModel(2, 1, type = KeyType.CHAR, centerText = "ま", leftText = "み", upText = "む", rightText = "め", downText = "も"),
        KeyModel(2, 2, type = KeyType.CHAR, centerText = "や", leftText = "（", upText = "ゆ", rightText = "）", downText = "よ"),
        KeyModel(2, 3, type = KeyType.CHAR, centerText = "ら", leftText = "り", upText = "る", rightText = "れ", downText = "ろ"),
        KeyModel(2, 4, rowSpan = 2, type = KeyType.ENTER, centerText = "→"), // 縦2マス結合

        // Row 3
        KeyModel(3, 0, type = KeyType.SWITCH_EMOJI, centerText = "😀"),
        KeyModel(3, 1, type = KeyType.DAKUTEN, centerText = "小/゛"),
        KeyModel(3, 2, type = KeyType.CHAR, centerText = "わ", leftText = "を", upText = "ん", rightText = "ー", downText = "〜"),
        KeyModel(3, 3, type = KeyType.CHAR, centerText = "、", leftText = "。", upText = "？！", rightText = "", downText = "")
    )

    private val rows = 4
    private val cols = 5

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val density = context.resources.displayMetrics.density
        val height = (235 * density).toInt()
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        val cellW = width.toFloat() / cols
        val cellH = height.toFloat() / rows
        val margin = 3f

        for (key in kanaKeys) {
            val left = key.col * cellW + margin
            val top = key.row * cellH + margin
            val right = (key.col + key.colSpan) * cellW - margin
            val bottom = (key.row + key.rowSpan) * cellH - margin
            val rect = RectF(left, top, right, bottom)

            val isPressed = (key == activeKey)
            val paint = when {
                isPressed -> keyActiveBgPaint
                key.type == KeyType.ENTER -> keyEnterBgPaint
                key.col == 0 || (key.col == 4 && key.row < 2) -> keySideBgPaint
                else -> keyBgPaint
            }

            canvas.drawRoundRect(rect, 10f, 10f, paint)

            val isEnter = (key.type == KeyType.ENTER)
            val p = if (isEnter) whiteTextPaint else textPaint

            val displayCenterText = if (isEnter && isComposing) "確定" else key.centerText
            val centerX = rect.centerX()
            val centerY = rect.centerY() - ((p.descent() + p.ascent()) / 2)

            canvas.drawText(displayCenterText, centerX, centerY, p)

            // フリックガイド文字の描画
            if (key.type == KeyType.CHAR) {
                if (key.leftText.isNotEmpty()) canvas.drawText(key.leftText, rect.left + 16f, rect.centerY() + 8f, subTextPaint)
                if (key.upText.isNotEmpty()) canvas.drawText(key.upText, centerX, rect.top + 24f, subTextPaint)
                if (key.rightText.isNotEmpty()) canvas.drawText(key.rightText, rect.right - 16f, rect.centerY() + 8f, subTextPaint)
                if (key.downText.isNotEmpty()) canvas.drawText(key.downText, centerX, rect.bottom - 8f, subTextPaint)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val cellW = width.toFloat() / cols
        val cellH = height.toFloat() / rows

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                touchDownX = event.x
                touchDownY = event.y
                val col = (event.x / cellW).toInt().coerceIn(0, cols - 1)
                val row = (event.y / cellH).toInt().coerceIn(0, rows - 1)

                // 2マス結合されている Enter ボタンの判定対応
                activeKey = kanaKeys.find { key ->
                    val cEnd = key.col + key.colSpan
                    val rEnd = key.row + key.rowSpan
                    col >= key.col && col < cEnd && row >= key.row && row < rEnd
                }
                currentDirection = FlickDirection.CENTER
                invalidate()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                activeKey?.let {
                    val dx = event.x - touchDownX
                    val dy = event.y - touchDownY
                    val threshold = 35f

                    currentDirection = if (abs(dx) > threshold || abs(dy) > threshold) {
                        if (abs(dx) > abs(dy)) {
                            if (dx > 0) FlickDirection.RIGHT else FlickDirection.LEFT
                        } else {
                            if (dy > 0) FlickDirection.DOWN else FlickDirection.UP
                        }
                    } else {
                        FlickDirection.CENTER
                    }
                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                activeKey?.let { key ->
                    when (key.type) {
                        KeyType.CHAR -> {
                            val inputChar = key.getChar(currentDirection)
                            actionListener?.onCharInput(inputChar)
                        }
                        KeyType.DELETE -> actionListener?.onDelete()
                        KeyType.DAKUTEN -> actionListener?.onToggleDakuten()
                        KeyType.SPACE_OR_NEXT -> actionListener?.onSpaceOrNext()
                        KeyType.ENTER -> actionListener?.onEnter()
                        KeyType.CURSOR_RIGHT -> actionListener?.onCursorRight()
                        KeyType.UNDO -> actionListener?.onUndo()
                        KeyType.SWITCH_EN -> actionListener?.onSwitchLanguage(KeyboardMode.QWERTY_EN)
                        KeyType.SWITCH_EMOJI -> actionListener?.onSwitchLanguage(KeyboardMode.EMOJI)
                    }
                }
                activeKey = null
                currentDirection = FlickDirection.CENTER
                invalidate()
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                activeKey = null
                currentDirection = FlickDirection.CENTER
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}