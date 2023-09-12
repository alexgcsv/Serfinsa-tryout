package com.tryout.serfinsa.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import com.tryout.serfinsa.R
import com.tryout.serfinsa.activities.auth.AuthActivity


class Core(var context: Context) {

    /** Val-Var */

    /** Instances */
    val gson = Gson()


    //Alerts
    //Build an alert
    fun buildMessage(title: String,message:String){
        MaterialDialog(context).show {
            title(text = title)
            message(text = message)
            positiveButton(text  = "Ok")
            //icon(R.drawable.)
        }
    }

    //--Loading dialog
    fun showLoadingDialog(): Dialog {
        val progressDialog = Dialog(context)
        progressDialog.let {
            it.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            it.setContentView(R.layout.auth_layout)
            it.setCancelable(false)
            it.setCanceledOnTouchOutside(false)
            it.show()
            return it
        }
    }



    //Skip showedlayout
    fun skipShowAuthActivity(){
        Singleton.init(context)
        if (Singleton.Auth.ShowAuthActivityShowed){ context.launchActivity<AuthActivity>()}
    }

    //is already logged
    fun isAlreadyLogged(){
        Singleton.init(context)
        //if (Singleton.Others.isLogged){ context.launchActivity<MenuActivity>()}
    }


    //Change background - Bottom action bar
    fun changeBackgroundBottomActionBar(color: Int){ context.activity()!!.window.navigationBarColor = ContextCompat.getColor(context,R.color.color_primary); }


    fun getBitmapFromView( bmp: Bitmap?): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(context.externalCacheDir, System.currentTimeMillis().toString() + ".jpg")
            val out = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.close()

            // Use FileProvider to get a content URI for the saved file
            val authority = "${context.packageName}.fileprovider"
            bmpUri = FileProvider.getUriForFile(context, authority, file)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }

    //Get now
    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun now(): String{
        val date = getCurrentDateTime()
        return date.toString("yyyy/MM/dd HH:mm:ss")
    }


    //Menu

    //RecyclerView
    /*Set recyclerview*/
    fun setRecyclerView(recyclerView: RecyclerView){
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
    }

    /*Set horizontal recyclerview*/
    fun setHorizontalRecyclerView(recyclerView: RecyclerView){
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
    }

    /*Set recyclerview grid*/
    fun setRecyclerGridView(recyclerView: RecyclerView){
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
    }

    //Firestore
    fun firestore(): FirebaseFirestore { return Firebase.firestore }

    //Auth
    /*Google*/
    fun googleSignInIntent():Intent{

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.google_web_client_id))
            .requestEmail()
            .build()

        // Configure Google Sign In
        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
        return googleSignInClient.signInIntent
    }



    //Clear prefeerences
    fun clearAllPreferences(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("SerfinsaAppPreferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
    }




}


//**INLINE

//On activity result
inline fun ActivityResult.checkResultAndExecute(block: ActivityResult.() -> Unit) =
    if (resultCode == Activity.RESULT_OK) runCatching(block)
    else Result.failure(Exception("Something went wrong"))

//--Actvities

tailrec fun Context.activity(): Activity? = when (this) {
    is Activity -> this
    else -> (this as? ContextWrapper)?.baseContext?.activity()
}

//Launch activity without overloading
inline fun <reified T : Activity> Context.launchActivity(
    noinline init: Intent.() -> Unit = {}) {

    val intent = newIntent<T>(this)
    intent.init()

    startActivity(intent)

}

inline fun <reified T : Any> newIntent(context: Context): Intent = Intent(context, T::class.java)


//Delay actions
fun View.disableClickTemporarily(){
    isClickable = false
    postDelayed({
        isClickable = true
    },3170)
}


//parcelable
fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T
{
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        activity.intent.getSerializableExtra(name, clazz)!!
    else
        activity.intent.getSerializableExtra(name) as T
}

//To show toast
fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


fun PackageManager.getPackageInfoCompat(
    packageName: String,
    flags: Int = 0
): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
    }