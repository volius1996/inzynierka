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
import kotlinx.android.synthetic.main.fragment_second.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class SecondFragment : Fragment() {
    lateinit var url : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val x = this.activity!!.intent.extras!!.getString("info")
        val ip = this.activity!!.intent.extras!!.getString("IP")
        url = "http://$ip/"
        val y = x.split(",").toTypedArray()
        val starttime = y[5].toInt()
        val stoptime = y[9].toInt()
        StartTimePicker2.text =
        "" + (starttime / 60) / 10 + (starttime / 60) % 10 + ":" + (starttime % 60) / 10 + (starttime % 60) % 10
        StopTimePicker2.text = "" + (stoptime / 60) / 10 + (stoptime / 60) % 10 + ":" + (stoptime % 60) / 10 + (stoptime % 60) % 10
        sliderValue2.text = ""+(180*y[13].toInt())/100 +"°"
        slider2.setProgress(y[13].toInt()-1)
        if(y[1]=="1") switch2.setChecked(true)
        else switch2.setChecked(false)
        StartTimePicker2.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY,hour)
                cal.set(Calendar.MINUTE,minute)
                StartTimePicker2.text = SimpleDateFormat("HH:mm").format(cal.time)
                var code = StartTimePicker2.text.split(":").toTypedArray()
                connect(url,"23" +code[0] + code[1])
            }
            TimePickerDialog(requireContext(),timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(
                Calendar.MINUTE), true).show()
        }
        StopTimePicker2.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY,hour)
                cal.set(Calendar.MINUTE,minute)
                StopTimePicker2.text = SimpleDateFormat("HH:mm").format(cal.time)
                var code = StopTimePicker2.text.split(":").toTypedArray()
                connect(url,"24" +code[0] + code[1])
            }
            TimePickerDialog(requireContext(),timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(
                Calendar.MINUTE), true).show()
        }
        slider2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                sliderValue2.text = ""+(180*p1)/100 +"°"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                println("")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                when {
                    p0!!.progress == 100 -> connect(url, "22${p0.progress}")
                    p0.progress in 10..99 -> connect(url, "220${p0.progress}")
                    p0.progress < 10 -> connect(url, "2200${p0.progress}")
                }
            }
        })
        switch2.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if (onSwitch)
                connect(url, "211")
            else
                connect(url, "210")
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

