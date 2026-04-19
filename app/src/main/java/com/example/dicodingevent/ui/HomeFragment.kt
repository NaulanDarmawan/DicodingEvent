package com.example.dicodingevent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.R
import com.example.dicodingevent.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // 🔥 Panggil ViewModel menggunakan Factory
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup Adapter Horizontal (Active)
        val carouselAdapter = CarouselAdapter { event ->
            val bundle = Bundle().apply { putString("eventId", event.id.toString()) }
            findNavController().navigate(R.id.navigation_detail, bundle)
        }
        binding.rvCarousel.adapter = carouselAdapter

        // Setup Adapter Vertical (Finished)
        val finishedAdapter = EventAdapter { event ->
            val bundle = Bundle().apply { putString("eventId", event.id.toString()) }
            findNavController().navigate(R.id.navigation_detail, bundle)
        }
        binding.rvFinishedHome.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvFinishedHome.adapter = finishedAdapter

        // Observe Data Active (Carousel)
        homeViewModel.listActive.observe(viewLifecycleOwner) { events ->
            carouselAdapter.submitList(events)
        }

        // Observe Data Finished (Vertical List)
        homeViewModel.listFinished.observe(viewLifecycleOwner) { events ->
            finishedAdapter.submitList(events)
        }

        // Observe Loading
        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        // Setup Fitur Pencarian
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    homeViewModel.searchEvents(query)

                    binding.tvTitleUpcoming.visibility = View.GONE
                    binding.rvCarousel.visibility = View.GONE
                    binding.tvTitleFinished.text = getString(R.string.search_result_format, query)
                    binding.searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    binding.tvTitleUpcoming.visibility = View.VISIBLE
                    binding.rvCarousel.visibility = View.VISIBLE
                    binding.tvTitleFinished.text = getString(R.string.finished_events)
                    homeViewModel.fetchHomeData()
                }
                return false
            }
        })

        // Setup Error Handling
        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                android.widget.Toast.makeText(requireContext(), errorMsg, android.widget.Toast.LENGTH_SHORT).show()
                homeViewModel.clearErrorMessage()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}