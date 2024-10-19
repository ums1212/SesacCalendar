package com.sesac.sesacscheduler

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sesac.sesacscheduler.common.logE

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        logE("mainActivity","onCreate")

        // 오버레이 권한
        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission()
        }

        // 위치 권한
        multiplePermissionsLauncher.launch(permissions)
    }

    private val overlayPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Settings.canDrawOverlays(this)) {
            Log.d("MainActivity", "오버레이 권한이 허용되었습니다.")
        } else {
            Log.d("MainActivity", "오버레이 권한이 거부되었습니다.")
        }
    }

    // 오버레이 권한 요청
    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        overlayPermissionLauncher.launch(intent)
    }

    private val multiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var result = true
        permissions.entries.forEach { (_, isGranted) ->
            when {
                isGranted -> {
                    // 개별 권한이 승인된 경우 처리할 작업
                }
                !isGranted -> {
                    // 권한이 거부된 경우 처리할 작업
                    result = false
                }
                else -> {
                    // 사용자가 "다시 묻지 않음"을 선택한 경우 처리할 작업
                    result = false
                }
            }
        }
    }

    private val permissions = arrayOf(
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION
    )

}