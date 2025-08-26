package com.tanveer.lostandcampusapp.model

import android.net.Uri
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.IOException

object CloudinaryHelper {
    private const val CLOUD_NAME = " dyeywm7b5"
    private const val UPLOAD_PRESET = "unsigned_preset"

    fun uploadImage(
        imageFile: File,
        onResult: (success: Boolean, url: String?) -> Unit
    ) {
        val client = OkHttpClient()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", imageFile.name,
                RequestBody.create("image/*".toMediaTypeOrNull(), imageFile))
            .addFormDataPart("upload_preset", UPLOAD_PRESET)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onResult(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        onResult(false, null)
                    } else {
                        val json = response.body?.string()
                        // extract secure_url from JSON
                        val url = Regex("\"secure_url\":\"(.*?)\"")
                            .find(json ?: "")?.groupValues?.get(1)
                        onResult(true, url)
                    }
                }
            }
        })
    }
}
