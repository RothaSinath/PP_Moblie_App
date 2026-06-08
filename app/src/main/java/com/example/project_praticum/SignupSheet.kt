package com.example.project_praticum

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SignupSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSignUp = view.findViewById<Button>(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            // 1. Navigate to MainActivity instead of HomeActivity
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)

            // 2. Kill StartUpActivity so the user can't back-button into it
            activity?.finish()

            // 3. Dismiss this bottom sheet overlay cleanly
            dismiss()
        }
    }
}