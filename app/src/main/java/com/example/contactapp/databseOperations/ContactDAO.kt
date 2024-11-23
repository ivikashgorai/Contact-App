package com.example.contactapp.databseOperations

import androidx.compose.ui.graphics.Color
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

// sometimes according to use we have to develop the code here
@Dao
interface ContactDAO {
    @Query("SELECT * FROM Contact_Table")
    suspend fun getAllContact(): List<Contact> // this fun will get data from database

   @Insert // uses for inserting in database
    suspend fun addContact(contact: Contact) // this fun inserts the data

    @Query("DELETE FROM Contact_Table WHERE id = :id")// this will delete the contact
    suspend fun deleteContact(id:Int)

    @Query("SELECT * FROM Contact_Table WHERE firstName LIKE '%' || :keyword || '%' OR lastName LIKE '%' || :keyword || '%' OR number LIKE '%' || :keyword || '%'")
    // for searching the contact
     suspend fun searchContact(keyword:String) : List<Contact>

    @Query("SELECT * FROM Contact_Table WHERE id = :id")// Search by id
    suspend fun searchContactByID(id:Int) : Contact

    @Query("UPDATE Contact_Table SET firstName = :firstName, lastName = :lastName, number = :number WHERE id = :id")
    suspend fun updateContact(id: Int, firstName: String, lastName: String, number: String)
}