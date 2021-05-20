package com.weather.info.ui.login

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.PhoneAuthProvider
import com.weather.info.base.viewmodel.BaseViewModel
import com.weather.info.data.AppDataManager
import com.weather.info.data.firebase.FireBaseAuthProvider
import com.weather.info.data.firebase.PhoneCallbacksListener
import com.weather.info.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    override val appDataManager: AppDataManager,
    override val fireBaseAuthProvider: FireBaseAuthProvider
) : BaseViewModel(appDataManager, fireBaseAuthProvider),
    PhoneCallbacksListener {

    companion object {
        private const val RESEND_WAIT_MILLIS: Long = 30000
        private const val TICK_INTERVAL_MILLIS: Long = 1000
    }

    val state = MutableLiveData<String>()

    var hintNotAsked = true

    init {
        fireBaseAuthProvider.setPhoneCallbacksListener(this)
        if (fireBaseAuthProvider.isUserVerified()) {
            state.value = Constants.MOVE_TO_HOME
        }
    }

    var selectedPhoneNumber = MutableLiveData<String>()
    var selectedOtpNumber = MutableLiveData<String>()

    var millisUntilFinished = RESEND_WAIT_MILLIS
    private val resendCodeLooper: Handler = Handler(Looper.getMainLooper())
    private val resendCodeCountdown = Runnable { processCountdownTick() }
    var showResendCodeText = MutableLiveData<Boolean>()

    var phone: String = ""
    var otp: String = ""

    fun sendOtpToPhone(activity: FragmentActivity, phone: String) {
        this.phone = phone
        state.value = Constants.SEND_OTP
        fireBaseAuthProvider.sendVerificationCode(activity, phone)
    }

    fun verifyOtp(otp: String) {
        this.otp = otp
        state.value = Constants.PERFORM_SIGN_IN
    }

    fun resendOtp(activity: FragmentActivity) {
        state.value = Constants.SEND_OTP
        fireBaseAuthProvider.resendCode(activity, phone)
        resetCountdownTick()
    }

    fun checkIfPhoneIsValid(phone: String): Boolean {
        return phone.let {
            !it.isBlank() && (it.length > 10)
        }
    }

    fun checkIfOtpIsValid(otp: String): Boolean {
        return otp.let {
            !it.isBlank() && (it.length == 6)
        }
    }

    fun processCountdownTick() {
        millisUntilFinished -= TICK_INTERVAL_MILLIS
        when {
            millisUntilFinished <= 0 -> {
                showResendCodeText.value = true
            }
            else -> {
                showResendCodeText.value = false
                resendCodeLooper.postDelayed(resendCodeCountdown, TICK_INTERVAL_MILLIS)
            }
        }
    }

    private fun resetCountdownTick() {
        showResendCodeText.value = false
        millisUntilFinished = RESEND_WAIT_MILLIS
        resendCodeLooper.postDelayed(resendCodeCountdown, TICK_INTERVAL_MILLIS)
    }

    fun clearCountdownTick() {
        resendCodeLooper.removeCallbacks(resendCodeCountdown)
    }

    override fun onVerificationCompleted() {
        success.value = "Verification Completed"
        state.value = Constants.MOVE_TO_HOME
    }

    override fun onVerificationCodeDetected(code: String) {
        Log.d(TAG, "AuthActivityViewModel onReceive: success $code")
        selectedOtpNumber.value = code
    }

    override fun onVerificationFailed(message: String) {
        Log.d(TAG, message)
        error.value = message
    }

    override fun onCodeSent(
        verificationId: String?,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        success.value = "OTP has sent"
        state.value = Constants.OTP_VERIFICATION_UI
    }

}