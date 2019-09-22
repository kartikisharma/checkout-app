package kartiki.checkoutapp.network
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface HerokuService {
    @GET("items")
    fun getItems() : Deferred<Response<List<Item>>>

    @GET("items/{barcode}")
    fun getItemWithBarcode(
        @Path("barcode") barcode: String
    ) : Deferred<Response<Item>>

    @Multipart
    @PUT("items/{barcode}/")
    fun modifyItemsAvailability(
        @Part("available") available: Boolean,
        @Path("barcode") barcode: String
    ) : Call<ResponseBody>

    @POST
    fun addItem(
        @Body item: Item
    ) : Call<Item>
}