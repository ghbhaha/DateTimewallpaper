package com.suda.datetimewallpaper.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.zhouwei.library.CustomPopWindow
import com.suda.datetimewallpaper.ui.SetViewActivity
import com.suda.datetimewallpaper.R
import com.suda.datetimewallpaper.bean.WallPaperModel
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

        holder.arrowBt.setOnClickListener { it ->
            val contentView = LayoutInflater.from(it.context).inflate(R.layout.opt_layout, null)
            val window = CustomPopWindow.PopupWindowBuilder(it.context)
                .setView(contentView)
                .create()
                .showAsDropDown(holder.arrowBt_Inner, 0, 0)

            contentView.findViewById<View>(R.id.edit_conf).setOnClickListener {
                val intent = Intent(it.context, SetViewActivity::class.java)
                intent.putExtra("paperId", wallpaperModels[position].paperId)
                it.context.startActivity(intent)
                window.dissmiss()
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
                window.dissmiss()
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
                window.dissmiss()
            }

        }

        holder.cbModel.visibility = View.GONE
        holder.cbModel.isChecked = wallpaperModels[position].isCheck
    }

}

class WallPaperModelHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvName: TextView = itemView.findViewById(R.id.tv_model_name)
    val cbModel: CheckBox = itemView.findViewById(R.id.cb_model)
    val arrowBt: View = itemView.findViewById(R.id.arrow_bt)
    val arrowBt_Inner: View = itemView.findViewById(R.id.arrow_bt_inner)

}