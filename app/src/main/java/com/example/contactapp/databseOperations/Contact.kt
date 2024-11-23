package com.example.contactapp.databseOperations

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Contact_Table")
data class Contact(
    @PrimaryKey(autoGenerate = true)   val id: Int = 0, // unique identifier for each item
    // autoGenerate therefore auto. increment
        val firstName: String,
        val lastName: String,
        val number: Long,
    )