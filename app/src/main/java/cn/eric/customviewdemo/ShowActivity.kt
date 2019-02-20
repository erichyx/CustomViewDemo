package cn.eric.customviewdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cn.eric.customviewdemo.fragment.*
import cn.eric.customviewdemo.utils.ActivityUtils

class ShowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)

        initView()
    }

    private fun initView() {
        val type = intent.getStringExtra(UI_TYPE)
        val viewType = ViewType.valueOf(type)
        val fragment = when (viewType) {
            ViewType.WATERFALL_FLOW -> WaterfallLayoutFragment()
            ViewType.DRAG_BUBBLE -> DragBubbleViewFragment()
            ViewType.SCRATCH_CARD -> ScratchCardFragment()
            ViewType.PATH_MEASURE -> PathMeasureFragment()
            ViewType.SVG_MAP -> SVGFragment()
            ViewType.HONOR_AVATAR -> HonorAvatarFragment()
            ViewType.DASHBOARD -> DashboardFragment()
            ViewType.PIE_CHART -> PieChartFragment()
            ViewType.AVATAR -> AvatarFragment()
            ViewType.SPORTS -> SportsFragment()
            ViewType.IMAGE_TEXT -> ImageTextFragment()
            ViewType.CAMERA -> CameraViewFragment()
            ViewType.MATERIAL_EDIT_TEXT -> MaterialEditTextFragment()
            ViewType.SCALABLE_IMAGE_VIEW -> ScalableImageViewFragment()
        }

        ActivityUtils.replaceFragmentToActivity(supportFragmentManager, fragment,
                R.id.container)
    }

    enum class ViewType {
        WATERFALL_FLOW,DRAG_BUBBLE,SCRATCH_CARD,PATH_MEASURE,SVG_MAP,HONOR_AVATAR,
        DASHBOARD,PIE_CHART,AVATAR,SPORTS,IMAGE_TEXT,CAMERA,MATERIAL_EDIT_TEXT,SCALABLE_IMAGE_VIEW
    }

    companion object {
        private const val UI_TYPE = "ui_type"
        fun actionStart(context: Context, type: String) {
            val intent = Intent(context, ShowActivity::class.java)
            intent.putExtra(UI_TYPE, type)
            context.startActivity(intent)
        }
    }
}
