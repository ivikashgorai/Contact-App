package com.example.contactapp.viewmodals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.contactapp.databseOperations.Contact
import com.example.contactapp.databseOperations.contactRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class contactViewModal(private val contactRepository: contactRepo): ViewModel() {

    val contactList = MutableStateFlow<List<Contact>>(emptyList())
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

}