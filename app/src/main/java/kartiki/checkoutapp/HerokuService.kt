package kartiki.checkoutapp
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface HerokuService {
    @GET("items")
    fun getItems() : Call<List<Item>>

    @Multipart
    @PUT("items/{id}/")
    fun postItem(
        @Part("available") available: Boolean,
        @Path("id") itemId: Int
    ) : Call<ResponseBody>
}