package com.example.notifications.screens

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.notifications.NotificationActivityViewModel
import com.example.notifications.databinding.NotificationPermissionFragmentBinding

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MissingNotificationFragment : Fragment() {
    private val viewModel by lazy {
        ViewModelProvider(this)[NotificationActivityViewModel::class.java]
    }
    private var _binding: NotificationPermissionFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NotificationPermissionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRequestNotificationPermission.setOnClickListener {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(viewModel.permission), viewModel.requestCode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}