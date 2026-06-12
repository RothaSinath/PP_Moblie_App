package com.example.project_praticum

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class AccountFragment : Fragment(R.layout.fragment_account) {

    private lateinit var sessionManager: SessionManager

    private var btnBack: ImageButton? = null
    private var btnEdit: Button? = null
    private var btnLogout: LinearLayout? = null
    private var popupLogout: RelativeLayout? = null
    private var btnCancelLogout: TextView? = null
    private var btnConfirmLogout: TextView? = null
    private var txtProfileName: TextView? = null
    private var imgProfile: ImageView? = null

    private var btnNotification: LinearLayout? = null
    private var btnSecurity: LinearLayout? = null
    private var btnPaymentMethods: LinearLayout? = null
    private var btnHelpAndSupport: LinearLayout? = null

    private var edtName: EditText? = null
    private var edtEmail: EditText? = null
    private var edtPassword: EditText? = null
    private var edtConfirmPassword: EditText? = null
    private var btnSave: TextView? = null

    private var didInitialLoad = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        bindViews(view)
        showLocalUserData()
        loadUserFromApi()
        setupActions()

        didInitialLoad = true
    }

    override fun onResume() {
        super.onResume()

        if (didInitialLoad) {
            showLocalUserData()
            loadUserFromApi()
        }
    }

    private fun bindViews(view: View) {
        btnBack = findViewByIdIfExists(view, "btnBack")
        btnEdit = findViewByIdIfExists(view, "btnEdit")
        btnLogout = findViewByIdIfExists(view, "btnLogout")
        popupLogout = findViewByIdIfExists(view, "popup_Logout")
        btnCancelLogout = findViewByIdIfExists(view, "btnCancelLogout")
        btnConfirmLogout = findViewByIdIfExists(view, "btnConfirmLogout")
        txtProfileName = findViewByIdIfExists(view, "txtProfileName")
        imgProfile = findViewByIdIfExists(view, "imgProfile")

        btnNotification = findViewByIdIfExists(view, "btnNotification")
        btnSecurity = findViewByIdIfExists(view, "btnSecurity")
        btnPaymentMethods = findViewByIdIfExists(view, "btnPaymentMethods")
        btnHelpAndSupport = findViewByIdIfExists(view, "btnHelpAndSupport")

        edtName = findViewByIdIfExists(view, "edtName")
        edtEmail = findViewByIdIfExists(view, "edtEmail")
        edtPassword = findViewByIdIfExists(view, "edtPassword")
        edtConfirmPassword = findViewByIdIfExists(view, "edtConfirmPassword")
        btnSave = findViewByIdIfExists(view, "btnSave")
    }

    private fun showLocalUserData() {
        val name = sessionManager.getUserName()
        val email = sessionManager.getUserEmail()
        val avatarUrl = sessionManager.getUserAvatarUrl()

        txtProfileName?.text = name.uppercase()
        edtName?.setText(name)
        edtEmail?.setText(email)
        edtPassword?.setText("")
        edtConfirmPassword?.setText("")

        loadProfileImage(avatarUrl)
    }

    private fun loadUserFromApi() {
        val token = sessionManager.getBearerToken()

        if (token == null) {
            goToStartUp()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getUser(token)

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    val rawToken = sessionManager.getToken()

                    if (!rawToken.isNullOrBlank()) {
                        sessionManager.saveAuth(rawToken, user)
                    }

                    txtProfileName?.text = user.name.uppercase()
                    edtName?.setText(user.name)
                    edtEmail?.setText(user.email)
                    edtPassword?.setText("")
                    edtConfirmPassword?.setText("")

                    loadProfileImage(user.avatar_url)
                } else if (response.code() == 401) {
                    sessionManager.clearSession()
                    goToStartUp()
                } else {
                    Log.e(
                        "AccountProfile",
                        "Get user failed: ${response.code()} ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("AccountProfile", "Failed to load user", e)

                Toast.makeText(
                    requireContext(),
                    "Connection error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loadProfileImage(avatarUrl: String?) {
        val imageView = imgProfile ?: return

        if (!avatarUrl.isNullOrBlank()) {
            val finalUrl = convertLocalhostUrl(avatarUrl) + "?t=${System.currentTimeMillis()}"

            Log.d("AccountProfile", "Loading profile image: $finalUrl")

            Glide.with(imageView.context)
                .load(finalUrl)
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .circleCrop()
                .into(imageView)
        } else {
            Log.d("AccountProfile", "No avatar url")
            imageView.setImageResource(R.drawable.profile)
        }
    }

    private fun setupActions() {
        btnBack?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        btnEdit?.setOnClickListener {
            openEditProfile()
        }

        btnNotification?.setOnClickListener {
            openFragment(NotificationFragment())
        }

        btnSecurity?.setOnClickListener {
            openFragment(SecurityFragment())
        }

        btnPaymentMethods?.setOnClickListener {
            openFragment(PaymentMethodsFragment())
        }

        btnHelpAndSupport?.setOnClickListener {
            openFragment(HelpSupportFragment())
        }

        btnLogout?.setOnClickListener {
            popupLogout?.visibility = View.VISIBLE
        }

        btnCancelLogout?.setOnClickListener {
            popupLogout?.visibility = View.GONE
        }

        btnConfirmLogout?.setOnClickListener {
            logout()
        }

        btnSave?.setOnClickListener {
            validateProfileInput()
        }
    }

    private fun openFragment(fragment: Fragment) {
        val containerId = (requireView().parent as? ViewGroup)?.id

        if (containerId == null || containerId == View.NO_ID) {
            Toast.makeText(requireContext(), "Fragment container not found", Toast.LENGTH_SHORT).show()
            return
        }

        parentFragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun openEditProfile() {
        val containerId = (requireView().parent as? ViewGroup)?.id

        if (containerId == null || containerId == View.NO_ID) {
            Toast.makeText(requireContext(), "Fragment container not found", Toast.LENGTH_SHORT).show()
            return
        }

        parentFragmentManager.beginTransaction()
            .replace(containerId, EditProfileFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun validateProfileInput() {
        val name = edtName?.text?.toString()?.trim().orEmpty()
        val email = edtEmail?.text?.toString()?.trim().orEmpty()
        val password = edtPassword?.text?.toString()?.trim().orEmpty()
        val confirmPassword = edtConfirmPassword?.text?.toString()?.trim().orEmpty()

        if (name.isEmpty()) {
            edtName?.error = "Name is required"
            return
        }

        if (email.isEmpty()) {
            edtEmail?.error = "Email is required"
            return
        }

        if (password.isNotEmpty() || confirmPassword.isNotEmpty()) {
            if (password.length < 6) {
                edtPassword?.error = "Password must be at least 6 characters"
                return
            }

            if (password != confirmPassword) {
                edtConfirmPassword?.error = "Password does not match"
                return
            }
        }

        Toast.makeText(
            requireContext(),
            "Use Edit Profile to update account",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun logout() {
        val token = sessionManager.getBearerToken()

        popupLogout?.visibility = View.GONE

        if (token == null) {
            sessionManager.clearSession()
            goToStartUp()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                ApiClient.apiService.logout(token)
            } catch (_: Exception) {
            } finally {
                sessionManager.clearSession()
                Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
                goToStartUp()
            }
        }
    }

    private fun goToStartUp() {
        val intent = Intent(requireContext(), StartUpActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun convertLocalhostUrl(url: String): String {
        return url
            .replace("http://localhost:8000", "http://127.0.0.1:8000")
            .replace("http://10.0.2.2:8000", "http://127.0.0.1:8000")
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : View> findViewByIdIfExists(view: View, idName: String): T? {
        val id = resources.getIdentifier(idName, "id", requireContext().packageName)
        return if (id != 0) view.findViewById(id) as? T else null
    }
}