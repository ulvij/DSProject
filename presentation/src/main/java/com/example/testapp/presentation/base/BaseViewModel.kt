package com.example.testapp.presentation.base

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.example.testapp.domain.base.BaseUseCase
import com.example.testapp.domain.base.CompletionBlock
import com.example.testapp.domain.exception.NetworkError
import com.example.testapp.domain.exception.ServerError
import com.example.testapp.presentation.R
import com.example.testapp.presentation.base.*
import com.example.testapp.presentation.utils.NavigationCommand
import com.example.testapp.presentation.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel<State, Effect> : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _effect = SingleLiveEvent<Effect>()
    val effect: SingleLiveEvent<Effect>
        get() = _effect

    private val _commonEffect = SingleLiveEvent<BaseEffect>()
    val commonEffect: LiveData<BaseEffect>
        get() = _commonEffect

    val navigationCommands = SingleLiveEvent<NavigationCommand>()

    protected fun postState(state: State) {
        _state.postValue(state!!)
    }

    protected fun postEffect(effect: Effect) {
        _effect.postValue(effect!!)
    }

    fun navigate(directions: NavDirections) {
        navigationCommands.postValue(NavigationCommand.To(directions, null))
    }

    fun navigate(command: NavigationCommand) {
        navigationCommands.postValue(command)
    }

    private var arguments: Bundle? = null

    fun setArguments(data: Bundle?) {
        arguments = data
    }

    fun getArguments(): Bundle? {
        return arguments
    }

    /**
     * Override if arguments will be used
     */
    open fun loadArguments() {}


    suspend fun <P, R> BaseUseCase<P, R>.getWith(param: P): R {
        var result: R? = null
        val block: CompletionBlock<R> = {
            onSuccess = { result = it }
            onError = { throw it }
        }
        execute(param, block)
        return result!!
    }

    fun launchAll(
        loadingHandle: (Boolean) -> Unit = ::setLoading,
        errorHandle: (Throwable) -> Unit = ::handleError,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                loadingHandle(true)
                block()
            } catch (t: Throwable) {
                loadingHandle(false)
                errorHandle(t)
            }
        }
    }

    protected fun handleError(t: Throwable) {
        when (t) {
            is ServerError.ServerIsDown -> _commonEffect.postValue(BackEndError())
            is ServerError.Unexpected -> _commonEffect
                .postValue(MessageError(R.string.unknown_error_text, cause = t))
            is NetworkError -> _commonEffect.postValue(NoInternet())
            else -> _commonEffect.postValue(UnknownError(cause = t))
        }
    }

    private fun <T> Flow<T>.setLoading(loadingHandle: (Boolean) -> Unit = ::setLoading): Flow<T> =
        flow {
            this@setLoading
                .onStart { loadingHandle(true) }
                .onCompletion { loadingHandle(false) }
                .collect { value ->
                    loadingHandle(false)
                    emit(value)
                }
        }

    protected fun <T> Flow<T>.handleError(): Flow<T> = catch { handleError(it) }

    protected fun <T> Flow<T>.launch(
        scope: CoroutineScope = viewModelScope,
        loadingHandle: (Boolean) -> Unit = ::setLoading
    ): Job =
        this.handleError()
            .setLoading(loadingHandle)
            .launchIn(scope)

    protected fun <T> Flow<T>.launchNoLoading(scope: CoroutineScope = viewModelScope): Job =
        this.handleError()
            .launchIn(scope)

    protected fun <P, R, U : BaseUseCase<P, R>> U.launch(
        param: P,
        loadingHandle: (Boolean) -> Unit = ::setLoading,
        block: CompletionBlock<R> = {}
    ) {
        viewModelScope.launch {
            val actualRequest = BaseUseCase.Request<R>().apply(block)

            val proxy: CompletionBlock<R> = {
                onStart = {
                    loadingHandle(true)
                    actualRequest.onStart?.invoke()
                }
                onSuccess = {
                    loadingHandle(false)
                    actualRequest.onSuccess(it)
                }
                onCancel = {
                    loadingHandle(false)
                    actualRequest.onCancel?.invoke(it)
                }
                onError = {
                    Timber.e(it)
                    loadingHandle(false)
                    handleError(it)
                    actualRequest.onError?.invoke(it)
                }
            }
            execute(param, proxy)
        }
    }


    protected fun setLoading(state: Boolean) {
        _isLoading.postValue(state)
    }

    fun isLoading(): Boolean {
        return isLoading.value ?: false
    }
}