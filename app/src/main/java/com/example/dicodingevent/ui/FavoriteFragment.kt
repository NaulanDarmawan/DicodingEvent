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
import com.example.dicodingevent.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    // Gunakan EventViewModel yang sama karena fungsinya sudah ada di sana
    private val viewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FavoriteAdapter { event ->
            // Saat diklik, lempar ID ke DetailFragment
            val bundle = Bundle().apply {
                putString("eventId", event.id)
            }
            // Pastikan R.id.detailFragment sesuai dengan ID fragment detail di nav_graph.xml kamu
            findNavController().navigate(R.id.navigation_detail, bundle)
        }

        binding.rvFavorite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorite.adapter = adapter

        // Tampilkan indikator loading (opsional karena room sangat cepat)
        binding.progressBar.visibility = View.VISIBLE

        // Ambil data langsung dari Database secara Real-time!
        viewModel.getAllFavoriteEvents().observe(viewLifecycleOwner) { favoriteEvents ->
            binding.progressBar.visibility = View.GONE
            adapter.submitList(favoriteEvents)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}