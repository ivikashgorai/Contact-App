package com.example.contactapp.databseOperations

class contactRepo(private val contactDao: ContactDAO) {

    suspend fun getAllData() : List<Contact> = contactDao.getAllContact();
    suspend fun addContact(contact: Contact) = contactDao.addContact(contact)
    suspend fun deleteContact(id:Int) = contactDao.deleteContact(id)
    suspend fun searchContact(keyword:String) = contactDao.searchContact(keyword)
}