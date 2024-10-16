package com.example.contactapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.example.contactapp.R
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var listOfaContacts = remember {
                mutableStateListOf<Contact>()
            }
            var search by remember {
                mutableStateOf("")
            }
            var isDeleteMode by remember {
                mutableStateOf(false)
            }
            var selectedContacts = remember {
                mutableStateListOf<Contact>()
            }

            // Handle back button to cancel delete mode
            BackHandler(enabled = isDeleteMode) {
                isDeleteMode = false
                selectedContacts.clear()
            }

            var searchList = listOfaContacts.filter {
                it.firstName.contains(search, ignoreCase = true) ||
                        it.lastName.contains(search, ignoreCase = true) ||
                        it.number.toString().contains(search)
            }
            Box {
                Column(
                    modifier = Modifier
                        .background(color = Color.Black)
                        .fillMaxSize()
                ) {
                    if (isDeleteMode) {
                        DeleteHeader(selectedContacts, listOfaContacts, onCancel = {
                            isDeleteMode = false
                            selectedContacts.clear()
                        })
                    } else {
                        Search(search, { search = it })
                    }

                    Person(
                        searchList as MutableList<Contact>,
                        isDeleteMode,
                        selectedContacts,
                        onLongPress = { contact ->
                            isDeleteMode = true
                            selectedContacts.add(contact)
                        },
                        onDeselectAll = {
                            // Automatically exit delete mode if no contacts are selected
                            isDeleteMode = false
                        }
                    )
                }
                if(!isDeleteMode){
                    AddContact(listOfaContacts)
                }
            }
        }
    }
}

// Function to toggle selection of a contact
fun toggleSelection(contact: Contact, selectedContacts: MutableList<Contact>) {
    if (selectedContacts.contains(contact)) {
        selectedContacts.remove(contact) // Deselect if already selected
    } else {
        selectedContacts.add(contact) // Select if not selected
    }
}

@Composable
fun Search(search: String, OnValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = search,
        onValueChange = {
            OnValueChange(it)
        },
        placeholder = { Text(text = "Search Contacts") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp, start = 20.dp, end = 20.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(color = Color(50, 50, 50)),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            unfocusedPlaceholderColor = Color(200, 200, 200),
        ),
        textStyle = TextStyle(fontSize = 16.sp),
        singleLine = true,
        leadingIcon = {
            Image(
                painter = painterResource(R.drawable.icons_search),
                contentDescription = "search icon",
                modifier = Modifier.size(25.dp)
            )
        }
    )
}

@Composable
fun Person(
    listOfaContacts: MutableList<Contact>,
    isDeleteMode: Boolean,
    selectedContacts: MutableList<Contact>,
    onLongPress: (Contact) -> Unit,
    onDeselectAll: () -> Unit // New callback for deselecting all
) {
    val sortedContacts = listOfaContacts.sortedBy { it.firstName.first() }

    LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
        items(sortedContacts) { contact ->
            val isSelected = selectedContacts.contains(contact)
            DesignOfContact(
                contact.firstName.trim(),
                contact.lastName.trim(),
                contact.number,
                isSelected,
                onLongPress = { selectedContact ->
                    if (isDeleteMode) {
                        if (isSelected) {
                            // Deselect contact
                            selectedContacts.remove(selectedContact)
                            if (selectedContacts.isEmpty()) {
                                // If no contacts are selected, trigger onDeselectAll callback
                                onDeselectAll()
                            }
                        } else {
                            // Select contact
                            selectedContacts.add(selectedContact)
                        }
                    } else {
                        onLongPress(selectedContact)
                    }
                },
                contact = contact,
                onClick = { clickedContact ->
                    // Same logic for onClick in delete mode
                    if (isDeleteMode) {
                        if (selectedContacts.contains(clickedContact)) {
                            // Deselect contact
                            selectedContacts.remove(clickedContact)
                            if (selectedContacts.isEmpty()) {
                                onDeselectAll()
                            }
                        } else {
                            // Select contact
                            selectedContacts.add(clickedContact)
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DesignOfContact(
    firstName: String,
    lastName: String,
    number: Long,
    isSelected: Boolean,
    onLongPress: (Contact) -> Unit,
    contact: Contact,
    onClick: (Contact) -> Unit // Adding onClick parameter for selection
) {
    Row(
        modifier = Modifier
            .background(color = Color.Black)
            .background(if (isSelected) Color.Red else Color.Black)
            .fillMaxWidth()
            .height(70.dp)
            .combinedClickable(
                onClick = { onClick(contact) }, // Handle click for selection/deselection
                onLongClick = { onLongPress(contact) } // Handle long press for enabling delete mode
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = firstName.substring(0, 1),
            modifier = Modifier
                .padding(start = 40.dp)
                .clip(CircleShape)
                .height(45.dp)
                .width(45.dp)
                .background(color = getRandomColor())
                .wrapContentHeight(Alignment.CenterVertically),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Text(
            text = "$firstName $lastName\n$number",
            modifier = Modifier.padding(start = 40.dp),
            fontSize = 17.sp,
            color = Color.White
        )
    }
}


fun getRandomColor(): Color {
    val red = Random.nextInt(0, 256)
    val green = Random.nextInt(0, 256)
    val blue = Random.nextInt(0, 256)

    return Color(red, green, blue)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddContact(listOfaContacts: MutableList<Contact>) {
    val context = LocalContext.current

    var bottomSheet by remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        FloatingActionButton(
            onClick = {
                bottomSheet = true
            },
            containerColor = Color(60, 60, 60),
            modifier = Modifier.padding(end = 35.dp, bottom = 50.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.plus),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }
    }
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }

    if (bottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                bottomSheet = false
                firstname = ""
                lastname = ""
                number = ""
            },
            containerColor = Color(60, 60, 60),
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = firstname,
                    onValueChange = { firstname = it },
                    modifier = Modifier.padding(top = 20.dp),
                    label = { Text(text = "First Name") },
                    textStyle = TextStyle(fontSize = 17.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = lastname,
                    onValueChange = { lastname = it },
                    modifier = Modifier.padding(top = 20.dp),
                    label = { Text(text = "Last Name") },
                    textStyle = TextStyle(fontSize = 17.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    modifier = Modifier.padding(top = 20.dp),
                    label = { Text(text = "Mobile Number") },
                    textStyle = TextStyle(fontSize = 17.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (firstname.isNotEmpty() && number.isNotEmpty()) {
                            val contact = Contact(firstname, lastname, number.toLong())
                            listOfaContacts.add(contact)
                            bottomSheet = false
                            firstname = ""
                            lastname = ""
                            number = ""
                        } else if (firstname.isEmpty()) {
                            Toast.makeText(context, "Please Enter First Name", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(context, "Please Enter Mobile Number", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 40.dp, end = 40.dp, bottom = 0.dp)
                        .height(50.dp)
                ) {
                    Text(text = "Add Contact", fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun DeleteHeader(
    selectedContacts: MutableList<Contact>,
    listOfaContacts: MutableList<Contact>,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp, start = 20.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onCancel) {
            Text(text = "Cancel", color = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = {
                // Delete selected contacts
                listOfaContacts.removeAll(selectedContacts)
                onCancel()
            }
        ) {
            Text(text = "Delete (${selectedContacts.size})", color = Color.White)
        }
    }
}

data class Contact(
    val firstName: String,
    val lastName: String,
    val number: Long,
)
