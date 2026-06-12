package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translations")
data class TranslationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sourceLanguage: String,
    val targetLanguage: String,
    val sourceCode: String,
    val translatedCode: String,
    val timestamp: Long = System.currentTimeMillis()
)
