package cn.eric.customviewdemo.fragment

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import cn.eric.customviewdemo.utils.dp2px

/**
 * Created by eric on 2019/3/31
 */
class CityAdapter : RecyclerView.Adapter<CityViewHolder>() {
    private var items = mutableListOf<String>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CityViewHolder {
        val context = p0.context
        val textView = TextView(context)
        textView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dp2px(40f).toInt())
        return CityViewHolder(textView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: CityViewHolder, p1: Int) {
        p0.textView.text = items[p1]
    }

    fun setData(data : List<String>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }
}

data class CityViewHolder(val textView : TextView) : RecyclerView.ViewHolder(textView)