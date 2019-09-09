package kartiki.checkoutapp.network

import com.google.gson.annotations.SerializedName

class Item(
    @field:SerializedName("name")
    internal var name : String,
    @field:SerializedName("barcode")
    internal var barcode : String,
    @field:SerializedName("available")
    internal var available : Boolean,
    @field:SerializedName("id")
    internal var id : Long
)