package com.example.blaezar.fragments

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blaezar.R
import com.example.blaezar.data.viewModel
import org.w3c.dom.Text
import java.io.FileReader


class UploadFragment : Fragment() {
    private val viewModel: viewModel by viewModels()
    private var selectedFileUri: Uri? = null
    private var fileNamee = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false)


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        val uploadButton = view.findViewById<Button>(R.id.uploadButton)

        val sendButton = view.findViewById<Button>(R.id.sendButton)




        uploadButton.setOnClickListener {
            openFilePicker()
        }
        sendButton.setOnClickListener {
            if (fileNamee != null || fileNamee != " ") {
                val fileName = fileNamee
                fileName?.let { it1 -> viewModel.testPush(it1) }
            } else {
                Toast.makeText(requireContext(), "No file selected", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/json", "application/zip"))

        val tempFile = createTempFile("temp", ".json", requireContext().getExternalFilesDir(null))

        val fileUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            tempFile
        )

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivityForResult(intent, FILE_PICK_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val fileName = getFileNameFromUri(uri)
                if (fileName != null) {
                    view?.let { view ->
                        viewModel.setFileName(fileName, view.findViewById<TextView>(R.id.selectedItemTv)
                        )
                        fileNamee=fileName
                    }
                    Toast.makeText(requireContext(), "Selected file: $fileName", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error getting file name", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun getFileNameFromUri(uri: Uri): String? {
        var fileName: String? = null
        try {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex != -1) {
                        fileName = it.getString(columnIndex)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fileName
    }



    companion object {
        const val FILE_PICK_REQUEST_CODE = 123 // Arbitrary request code
        private const val REQUEST_STORAGE_PERMISSION = 123 // Use any unique request code
    }
}
