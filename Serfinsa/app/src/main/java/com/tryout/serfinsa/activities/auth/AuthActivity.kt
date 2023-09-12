package com.tryout.serfinsa.activities.auth

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tryout.serfinsa.network.auth.AuthNetworking
import com.tryout.serfinsa.databinding.AuthLayoutBinding
import com.tryout.serfinsa.R
import com.tryout.serfinsa.network.auth.AuthGoogleCallBack
import com.tryout.serfinsa.utils.*


class AuthActivity : AppCompatActivity(), View.OnClickListener {

    /** Val-Var */

    /** Instances */
    var acceptButton: MaterialButton? = null
    var messageTextView: MaterialTextView? = null
    private var progressDialog : Dialog? = null
    private lateinit var binding: AuthLayoutBinding
    val core by lazy { Core(this) }
    //-- Initialize Firebase Auth
    private var firebaseAuth: FirebaseAuth? = null
    private lateinit var  firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_layout)

        binding = AuthLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Calling our instances for components, view, and elements
        initInstances()

        //Initializing our elements/views/utils
        initViews()

    }

    //To call views(Elements, components) on XML
    private fun initViews(){
    }

    //To set instances (OnclickListener, etc...)
    private fun initInstances(){

        //SetOnClickListener
        binding.googleButtonSiginIn.setOnClickListener(this)

        //Init singleton
        Singleton.init(this@AuthActivity)

        //Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Auth
        firebaseAuth = Firebase.auth


    }

    override fun onBackPressed() {return }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        try { firebaseUser = firebaseAuth!!.currentUser!! } catch (e: Exception) { }
    }

    //OnclickListener
    override fun onClick(v: View?) {
        v.let { it!!.disableClickTemporarily()}
        when (v?.id) {
            R.id.googleButtonSiginIn-> {googleData.launch(core.googleSignInIntent())}

        }
    }

    //General

    //Loading dialog
    private fun showLoading() {
        runOnUiThread {
            progressDialog = core.showLoadingDialog()
        }
    }

    private fun hideLoading() {
        runOnUiThread {
            if (progressDialog != null) {
                progressDialog?.let { if (it.isShowing) it.cancel() }
            }
        }
    }

    //Build mesage bottom sheet
    @SuppressLint("CutPasteId")
    private fun buildBottomSheetSearch(message: String, color:Int){
        MaterialDialog(this, BottomSheet(LayoutMode.MATCH_PARENT)).show {
            this.customView(R.layout.alert_message_layout)

            //Set RecyclerView
            acceptButton = this.getCustomView().findViewById(R.id.acceptButtonAlertMessage)
            messageTextView = this.getCustomView().findViewById(R.id.titleTextViewAlertmessage)

            this.getCustomView().findViewById<ImageView>(R.id.playerImageview)
                .setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.MULTIPLY);

            this.getCustomView().findViewById<MaterialTextView>(R.id.titleTextViewAlertmessage)
                .text = message

            this.getCustomView().findViewById<MaterialButton>(R.id.acceptButtonAlertMessage)
                .setOnClickListener {
                    this.cancel()
                }
            this.onDismiss { }

            this.cancelOnTouchOutside(true)
            this.cornerRadius(5F)
        }
    }

    //Networking
    //--Auth


    /*--Google--*/
    //Task to check is user is valid at Google / oauth call
    private val googleData=registerForActivityResult(ActivityResultContracts.StartActivityForResult()
    ) {
        it.checkResultAndExecute {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let {
                Singleton.GoogleAuth.photo = account.photoUrl.let { account.photoUrl.toString()  }
                Singleton.GoogleAuth.displayName = account.displayName ?: "" // Full Name
                Singleton.GoogleAuth.givenName = account.givenName ?: "" //First name
                Singleton.GoogleAuth.familyName = account.familyName ?: "" //Last name
                Singleton.GoogleAuth.email = account.email ?: ""
                googleAuth(account.idToken!!)
            }

        }.onFailure { e -> runOnUiThread { buildBottomSheetSearch(getString(R.string.issue_auth_dialog), R.color.color_primary) }}
    }

    /**
     * @param idToken          Token to get credential at google auth.
     * @return onRequest - You have been logged successfully.
     * @throws onRequestFailed An issue will be logged in order to check some missing requirements.
     */
    private fun googleAuth(idToken: String) {
        AuthNetworking().Auth(this).googleAuth(idToken,firebaseAuth!!,object :
            AuthGoogleCallBack {
            override fun onRequest(firebaseUser: FirebaseUser) {
                this@AuthActivity.firebaseUser = firebaseUser
                //checkUser(firebaseUser.uid)

                Toast.makeText(this@AuthActivity,"HOLAAA",Toast.LENGTH_SHORT).show()
            }

            override fun onRequestFailed() {
                runOnUiThread { buildBottomSheetSearch(getString(R.string.issue_auth_dialog), R.color.color_primary) }
            }

        })
    }
    /*--Google--*/

    /*--Facebook--*/
    /**
     * @param idToken          Token to get credential at google auth.
     * @return onRequest - You have been logged successfully.
     * @throws onRequestFailed An issue will be logged in order to check some missing requirements.
     */


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }



}