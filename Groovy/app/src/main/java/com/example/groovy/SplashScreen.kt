package com.example.groovy

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import java.util.jar.Manifest

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    var permissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.MODIFY_AUDIO_SETTINGS
    )
    val permissionCode = 10001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash_screen)

        if(checkPermission())
        {
            goHome()
        }
        else
        {
            ActivityCompat.requestPermissions(this@SplashScreen,permissions, permissionCode)
        }


    }
    private fun checkPermission(): Boolean {
        for(perms in permissions)
        {
            val data = application.checkCallingOrSelfPermission(perms)
            if(data != PackageManager.PERMISSION_GRANTED)
            {
                return false
            }
        }
        return true
    }
    private fun goHome() {
        Handler().postDelayed({
            startActivity(Intent(applicationContext,MainActivity::class.java))
            finish()
        },10000)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode)
        {
            permissionCode ->
            {
                if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED)
                {
                    goHome()
                }
                else
                {
                    showToast("Please grant permissions")
                }
            }
            else ->
            {
                showToast("Error Occurred")

            }
        }
    }

    private fun showToast(msg:String)
    {
        Toast.makeText(applicationContext,msg,Toast.LENGTH_SHORT).show()
        finish()
    }
}