package com.example.dialectkeyboard

enum class FlickDirection {
    CENTER, LEFT, UP, RIGHT, DOWN
}

enum class KeyType {
    CHAR, DAKUTEN, DELETE, SPACE_OR_NEXT, ENTER, SWITCH_EN, SWITCH_EMOJI, CURSOR_RIGHT, UNDO
}

data class KeyModel(
    val row: Int,
    val col: Int,
    val rowSpan: Int = 1,
    val colSpan: Int = 1,
    val type: KeyType,
    val centerText: String,
    val leftText: String = "",
    val upText: String = "",
    val rightText: String = "",
    val downText: String = ""
) {
    fun getChar(direction: FlickDirection): String {
        return when (direction) {
            FlickDirection.CENTER -> centerText
            FlickDirection.LEFT -> leftText.ifEmpty { centerText }
            FlickDirection.UP -> upText.ifEmpty { centerText }
            FlickDirection.RIGHT -> rightText.ifEmpty { centerText }
            FlickDirection.DOWN -> downText.ifEmpty { centerText }
        }
    }
}

enum class KeyboardMode {
    KANA_TENKEY,
    QWERTY_EN,
    EMOJI
}

interface KeyActionListener {
    fun onCharInput(char: String)
    fun onToggleDakuten()
    fun onDelete()
    fun onDeleteAll()
    fun onSpaceOrNext()
    fun onEnter()
    fun onSwitchLanguage(mode: KeyboardMode)
    fun onCursorRight()
    fun onUndo()
}