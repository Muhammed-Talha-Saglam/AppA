package com.example.appa

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.example.someAidl.ISomeAidlInterface

import com.google.gson.Gson
import java.lang.Exception

data class Response(val name: String, val desc: String)


class MainActivity : AppCompatActivity() {

    private var aidlService: ISomeAidlInterface? = null
    private var aidlServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i("aidlServiceConnection", "onServiceConnected")
            try {
                aidlService = ISomeAidlInterface.Stub.asInterface(service)
                val json = aidlService?.response
                val response = Gson().fromJson(json, Response::class.java)
                Log.i("aidlServiceConnection", "Response: $response")
            }catch (e: Exception) {
                Log.i("aidlServiceConnection", "onServiceConnected error: $e")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("aidlServiceConnection", "onServiceDisconnected")
            aidlService = null
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindToService()
    }

    private fun bindToService() {
        try {
            val intent = Intent("someService.AIDL")
            val explicitIntent = convertImplicitIntentToExplicitIntent(this,intent)
            if (explicitIntent != null) {
                bindService(explicitIntent, aidlServiceConnection, BIND_AUTO_CREATE)
            }
        } catch (e: Exception) {
            Log.i("bindToFiscalService", "e: $e")
        }
    }

    fun convertImplicitIntentToExplicitIntent(ct: Context, implicitIntent: Intent): Intent? {
        val pm = ct.packageManager
        val resolveInfoList = pm.queryIntentServices(implicitIntent, 0)
        if (resolveInfoList == null || resolveInfoList.size != 1) {
            return null
        }
        val serviceInfo = resolveInfoList[0]
        val component = ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name)
        val explicitIntent = Intent(implicitIntent)
        explicitIntent.component = component
        return explicitIntent
    }

}