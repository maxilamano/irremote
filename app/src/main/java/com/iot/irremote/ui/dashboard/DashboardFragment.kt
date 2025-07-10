package com.iot.irremote.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.iot.irremote.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.syncButton.setOnClickListener {
            findNavController().navigate(com.iot.irremote.R.id.navigation_sync)
        }

        // Puedes dejar esto si el ViewModel tendrá lógica luego
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        dashboardViewModel.text.observe(viewLifecycleOwner) {
            // Si necesitas actualizar algo con LiveData
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
