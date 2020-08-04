package com.example.smartgardenapp.view



import android.annotation.SuppressLint
import android.app.TimePickerDialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar

import kotlinx.android.synthetic.main.fragment_first.*
import java.text.SimpleDateFormat
import java.util.*



import android.widget.Toast
import com.example.smartgardenapp.R
import com.example.smartgardenapp.api.apiService
import com.example.smartgardenapp.api.dataModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory





class FirstFragment : Fragment() {
    lateinit var url : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_first, container, false)
    }


    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val x = this.activity!!.intent.extras!!.getString("info")
        val ip = this.activity!!.intent.extras!!.getString("IP")
        url = "http://$ip/"
        val y = x.split(",").toTypedArray()
        val starttime = y[4].toInt()
        val stoptime = y[8].toInt()
        StartTimePicker1.text =
            "" + (starttime / 60) / 10 + (starttime / 60) % 10 + ":" + (starttime % 60) / 10 + (starttime % 60) % 10;
        StopTimePicker1.text =
            "" + (stoptime / 60) / 10 + (stoptime / 60) % 10 + ":" + (stoptime % 60) / 10 + (stoptime % 60) % 10;
        sliderValue1.text = "" + (180 * y[12].toInt()) / 100 + "°"
        slider1.setProgress(y[12].toInt() - 1)
        if (y[0] == "1") switch1.setChecked(true)
        else switch1.setChecked(false)

        StartTimePicker1.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                StartTimePicker1.text = SimpleDateFormat("HH:mm").format(cal.time)
                var code = StartTimePicker1.text.split(":").toTypedArray()
                connect(url,"13" +code[0] + code[1])
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()

        }
        StopTimePicker1.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                StopTimePicker1.text = SimpleDateFormat("HH:mm").format(cal.time)
                var code = StopTimePicker1.text.split(":").toTypedArray()
                connect(url,"14" +code[0] + code[1])
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()

        }

        slider1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                sliderValue1.text = "" + (180 * p1) / 100 + "°"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                println("")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                when {
                    p0!!.progress == 100 -> connect(url, "12${p0.progress}")
                    p0.progress in 10..99 -> connect(url, "120${p0.progress}")
                    p0.progress < 10 -> connect(url, "1200${p0.progress}")
                }

            }
        })
        switch1.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if (onSwitch)
                connect(url, "111")
            else
                connect(url, "110")
        }
    }
@SuppressLint("CheckResult")
private fun connect(url:String, code:String){
    Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(url)
        .build().create(apiService::class.java)
        .fetchData("?kod=$code")
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            { success(it,url,code)},
            {myerror(it,url,code)}
        )
    println(url)
    println(code)

}
    fun success (model: dataModel, url:String, code:String){
        println(model.data)
        if(model.data == code) {
            Toast.makeText(context, "Sukces", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(context, "Blędna odpowiedź", Toast.LENGTH_SHORT).show()

            connect(url,code)
        }
    }
    fun myerror(throwable: Throwable,url:String,code:String){
        Toast.makeText(context, "Brak odpowiedzi", Toast.LENGTH_SHORT).show()

        connect(url,code)
    }
}




