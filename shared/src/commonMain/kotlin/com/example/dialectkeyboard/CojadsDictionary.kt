package com.example.dialectkeyboard

/**
 * 方言辞書のデータモデル
 */
data class DialectEntry(
    val word: String,      // 方言（例: おおきに）
    val reading: String,   // 読み（例: おおきに）
    val region: String,    // 都道府県/地域（例: 大阪）
    val standard: String   // 標準語（例: ありがとう）
)

/**
 * Trieのノード構造
 */
class TrieNode {
    val children = mutableMapOf<Char, TrieNode>()
    val entries = mutableListOf<DialectEntry>()
}

/**
 * 共通で利用する辞書管理クラス
 */
class CojadsDictionary {
    private val root = TrieNode()
    private val allEntries = mutableListOf<DialectEntry>()

    /**
     * TSV形式の文字列データから辞書を初期化します
     * @param tsvContent cojads_dict.tsv の中身を文字列で渡す
     */
    fun loadFromTsvString(tsvContent: String) {
        root.children.clear()
        allEntries.clear()

        val lines = tsvContent.split("\n")
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue

            val parts = trimmed.split("\t")
            if (parts.size >= 4) {
                val entry = DialectEntry(
                    word = parts[0].trim(),
                    reading = parts[1].trim(),
                    region = parts[2].trim(),
                    standard = parts[3].trim()
                )
                allEntries.add(entry)
                insertToTrie(entry)
            }
        }
    }

    private fun insertToTrie(entry: DialectEntry) {
        var current = root
        for (ch in entry.reading) {
            current = current.children.getOrPut(ch) { TrieNode() }
        }
        current.entries.add(entry)
    }

    /**
     * 読み（ひらがな）の前方一致検索を行う関数
     */
    fun searchPrefixLocal(prefix: String, selectedPrefs: List<String>): List<DialectEntry> {
        if (prefix.isEmpty()) return emptyList()

        var current = root
        for (ch in prefix) {
            val nextNode = current.children[ch] ?: return emptyList()
            current = nextNode
        }

        val result = mutableListOf<DialectEntry>()
        collectAll(current, result)

        // 選択された地域フィルタリング
        if (selectedPrefs.isEmpty()) {
            return result
        }
        return result.filter { entry ->
            selectedPrefs.any { pref -> entry.region.contains(pref) }
        }
    }

    private fun collectAll(node: TrieNode, result: MutableList<DialectEntry>) {
        result.addAll(node.entries)
        for (child in node.children.values) {
            collectAll(child, result)
        }
    }
}