package com.example.contactapp


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.contactapp.databseOperations.Contact
import com.example.contactapp.entity.ContactDatabase
import kotlin.random.Random
import androidx.room.Room
import com.example.contactapp.databseOperations.contactRepo
import com.example.contactapp.viewmodals.ViewModalFactory
import com.example.contactapp.viewmodals.contactViewModal
import kotlin.text.isNotEmpty
import kotlin.text.toLong

// for customized font get font download
//res -> new -> android resource directory -> resource type font (paste the font file there)
// fontFamily = FontFamily(Font(R.font.Roboto_one))

//For room database
//add dependencies search in google

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val contactDatabase by lazy {
                Room.databaseBuilder(this.applicationContext, ContactDatabase::class.java, "contact_database")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            val repository by lazy{
                contactRepo(contactDatabase.contactDao())
            }
            val contactViewModel : contactViewModal by viewModels{ // pass the factory we created
                ViewModalFactory(repository)
            }
            MyApp(contactViewModel)
        }
    }
}

@Composable
fun MyApp(viewModal: contactViewModal) {
    var search by remember {
        mutableStateOf("")
    }


    Box {
        Column(
            modifier = Modifier
                .background(color = Color.Black)
                .fillMaxSize()
        ) {
            Search(search, {
                search = it
            viewModal.searchContact(it)})
                Person(viewModal)
        }
        AddContact(viewModal)
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
fun Person(viewModal: contactViewModal) {
        val sortedContacts by viewModal.contactList.collectAsState()

        LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
            items(sortedContacts) { contact ->
                DesignOfContact(
                    viewModal,
                    contact.id,
                    nameColor = Color(contact.nameColor),
                    firstName = contact.firstName.trim(),
                    lastName = contact.lastName.trim(),
                    number = contact.number.toLong()
                )
            }
        }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DesignOfContact(viewModal: contactViewModal,id:Int,nameColor:Color,firstName: String, lastName: String, number: Long) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .background(color = Color.Black)
            .fillMaxWidth()
            .height(70.dp),
        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = firstName.substring(0, 1),
            modifier = Modifier
                .padding(start = 30.dp)
                .clip(CircleShape)
                .height(45.dp)
                .width(45.dp)
                .background(Color(nameColor.red, nameColor.green, nameColor.blue))
                .wrapContentHeight(Alignment.CenterVertically),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Text(
            text = "$firstName $lastName\n$number",
            modifier = Modifier
                .padding(start = 40.dp)
                .width(150.dp),
            fontSize = 17.sp,
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.roboto_one))
        )

        var edit by remember {
            mutableStateOf(false)
        }

        Image(painter = painterResource(R.drawable.edit),"edit", modifier = Modifier
            .padding(end = 10.dp)
            .size(20.dp)
            .clickable(onClick = {
                edit = true
                viewModal.searchContactById(id)
            })
        )
        //edit contact
        var updateFirstName by remember {
            mutableStateOf("")
        }
        var updateLastName by remember {
            mutableStateOf("")
        }
        var updateNumber by remember {
            mutableStateOf("")
        }
        var updateContact: Contact = viewModal.updateContact
        updateFirstName = updateContact.firstName
        updateLastName = updateContact.lastName
        updateNumber = updateContact.number
        if(edit) {
            ModalBottomSheet(
                onDismissRequest = {
                    edit = false
                    updateFirstName = updateContact.firstName
                    updateLastName = updateContact.lastName
                    updateNumber = updateContact.number
                },
                containerColor = Color(60, 60, 60),
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        value = updateFirstName,
                        onValueChange = { updateFirstName = it },
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
                        value = updateLastName,
                        onValueChange = { updateLastName = it },
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
                        value = updateNumber,
                        onValueChange = { updateNumber = it },
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (updateFirstName.isNotEmpty() && updateNumber.isNotEmpty()) {
                                updateFirstName =
                                    updateFirstName.trim()[0].uppercase() + updateFirstName.substring(
                                        1
                                    )
                                if (updateLastName.isNotEmpty()) {
                                    updateLastName =
                                        updateLastName.trim()[0].uppercase() + updateLastName.substring(
                                            1
                                        )
                                }
//                                val contact = Contact(id = updateContact.id,nameColor = updateContact.nameColor,firstName = updateFirstName, lastName = updateLastName, number  = updateNumber)
                                viewModal.updateContact(
                                    id,
                                    updateFirstName,
                                    updateLastName,
                                    updateNumber
                                )
                                Toast.makeText(context,"Contact Updated", Toast.LENGTH_SHORT).show()
                                edit = false

                            } else if (updateFirstName.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Please Enter First Name",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please Enter Mobile Number",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 40.dp, end = 40.dp, bottom = 0.dp)
                            .height(50.dp)
                    )
                    {
                        Text(text = "Add Contact", fontSize = 15.sp)
                    }

                }
            }
        }


        var isShowDialog by remember {
            mutableStateOf(false)
        }

        LogOutDialog(isShowDialog,onDismiss={
            isShowDialog = false
        },onConfirm={
            viewModal.deleteContact(id)
            isShowDialog = false
        })
        Image(
            painter = painterResource(R.drawable.delete), "delete",
            modifier = Modifier
                .padding(start = 15.dp)
                .size(20.dp)
                .clickable(onClick = {
                    isShowDialog = true
//                viewModal.deleteContact(id)
                })
        )
    }
}

@Composable
 fun LogOutDialog(isShowDialog:Boolean,onDismiss:()->Unit,onConfirm:()->Unit){
     if(isShowDialog) {
         AlertDialog(onDismissRequest = {
             onDismiss()
         },
             confirmButton = {
                 TextButton(onClick = {
                     onConfirm()
                 }) {
                     Text("Yes")
                 }
             },
             dismissButton = {
                 TextButton(onClick = {
                     onDismiss()
                 }) {
                     Text("No")
                 }
             },
             title = {
                 Text("Delete Contact?")
             },
             text = {
                 Text("Are you sure you want to delete this contact?")
             }
         )
     }
 }





@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddContact(viewModal: contactViewModal) {
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (firstname.isNotEmpty() && number.isNotEmpty()) {
                            firstname = firstname.trim()[0].uppercase()+firstname.substring(1)
                            if(lastname.isNotEmpty()){
                                lastname =  lastname.trim()[0].uppercase()+lastname.substring(1)
                            }
                            val contact = Contact(nameColor = Color(Random.nextInt(256),Random.nextInt(256),Random.nextInt(256)).toArgb(),firstName = firstname, lastName = lastname, number  = number)
                            viewModal.addContact(contact)
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