package com.kky.pokedex.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "like")
data class Like(
    @PrimaryKey(autoGenerate = false)
    val id: Int
)