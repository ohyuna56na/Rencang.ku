package com.oyn.rencangku.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.databinding.FragmentProfileBinding
import com.oyn.rencangku.network.ApiClient
import com.oyn.rencangku.ui.onboarding.ActivityOnboardingLast
import com.oyn.rencangku.ui.preference.PreferenceActivity
import com.oyn.rencangku.ui.profile.EditProfileActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        sessionManager = SessionManager(requireContext())

        val factory = ProfileViewModelFactory(
            ApiClient.RestaurantApi,
            sessionManager
        )

        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        observeData()
        setupLogout()
        setupMenu()

        viewModel.loadProfile()

        return binding.root
    }

    private fun observeData() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvUserName.text = user.name
                binding.tvUserEmail.text = user.email
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupMenu() {

        binding.menuEditProfile.setOnClickListener {

            startActivity(
                Intent(requireContext(), EditProfileActivity::class.java)
            )
        }

        binding.menuEditPreference.setOnClickListener {

            startActivity(
                Intent(requireContext(), PreferenceActivity::class.java)
            )
        }
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()
            startActivity(Intent(requireContext(), ActivityOnboardingLast::class.java))
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
