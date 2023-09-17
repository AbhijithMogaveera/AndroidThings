package com.example.notifications.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.RemoteInput
import com.example.notifications.data.users.Message
import com.example.notifications.data.users.allGroups
import com.example.notifications.data.users.userFoo
import com.example.notifications.notification.util.showMessagingNotification

const val KEY_TEXT_REPLY = "KEY_TEXT_REPLY"

class ReplyBroadCastListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val remoteInput: Bundle = RemoteInput.getResultsFromIntent(intent) ?: return
        val replyText = remoteInput.getCharSequence(KEY_TEXT_REPLY).toString()
        val gpId = intent.getIntExtra("id", -1)
        allGroups.firstOrNull {
            it.id == gpId
        }?.let {
            showMessagingNotification(context, it.apply {
                addMessage(
                    Message(
                        message = replyText,
                        user = userFoo
                    )
                )
            })
        }
    }
}
