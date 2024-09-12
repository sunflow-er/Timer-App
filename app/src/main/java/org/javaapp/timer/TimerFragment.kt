package org.javaapp.timer

import android.os.Bundle
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.javaapp.timer.databinding.FragmentTimerBinding
import java.util.Timer
import kotlin.concurrent.timer
import kotlin.math.abs

private const val FIRST = 0
private const val RUNNNING = 1
private const val STOPPED = 2

class TimerFragment(private val currentPlayerId : String) : Fragment() {
    private lateinit var binding: FragmentTimerBinding
    private lateinit var database :DatabaseReference

    private var state = FIRST
    private var timer: Timer? = null
    private var startTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database(Key.DB_URL).reference
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startAndStopButton.setOnClickListener {
            if (state == FIRST) {
                binding.timeText.visibility = View.INVISIBLE
                startTimer()
            } else if (state == RUNNNING) {
                binding.timeText.visibility = View.VISIBLE
                stopTimer()
            } else { // state == STOPPED
                comfirmRecord()
            }
        }
    }

    private fun startTimer() {
        state = RUNNNING
        binding.startAndStopButton.text = "Stop"
        startTime = System.currentTimeMillis()
        timer = timer(initialDelay = 0, period = 1) {
            val elapsedTime = System.currentTimeMillis() - startTime
            val second = elapsedTime / 1000
            val milliSecond = elapsedTime % 1000

            activity?.runOnUiThread {
                binding.timeText.text = String.format("%02d.%03d", second, milliSecond)
            }
        }
    }

    private fun stopTimer() {
        state = STOPPED
        binding.startAndStopButton.text = "순위 확인"
        timer?.cancel()
        timer = null
    }

    private fun comfirmRecord() {
        state = FIRST

        val playerRecord = mutableMapOf<String,Any>()
        playerRecord["record"] = binding.timeText.text.toString()
        playerRecord["gap"] = abs(binding.timeText.text.toString().toDouble() - 7.000).toString()

        database.child(Key.DB_PLAYER).child(currentPlayerId).updateChildren(playerRecord)

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RecordFramgment(currentPlayerId))
            .addToBackStack(null)
            .commit()
    }
}