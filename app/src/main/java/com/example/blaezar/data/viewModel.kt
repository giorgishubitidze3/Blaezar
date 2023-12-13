package com.example.blaezar.data

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader


class viewModel: ViewModel() {



    private val TAG = "YourTag"
    private val database = FirebaseDatabase.getInstance()
    private val myDataReference = database.getReference("myData")
    private val interestsReference = database.getReference("user_interests")

    fun getNumberOfItems(callback: (Int) -> Unit) {
        interestsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemCount = snapshot.childrenCount
                callback(itemCount.toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
                callback(-1) // Indicate an error with a negative value
            }
        })
    }

    // Fetch a specific item's data and set it as text for a TextView
    fun fetchAndSetItemData(textView: TextView) {
        interestsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jsonString = snapshot.getValue(String::class.java)

                if (jsonString != null) {
                    // Convert the JSON string back to jsonData object
                    val gson = Gson()
                    val jsonData = gson.fromJson(jsonString, jsonData::class.java)

                    // Check if jsonData is not null and has non-empty topics_v2
                    val firstTopic = jsonData?.topics_v2?.firstOrNull() ?: "No topics available"
                    textView.text = "First Topic: $firstTopic"
                } else {
                    // Handle the case where jsonString is null
                    textView.text = "No data available"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
                Log.e(TAG, "Failed to fetch item data.", error.toException())
            }
        })
    }


    private fun uploadInterests(jsonData: jsonData) {
        // Assuming you want to overwrite the existing data in 'user_interests'
        interestsReference.setValue(jsonData)
    }

    fun handleFileSelection(
        application: Application,
        appContext: Context,
        fileUri: Uri,
        callback: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val fileName = getFileName(application, fileUri)
            if (fileName != null) {
                callback(fileName)
            }

            try {
                // Read the JSON file from the assets folder
                val inputStream = fileName?.let { appContext.assets.open(it) }
                val reader = BufferedReader(InputStreamReader(inputStream))
                val jsonString = reader.use { it.readText() }

                // Parse JSON using Gson
                val gson = Gson()
                val jsonData = gson.fromJson(jsonString, jsonData::class.java)

                // Upload interests to Firebase
                uploadInterests(jsonData)
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle any errors during file reading or Firebase upload
                // For example, show an error message to the user
                callback("Error: $e")
            }
        }
    }

    fun getFileName(application: Application, uri: Uri): String? {
        // Get the file name from the URI
        val cursor = application.contentResolver.query(uri, null, null, null, null)
        val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor?.moveToFirst()
        val name = cursor?.getString(nameIndex ?: -1)
        cursor?.close()
        return name
    }
}