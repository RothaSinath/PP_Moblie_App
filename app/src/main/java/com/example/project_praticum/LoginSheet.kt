package com.example.project_praticum

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LoginSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSignIn = view.findViewById<Button>(R.id.btnLogin)

        btnSignIn.setOnClickListener {
            // 1. Fire intent to launch MainActivity
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)

            // 2. Close StartUpActivity so the user can't back-button into it
            activity?.finish()

            // 3. Dismiss this sheet overlay cleanly
            dismiss()
        }
    }
}