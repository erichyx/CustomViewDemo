package cn.eric.customviewdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun waterfallClick(view : View) {
        ShowActivity.actionStart(this, ShowActivity.ViewType.WATERFALL_FLOW.name)
    }

    fun dragBubbleClick(view: View) {
        ShowActivity.actionStart(this, ShowActivity.ViewType.DRAG_BUBBLE.name)
    }

    fun scratchCardClick(view: View) {
        ShowActivity.actionStart(this, ShowActivity.ViewType.SCRATCH_CARD.name)
    }

    fun pathMeasureClick(view: View) {
        ShowActivity.actionStart(this, ShowActivity.ViewType.PATH_MEASURE.name)
    }

    fun svgClick(view: View) {
        ShowActivity.actionStart(this, ShowActivity.ViewType.SVG_MAP.name)
    }

    fun honorAvatarClick(view: View) {
        ShowActivity.actionStart(this, ShowActivity.ViewType.HONOR_AVATAR.name)
    }

    fun dashboardClick(view: View) {
        ShowActivity.actionStart(this, ShowActivity.ViewType.DASHBOARD.name)
    }

    fun pieChartClick(view: View) {
        ShowActivity.actionStart(this, ShowActivity.ViewType.PIE_CHART.name)
    }

    fun avatarClick(view: View) {
        ShowActivity.actionStart(this, ShowActivity.ViewType.AVATAR.name)
    }

    fun sportsClick(view: View) {
        ShowActivity.actionStart(this, ShowActivity.ViewType.SPORTS.name)
    }

    fun imageTextClick(view: View) {
        ShowActivity.actionStart(this, ShowActivity.ViewType.IMAGE_TEXT.name)
    }

    fun cameraClick(view: View) {
        ShowActivity.actionStart(this, ShowActivity.ViewType.CAMERA.name)
    }

    fun materialEditTextClick(view: View) {
        ShowActivity.actionStart(this, ShowActivity.ViewType.MATERIAL_EDIT_TEXT.name)
    }

    fun scalableImageViewClick(view: View) {
        ShowActivity.actionStart(this, ShowActivity.ViewType.SCALABLE_IMAGE_VIEW.name)
    }
}
