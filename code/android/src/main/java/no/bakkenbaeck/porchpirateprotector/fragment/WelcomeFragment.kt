package no.bakkenbaeck.porchpirateprotector.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import no.bakkenbaeck.porchpirateprotector.R

import kotlinx.android.synthetic.main.fragment_welcome.*
import no.bakkenbaeck.porchpirateprotector.manager.KeyStoreManager
import no.bakkenbaeck.pppshared.presenter.WelcomePresenter
import no.bakkenbaeck.pppshared.view.WelcomeView

class WelcomeFragment: Fragment(), WelcomeView {

    private val presenter by lazy { WelcomePresenter(this, KeyStoreManager(this.context!!)) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_create_account.setOnClickListener { presenter.selectedCreateAccountButton() }
        button_login.setOnClickListener { presenter.selectedLoginButton() }

        presenter.skipWelcomeIfLoggedIn()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    // WELCOME VIEW

    override fun navigateToLogin() {
        findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
    }

    override fun navigateToCreateAccount() {
        findNavController().navigate(R.id.action_welcomeFragment_to_createAccountFragment)
    }

    override fun navigateToDeviceList() {
        findNavController().navigate(R.id.action_welcomeFragment_to_deviceListFragment)
    }
}