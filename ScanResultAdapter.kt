package com.moby.antivirus

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class ScanResultAdapter(private val list: List<ScanResult>) : RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvAppName: TextView = view.findViewById(R.id.tvAppName)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnUninstall: Button = view.findViewById(R.id.btnUninstall)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.ivIcon.setImageDrawable(item.icon)
        holder.tvAppName.text = item.appName
        holder.tvStatus.text = item.status
        if (item.isThreat) {
            holder.tvStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
            holder.btnUninstall.visibility = View.VISIBLE
            holder.btnUninstall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DELETE)
                intent.data = Uri.parse("package:${item.packageName}")
                holder.itemView.context.startActivity(intent)
            }
        } else {
            holder.tvStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
            holder.btnUninstall.visibility = View.GONE
        }
    }

    override fun getItemCount() = list.size
}