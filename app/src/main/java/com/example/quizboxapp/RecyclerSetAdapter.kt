package com.example.quizboxapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class RecyclerSetAdapter(
    private val context: Context,
    private var numOfSet: Int
)
    : RecyclerView.Adapter<RecyclerSetAdapter.SetViewHolder>() {
    class SetViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val buttonSet : Button = view.findViewById(R.id.btn_set)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.set_item_layout, parent, false)
        return SetViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        holder.buttonSet.text = "Set " + (position + 1)
        holder.buttonSet.setOnClickListener {
            val intent = Intent(it.context, QuestionActivity::class.java)
            intent.putExtra("Set", "Set${position+1}")

            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return numOfSet
    }
}