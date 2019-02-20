package cn.eric.customviewdemo.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cn.eric.customviewdemo.R

/**
 * A simple [Fragment] subclass.
 *
 */
class ScalableImageViewFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scalable_image_view, container, false)
    }


}