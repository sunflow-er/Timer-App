package org.javaapp.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.javaapp.timer.databinding.FragmentRecordBinding

class RecordFramgment(private val currentPlayerId : String) : Fragment() {
    private lateinit var binding : FragmentRecordBinding
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
        binding = FragmentRecordBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database.child(Key.DB_PLAYER).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playerList = mutableListOf<Player>()
                var currentPlayer: Player? = null

                snapshot.children.forEach {
                    val player = it.getValue(Player::class.java)
                    player ?: return

                    if (player.id == currentPlayerId) {
                        currentPlayer = player
                    }
                    playerList.add(player)
                }

                playerList.sortBy { it.gap?.toDouble() }
                val rank = playerList.indexOf(currentPlayer) + 1

                currentPlayer?.let {
                    binding.recordText.text = it.record
                    if (rank == 1) {
                        binding.rankText.setTextColor(ContextCompat.getColor(context!!, R.color.gold))
                    } else if (rank == 2) {
                        binding.rankText.setTextColor(ContextCompat.getColor(context!!, R.color.silver))
                    } else if (rank == 3) {
                        binding.rankText.setTextColor(ContextCompat.getColor(context!!, R.color.bronze))
                    }
                    binding.rankText.text = rank.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        binding.backToStartButton.setOnClickListener {
            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, StartFragment()).commit()
        }
    }
}