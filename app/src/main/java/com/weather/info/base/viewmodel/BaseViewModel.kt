package com.weather.info.base.viewmodel

import android.os.Handler
import android.webkit.MimeTypeMap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.weather.info.data.AppDataManager
import com.weather.info.data.firebase.FireBaseAuthProvider
import com.weather.info.data.model.error.ErrorPojoClass
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

abstract class BaseViewModel(
    open val appDataManager: AppDataManager,
    open val fireBaseAuthProvider: FireBaseAuthProvider
) : ViewModel() {
    protected var TAG: String = this.javaClass.simpleName

    protected var compositeDisposable = CompositeDisposable()

    var loading = MutableLiveData<Boolean>()
    var bottomLoading = MutableLiveData<Boolean>()
    var totalCount = MutableLiveData<Int>()

    var isTokenExpired = MutableLiveData<Boolean>()
    var isServerNotAvailable = MutableLiveData<Boolean>()

    var success = MutableLiveData<String>()
    var info = MutableLiveData<String>()
    var normal = MutableLiveData<String>()

    var warning = MutableLiveData<String>()

    var error = MutableLiveData<String>()
    var unAuthorizeError = MutableLiveData<String>()

    val logoutSuccess = MutableLiveData<Boolean>()

    fun logoutUser() {
        appDataManager.logout()
        fireBaseAuthProvider.firebaseAuth.signOut()
        Handler().postDelayed({
            logoutSuccess.value = true
        }, 1000)
    }


    fun getRequestBody(str: String?): RequestBody {

        return str?.toRequestBody("text/plain".toMediaTypeOrNull())
            ?: "".toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun getRequestBody(str: Int?): RequestBody {
        return if (str != null) {
            ("" + str).toRequestBody("text/plain".toMediaTypeOrNull())
        } else {
            "".toRequestBody("text/plain".toMediaTypeOrNull())
        }
    }

    fun getRequestBody(str: Long?): RequestBody {
        return if (str != null) {
            ("" + str).toRequestBody("text/plain".toMediaTypeOrNull())
        } else {
            "".toRequestBody("text/plain".toMediaTypeOrNull())
        }
    }

    fun getMimeType(url: String): MediaType? {
        var type = "multipart/form-data"
        val extension: String? = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)!!
        }
        return type.toMediaTypeOrNull()
    }


    abstract inner class MyDisposableObserver<T> : DisposableObserver<Response<T>>() {

        override fun onStart() {
            super.onStart()
            showProgress(true)
        }


        override fun onNext(response: Response<T>) {
            when (response.code()) {
                200 -> {
                    if (response.body() != null) {
                        processResult(response)
                    } else {
                        info.value = response.message()
                    }
                }
                /*500 -> {
                    if (response.message().isNotEmpty()){
                        message.value = response.message()
                    }else {
                        error.value = response.errorBody()?.string()
                    }
                }
                400 -> {
                    if (response.message().isNotEmpty()){
                        message.value = response.message()
                    }else {
                        error.value = response.errorBody()?.string()
                    }
                }*/
                502 -> {
                    isServerNotAvailable.value = true
                }
                400 -> {
                    //Error: Bad Request
                    val errorBody = response.errorBody()
                    if (errorBody != null) {
                        val errorString = errorBody.string()
                        val errorPojo = Gson().fromJson(errorString, ErrorPojoClass::class.java)
                        error.value = errorPojo.message ?: "Bad Request"
                    } else {
                        error.value = "Bad Request"
                    }

                }
                500 -> {
                    //Error: Bad Request
                    val errorBody = response.errorBody()
                    if (errorBody != null) {
                        val errorString = errorBody.string()
                        val errorPojo = Gson().fromJson(errorString, ErrorPojoClass::class.java)
                        error.value = errorPojo.title ?: "Internal Server error"
                    } else {
                        error.value = "Internal Server error"
                    }

                }
                401 -> {
                    //Unauthorized
                    val errorBody = response.errorBody()
                    if (errorBody != null) {
                        val errorString = errorBody.string()
                        val errorPojo = Gson().fromJson(errorString, ErrorPojoClass::class.java)
                        unAuthorizeError.value = errorPojo.detail ?: "Unauthorized"
                    } else {
                        unAuthorizeError.value = "Unauthorized"
                    }
                }
                403 -> {
                    //Forbidden
                }
                404 -> {
                    //Not Found
                }
                else -> {
                    if (response.message().isNotEmpty()) {
                        normal.value = response.message()
                    } else {
                        error.value = response.errorBody()?.string()
                    }
                }
            }
        }

        override fun onComplete() {
            showProgress(false)
        }

        override fun onError(e: Throwable) {
            showProgress(false)
            error.value = e.message
        }

        open fun showProgress(isVisible: Boolean) {
            loading.value = isVisible
        }

        abstract fun processResult(response: Response<T>)

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    inline fun <T> launchPagingAsync(
        crossinline execute: suspend () -> Flow<T>,
        crossinline onSuccess: (Flow<T>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = execute()
                onSuccess(result)
            } catch (ex: Exception) {
                error.value = ex.message
            }
        }
    }

    inline fun <T> launchAsync(
        crossinline execute: suspend () -> Response<T>,
        crossinline onSuccess: (T) -> Unit,
        showProgress: Boolean = true
    ) {
        viewModelScope.launch {
            if (showProgress)
                loading.value = true
            try {
                val result = execute()
                if (result.isSuccessful)
                    onSuccess(result.body()!!)
                else
                    error.value = result.message()
            } catch (ex: Exception) {
                error.value = ex.message
            } finally {
                loading.value = false
            }
        }
    }

}