package com.weather.info.data.firebase

import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FireBaseAuthProvider @Inject constructor(val firebaseAuth: FirebaseAuth) {

    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var verificationId: String

    private lateinit var phoneCallbacksListener: PhoneCallbacksListener

    fun setPhoneCallbacksListener(listner: PhoneCallbacksListener) {
        this.phoneCallbacksListener = listner
    }

    init {
        firebaseAuth.setLanguageCode(Locale.getDefault().language)
    }

    private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    phoneCallbacksListener.onVerificationCodeDetected(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        // Invalid request
                        phoneCallbacksListener.onVerificationFailed(e.message ?: " ")
                    }
                    is FirebaseTooManyRequestsException -> {
                        // The SMS quota for the project has been exceeded
                        phoneCallbacksListener.onVerificationFailed(e.message ?: " ")
                    }
                    else -> {
                        phoneCallbacksListener.onVerificationFailed(e.message ?: " ")
                    }
                }
                Log.d(
                    "Firebase ---> ",
                    "FireBaseAuthProvider.onVerificationFailed e() ${e.message}"
                )
            }

            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
                resendToken = forceResendingToken
                phoneCallbacksListener.onCodeSent(s, forceResendingToken)
            }
        }

    fun sendVerificationCode(activity: FragmentActivity, phone: String) {
        val phoneAuthOption = PhoneAuthOptions.Builder(firebaseAuth).setPhoneNumber(phone.trim())
            .setTimeout(30, TimeUnit.SECONDS)
            .setActivity(activity).setCallbacks(callbacks).build()
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOption)
    }

    fun verifyVerificationCode(code: String): PhoneAuthCredential {
        return PhoneAuthProvider.getCredential(verificationId, code)
    }

    fun resendCode(activity: FragmentActivity, phone: String) {
        val phoneAuthOption =
            PhoneAuthOptions.Builder(firebaseAuth).setForceResendingToken(resendToken)
                .setPhoneNumber(phone.trim()).setTimeout(30, TimeUnit.SECONDS)
                .setActivity(activity).setCallbacks(callbacks).requireSmsValidation(true).build()
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOption)
    }

    fun isUserVerified(): Boolean {
        return firebaseAuth.currentUser != null
    }
}

interface PhoneCallbacksListener {
    fun onVerificationCompleted()
    fun onVerificationCodeDetected(code: String)
    fun onVerificationFailed(message: String)
    fun onCodeSent(
        verificationId: String?,
        token: PhoneAuthProvider.ForceResendingToken?
    )
}