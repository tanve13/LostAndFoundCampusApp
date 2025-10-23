package com.tanveer.lostandcampusapp.Admin.Repository

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tanveer.lostandcampusapp.data.FileUtils
import com.tanveer.lostandcampusapp.model.ProfileDataClass
import kotlinx.coroutines.tasks.await
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import javax.inject.Inject

class AdminUserRepository  @Inject constructor(){

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getAdminProfile(uid: String): ProfileDataClass? {
        val doc = firestore.collection("admins").document(uid).get().await()
        return doc.toObject(ProfileDataClass::class.java)
    }

    suspend fun updateAdminProfile(context: Context, uid: String, name: String, email: String, photoUri: Uri?) {
        val data = hashMapOf(
            "userName" to name,
            "email" to email
        )
        photoUri?.let {
            // Upload photo to Cloudinary/Firebase Storage, get URL synchronously
            val file = FileUtils.from(context, it)
            val uploadedUrl = uploadPhotoToCloudinary(context, file)
            data["profilePicUrl"] = uploadedUrl
        }
        firestore.collection("admins").document("").update(data as Map<String, Any>).await()
    }

    private suspend fun uploadPhotoToCloudinary(context: Context, file: File): String = withContext(Dispatchers.IO) {
        val CLOUD_NAME = "dyeywm7b5" // <-- yaha apna cloud name daalo
        val UPLOAD_PRESET = "unsigned_preset" // <-- yaha apna upload preset daalo
        val url = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
            .addFormDataPart("upload_preset", UPLOAD_PRESET)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw Exception("Cloudinary upload failed: ${response.message}")

        val responseBody = response.body?.string() ?: throw Exception("No response from Cloudinary")
        val json = JSONObject(responseBody)
        return@withContext json.getString("secure_url") // Yeh hi link image ki Firebase me save karo
    }


}
