package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.model.UserCredentials
import no.bakkenbaeck.pppshared.validator.InputValidator
import no.bakkenbaeck.pppshared.validator.ValidationResult
import no.bakkenbaeck.pppshared.view.CreateAccountView
import kotlin.properties.Delegates

class CreateAccountPresenter(
    val view: CreateAccountView,
    storage: SecureStorage
): BaseCoroutinePresenter(secureStorage = storage) {

    /// Any error which has occurred in validating the user's email address.
    private var emailError: String? by Delegates.observable<String?>(null) { _, _, newValue ->
        view.emailErrorUpdated(newValue)
    }

    /// Any error which has occurred in validating the user's password.
    private var passwordError: String? by Delegates.observable<String?>(null) { _, _, newValue ->
        view.passwordErrorUpdated(newValue)
    }

    /// Any error which has occurred in confirming the user's password.
    private var confirmPasswordError: String? by Delegates.observable<String?>(null) { _, _, newValue ->
        view.confirmPasswordErrorUpdated(newValue)
    }

    private var apiError: String? by Delegates.observable<String?>(null) { _, _, newValue ->
        view.apiErrorUpdated(newValue)
    }

    fun validateEmail() {
        val emailResult = InputValidator.validateIsEmail(view.email, "email")
        when (emailResult) {
            is ValidationResult.Invalid -> emailError = emailResult.reason
            is ValidationResult.Valid -> emailError = null
        }
    }

    fun validatePassword() {
        val passwordResult = InputValidator.validateInputAtLeastLength(6, view.password, "password")

        when (passwordResult) {
            is ValidationResult.Invalid -> passwordError = passwordResult.reason
            is ValidationResult.Valid -> passwordError = null
        }
    }

    fun validateConfirmPassword() {
        val confirmPasswordResult = InputValidator.validateNonNullMatches(
            "password",
            view.password,
            "confirm password",
            view.confirmPassword
        )

        when (confirmPasswordResult) {
            is ValidationResult.Invalid -> confirmPasswordError = confirmPasswordResult.reason
            is ValidationResult.Valid -> confirmPasswordError = null
        }
    }

    fun validateAllInput() {
        validateEmail()
        validatePassword()
        validateConfirmPassword()
    }

    fun isCurrentInputValid(): Boolean {
        validateAllInput()
        return emailError == null
                && passwordError == null
                && confirmPasswordError == null
    }

    suspend fun createAccountAsync(): Boolean {
        validateAllInput()
        if (!isCurrentInputValid()) {
            return false
        }

        // If input is valid, these will not be null.
        val creds = UserCredentials(view.email!!, view.password!!)
        view.setSubmitButtonEnabled(false)
        view.startLoadingIndicator()
        apiError = null

        var result = false
        try {
            val token = api.createAccount(creds)
            secureStorage.storeTokenString(token.token)
            view.accountSuccessfullyCreated()
            result = true
        } catch (exception: Exception) {
            apiError = exception.message
        }

        view.setSubmitButtonEnabled(true)
        view.stopLoadingIndicator()
        return result
    }

    fun createAccount() {
        launch {
            createAccountAsync()
        }
    }

    override fun handleError(error: Throwable) {
        apiError = error.message
    }
}