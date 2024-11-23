package com.example.contactapp.databseOperations

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity(tableName = "Contact_Table")
data class Contact(
    @PrimaryKey(autoGenerate = true) var id: Int = 0, // unique identifier for each item
    // autoGenerate therefore auto. increment
    var firstName: String,
    var lastName: String,
    var number: String,
    var nameColor: Int
    )