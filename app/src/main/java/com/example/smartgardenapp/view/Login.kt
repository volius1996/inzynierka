package com.example.smartgardenapp.view


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.login.*


import android.widget.Toast



import android.os.Handler
import com.example.smartgardenapp.R
import com.example.smartgardenapp.api.apiService
import com.example.smartgardenapp.api.dataModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class Login : AppCompatActivity() {
    private lateinit var baseUrl:String

    var ip:String=""
    var port:String=""
    private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        button.setOnClickListener {
            ip = editText.text.toString()
            port = editText2.text.toString()
            val mainActivity = Intent(this,
                MainActivity::class.java )
            mainActivity.putExtra("IP",ip)
            mainActivity.putExtra("port",port)
            this.baseUrl = "http://$ip/"
            Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(this.baseUrl)
                .build().create(apiService::class.java)
                .fetchData("?kod=0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { success(it,mainActivity,this)},
                    {myerror(it,this)}
                )






        }
    }
        fun success (model: dataModel, intent: Intent, context: Context){
            println(model.data)
            if(model.data != "blad") {
                intent.putExtra("info", model.data)
                startActivity(intent)
            }
            else{
                Toast.makeText(context, "Blędna odpowiedź", Toast.LENGTH_SHORT).show()
            }
        }
        fun myerror(throwable: Throwable,context: Context){
            Toast.makeText(context, "Brak odpowiedzi", Toast.LENGTH_SHORT).show()
        }

}





