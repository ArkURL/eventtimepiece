package com.arkurl.eventtimepiece.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arkurl.eventtimepiece.R
import com.arkurl.eventtimepiece.databinding.FragmentHomeBinding
import com.arkurl.eventtimepiece.presentation.ui.adapter.EventAdapter
import com.arkurl.eventtimepiece.presentation.viewmodel.EventViewModel
import kotlin.getValue

class HomeFragment: Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private val eventViewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById<RecyclerView>(R.id.event_card_recycle_view)
        adapter = EventAdapter(
            onItemClick = { event ->  },
            onAddEventListener = { event ->  },
            onEditEventListener = { event ->  },
            onDeleteEventListener = { event ->  }
        )

        eventViewModel.eventList.observe(viewLifecycleOwner) { events ->
            if (events.isNullOrEmpty()) {
                binding.welcomeText.visibility = View.VISIBLE
                binding.eventCardRecycleView.visibility = View.GONE
            } else {
                binding.welcomeText.visibility = View.GONE
                binding.eventCardRecycleView.visibility = View.VISIBLE
                adapter.submitList(events)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
}