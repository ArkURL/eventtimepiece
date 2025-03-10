package com.arkurl.eventtimepiece.presentation.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.arkurl.eventtimepiece.R
import com.arkurl.eventtimepiece.data.local.database.AppDatabase
import com.arkurl.eventtimepiece.data.local.model.Event
import com.arkurl.eventtimepiece.data.repository.EventRepository
import com.arkurl.eventtimepiece.databinding.ActivityMainBinding
import com.arkurl.eventtimepiece.databinding.DialogAboutBinding
import com.arkurl.eventtimepiece.databinding.DialogParentEventAddBinding
import com.arkurl.eventtimepiece.presentation.ui.adapter.EventAdapter
import com.arkurl.eventtimepiece.presentation.ui.fragment.HomeFragment
import com.arkurl.eventtimepiece.presentation.viewmodel.EventViewModel
import com.arkurl.eventtimepiece.presentation.viewmodel.EventViewModelFactory
import com.arkurl.eventtimepiece.util.AppUtils
import com.arkurl.eventtimepiece.util.LogTags
import com.arkurl.eventtimepiece.util.toHtml
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var eventViewModel: EventViewModel
    private lateinit var eventAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        init()
    }

    private fun init() {
        val database = AppDatabase.getDatabaseInstance(this)
        val repository = EventRepository(database.eventDao())
        val factory = EventViewModelFactory(repository)

        eventViewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]

        eventAdapter = EventAdapter(
            onItemClick = ::handleItemClick,
            onAddEventListener = ::handleAdapterAddEvent,
            onEditEventListener = ::handleAdapterEditEvent,
            onDeleteEventListener = ::handleAdapterDeleteEvent
        )

        binding.eventCardRecycleView.adapter = eventAdapter
        binding.eventCardRecycleView.layoutManager = LinearLayoutManager(this)



        eventViewModel.eventList.observe(this) { events ->
            if (events.isNullOrEmpty()) {
                binding.welcomeText.visibility = View.VISIBLE
                binding.eventCardRecycleView.visibility = View.GONE
            } else {
                binding.welcomeText.visibility = View.GONE
                binding.eventCardRecycleView.visibility = View.VISIBLE
                eventAdapter.submitList(events)
            }
        }

        eventViewModel.loadAllEvents()

        binding.floatingActionButton.setOnClickListener {
            showAddNewEventDialog()
        }
    }

    private fun showAddNewEventDialog() {
        val binding = DialogParentEventAddBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(this)
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
            Toast.makeText(this, getString(R.string.add_event_success), Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun showDeleteEventDialog(event: Event) {
        val warningIcon = ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_alert)
        warningIcon?.let {
            DrawableCompat.setTint(it, ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_error))
        }

        val dialog = MaterialAlertDialogBuilder(this)
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
                setBackgroundColor(ContextCompat.getColor(this@MainActivity, com.google.android.material.R.color.design_default_color_error))
                setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.white))
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.transparent))
                setTextColor(MaterialColors.getColor(dialog.context, com.google.android.material.R.attr.colorOnSurface, Color.GRAY))
            }
        }

        dialog.show()
    }

    private fun handleItemClick(event: Event) {
        Log.d(LogTags.MAIN_ACTIVITY, "handleItemClick: ")
    }

    private fun handleAdapterAddEvent(event: Event) {
        Log.d(LogTags.MAIN_ACTIVITY, "Add event: $event")
    }

    private fun handleAdapterEditEvent(event: Event) {
        Log.d(LogTags.MAIN_ACTIVITY, "Edit event: $event")
    }

    private fun handleAdapterDeleteEvent(event: Event) {
        Log.d(LogTags.MAIN_ACTIVITY, "Delete event: $event")
        showDeleteEventDialog(event)
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