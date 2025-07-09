package com.iot.irremote.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.iot.irremote.databinding.FragmentHomeBinding
import com.iot.irremote.network.MqttManager

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()

        MqttManager.connect(context.applicationContext,
            onConnected = {
                // Escuchar respuestas (opcional)
                MqttManager.subscribeToStatus("dispositivo123") { status ->
                    requireActivity().runOnUiThread {
                        Toast.makeText(context, "Arduino dice: $status", Toast.LENGTH_SHORT).show()
                    }
                }

                // Botón de encendido
                binding.imageButton.setOnClickListener {
                    MqttManager.publishCommand("dispositivo123", "POWER")
                    Toast.makeText(context, "Enviando: POWER", Toast.LENGTH_SHORT).show()
                }

                // Volumen +
                binding.imageButton5.setOnClickListener {
                    MqttManager.publishCommand("dispositivo123", "VOL_UP")
                    Toast.makeText(context, "Enviando: VOL_UP", Toast.LENGTH_SHORT).show()
                }

                // Volumen -
                binding.imageButton4.setOnClickListener {
                    MqttManager.publishCommand("dispositivo123", "VOL_DOWN")
                    Toast.makeText(context, "Enviando: VOL_DOWN", Toast.LENGTH_SHORT).show()
                }

                // Canal +
                binding.imageButton6.setOnClickListener {
                    MqttManager.publishCommand("dispositivo123", "CH_UP")
                    Toast.makeText(context, "Enviando: CH_UP", Toast.LENGTH_SHORT).show()
                }

                // Canal -
                binding.imageButton7.setOnClickListener {
                    MqttManager.publishCommand("dispositivo123", "CH_DOWN")
                    Toast.makeText(context, "Enviando: CH_DOWN", Toast.LENGTH_SHORT).show()
                }

            },
            onError = {
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Error al conectar MQTT: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
