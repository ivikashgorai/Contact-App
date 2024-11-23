package com.example.contactapp.viewmodals

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.findFirstRoot
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.contactapp.databseOperations.Contact
import com.example.contactapp.databseOperations.contactRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Optional.empty
import kotlin.random.Random

class contactViewModal(private val contactRepository: contactRepo): ViewModel() { // not return here

    val contactList = MutableStateFlow<List<Contact>>(emptyList())
    var updateContact : Contact = Contact(firstName = "", lastName = "", number = "", id = -1, nameColor = -1)


    init {
        getAllData() // whenever app launches it calls this function
    }

    fun getAllData(){
        viewModelScope.launch(Dispatchers.IO) {
            val allContact = contactRepository.getAllData()
            contactList.value = allContact
        }
    }

    fun addContact(contact: Contact){
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.addContact(contact)
            getAllData()
        }
    }

    fun deleteContact(id:Int){
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.deleteContact(id)
            getAllData()
        }
    }

    fun searchContact(keyword:String){
        viewModelScope.launch(Dispatchers.IO) {
            val searchContact = contactRepository.searchContact(keyword)
            contactList.value = searchContact
        }
    }

     fun searchContactById(id:Int){
        viewModelScope.launch(Dispatchers.IO) {
            val searchContactId = contactRepository.searchContactByID(id)
            updateContact = searchContactId
        }
    }

    fun updateContact(id:Int,firstName:String,lastName:String,number:String){
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.updateContact(id,firstName,lastName,number)
            getAllData()
        }
    }
}