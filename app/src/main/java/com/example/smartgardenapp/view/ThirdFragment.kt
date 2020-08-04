package com.example.smartgardenapp.view


import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import com.example.smartgardenapp.R
import com.example.smartgardenapp.api.apiService
import com.example.smartgardenapp.api.dataModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_third.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ThirdFragment : Fragment() {
    lateinit var url : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third, container, false)
    }
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val x = this.activity!!.intent.extras!!.getString("info")
        val ip = this.activity!!.intent.extras!!.getString("IP")
        url = "http://$ip/"
        val y = x.split(",").toTypedArray()
        val starttime = y[6].toInt()
        val stoptime = y[10].toInt()
        StartTimePicker3.text = "" + (starttime / 60) / 10 + (starttime / 60) % 10 + ":" + (starttime % 60) / 10 + (starttime % 60) % 10;
        StopTimePicker3.text = "" + (stoptime / 60) / 10 + (stoptime / 60) % 10 + ":" + (stoptime % 60) / 10 + (stoptime % 60) % 10;
        sliderValue3.text = ""+(180*y[14].toInt())/100 +"°"
        slider3.setProgress(y[14].toInt()-1)
        if(y[2]=="1") switch3.setChecked(true)
        else switch3.setChecked(false)

        StartTimePicker3.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY,hour)
                cal.set(Calendar.MINUTE,minute)
                StartTimePicker3.text = SimpleDateFormat("HH:mm").format(cal.time)
                var code = StartTimePicker3.text.split(":").toTypedArray()
                connect(url,"33" +code[0] + code[1])
            }
            TimePickerDialog(requireContext(),timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(
                Calendar.MINUTE), true).show()
        }
        StopTimePicker3.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY,hour)
                cal.set(Calendar.MINUTE,minute)
                StopTimePicker3.text = SimpleDateFormat("HH:mm").format(cal.time)
                var code = StopTimePicker3.text.split(":").toTypedArray()
                connect(url,"34" +code[0] + code[1])
            }
            TimePickerDialog(requireContext(),timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(
                Calendar.MINUTE), true).show()
        }
        slider3.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                sliderValue3.text = ""+(180*p1)/100 +"°"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                when {
                    p0!!.progress == 100 -> connect(url, "32${p0.progress}")
                    p0.progress in 10..99 -> connect(url, "320${p0.progress}")
                    p0.progress < 10 -> connect(url, "3200${p0.progress}")
                }
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                println("")
            }
        })
        switch3.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if (onSwitch)
                connect(url, "311")
            else
                connect(url, "310")
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


