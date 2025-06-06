package com.nhm.distributor.fcm
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.nhm.distributor.screens.interfaces.SMSListener
import com.google.android.gms.common.api.CommonStatusCodes.*
import com.google.android.gms.common.api.Status
import com.nhm.distributor.utils.parcelable

class VerifyBroadcastReceiver() : BroadcastReceiver() {
    companion object {
        private var smsListener: SMSListener? = null
        fun initSMSListener(listener: SMSListener) {
            smsListener = listener
        }
    }
    override fun onReceive(context: Context?, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val status = extras?.parcelable<Status>(SmsRetriever.EXTRA_STATUS)
                when (status!!.statusCode) {
                    SUCCESS -> {
                        val consentIntent = extras.parcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        smsListener?.onSuccess(consentIntent)
                    }
                    TIMEOUT -> {}
                }
            }
    }
}