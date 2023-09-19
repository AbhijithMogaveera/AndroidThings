package com.example.notifications.screens

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.notifications.data.users.AndroidGroup
import com.example.notifications.data.users.IosGroup
import com.example.notifications.databinding.FragmentSecondBinding
import com.example.notifications.notification.util.audio.PlayerAction
import com.example.notifications.notification.util.audio.PlayerNotificationListener
import com.example.notifications.notification.util.audio.audioPlayerManager
import com.example.notifications.notification.util.audio.prepareM3Notification
import com.example.notifications.notification.util.showExpandableNotification
import com.example.notifications.notification.util.showMessagingNotification
import com.example.notifications.notification.util.showProcessNotification
import com.example.notifications.notification.util.showSimpleNotification
import com.example.notifications.services.MediaPlayerNotificationService
import com.example.notifications.services.PlaybackService


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

        binding.showMedia3NotificationA.setOnClickListener {
            audioPlayerManager.updatePlayer(PlayerAction.Play)
            if(!audioPlayerManager.isSessionActive) {
                audioPlayerManager.prepareM3Notification(
                    onPrepared = { mediaSession, notificationManager ->
                    },
                    playerNotificationListener = PlayerNotificationListener(),
                )
            }
        }
        var isCustomPlayerMedia3ServiceStarted = false
        binding.showMedia3NotificationB.setOnClickListener {
            if(isCustomPlayerMedia3ServiceStarted)
                return@setOnClickListener
            isCustomPlayerMedia3ServiceStarted = true
            val sessionToken = SessionToken(
                requireContext().applicationContext,
                ComponentName(requireContext().applicationContext, PlaybackService::class.java)
            )
            MediaController.Builder(requireContext().applicationContext, sessionToken).buildAsync()
        }

        var isCustomPlayerNotificationServiceStarted = true
        binding.showCustomPlayerNotification.setOnClickListener {
            if (isCustomPlayerNotificationServiceStarted) {
                val serviceIntent =
                    Intent(requireActivity(), MediaPlayerNotificationService::class.java).apply {
                        action = MediaPlayerNotificationService.ACTION_SHOW_NOTIFICATION
                    }
                requireActivity().startService(serviceIntent)
                isCustomPlayerNotificationServiceStarted = false
                audioPlayerManager.updatePlayer(PlayerAction.Play)
            } else {
                val serviceIntent =
                    Intent(requireActivity(), MediaPlayerNotificationService::class.java).apply {
                        action = MediaPlayerNotificationService.ACTION_HIDE_NOTIFICATION
                    }
                requireActivity().startService(serviceIntent)
                isCustomPlayerNotificationServiceStarted = true
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}



