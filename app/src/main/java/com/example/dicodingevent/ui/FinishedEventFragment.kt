package com.example.dicodingevent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.databinding.FragmentFinishedEventBinding
import androidx.navigation.fragment.findNavController
import com.example.dicodingevent.R

class FinishedEventFragment : Fragment() {

    private var _binding: FragmentFinishedEventBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]
        val adapter = EventAdapter { event ->
            val bundle = Bundle().apply {
                putString("eventId", event.id.toString())
            }
            findNavController().navigate(R.id.navigation_detail, bundle)
        }

        binding.rvEvents.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvEvents.adapter = adapter

        // Memanggil API khusus event selesai (0)
        eventViewModel.findEvents(0)

        // Observasi Data
        eventViewModel.listEvent.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }

        // Observasi Loading
        eventViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        eventViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                android.widget.Toast.makeText(requireContext(), errorMsg, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Mencegah memory leak
    }
}