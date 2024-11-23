package com.example.contactapp.entity

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.contactapp.databseOperations.Contact
import com.example.contactapp.databseOperations.ContactDAO

// require no changes in this file
@Database(entities = [Contact::class], version =4)
abstract class ContactDatabase : RoomDatabase() {// this abstract because room jobs to write code
     abstract fun contactDao() : ContactDAO
}