package com.iot.irremote.ui.sync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.iot.irremote.databinding.FragmentSyncBinding
import com.iot.irremote.network.MqttManager

class SyncFragment : Fragment() {

    private var _binding: FragmentSyncBinding? = null
    private val binding get() = _binding!!

    private val steps = listOf("VOL_UP", "VOL_DOWN", "CH_UP", "CH_DOWN", "POWER")
    private var currentStep = 0
    private val deviceId = "dispositivo123"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSyncBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateInstruction()

        // Suscribirse a confirmaciones del Arduino
        MqttManager.subscribeToStatus(deviceId) { status ->
            requireActivity().runOnUiThread {
                if (status == "OK:${steps[currentStep]}") {
                    Toast.makeText(context, "Señal recibida: ${steps[currentStep]}", Toast.LENGTH_SHORT).show()
                    nextStep()
                }
            }
        }

        binding.confirmButton.setOnClickListener {
            val command = "SYNC:${steps[currentStep]}"
            MqttManager.publishCommand(deviceId, command)
        }

        binding.skipButton.setOnClickListener {
            Toast.makeText(context, "Omitido: ${steps[currentStep]}", Toast.LENGTH_SHORT).show()
            nextStep()
        }
    }

    private fun nextStep() {
        currentStep++
        if (currentStep < steps.size) {
            updateInstruction()
        } else {
            Toast.makeText(context, "Sincronización completa", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateInstruction() {
        binding.instructionText.text = "Apunta el control remoto y presiona: ${steps[currentStep]}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
