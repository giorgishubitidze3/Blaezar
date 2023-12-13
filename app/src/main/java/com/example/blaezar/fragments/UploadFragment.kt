package com.example.blaezar.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blaezar.R
import com.example.blaezar.data.viewModel
import org.w3c.dom.Text


class UploadFragment : Fragment() {
    private val viewModel: viewModel by viewModels()


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


        uploadButton.setOnClickListener {
            openFilePicker()
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
        startActivityForResult(intent, FILE_PICK_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                // Update the TextView with the selected file name
                view?.findViewById<TextView>(R.id.selectedItemTv)?.text =
                    "Selected item: ${viewModel.getFileName(requireActivity().application, uri)}"
                // Handle the selected file URI (e.g., upload it to Firebase)
                viewModel.handleFileSelection(
                    requireActivity().application,
                    requireContext(),
                    uri
                ) { result ->
                    // Handle the result as needed
                    println(result)
                }
            }
        }
    }



    companion object {
        const val FILE_PICK_REQUEST_CODE = 123 // Arbitrary request code
    }
}
