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
        lateinit var tvMessage: TextView
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
    private lateinit var btSend2: Button
    private lateinit var btDisconnect: Button
    //private lateinit var tvMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btConnect = findViewById(R.id.btConnect)
        btSend = findViewById(R.id.btSend)
        btSend2 = findViewById(R.id.btSend2)
        btDisconnect = findViewById(R.id.btDisconnect)
        tvMessage = findViewById(R.id.tvMessage)

        //init bluetooth adapter
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        // Fix depricated getDefaultAdapter() by the following:
        //val bAdapter = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        //bAdapter.getAdapter()

        // STUFF FOR COMMUNICATING
        btConnect.setOnClickListener{
            if(bAdapter.isEnabled) {
                //val intent = Intent(this, ControlActivity::class.java)
                //intent.putExtra(EXTRA_ADDRESS, address)
                //startActivity(intent)
                if(bluetoothSocket == null) {
                    ConnectToDevice(this).execute()
                }else {
                    Toast.makeText(this, "Already connected", Toast.LENGTH_LONG).show()
                }
            }else {
                Toast.makeText(this, "Not able to connect, bluetooth not turned on", Toast.LENGTH_LONG).show()
            }
        }

        // STUFF FOR COMMUNICATING
        btSend.setOnClickListener{
            if(bluetoothSocket != null) {
                //val intent = Intent(this, ControlActivity::class.java)
                //intent.putExtra(EXTRA_ADDRESS, address)
                //startActivity(intent)
                sendCommand("testmelding sendt;")
            }else {
                Toast.makeText(this, "Not able to send, bluetooth not connected", Toast.LENGTH_LONG).show()
            }
        }

        // STUFF FOR COMMUNICATING
        btSend2.setOnClickListener{
            if(bluetoothSocket != null) {
                //val intent = Intent(this, ControlActivity::class.java)
                //intent.putExtra(EXTRA_ADDRESS, address)
                //startActivity(intent)
                sendCommand("?start_lat=60.456&start_lon=9.123&end_lat=60.456&end_lon=9.123&time_min=43&rotations=234123&id_g=1&id_j=0&id_p=1;")
            }else {
                Toast.makeText(this, "Not able to send, bluetooth not connected", Toast.LENGTH_LONG).show()
            }
        }

        // STUFF FOR COMMUNICATING
        btDisconnect.setOnClickListener{
            if(bluetoothSocket != null) {
                //val intent = Intent(this, ControlActivity::class.java)
                //intent.putExtra(EXTRA_ADDRESS, address)
                //startActivity(intent)
                disconnect()
            }else {
                Toast.makeText(this, "Already disconnected", Toast.LENGTH_LONG).show()
            }
        }

        //Initiate continuously read BT message in a different thread
        doReadBTmessage(tvMessage)
    }

    // STUFF FOR COMMUNICATING
    var indexcnt = 0
    private fun sendCommand(input: String) {
        if (bluetoothSocket != null) {
            try{
                indexcnt++
                bluetoothSocket!!.outputStream.write((input+indexcnt.toString()).toByteArray())
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

        //private lateinit var tvMessage: TextView

        //constructor (regular way in Kotlin)
        init {
            //init list in constructor
            this.context = c
            //tvMessage = findViewById(R.id.tvMessage)
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg p0: Void?): String? {
            // If not connected, try to connect
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


            // If connected, try to read VIRKER IKKE, BLIR MULIGENS BARE KJØRT EN GANG
            // Instead make a new async task, by following https://www.youtube.com/watch?v=_HTDVTR1oPs
            //try {
            //    if (isConnected) {
            //        val stringBuilder = StringBuilder()
            //        var indexcnt = 0
            //        //var currentChar = ''
            //        while (bluetoothSocket!!.inputStream.available() > 0){
            //            var currentChar = bluetoothSocket!!.inputStream.read().toChar()
            //            stringBuilder.append(currentChar)
            //            indexcnt++
            //        }
            //        //Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show()
            //        tvMessage.text = stringBuilder.toString() + indexcnt.toString()

            //    }
            //} catch (e: IOException) {
            //    connectSuccess = false
            //    e.printStackTrace()
            //}



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

    // Continuously Read BT message in a different thread
    private fun doReadBTmessage(textView: TextView){
        Thread(Runnable {
            val stringBuilder = StringBuilder()
            var message = ""
            //var indexcnt = 0
            //val result = input.toString()
            try {
                while(true) {
                    if (isConnected) {


                        //var currentChar = ''
                        while (bluetoothSocket!!.inputStream.available() > 0) {
                            var currentChar = bluetoothSocket!!.inputStream.read().toChar()
                            stringBuilder.append(currentChar)
                            if (currentChar== ';') {
                                message = stringBuilder.toString()
                                stringBuilder.clear()
                            }
                        //indexcnt++
                        }
                        //Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show()
                        //tvMessage.text = stringBuilder.toString() + indexcnt.toString()

                    }
                    runOnUiThread {
                        textView.text = message
                        //textView.text = indexcnt.toString()
                    }
                    Thread.sleep(100) //sleep 0.1 second
                }
                } catch (e: IOException) {
                    //connectSuccess = false
                    e.printStackTrace()
                }



        }).start()
    }
}