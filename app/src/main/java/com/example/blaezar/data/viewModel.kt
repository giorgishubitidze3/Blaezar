package com.example.blaezar.data

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
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
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.InputStreamReader
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.nio.file.Files.getOwner


class viewModel: ViewModel() {


    private val database = FirebaseDatabase.getInstance()
    private val myDataReference = database.reference
    private val interestsReference = database.getReference("user_interests")


    fun testPush(fileName: String){
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName)
        val result = file.readText()
        myDataReference.setValue(result)
    }


    fun setFileName(name:String, textView: TextView){
        textView.text = name
    }




}