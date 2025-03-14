package com.arkurl.eventtimepiece.presentation.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arkurl.eventtimepiece.R
import com.arkurl.eventtimepiece.data.local.model.Event
import com.arkurl.eventtimepiece.databinding.DialogEventAddBinding
import com.arkurl.eventtimepiece.databinding.DialogEventEditBinding
import com.arkurl.eventtimepiece.databinding.DialogEventInfoBinding
import com.arkurl.eventtimepiece.databinding.FragmentHomeBinding
import com.arkurl.eventtimepiece.presentation.ui.adapter.EventAdapter
import com.arkurl.eventtimepiece.presentation.viewmodel.EventViewModel
import com.arkurl.eventtimepiece.util.TimeUtils
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.Instant
import kotlin.getValue

class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private lateinit var navController: NavController
    private val eventViewModel: EventViewModel by activityViewModels()
    
    companion object {
        private var TAG = HomeFragment::class.java.simpleName
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)
        binding.floatingActionButton.setOnClickListener {
            showAddNewEventDialog()
        }

        navController = findNavController()

        recyclerView = binding.eventCardRecycleView

        adapter = EventAdapter(
            onItemClick = { event -> handleItemClick(event) },
            onInfoEventListener = { event -> handleAdapterInfoEvent(event) },
            onEditEventListener = { event -> handleAdapterEditEvent(event) },
            onDeleteEventListener = { event -> handleAdapterDeleteEvent(event) }
        )

        eventViewModel.parentEventList.observe(viewLifecycleOwner) { events ->
            if (events.isNullOrEmpty()) {
                binding.welcomeText.visibility = View.VISIBLE
                binding.eventCardRecycleView.visibility = View.GONE
            } else {
                binding.welcomeText.visibility = View.GONE
                binding.eventCardRecycleView.visibility = View.VISIBLE
                adapter.submitList(events)
            }
        }

        eventViewModel.loadParentEvents()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun showAddNewEventDialog() {
        val binding = DialogEventAddBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnOk.setOnClickListener {
            val title = binding.editTitle.text.toString().trim()
            val description = binding.editDescription.text.toString().trim()

            if (title.isEmpty()) {
                binding.editTitle.error = getString(R.string.event_title_is_empty_hint)
                return@setOnClickListener
            }

            eventViewModel.insert(title, description, null)
            dialog.dismiss()
            Toast.makeText(requireContext(), getString(R.string.add_event_success), Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }


    private fun handleItemClick(event: Event) {
        Log.d(TAG, "handleItemClick: ")
        val actionHomeToChildEvent =
            HomeFragmentDirections.actionHomeToChildEvent(eventId = event.id)
        navController.navigate(actionHomeToChildEvent)
    }

    private fun handleAdapterInfoEvent(event: Event) {
        Log.d(TAG, "Add event: $event")
        val binding = DialogEventInfoBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()


        binding.textTitle.text = event.name
        binding.textDescription.text = event.description.toString()
        binding.textCreatedTime.text = TimeUtils.formatInstant(event.createTime)
        binding.textUpdatedTime.text = TimeUtils.formatInstant(event.updateTime)

        binding.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun handleAdapterEditEvent(event: Event) {
        Log.d(TAG, "Edit event: $event")
        val binding = DialogEventEditBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()

        binding.editTitle.setText(event.name)
        binding.editDescription.setText(event.description)
        binding.textCreatedTime.text = TimeUtils.formatInstant(event.createTime)
        binding.textUpdatedTime.text = TimeUtils.formatInstant(event.updateTime)

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnOk.setOnClickListener {
            val title = binding.editTitle.text.toString().trim()
            val description = binding.editDescription.text.toString().trim()

            if (title.isEmpty()) {
                binding.editTitle.error = getString(R.string.event_title_is_empty_hint)
                return@setOnClickListener
            }

            eventViewModel.updateEvent(event.copy(name = title, description = description, updateTime = Instant.now()))
            dialog.dismiss()
            Toast.makeText(requireContext(), getString(R.string.edit_event_success), Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun handleAdapterDeleteEvent(event: Event) {
        Log.d(TAG, "Delete event: $event")
        showDeleteEventDialog(event)
    }

    private fun showDeleteEventDialog(event: Event) {
        val warningIcon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_dialog_alert)
        warningIcon?.let {
            DrawableCompat.setTint(it, ContextCompat.getColor(requireContext(), com.google.android.material.R.color.design_default_color_error))
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_event_dialog_title)
            .setMessage(R.string.delete_event_dialog_content)
            .setIcon(warningIcon)
            .setPositiveButton(R.string.delete) { dialog, _ ->
                eventViewModel.deleteEvent(event)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                setBackgroundColor(ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_error))
                setTextColor(ContextCompat.getColor(context, android.R.color.white))
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
                setTextColor(MaterialColors.getColor(dialog.context, com.google.android.material.R.attr.colorOnSurface, Color.GRAY))
            }
        }

        dialog.show()
    }

}