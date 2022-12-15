package com.example.favouritedishes.views.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.example.favouritedishes.R
import com.example.favouritedishes.databinding.ActivityMainBinding
import com.example.favouritedishes.models.notification.NotificationWorker
import com.example.favouritedishes.utils.Constants
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    //263 previous Lesson
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_all_dishes, R.id.navigation_favourite, R.id.navigation_random)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        if (intent.hasExtra(Constants.NOTIFICATION_ID)){
            val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, 0)
            binding.navView.selectedItemId = R.id.navigation_random
        }

        startWork()
    }

    private fun startWork() = WorkManager.getInstance(this)
                                        .enqueueUniquePeriodicWork("Dish Notification Work",
                                            ExistingPeriodicWorkPolicy.KEEP,
                                            createWorkRequest())

    private fun createWorkRequest() = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
        .setConstraints(createConstraints()).build()

    private fun createConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .setRequiresCharging(false)
        .setRequiresBatteryNotLow(true).build()

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    fun hideBottomNavigation(){
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(binding.navView.height.toFloat()).duration = 300
        binding.navView.visibility = View.GONE
    }

    fun showBottomNavigation(){
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(0f).duration = 300
        binding.navView.visibility = View.VISIBLE
    }
}