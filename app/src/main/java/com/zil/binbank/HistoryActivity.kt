package com.zil.binbank

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zil.binbank.databinding.ActivityHistoryBinding
import java.io.File
import java.io.FileInputStream

class HistoryActivity : AppCompatActivity() {

    private var _binding: ActivityHistoryBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    val arrayRequests = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHistoryBinding.inflate(layoutInflater)
        recyclerView = binding.recyclerView
        setContentView(binding.root)

        val historyFile = File(this.filesDir.toString(), "history_bin.txt")
        if (historyFile.exists()){
            val input = FileInputStream(historyFile)
            val stringOfFile = input.readBytes().decodeToString()
            arrayRequests.addAll(stringOfFile.split(",").toMutableList())
        }
        initRecyclerView()
    }

    fun initRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = HistoryRecyclerAdapter(arrayRequests.reversed())
    }
}