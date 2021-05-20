package com.weather.info.ui.login

import android.app.Activity
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.weather.info.R
import com.weather.info.base.BaseActivity
import com.weather.info.databinding.ActivityLoginBinding
import com.weather.info.ui.dashboard.DashboardActivity
import com.weather.info.utils.Constants
import com.weather.info.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    private val loginViewModel: LoginViewModel by viewModels()

    override val layoutRes: Int = R.layout.activity_login

    override fun getViewModel(): LoginViewModel = loginViewModel

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    companion object {
        fun startInstanceWithBackStackCleared(context: Context?) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intent)
        }
    }

    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val consentIntent =
                            extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        try {
                            val registerIntent =
                                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                                    if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                                        val message =
                                            it.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                                        val oneTimeCode = message?.substring(0, 6)
                                        getViewModel().selectedOtpNumber.value = oneTimeCode?.trim()
                                    }
                                }
                            registerIntent.launch(consentIntent)
                        } catch (e: ActivityNotFoundException) {
                            getViewModel().error.value = (e.message ?: "Something went wrong")
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        // Time out occurred, handle the error.
                    }
                }
            }
        }
    }

    override fun onReadyToRender(
        viewModel: LoginViewModel,
        binder: ActivityLoginBinding,
        savedInstanceState: Bundle?
    ) {
        binder.viewModel = viewModel

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationReceiver, intentFilter)

        binder.buttonVerifyPhone.setOnClickListener {
            hideKeyboard()
            if (viewModel.checkIfPhoneIsValid(binder.etMobile.text.toString())) {
                viewModel.sendOtpToPhone(this, binder.etMobile.text.toString())
            } else {
                binder.mobile.error = "Invalid Phone: Please enter phone number with country code"
            }
        }

    }

    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val credentialsClient = Credentials.getClient(this)
        val intent = credentialsClient.getHintPickerIntent(hintRequest)

        val result =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                    val credential = it.data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                    getViewModel().selectedPhoneNumber.value = credential?.id
                }
            }
        getViewModel().hintNotAsked = false
        result.launch(IntentSenderRequest.Builder(intent).build())
    }

    override fun onStart() {
        super.onStart()
        if (getViewModel().hintNotAsked) {
            requestHint()
        }
    }

    override fun subscribeObserver() {
        super.subscribeObserver()

        getViewModel().state.observe(this) {
            if (it != null && it.isNotEmpty()) {
                when (it) {
                    Constants.SEND_OTP -> {
                        startSMSListener()
                    }
                    Constants.MOVE_TO_HOME -> {
                        goToHomeActivity()
                    }
                    Constants.PERFORM_SIGN_IN -> {
                        signInWithPhoneAuthCredential(
                            getViewModel().fireBaseAuthProvider.verifyVerificationCode(
                                getViewModel().otp
                            )
                        )
                    }
                    Constants.OTP_VERIFICATION_UI -> {
                        setupOtpView()
                    }

                }

                getViewModel().state.value = null
            }
        }

    }

    private fun setupOtpView() {
        binder.bottom.visibility = View.VISIBLE
        getViewModel().processCountdownTick()

        val spannable = SpannableStringBuilder("Didn't received OTP? Resend")
        spannable.setSpan(
            ForegroundColorSpan(Color.GRAY),
            spannable.indexOf("Resend"), spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binder.textViewTicker.text = spannable

        binder.buttonVerifyOtp.setOnClickListener {
            if (getViewModel().checkIfOtpIsValid(binder.otpEditText.text.toString())) {
                getViewModel().verifyOtp(binder.otpEditText.text.toString())
            } else {
                getViewModel().error.value = "Invalid Otp: Please enter correct OTP"
            }
        }

        binder.textViewTicker.setOnClickListener {
            getViewModel().resendOtp(this)
        }

        getViewModel().selectedOtpNumber.observe(this, { value ->
            binder.otpEditText.setText(value ?: "")
        })

        getViewModel().showResendCodeText.observe(this) {
            updateResentText(it)
        }
    }

    private fun updateResentText(shouldShowResend: Boolean?) {
        when (shouldShowResend) {
            true -> {
                binder.textViewTicker.visibility = View.VISIBLE
                binder.textViewTicker.text = getString(R.string.resent_otp)
                binder.textViewTicker.setOnClickListener {

                }
            }
            false -> {
                binder.textViewTicker.visibility = View.VISIBLE
                binder.textViewTicker.text = String.format(
                    getString(R.string.resent_otp_timer),
                    getViewModel().millisUntilFinished.div(1000)
                )
                binder.textViewTicker.setOnClickListener(null)
            }
            else -> {
                binder.textViewTicker.visibility = View.GONE
            }
        }
    }

    private fun goToHomeActivity() {
        DashboardActivity.startInstanceWithBackStackCleared(this)
    }

    private fun startSMSListener() {
        val smsRetrieverClient = SmsRetriever.getClient(this)
        val task = smsRetrieverClient.startSmsUserConsent(null)
        task.addOnSuccessListener {
            getViewModel().success.value = "SMS Retriever starts"
        }
        task.addOnFailureListener {
            getViewModel().success.value = "Error"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        getViewModel().clearCountdownTick()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) {
                if (it.isSuccessful) {
                    goToHomeActivity()
                } else {
                    // Show Error
                    if (it.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        getViewModel().error.value = it.exception?.message ?: "Verification Failed"
                    } else {
                        getViewModel().error.value = "Verification Failed"
                    }
                }
            }.addOnFailureListener {
                getViewModel().error.value = it.message ?: "Verification Failed"
            }
    }

}