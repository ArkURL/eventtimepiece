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
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.arkurl.eventtimepiece.R
import com.arkurl.eventtimepiece.data.local.dao.EventDao
import com.arkurl.eventtimepiece.data.local.model.EventWithParentModel
import com.arkurl.eventtimepiece.data.repository.EventRepository
import com.arkurl.eventtimepiece.databinding.DialogEventAddBinding
import com.arkurl.eventtimepiece.databinding.DialogEventChildEditBinding
import com.arkurl.eventtimepiece.databinding.DialogEventChildInfoBinding
import com.arkurl.eventtimepiece.databinding.FragmentChildEventBinding
import com.arkurl.eventtimepiece.presentation.ui.adapter.ChildEventAdapter
import com.arkurl.eventtimepiece.presentation.viewmodel.EventViewModel
import com.arkurl.eventtimepiece.presentation.viewmodel.EventViewModelFactory
import com.arkurl.eventtimepiece.util.TimeUtils
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.Instant

class ChildEventFragment: Fragment(R.layout.fragment_child_event) {
    private lateinit var binding: FragmentChildEventBinding
    private lateinit var adapter: ChildEventAdapter
    private lateinit var navController: NavController
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory(EventRepository.getInstance(requireContext()))
    }
    private val args: ChildEventFragmentArgs by navArgs()

    companion object {
        private val TAG = ChildEventFragment::class.java.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChildEventBinding.bind(view)
        Log.d(TAG, "onViewCreated: [event id]: " + args.eventId)

        navController = findNavController()

        adapter = ChildEventAdapter(
            onItemClick = ::handleItemClick,
            onInfoEventListener = ::handleInfoEvent,
            onEditEventListener = ::handleEditEvent,
            onDeleteEventListener = ::handleDeleteEvent
        )

        eventViewModel.childEventList.observe(viewLifecycleOwner) { childEvents ->
            Log.d(TAG, "onViewCreated: [events]: $childEvents")
            if (childEvents.isNullOrEmpty()) {
                binding.textNewSomeChildEvent.visibility = View.VISIBLE
                binding.recycleViewChildEvent.visibility = View.GONE
            }
            else {
                binding.textNewSomeChildEvent.visibility = View.GONE
                binding.recycleViewChildEvent.visibility = View.VISIBLE
                adapter.submitList(childEvents)
            }
        }

        eventViewModel.loadChildEventsByParentEventId(args.eventId)

        binding.recycleViewChildEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleViewChildEvent.adapter = adapter

        binding.floatingActionButton.setOnClickListener {
            showAddNewChildEvent()
        }
    }

    private fun showAddNewChildEvent() {
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

            eventViewModel.insert(name = title, description = description, parentEventId = args.eventId)
            dialog.dismiss()
            Toast.makeText(requireContext(), getString(R.string.add_event_success), Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun handleItemClick(event: EventWithParentModel) {
        Log.d(TAG, "handleItemClick: [event]: $event")

        val actionChildEventToEventTimer =
            ChildEventFragmentDirections.actionChildEventToEventTimer(event.id)

        navController.navigate(actionChildEventToEventTimer)
    }

    private fun handleInfoEvent(event: EventWithParentModel) {
        val binding = DialogEventChildInfoBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()

        binding.textTitle.text = event.eventName
        binding.textDescription.text = event.eventDescription
        binding.textCreatedTime.text = TimeUtils.formatInstant(event.eventCreateTime)
        binding.textUpdatedTime.text = TimeUtils.formatInstant(event.eventUpdateTime)
        binding.textParentEventName.text = event.parentEventName

        binding.btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun handleEditEvent(event: EventWithParentModel) {
        val binding = DialogEventChildEditBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()

        binding.textParentEventName.text = event.parentEventName
        binding.editTitle.setText(event.eventName)
        binding.editDescription.setText(event.eventDescription)
        binding.textCreatedTime.text = TimeUtils.formatInstant(event.eventCreateTime)
        binding.textUpdatedTime.text = TimeUtils.formatInstant(event.eventUpdateTime)

        binding.btnOk.setOnClickListener {
            val title = binding.editTitle.text.toString().trim()
            val description = binding.editDescription.text.toString().trim()

            if (title.isEmpty()) {
                binding.editTitle.error = getString(R.string.event_title_is_empty_hint)
                return@setOnClickListener
            }

            eventViewModel.updateEvent(event.selfModel.copy(
                name = title,
                description = description,
                updateTime = Instant.now()
            ))

//            同步更新父级事项更新时间
            eventViewModel.updateEvent(event.parentModel.copy(
                updateTime = Instant.now()
            ))

            Toast.makeText(requireContext(), getString(R.string.edit_event_success), Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun handleDeleteEvent(event: EventWithParentModel) {
        showDeleteEventDialog(event)
    }

    private fun showDeleteEventDialog(event: EventWithParentModel) {
        val warningIcon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_dialog_alert)
        warningIcon?.let {
            DrawableCompat.setTint(it, ContextCompat.getColor(requireContext(), com.google.android.material.R.color.design_default_color_error))
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_event_dialog_title)
            .setMessage(R.string.delete_event_dialog_content)
            .setIcon(warningIcon)
            .setPositiveButton(R.string.delete) { dialog, _ ->
                eventViewModel.deleteEvent(event.selfModel)
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