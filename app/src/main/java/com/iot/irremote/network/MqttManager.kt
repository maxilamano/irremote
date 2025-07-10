package com.iot.irremote.network

import android.util.Log
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.*

object MqttManager {

    private const val BROKER_URI = "tcp://broker.hivemq.com:1883"
    private val CLIENT_ID = "IRRemoteApp_" + UUID.randomUUID().toString()

    private var mqttClient: MqttClient? = null

    fun connect(onConnected: () -> Unit, onError: (Throwable) -> Unit) {
        try {
            mqttClient = MqttClient(BROKER_URI, CLIENT_ID, MemoryPersistence())

            val options = MqttConnectOptions().apply {
                isCleanSession = true
            }

            mqttClient?.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.e("MQTT", "Conexión perdida", cause)
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    // El callback real se establece en subscribeToStatus()
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            })

            mqttClient?.connect(options)
            Log.d("MQTT", "Conectado exitosamente al broker MQTT")
            onConnected()

        } catch (e: Exception) {
            Log.e("MQTT", "Error al conectar", e)
            onError(e)
        }
    }

    fun publishCommand(deviceId: String, command: String) {
        try {
            val topic = "/$deviceId/cmd"
            val message = MqttMessage(command.toByteArray())
            mqttClient?.publish(topic, message)
            Log.d("MQTT", "Publicado a $topic: $command")
        } catch (e: Exception) {
            Log.e("MQTT", "Error al publicar", e)
        }
    }

    fun subscribeToStatus(deviceId: String, onMessage: (String) -> Unit) {
        try {
            val topic = "/$deviceId/status"
            mqttClient?.subscribe(topic, 1)
            mqttClient?.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.e("MQTT", "Conexión perdida", cause)
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val content = message?.toString() ?: ""
                    Log.d("MQTT", "Mensaje recibido de $topic: $content")
                    onMessage(content)
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            })
        } catch (e: Exception) {
            Log.e("MQTT", "Error al suscribirse", e)
        }
    }

    fun disconnect() {
        try {
            mqttClient?.disconnect()
            mqttClient?.close()
            mqttClient = null
            Log.d("MQTT", "Desconectado del broker")
        } catch (e: Exception) {
            Log.e("MQTT", "Error al desconectar", e)
        }
    }
}
