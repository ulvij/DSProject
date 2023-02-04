package com.example.testapp.presentation.base

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.testapp.presentation.R
import com.example.testapp.presentation.utils.NavigationCommand

abstract class BaseFragment<State, Effect, ViewModel : BaseViewModel<State, Effect>, B : ViewBinding> :
    Fragment(), LifecycleOwner {

    protected abstract val bindingCallback: (LayoutInflater, ViewGroup?, Boolean) -> B

    abstract val viewModel: ViewModel

    protected lateinit var binding: B

    protected open val noInternetDialog: AlertDialog? by lazy {
        AlertDialog.Builder(requireContext(), R.style.RoundAlertDialog).also {
            it.setTitle(R.string.no_internet_title)
            it.setMessage(R.string.no_internet_error_text)
            it.setPositiveButton(R.string.dialog_btn_ok) { dialog, _ ->
                dialog.dismiss()
            }

        }.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = bindingCallback.invoke(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.setArguments(arguments)
            viewModel.loadArguments()
        }
    }

    protected open val bindViews: B.() -> Unit = {}

    protected open fun observeState(state: State) {
        // override when observing
    }

    protected open fun observeEffect(effect: Effect) {
        // override when observing
    }

    protected open fun showNoInternet() {
        if (noInternetDialog?.isShowing == false)
            noInternetDialog?.show()
    }

    fun launchOnLifecycleScope(execute: suspend () -> Unit) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            execute()
        }
    }

    protected open fun showBackEndError() {
        AlertDialog.Builder(requireContext(), R.style.RoundAlertDialog).also {
            it.setTitle(R.string.unknown_error_title)
            it.setMessage(R.string.unknown_error_text)
            it.setPositiveButton(R.string.dialog_btn_ok) { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    protected open fun showMessage(
        message: Int
    ) {
        AlertDialog.Builder(requireContext(), R.style.RoundAlertDialog).also {
            it.setMessage(message)
            it.setPositiveButton(R.string.dialog_btn_ok) { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    protected open fun showError(
        messageId: Int,
        title: Int? = R.string.unknown_error_title,
        cause: Throwable? = null
    ) {
        AlertDialog.Builder(requireContext(), R.style.RoundAlertDialog).also {
            it.setTitle(R.string.unknown_error_title)
            it.setMessage(getString(messageId))
            it.setPositiveButton(R.string.dialog_btn_ok) { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    protected open fun showLoading() {
        // override in child class
    }

    protected open fun hideLoading() {
        // override in child class
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(binding)
        viewModel.isLoading.observe(viewLifecycleOwner) { state ->
            if (state) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        viewModel.state.observe(viewLifecycleOwner, ::observeState)
        viewModel.effect.observe(viewLifecycleOwner, ::observeEffect)
        viewModel.commonEffect.observe(viewLifecycleOwner) {
            when (it) {
                is NoInternet -> showNoInternet()
                is BackEndError -> showBackEndError()
                is UnknownError -> showBackEndError()
                is MessageError -> showError(it.messageId, it.titleId, it.cause)
                else -> error("Unknown BaseEffect: $it")
            }
        }

        viewModel.navigationCommands.observe(viewLifecycleOwner) { command ->
            when (command) {
                is NavigationCommand.To -> {
                    val navBuilder = NavOptions.Builder()
                    navBuilder
                        .setEnterAnim(R.anim.nav_default_enter_anim)
                        .setExitAnim(R.anim.nav_default_exit_anim)
                        .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                        .setPopExitAnim(R.anim.nav_default_pop_exit_anim)

                    command.extra?.let { bundle ->
                        findNavController().navigate(
                            command.directions.actionId,
                            bundle,
                            navBuilder.build()
                        )
                    } ?: run {
                        findNavController().navigate(
                            command.directions,
                            navBuilder.build()
                        )
                    }
                }
                is NavigationCommand.BackTo -> findNavController().getBackStackEntry(command.destinationId)
                is NavigationCommand.Back -> findNavController().popBackStack()
            }
        }
    }

    protected fun getColor(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(requireContext(), colorRes)
    }

    protected fun <T : DialogFragment> T.show(tag: String? = null): T {
        this.show(this@BaseFragment.parentFragmentManager, tag)
        return this
    }

    open fun onNewIntent(intent: Intent?) {}

    protected fun themeColor(@AttrRes id: Int, alternative: Int = Color.TRANSPARENT): Int {
        val typedValue = TypedValue()
        val theme = context?.theme ?: return alternative
        theme.resolveAttribute(id, typedValue, true)
        return typedValue.data
    }

    protected fun updateBoldSpanWithBoldFont(text: CharSequence, color: Int): SpannableString {
        val new = SpannableString(text)
        val spans = new.getSpans(0, new.length, StyleSpan::class.java)
        for (boldSpan in spans) {
            val start: Int = new.getSpanStart(boldSpan)
            val end: Int = new.getSpanEnd(boldSpan)
            new.setSpan(TypefaceSpan("gilroy_bold"), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            new.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return new
    }

    protected fun <T> LiveData<T>.observe(block: (T) -> Unit) {
        observe(viewLifecycleOwner, block)
    }

}