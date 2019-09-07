package kartiki.checkoutapp

import com.google.gson.annotations.SerializedName

class Item(
    @field:SerializedName("name")
    internal var name : String,
    @field:SerializedName("available")
    internal var available : Boolean,
    @field:SerializedName("id")
    internal var id : Long
)