package ru.otus.androidcomponents.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import ru.otus.androidcomponents.R
import ru.otus.androidcomponents.databinding.FragmentSharingBinding
import java.io.File
import java.io.FileOutputStream

/**
 * Illustrates data sharing between applications and a file-provider to share resource file
 */
class SharingFragment : Fragment() {
    private var _binding: FragmentSharingBinding? = null
    private val binding get() = requireNotNull(_binding) { "Binding not created or destroyed" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSharingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        // Find buttons and bind click listeners
        setOk.setOnClickListener {
            imageView.setImageResource(R.drawable.ic_ok)
            textView.text = getString(R.string.looks_good)
        }
        setError.setOnClickListener {
            imageView.setImageResource(R.drawable.ic_error)
            textView.text = getString(R.string.looks_bad)
        }

        // Bind share button action
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.share -> {
                    onShare()
                    true
                }
                else -> false
            }
        }
    }

    private fun onShare() = with(binding) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/png"
            putExtra(Intent.EXTRA_TEXT, textView.text)
            putExtra(Intent.EXTRA_STREAM, getStatusPicture())
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_status)))
    }

    /**
     * Converts drawable to bitmap and saves to the cache-dir
     * Then creates an URL using application file provider
     */
    private fun getStatusPicture(): Uri = with(binding) {
        val context = requireContext().applicationContext
        val bitmap = imageView.drawable.toBitmap()
        val output = File(context.cacheDir, "status.png")
        FileOutputStream(output).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            it.flush()
        }
        return FileProvider.getUriForFile(context, context.packageName, output)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}