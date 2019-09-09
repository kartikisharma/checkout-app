package kartiki.checkoutapp.network
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface HerokuService {
    @GET("items")
    fun getItems() : Deferred<Response<List<Item>>>

    @Multipart
    @PUT("items/{id}/")
    fun modifyItemsAvailability(
        @Part("available") available: Boolean,
        @Path("id") itemId: Int
    ) : Deferred<Response<Item>>

    @POST
    fun addItem(
        @Body item: Item
    ) : Call<Item>
}