package com.example.project_praticum

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var sessionManager: SessionManager

    private lateinit var btnBack: ImageView
    private lateinit var imgProfile: ImageView
    private lateinit var profileImageContainer: FrameLayout
    private lateinit var btnChangeImage: ImageView
    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var btnSave: TextView

    private var selectedImageUri: Uri? = null

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            imgProfile.setImageURI(uri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        bindViews(view)
        showUserData()
        setupActions()
    }

    private fun bindViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        imgProfile = view.findViewById(R.id.imgProfile)
        profileImageContainer = view.findViewById(R.id.profileImageContainer)
        btnChangeImage = view.findViewById(R.id.btnChangeImage)
        edtName = view.findViewById(R.id.edtName)
        edtEmail = view.findViewById(R.id.edtEmail)
        edtPassword = view.findViewById(R.id.edtPassword)
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword)
        btnSave = view.findViewById(R.id.btnSave)
    }

    private fun showUserData() {
        edtName.setText(sessionManager.getUserName())
        edtEmail.setText(sessionManager.getUserEmail())
        edtPassword.setText("")
        edtConfirmPassword.setText("")

        val avatarUrl = sessionManager.getUserAvatarUrl()

        if (!avatarUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(convertLocalhostUrl(avatarUrl))
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .circleCrop()
                .into(imgProfile)
        } else {
            imgProfile.setImageResource(R.drawable.profile)
        }
    }

    private fun setupActions() {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        profileImageContainer.setOnClickListener {
            imagePicker.launch("image/*")
        }

        btnChangeImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        btnSave.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile() {
        val token = sessionManager.getBearerToken()

        if (token == null) {
            Toast.makeText(requireContext(), "Please login again", Toast.LENGTH_SHORT).show()
            return
        }

        val name = edtName.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()
        val confirmPassword = edtConfirmPassword.text.toString().trim()

        if (name.isEmpty()) {
            edtName.error = "Name is required"
            return
        }

        if (email.isEmpty()) {
            edtEmail.error = "Email is required"
            return
        }

        if (password.isNotEmpty() || confirmPassword.isNotEmpty()) {
            if (password.length < 6) {
                edtPassword.error = "Password must be at least 6 characters"
                return
            }

            if (password != confirmPassword) {
                edtConfirmPassword.error = "Password does not match"
                return
            }
        }

        btnSave.isEnabled = false
        btnSave.text = "Saving..."

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val passwordBody = if (password.isNotEmpty()) {
                    password.toPlainRequestBody()
                } else {
                    null
                }

                val confirmPasswordBody = if (confirmPassword.isNotEmpty()) {
                    confirmPassword.toPlainRequestBody()
                } else {
                    null
                }

                val avatarPart = selectedImageUri?.let {
                    createImagePart(it)
                }

                val response = ApiClient.apiService.updateProfile(
                    token = token,
                    name = name.toPlainRequestBody(),
                    email = email.toPlainRequestBody(),
                    password = passwordBody,
                    passwordConfirmation = confirmPasswordBody,
                    avatar = avatarPart
                )

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!.user

                    sessionManager.saveUser(user)

                    if (!user.avatar_url.isNullOrBlank()) {
                        Glide.with(this@EditProfileFragment)
                            .load(convertLocalhostUrl(user.avatar_url))
                            .placeholder(R.drawable.profile)
                            .error(R.drawable.profile)
                            .circleCrop()
                            .into(imgProfile)
                    }

                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    val error = response.errorBody()?.string() ?: "Update failed"
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Connection error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                btnSave.isEnabled = true
                btnSave.text = "Save"
            }
        }
    }

    private fun String.toPlainRequestBody(): RequestBody {
        return this.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun createImagePart(uri: Uri): MultipartBody.Part {
        val file = uriToTempFile(uri)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = file.name,
            body = requestBody
        )
    }

    private fun uriToTempFile(uri: Uri): File {
        val fileName = getSafeFileName(uri)
        val tempFile = File(requireContext().cacheDir, fileName)

        requireContext().contentResolver.openInputStream(uri).use { input ->
            tempFile.outputStream().use { output ->
                input?.copyTo(output)
            }
        }

        return tempFile
    }

    private fun getSafeFileName(uri: Uri): String {
        var fileName = "profile_${System.currentTimeMillis()}.jpg"

        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (it.moveToFirst() && nameIndex >= 0) {
                val originalName = it.getString(nameIndex)
                val extension = originalName.substringAfterLast('.', "jpg")
                fileName = "profile_${System.currentTimeMillis()}.$extension"
            }
        }

        return fileName
    }

    private fun convertLocalhostUrl(url: String): String {
        return url
            .replace("http://localhost:8000", "http://127.0.0.1:8000")
            .replace("http://10.0.2.2:8000", "http://127.0.0.1:8000")
    }
}