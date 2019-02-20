package cn.eric.customviewdemo.fragment


import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cn.eric.customviewdemo.R
import cn.eric.customviewdemo.widget.HonorAvatarView
import cn.eric.customviewdemo.utils.dp2px
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition


/**
 * A simple [Fragment] subclass.
 *
 */
class HonorAvatarFragment : Fragment(),View.OnClickListener {

    private lateinit var honorAvatarView : HonorAvatarView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_honor_avatar, container, false)
        honorAvatarView = root.findViewById(R.id.honorAvatarView)
        root.findViewById<View>(R.id.btn_set_avatat).setOnClickListener(this)
        return root
    }

    override fun onClick(v: View?) {


        val requestOptions = RequestOptions().override(dp2px(54f).toInt(), dp2px(58f).toInt())
        Glide.with(this).load("http://xcxstatic.xmappservice.com/4a4f2d023f4c08a3.jpg").apply(requestOptions)
                .into(object :SimpleTarget<Drawable>(){
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {

                        val bitmapDrawable = resource as BitmapDrawable
                        honorAvatarView.setAvatar(bitmapDrawable.bitmap)
                    }
                })
    }


}
