package ru.otus.androidcomponents.ui

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ru.otus.androidcomponents.data.CalendarData
import ru.otus.androidcomponents.databinding.FragmentContentBinding

/**
 * Illustrates content-provider usage to get a list of calendars
 */
class ContentFragment : Fragment() {
    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!
    private val calendars = CalendarAdapter()

    private lateinit var permissionRequestLauncher: ActivityResultLauncher<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.calendars.apply {
            adapter = calendars
        }

        permissionRequestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                getCalendars()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please allow this app to access your calendar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()

        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR)) {
            getCalendars()
        } else {
            permissionRequestLauncher.launch(Manifest.permission.READ_CALENDAR)
        }
    }

    /**
     * Uses content-provider to get a list of calendars
     */
    private fun getCalendars() {
        // Provider is identified with URI
        val uri: Uri = CalendarContract.Calendars.CONTENT_URI
        // The same as WHERE clause in SQL
        val selection = "((${CalendarContract.Calendars.VISIBLE}) = 1 AND (${CalendarContract.Calendars.ACCOUNT_TYPE} != ?))"
        // Arguments for placeholders in `selection`
        val selectionArgs: Array<String> = arrayOf(CalendarContract.ACCOUNT_TYPE_LOCAL)
        // Record sort order
        val sort = CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        // Request a cursor that iterates provider record
        val cursor: Cursor? = requireActivity().contentResolver.query(
            uri,
            EVENT_PROJECTION,
            selection,
            selectionArgs,
            sort
        )

        // If provider crashes or errors, the cursor may be null
        if (null == cursor) {
            Toast.makeText(
                requireContext(),
                "Error. No cursor obtained",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val data = mutableListOf<CalendarData>()
        // Iterate records and fill the list
        cursor.use { cur ->
            while (cur.moveToNext()) {
                // Get the field values
                data.add(
                    CalendarData(
                        id = cur.getLong(PROJECTION_ID_INDEX),
                        color = cur.getInt(PROJECTION_CALENDAR_COLOR_INDEX),
                        displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX).orEmpty(),
                        account = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX)
                )
                )
            }
        }
        calendars.submitList(data)
    }

    companion object {
        private val EVENT_PROJECTION = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.ACCOUNT_NAME
        )
        private const val PROJECTION_ID_INDEX = 0
        private const val PROJECTION_DISPLAY_NAME_INDEX = 1
        private const val PROJECTION_CALENDAR_COLOR_INDEX = 2
        private const val PROJECTION_ACCOUNT_NAME_INDEX = 3
    }
}