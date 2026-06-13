package com.example.backend.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uuid: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val phone: String,
    val relationship: String,
    val isPrimary: Boolean = false,
    val user_email: String = ""
)
