package com.example.dialectkeyboard

object DialectRegionManager {
    val REGION_MAP = mapOf(
        "北海道" to listOf("北海道"),
        "東北" to listOf("青森", "岩手", "宮城", "秋田", "山形", "福島"),
        "関東" to listOf("茨城", "栃木", "群馬", "埼玉", "千葉", "東京", "神奈川"),
        "中部" to listOf("新潟", "富山", "石川", "福井", "山梨", "長野", "岐阜", "静岡", "愛知"),
        "近畿" to listOf("三重", "滋賀", "京都", "大阪", "兵庫", "奈良", "和歌山"),
        "中国・四国" to listOf("鳥取", "島根", "岡山", "広島", "山口", "徳島", "香川", "愛媛", "高知"),
        "九州・沖縄" to listOf("福岡", "佐賀", "長崎", "熊本", "大分", "宮崎", "鹿児島", "沖縄")
    )

    fun getAllPrefectures(): Set<String> {
        return REGION_MAP.values.flatten().toSet()
    }
}