package com.example.bluetoothtest2

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        var myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var bluetoothSocket: BluetoothSocket? = null
        lateinit var progress: ProgressDialog
        lateinit var bluetoothAdapter: BluetoothAdapter
        var isConnected: Boolean = false
        var address = "98:DA:50:00:B7:3F"

        //val EXTRA_ADDRESS: String = "Device_address"
    }

    //bluetooth adapter
    lateinit var bAdapter: BluetoothAdapter

    //UI elements
    private lateinit var btConnect: Button
    private lateinit var btSend: Button
    private lateinit var btDisconnect: Button
    private lateinit var tvMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btConnect = findViewById(R.id.btConnect)
        btSend = findViewById(R.id.btSend)
        btDisconnect = findViewById(R.id.btDisconnect)
        tvMessage = findViewById(R.id.tvMessage)

        //init bluetooth adapter
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        // Fix depricated getDefaultAdapter() by the following:
        //val bAdapter = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        //bAdapter.getAdapter()

        // STUFF FOR COMMUNICATING
        btConnect.setOnClickListener{
            if(true) {
                //val intent = Intent(this, ControlActivity::class.java)
                //intent.putExtra(EXTRA_ADDRESS, address)
                //startActivity(intent)
                ConnectToDevice(this).execute()
            }else {
                Toast.makeText(this, "Not able to connect", Toast.LENGTH_LONG).show()
            }
        }

        // STUFF FOR COMMUNICATING
        btSend.setOnClickListener{
            if(true) {
                //val intent = Intent(this, ControlActivity::class.java)
                //intent.putExtra(EXTRA_ADDRESS, address)
                //startActivity(intent)
                sendCommand("testmelding sendt;")
            }else {
                Toast.makeText(this, "Not able to send", Toast.LENGTH_LONG).show()
            }
        }

        // STUFF FOR COMMUNICATING
        btDisconnect.setOnClickListener{
            if(true) {
                //val intent = Intent(this, ControlActivity::class.java)
                //intent.putExtra(EXTRA_ADDRESS, address)
                //startActivity(intent)
                disconnect()
            }else {
                Toast.makeText(this, "Not able to disconnect", Toast.LENGTH_LONG).show()
            }
        }

    }

    // STUFF FOR COMMUNICATING
    private fun sendCommand(input: String) {
        if (bluetoothSocket != null) {
            try{
                bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // STUFF FOR COMMUNICATING
    private fun receiveCommand() {
        if (bluetoothSocket != null) {
            try{
                val stringBuilder = StringBuilder()
                //var currentChar = ''
                while (bluetoothSocket!!.inputStream.available() > 0){
                    var currentChar = bluetoothSocket!!.inputStream.read().toChar()
                    stringBuilder.append(currentChar)
                }
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show()
                //m_bluetoothSocket!!.inputStream.
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // STUFF FOR COMMUNICATING
    private fun disconnect() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.close()
                bluetoothSocket = null
                isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        //finish()
    }

    // STUFF FOR COMMUNICATING
    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        //constructor (regular way in Kotlin)
        init {
            //init list in constructor
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (bluetoothSocket == null || !isConnected) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
            } else {
                isConnected = true
            }
            progress.dismiss()
        }
    }
}