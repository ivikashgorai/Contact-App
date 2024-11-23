package com.example.contactapp.viewmodals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactapp.databseOperations.contactRepo

class ViewModalFactory(private val contactRepository: contactRepo):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(contactViewModal::class.java)){
            return contactViewModal(contactRepository) as T
        }
        return super.create(modelClass)
    }
}