package kartiki.checkoutapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://shielded-refuge-24263.herokuapp.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(HerokuService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //TODO remove the button, replace with list populated by a getItems call
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

// PUT
//                val call = service.postItem(true, 2)
//                call.enqueue(object : Callback<ResponseBody> {
//                    override fun onResponse(
//                        call: Call<ResponseBody>,
//                        response: Response<ResponseBody>
//                    ) {
//                        try {
////                            val body = response.body()
//                            textView.text = response.code().toString()
//                        } catch (e: IOException) {
//                            e.printStackTrace()
//                            textView.text = e.message
//                        }
//
//                    }
//
//                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                        t.printStackTrace()
//                        textView.text = t.message
//                    }
//                })
//            }
            }
        })
    }

//            GET
//                button.setOnClickListener(object : View.OnClickListener {
//                    override fun onClick(v: View?) {
//                        loadingSpinner.visibility = View.VISIBLE
//                        val call = service.getItems()
//                        call.enqueue(object : Callback<List<Item>> {
//                            override fun onResponse(
//                                call: Call<List<Item>>,
//                                response: Response<List<Item>>
//                            ) {
//                                try {
//                                    val body = response.body()
//                                    if (body != null && body.isNotEmpty()) {
//                                        textView.text = body[0].id.toString()
//                                    }
//                                    loadingSpinner.visibility = View.GONE
//                                } catch (e: IOException) {
//                                    e.printStackTrace()
//                                    textView.text = e.message
//                                }
//
//                            }
//
//                            override fun onFailure(call: Call<List<Item>>, t: Throwable) {
//                                t.printStackTrace()
//                                textView.text = t.message
//                            }
//                        })
//                    }
//                })
//            }
//        })
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.barcode_scanner -> {
                startActivity(Intent(this@MainActivity,
                    LiveBarcodeScanningActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

