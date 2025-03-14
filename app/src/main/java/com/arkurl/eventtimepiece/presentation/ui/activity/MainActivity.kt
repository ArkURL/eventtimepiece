package com.arkurl.eventtimepiece.presentation.ui.activity

import android.Manifest.permission
import android.Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.arkurl.eventtimepiece.R
import com.arkurl.eventtimepiece.data.local.database.AppDatabase
import com.arkurl.eventtimepiece.data.repository.EventRepository
import com.arkurl.eventtimepiece.databinding.ActivityMainBinding
import com.arkurl.eventtimepiece.databinding.DialogAboutBinding
import com.arkurl.eventtimepiece.presentation.viewmodel.EventViewModel
import com.arkurl.eventtimepiece.presentation.viewmodel.EventViewModelFactory
import com.arkurl.eventtimepiece.util.AppUtils
import com.arkurl.eventtimepiece.util.toHtml
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import java.util.jar.Manifest

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var eventViewModel: EventViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission())
    { isGranted ->
        if (isGranted) {
            // Permission is granted
            Log.d(TAG, "permission: granted")
        } else {
            // Permission is denied
        }
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (checkSelfPermission(FOREGROUND_SERVICE_SPECIAL_USE)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(FOREGROUND_SERVICE_SPECIAL_USE)
            } else {
                Log.d(TAG, "onStart: granted") // ✅ 已有权限，直接启动
            }
        } else {
            Log.d(TAG, "onStart: no need to grant") // ✅ Android 13 以下不需要权限
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(this, navController)

        init()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun init() {
        val database = AppDatabase.getDatabaseInstance(this)
        val repository = EventRepository(database.eventDao())
        val factory = EventViewModelFactory(repository)

        eventViewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.about -> {
                val dialogAboutBinding = DialogAboutBinding.inflate(layoutInflater)
                dialogAboutBinding.aboutDialogBuildVersion.text = getString(R.string.build_version, AppUtils.getVersionName(this))
                val content = getString(R.string.about_dialog_content, AppUtils.getProjectLinkHtml()).toHtml()
                dialogAboutBinding.aboutDialogContent.movementMethod = LinkMovementMethod.getInstance()
                dialogAboutBinding.aboutDialogContent.text = content
                run {
                    MaterialAlertDialogBuilder(this)
                        .setView(dialogAboutBinding.root)
                        .show()
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}