package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val readTimeMinutes: Int,
    val summary: String,
    val content: String,
    val publishDate: String,
    val isBookmarked: Boolean = false
)
