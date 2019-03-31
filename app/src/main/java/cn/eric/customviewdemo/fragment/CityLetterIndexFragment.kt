package cn.eric.customviewdemo.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.eric.customviewdemo.R
import cn.eric.customviewdemo.widget.LetterIndexView
import kotlinx.android.synthetic.main.fragment_city_letter_index.*


/**
 * A simple [Fragment] subclass.
 *
 */
class CityLetterIndexFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_city_letter_index, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cityAdapter = CityAdapter()
        rv_city.apply {
            setHasFixedSize(true)
            adapter = cityAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        cityAdapter.setData(getData())

        val letterMap = hashMapOf<String,Int>().apply {
            put("A", 0)
            put("B", 18)
            put("C", 22)
            put("F", 25)
            put("I", 28)
            put("K", 31)
            put("L", 34)
            put("M", 35)
            put("N", 37)
            put("S", 38)
            put("T", 40)
            put("X", 43)
        }

        letterIndexView.setOnTouchingLetterChangedListener(object : LetterIndexView.OnTouchingLetterChangedListener {

            override fun onHit(letter: String) {
                tv_hint.visibility = View.VISIBLE
                tv_hint.text = letter
                var index = -1
                if ("↑" == letter) {
                    index = 0
                } else if (letterMap.containsKey(letter)) {
                    index = letterMap.getValue(letter)
                }
                if (index < 0) {
                    return
                }
                rv_city.scrollToPosition(index)
            }

            override fun onCancel() {
                tv_hint.visibility = View.GONE
            }
        })
    }

    private fun getData(): List<String> {
        val data = mutableListOf<String>()
        data.add("abc")
        data.add("aabc")
        data.add("aabc")
        data.add("aabc")
        data.add("aabc")
        data.add("aabc")
        data.add("aabc")
        data.add("aabc")
        data.add("aabc")
        data.add("aabc")
        data.add("aabc")
        data.add("aabc")
        data.add("aabc")
        data.add("aabc")
        data.add("aabc")
        data.add("aabc")
        data.add("aabc")
        data.add("babc")
        data.add("b")
        data.add("bc")
        data.add("babc")
        data.add("cabc")
        data.add("cabc")
        data.add("cabc")
        data.add("fabc")
        data.add("fabc")
        data.add("fabc")
        data.add("iabc")
        data.add("i")
        data.add("ic")
        data.add("kabc")
        data.add("kabc")
        data.add("kabc")
        data.add("leavesC")
        data.add("mabc")
        data.add("mabc")
        data.add("nabc")
        data.add("sabc")
        data.add("sabc")
        data.add("tabc")
        data.add("tabc")
        data.add("tabc")
        data.add("xabc")
        data.add("xabc")
        data.add("xabc")
        return data
    }


}
