package com.example.dicodingevent.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.dicodingevent.R
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.FragmentDetailBinding
import androidx.core.net.toUri

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil ID dari argumen Navigation
        val eventId = arguments?.getString("eventId") ?: ""

        val viewModel = ViewModelProvider(this)[EventViewModel::class.java]

        // Panggil fungsi untuk ambil detail (kita akan buat fungsinya di ViewModel)
        viewModel.getDetailEvent(eventId)

        viewModel.detailEvent.observe(viewLifecycleOwner) { event ->
            displayEventDetail(event)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                android.widget.Toast.makeText(requireContext(), errorMsg, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayEventDetail(event: ListEventsItem) {
        binding.tvName.text = event.name
        binding.tvOwner.text = getString(R.string.organizer_format, event.ownerName)
        binding.tvTime.text = getString(R.string.time_format, event.beginTime)

        // Hitung Sisa Kuota: Quota - Registrants
        val remainingQuota = event.quota - event.registrants
        binding.tvQuota.text = getString(R.string.quota_format, remainingQuota)

        Glide.with(this).load(event.mediaCover).into(binding.ivMediaCover)

        // Tampilkan Deskripsi di WebView
        binding.wvDescription.loadDataWithBaseURL(null, event.description, "text/html", "UTF-8", null)

        // Tombol Registrasi (Buka Browser)
        binding.btnRegister.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, event.link.toUri())
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}