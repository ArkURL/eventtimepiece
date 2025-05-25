package com.arkurl.eventtimepiece.presentation.ui.fragment

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arkurl.eventtimepiece.R
import com.arkurl.eventtimepiece.data.repository.EventRepository
import com.arkurl.eventtimepiece.databinding.FragmentEventTimerBinding
import com.arkurl.eventtimepiece.presentation.viewmodel.EventViewModel
import com.arkurl.eventtimepiece.presentation.viewmodel.EventViewModelFactory
import com.arkurl.eventtimepiece.presentation.viewmodel.TimerViewModel
import com.arkurl.eventtimepiece.service.TimerService
import com.arkurl.eventtimepiece.util.TimeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TimerFragment : Fragment(R.layout.fragment_event_timer) {
    private val args: TimerFragmentArgs by navArgs()
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory(EventRepository.getInstance(requireContext()))
    }
    private val timerViewModel: TimerViewModel by viewModels()
    private lateinit var binding: FragmentEventTimerBinding

    companion object {
        private val TAG = TimerFragment::class.java.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEventTimerBinding.bind(view)

        // 初始化计时器和按钮状态
        timerViewModel.startTimer()
        observeTimerState()

        eventViewModel.loadSpecifyEvent(args.eventId)
        observeEvent()

        binding.btnShift.setOnClickListener {
            timerViewModel.pauseTimer()
        }

        binding.btnFinish.setOnClickListener {
            finishAndSaveTime()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        })
    }

    private fun observeEvent() {
        eventViewModel.event.observe(viewLifecycleOwner) { event ->
            binding.eventName.text = event.name
        }

    }

    private fun observeTimerState() {
        // 观察时间更新
        timerViewModel.elapsedTime.observe(viewLifecycleOwner) { time ->
            Log.d(TAG, "Elapsed time: $time")
            updateTimerDisplay(time)
        }

        // 观察计时器运行状态
        timerViewModel.isRunning.observe(viewLifecycleOwner) { isRunning ->
            updateButtonState(isRunning)
        }
    }

    private fun updateTimerDisplay(time: Long) {
        val timeCostStr = TimeUtils.formatTimeCost(time)
        val (hour, minute, second) = timeCostStr.split(":")
        binding.tvHourTens.text = hour[0].toString()
        binding.tvHourOnes.text = hour[1].toString()
        binding.tvMinuteTens.text = minute[0].toString()
        binding.tvMinuteOnes.text = minute[1].toString()
        binding.tvSecondTens.text = second[0].toString()
        binding.tvSecondOnes.text = second[1].toString()
    }

    private fun updateButtonState(isRunning: Boolean) {
        binding.btnShift.apply {
            setIconResource(if (isRunning) R.drawable.pause_24px else R.drawable.play_arrow_24px)
            text = getString(if (isRunning) R.string.pause else R.string.start)
        }
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_back_title)
            .setMessage(R.string.confirm_back_message)
            .setPositiveButton(R.string.confirm) {_, _ ->
                finishAndSaveTime()
            }
            .setNegativeButton(R.string.cancel) { _, _ ->

            }
            .show()
    }

    private fun finishAndSaveTime() {
        timerViewModel.stopTimer()
        eventViewModel.updateEventAndParentTimeCostById(
            args.eventId,
            timerViewModel.elapsedTime.value ?: 0L
        )
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}