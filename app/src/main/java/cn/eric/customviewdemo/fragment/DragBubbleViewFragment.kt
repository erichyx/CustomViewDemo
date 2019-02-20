package cn.eric.customviewdemo.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cn.eric.customviewdemo.R
import kotlinx.android.synthetic.main.fragment_drag_bubble_view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class DragBubbleViewFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drag_bubble_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_reset_bubble.setOnClickListener { drag_bubble_view.reset() }
    }
}
