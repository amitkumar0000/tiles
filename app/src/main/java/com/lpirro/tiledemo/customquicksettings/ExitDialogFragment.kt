package com.lpirro.tiledemo.customquicksettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.lpirro.tiledemo.databinding.ExitDialogLayoutBinding

class ExitDialogFragment: DialogFragment() {

    lateinit var  binding: ExitDialogLayoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ExitDialogLayoutBinding.inflate(LayoutInflater.from(inflater.context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.okay.setOnClickListener {
            binding.passwordText.text
        }

        binding.cancel.setOnClickListener {
            dismiss()
        }
    }
}