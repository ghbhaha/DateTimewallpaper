package com.suda.datetimewallpaper.adapter

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.suda.datetimewallpaper.R
import com.suda.datetimewallpaper.bean.WallPaperModel
import com.suda.datetimewallpaper.service.WidgetRefreshService1
import com.suda.datetimewallpaper.service.WidgetRefreshService2
import com.suda.datetimewallpaper.ui.SetViewActivity
import com.suda.datetimewallpaper.util.SharedPreferencesUtil
import me.drakeet.materialdialog.MaterialDialog


/**
 * @author guhaibo
 * @date 2019/4/23
 */
class WallPaperModelAdapter(val wallpaperModels: MutableList<WallPaperModel>) :
    RecyclerView.Adapter<WallPaperModelHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallPaperModelHolder {
        return WallPaperModelHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_model, parent, false))
    }

    override fun getItemCount(): Int {
        return wallpaperModels.size
    }

    override fun onBindViewHolder(holder: WallPaperModelHolder, position: Int) {
        holder.tvName.text = wallpaperModels[position].modelName
        holder.itemView.setOnClickListener(OnClick(holder, position))
        holder.arrowBt.setOnClickListener(OnClick(holder, position))
        holder.cbModel.visibility = View.GONE
        holder.cbModel.isChecked = wallpaperModels[position].isCheck
    }

    inner class OnClick(val holder: WallPaperModelHolder, var position: Int) : View.OnClickListener {
        override fun onClick(v: View) {
            val contentView = LayoutInflater.from(v.context).inflate(R.layout.layout_opt, null)
            val dialog = AlertDialog.Builder(holder.itemView.context)
                .setView(contentView)
                .setCancelable(true)
                .create()

            dialog.show()


            contentView.findViewById<View>(R.id.edit_conf).setOnClickListener {
                val intent = Intent(it.context, SetViewActivity::class.java)
                intent.putExtra("paperId", wallpaperModels[position].paperId)
                it.context.startActivity(intent)
                dialog.cancel()
            }


            contentView.findViewById<View>(R.id.reset_widget).setOnClickListener {
                SharedPreferencesUtil(it.context).lastWidgetId = wallpaperModels[position].paperId
                val intent = Intent(it.context, WidgetRefreshService1::class.java)
                intent.putExtra("command", 1)
                it.context.startService(intent)
                val intent2 = Intent(it.context, WidgetRefreshService2::class.java)
                intent2.putExtra("command", 1)
                it.context.startService(intent2)
                dialog.cancel()
            }

            contentView.findViewById<View>(R.id.edit_name).setOnClickListener { it ->
                val loadDialog = MaterialDialog(it.context)
                val editText = EditText(it.context)
                editText.setText(holder.tvName.text.toString())
                loadDialog.setContentView(editText)
                loadDialog.setTitle("修改名字")
                loadDialog.setCanceledOnTouchOutside(true)
                loadDialog.setNegativeButton("取消") {
                    loadDialog.dismiss()
                }
                loadDialog.setPositiveButton("确认") {
                    if (editText.text.isEmpty()) {
                        Toast.makeText(it.context, "请输入名字", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    val sp = SharedPreferencesUtil(it.context)
                    wallpaperModels[position].modelName = editText.text.toString()
                    sp.editName(wallpaperModels[position].paperId, editText.text.toString())
                    notifyDataSetChanged()
                    loadDialog.dismiss()
                }
                loadDialog.show()
                dialog.cancel()
            }

            contentView.findViewById<View>(R.id.edit_delete).setOnClickListener {
                if (wallpaperModels.size == 1) {
                    Toast.makeText(it.context, "请至少保留一个轮盘", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val sp = SharedPreferencesUtil(it.context)
                sp.deleteConf(wallpaperModels[position].paperId)
                wallpaperModels.removeAt(position)
                notifyDataSetChanged()
                dialog.cancel()
            }

        }
    }

}

class WallPaperModelHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvName: TextView = itemView.findViewById(R.id.tv_model_name)
    val cbModel: CheckBox = itemView.findViewById(R.id.cb_model)
    val arrowBt: View = itemView.findViewById(R.id.arrow_bt)
    val arrowBt_Inner: View = itemView.findViewById(R.id.arrow_bt_inner)

}