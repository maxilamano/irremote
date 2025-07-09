package com.iot.irremote.network

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

object MqttManager {

    private lateinit var mqttClient: MqttAndroidClient
    private const val BROKER_URI = "tcp://broker.hivemq.com:1883" // Público, sin login
    private val CLIENT_ID = "IRRemoteApp_" + System.currentTimeMillis()

    fun connect(context: Context, onConnected: () -> Unit, onError: (Throwable) -> Unit) {
        mqttClient = MqttAndroidClient(context.applicationContext, BROKER_URI, CLIENT_ID)
        val options = MqttConnectOptions().apply {
            isCleanSession = true
        }

        mqttClient.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d("MQTT", "Conexión exitosa al broker MQTT")
                onConnected()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.e("MQTT", "Fallo al conectar MQTT", exception)
                onError(exception ?: Exception("Error desconocido"))
            }
        })
    }

    fun publishCommand(deviceId: String, command: String) {
        if (!::mqttClient.isInitialized || !mqttClient.isConnected) return

        val topic = "/$deviceId/cmd"
        val message = MqttMessage(command.toByteArray())
        mqttClient.publish(topic, message)
        Log.d("MQTT", "Publicado a $topic: $command")
    }

    fun subscribeToStatus(deviceId: String, onMessage: (String) -> Unit) {
        val topic = "/$deviceId/status"
        mqttClient.subscribe(topic, 1)
        mqttClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.e("MQTT", "Conexión MQTT perdida", cause)
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val content = message?.toString() ?: ""
                Log.d("MQTT", "Mensaje recibido de $topic: $content")
                onMessage(content)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })
    }

    fun disconnect() {
        if (::mqttClient.isInitialized && mqttClient.isConnected) {
            mqttClient.disconnect()
        }
    }
}
