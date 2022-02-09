package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class PostPictureActivity : AppCompatActivity() {

    private lateinit var mAdapter: HashTagRvAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_picture)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val path = intent.getStringExtra("1")
        val file = File( path!!)

        setupAutocompleteTV()
        setupRV()

        Glide.with(applicationContext)
            .load(file)
            .centerCrop()
            .into( findViewById<ImageView>(R.id.pictureToPost))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                // API 5+ solution
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRV(){
        val rv = findViewById<RecyclerView>(R.id.hashtag_rv)
        val list = mutableListOf<String>()
        mAdapter = HashTagRvAdapter(list , applicationContext)

        mAdapter.msetupOnClickListner(object : HashTagRvAdapter.MyOnItemClickListner{
            override fun onItemClick(position: Int) {
                mAdapter.dataset.removeAt(position)
                mAdapter.notifyDataSetChanged()
            }

        })

        rv.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context , LinearLayoutManager.HORIZONTAL , false)
        }


    }

    private fun setupAutocompleteTV(){
        val autocomplete =  findViewById<AutoCompleteTextView>(R.id.autoCompleteTVLanguages)
        val language = arrayOf("Happy", "Love" , "Morning", "Sunset", "Outfit", "Fashion", "Rain", "Dogs", "Cats" , "Batman")
        val languageAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, language)

        autocomplete.onItemClickListener =  object  : AdapterView.OnItemClickListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                mAdapter.dataset.add(autocomplete.text.toString())
                mAdapter.notifyDataSetChanged()
                autocomplete.setText("")
            }
        }

        autocomplete.setAdapter(languageAdapter)
    }
}