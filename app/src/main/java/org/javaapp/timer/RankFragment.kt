package org.javaapp.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.javaapp.timer.databinding.FragmentRankBinding
import org.javaapp.timer.databinding.ItemRankBinding

class RankFragment : Fragment() {
    private lateinit var binding : FragmentRankBinding
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
        binding = FragmentRankBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rankRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RankAdapter(emptyList())
        }

        database.child(Key.DB_PLAYER).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rankList = mutableListOf<Player>()

                snapshot.children.forEach {
                    val player = it.getValue(Player::class.java)
                    player ?: return

                    rankList.add(player)
                }

                rankList.sortBy { it.gap?.toDouble() }
                binding.rankRecyclerView.adapter = RankAdapter(rankList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private inner class RankHolder(private val binding: ItemRankBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player : Player, rank : Int) {
            binding.rankText.text = rank.toString()
            if (rank == 1) {
                binding.rankText.setTextColor(ContextCompat.getColor(context!!, R.color.gold))
            } else if (rank == 2) {
                binding.rankText.setTextColor(ContextCompat.getColor(context!!, R.color.silver))
            } else if (rank == 3) {
                binding.rankText.setTextColor(ContextCompat.getColor(context!!, R.color.bronze))
            }
            binding.playerNameText.text = player.name
            binding.recordText.text = player.record.toString()
        }
    }

    private inner class RankAdapter(private val playerList : List<Player>) :RecyclerView.Adapter<RankHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankHolder {
            val binding = ItemRankBinding.inflate(layoutInflater, parent, false)

            return RankHolder(binding)
        }

        override fun getItemCount(): Int {
            return playerList.size
        }

        override fun onBindViewHolder(holder: RankHolder, position: Int) {
            holder.bind(playerList[position], position + 1)
        }

    }
}