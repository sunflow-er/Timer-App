package org.javaapp.timer

import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.javaapp.timer.databinding.FragmentPlayerBinding
import java.util.UUID

class PlayerFragment : Fragment() {
    private lateinit var binding : FragmentPlayerBinding
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database(Key.DB_URL).reference
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startButton.setOnClickListener {
            binding.nameEdit.filters = arrayOf(InputFilter.LengthFilter(3))

            if (binding.nameEdit.text.toString().isNullOrBlank() || binding.contactEdit.text.toString().isNullOrBlank()) {
                Toast.makeText(context, "잘못된 이름/연락처 형식입니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val player = mutableMapOf<String, Any>()
                val currentPlayerId = UUID.randomUUID().toString()
                player["id"] = currentPlayerId
                player["name"] = binding.nameEdit.text.toString()
                player["contact"] = binding.contactEdit.text.toString()
                player["record"] = "00.000"
                player["gap"] = "7"
                player["rank"] = "-"

                database.child(Key.DB_PLAYER).child(currentPlayerId).updateChildren(player)

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, TimerFragment(currentPlayerId))
                    .addToBackStack(null)
                    .commit()
            }



        }
    }
}