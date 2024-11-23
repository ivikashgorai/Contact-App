package com.example.contactapp.databseOperations

import androidx.compose.ui.graphics.Color

class contactRepo(private val contactDao: ContactDAO) {

    suspend fun getAllData() : List<Contact> = contactDao.getAllContact()
    suspend fun addContact(contact: Contact) = contactDao.addContact(contact)
    suspend fun deleteContact(id:Int) = contactDao.deleteContact(id)
    suspend fun searchContact(keyword:String): List<Contact> = contactDao.searchContact(keyword)
    suspend fun searchContactByID(id:Int):Contact = contactDao.searchContactByID(id)
    suspend fun updateContact(id:Int,firstName:String,lastName:String,number:String) = contactDao.updateContact(id,firstName,lastName,number)
}