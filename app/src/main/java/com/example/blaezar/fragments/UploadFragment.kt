package com.example.blaezar.fragments

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
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
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blaezar.R
import com.example.blaezar.data.viewModel
import org.w3c.dom.Text


class UploadFragment : Fragment() {
    private val viewModel: viewModel by viewModels()
    private var selectedFileUri: Uri? = null

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

        val textView2 = view.findViewById<TextView>(R.id.itemsCount2)

        val testButton = view.findViewById<Button>(R.id.testButton)

        val sendButton = view.findViewById<Button>(R.id.sendButton)


        uploadButton.setOnClickListener {
            openFilePicker()
        }

        testButton.setOnClickListener {
            viewModel.testPush()
        }


        sendButton.setOnClickListener {
            selectedFileUri?.let { uri ->
                viewModel.handleFileSelection(
                    requireActivity().application,
                    requireContext(),
                    uri
                ) { result ->
                    // Handle the result as needed
                    Log.d("myTag", result)
                }
            } ?: run {
                Toast.makeText(requireContext(), "Please choose a file first", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.getNumberOfItems { itemCount ->
            if (itemCount >= 0) {
                view.findViewById<TextView>(R.id.itemsCount).text = "Number of items: $itemCount"
            } else {
                Toast.makeText(requireContext(), "Error getting item count", Toast.LENGTH_SHORT).show()
            }
            Log.d("ItemCountDebug", "Item count: $itemCount")
        }


        viewModel.fetchAndSetItemData(textView2)

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
                val filePath = getPathFromUri(requireActivity().application, uri)
                if (filePath != null) {
                    view?.findViewById<TextView>(R.id.selectedItemTv)?.text =
                        "Selected item: $filePath"
                    selectedFileUri = Uri.parse("file://$filePath")
                } else {
                    Toast.makeText(requireContext(), "Error getting file path", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPathFromUri(application: Application, uri: Uri): String? {
        var path: String? = null
        try {
            val cursor = application.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                    if (columnIndex != -1) {
                        path = it.getString(columnIndex)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return path
    }

    companion object {
        const val FILE_PICK_REQUEST_CODE = 123 // Arbitrary request code
    }
}
