package kartiki.checkoutapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import androidx.recyclerview.widget.DividerItemDecoration




class MainActivity : AppCompatActivity() {
    private lateinit var itemsAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://shielded-refuge-24263.herokuapp.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    private val service = retrofit.create(HerokuService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewManager = LinearLayoutManager(this)
        fetchItems()
    }

    private fun fetchItems() {
        //TODO show spinner when loading
//        loadingSpinner.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.Main) {
            val getRequest = service.getItems()
            try {
                val response = getRequest.await()
                val items = response.body() // This is List<PlaceholderPosts>
                itemsAdapter = ItemsAdapter(items ?: emptyList())
                itemsListView.apply {
                    setHasFixedSize(true)
                    layoutManager = viewManager
                    adapter = itemsAdapter
                    addItemDecoration(
                        DividerItemDecoration(itemsListView.context, LinearLayout.VERTICAL)
                    )

                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

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
//    }
}

