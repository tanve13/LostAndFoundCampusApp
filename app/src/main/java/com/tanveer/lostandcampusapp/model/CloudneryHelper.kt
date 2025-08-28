import android.os.Handler
import android.os.Looper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException

object CloudinaryHelper {
    private const val CLOUD_NAME = "dyeywm7b5"
    private const val UPLOAD_PRESET = "unsigned_preset"

    fun uploadImage(
        imageFile: File,
        onResult: (success: Boolean, url: String?) -> Unit
    ) {
        val client = OkHttpClient()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                imageFile.name,
                RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            )
            .addFormDataPart("upload_preset", UPLOAD_PRESET)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 🔥 Always back to main thread
                Handler(Looper.getMainLooper()).post {
                    onResult(false, null)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val success = response.isSuccessful
                    val url = if (success) {
                        val json = response.body?.string()
                        Regex("\"secure_url\":\"(.*?)\"")
                            .find(json ?: "")?.groupValues?.get(1)
                    } else null

                    // 🔥 Always back to main thread
                    Handler(Looper.getMainLooper()).post {
                        onResult(success, url)
                    }
                }
            }
        })
    }
}
