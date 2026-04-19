package com.example.dicodingevent.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dicodingevent.databinding.FragmentSettingBinding
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(SettingPreferences.getInstance(requireContext().dataStore))
    }

    private lateinit var workManager: WorkManager

    // Permission launcher untuk notifikasi Android 13+
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Notifikasi diizinkan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Notifikasi ditolak", Toast.LENGTH_SHORT).show()
                binding.switchReminder.isChecked = false
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workManager = WorkManager.getInstance(requireContext())

        // 1. Observer untuk Dark Mode
        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            binding.switchTheme.isChecked = isDarkModeActive
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveThemeSetting(isChecked)
        }

        // 2. Observer untuk Daily Reminder
        viewModel.getReminderSettings().observe(viewLifecycleOwner) { isReminderActive ->
            binding.switchReminder.isChecked = isReminderActive
        }

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveReminderSetting(isChecked)
            if (isChecked) {
                // Minta izin dulu kalau Android 13 (Tiramisu) ke atas
                if (Build.VERSION.SDK_INT >= 33) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                startPeriodicTask()
            } else {
                cancelPeriodicTask()
            }
        }
    }

    private fun startPeriodicTask() {
        // Syarat: Hanya jalan saat ada internet
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Dijadwalkan 1 Hari sekali
        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "DailyReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    private fun cancelPeriodicTask() {
        workManager.cancelUniqueWork("DailyReminder")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}