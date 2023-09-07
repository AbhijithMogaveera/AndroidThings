package com.example.notifications.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notifications.data.users.AndroidGroup
import com.example.notifications.data.users.IosGroup
import com.example.notifications.databinding.FragmentSecondBinding
import com.example.notifications.notification.util.audio.PlayerAction
import com.example.notifications.notification.util.audio.audioPlayerManager
import com.example.notifications.notification.util.showExpandableNotification
import com.example.notifications.notification.util.showMessagingNotification
import com.example.notifications.notification.util.showProcessNotification
import com.example.notifications.notification.util.showSimpleNotification

class NotificationSampleFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.simpleNotification.setOnClickListener {
            showSimpleNotification(requireContext(), 0)
        }

        binding.exapandabelNotification.setOnClickListener {
            showExpandableNotification(requireContext(), 1)
        }

        binding.showMessagingNotification.setOnClickListener {
            showMessagingNotification(requireContext(), AndroidGroup)
            showMessagingNotification(requireContext(), IosGroup)
        }

        binding.showProgressNotification.setOnClickListener {
            showProcessNotification((0..100).random(), 3, requireContext())
        }

        binding.showMediaNotification.setOnClickListener {
            audioPlayerManager.updatePlayer(PlayerAction.Play)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}



