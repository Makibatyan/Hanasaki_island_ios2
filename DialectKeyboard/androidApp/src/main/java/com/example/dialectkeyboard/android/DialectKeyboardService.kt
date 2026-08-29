package com.example.dialectkeyboard.android

import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.dialectkeyboard.CojadsDictionary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class DialectKeyboardService : InputMethodService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val dictionary = CojadsDictionary()

    override fun onCreate() {
        super.onCreate()
        // assetsからの読み込みを安全に行う（ファイルがなくてもクラッシュさせない）
        try {
            val tsvData = applicationContext.assets.open("cojads_dict.tsv")
                .bufferedReader()
                .use { it.readText() }
            dictionary.loadFromTsvString(tsvData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onCreateInputView(): View {
        // レイアウトXMLを使わずコード上でビューを生成してクラッシュを防ぐ
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }
        val textView = TextView(this).apply {
            text = "Dialect Keyboard Active"
            textSize = 18f
        }
        layout.addView(textView)
        return layout
    }
}