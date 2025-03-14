package com.arkurl.eventtimepiece.presentation.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
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
import com.arkurl.eventtimepiece.util.TimeUtils

class TimerFragment : Fragment(R.layout.fragment_event_timer) {
    private val args: TimerFragmentArgs by navArgs()
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory(EventRepository.getInstance(requireContext()))
    }
    private val timerViewModel: TimerViewModel by viewModels()

    companion object {
        private val TAG = TimerFragment::class.java.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEventTimerBinding.bind(view)

        timerViewModel.startTimer()

        timerViewModel.elapsedTime.observe(viewLifecycleOwner) { time ->
            Log.d(TAG, "onViewCreated: time: $time")
            val timeCostStr = TimeUtils.formatTimeCost(time)
            val (hour, minute, second) = timeCostStr.split(":")
            binding.tvHourTens.text = hour[0].toString()
            binding.tvHourOnes.text = hour[1].toString()
            binding.tvMinuteTens.text = minute[0].toString()
            binding.tvMinuteOnes.text = minute[1].toString()
            binding.tvSecondTens.text = second[0].toString()
            binding.tvSecondOnes.text = second[1].toString()
        }

        timerViewModel.isTimerRunning.observe(viewLifecycleOwner) { isTimerRunning ->
            if (isTimerRunning) {
                binding.btnShift.text = getString(R.string.pause)
                binding.btnShift.icon = ContextCompat.getDrawable(requireContext(), R.drawable.pause_24px)
            } else {
                binding.btnShift.text = getString(R.string.start)
                binding.btnShift.icon = ContextCompat.getDrawable(requireContext(), R.drawable.play_arrow_24px)
            }
        }

        binding.btnShift.setOnClickListener {
           timerViewModel.shiftTimer()
        }

        binding.btnFinish.setOnClickListener {
            finishAndSaveTime()
        }
    }

    private fun finishAndSaveTime() {
        timerViewModel.stopTimer()
        Log.d(TAG, "finishAndSaveTime: ${timerViewModel.elapsedTime.value}")
        eventViewModel.updateEventAndParentTimeCostById(args.eventId, timerViewModel.elapsedTime.value ?: 0L)
        findNavController().navigateUp()
    }
}