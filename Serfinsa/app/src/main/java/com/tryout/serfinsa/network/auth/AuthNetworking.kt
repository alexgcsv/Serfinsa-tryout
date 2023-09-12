package com.tryout.serfinsa.network.auth

import android.content.Context
import com.google.firebase.auth.*
import com.tryout.serfinsa.network.auth.AuthGoogleCallBack
import com.tryout.serfinsa.utils.activity


class AuthNetworking {

    //Token Responses
    inner class Auth(var context: Context){

        //Google auth
         fun googleAuth(
            idToken: String,
            firebaseAuth: FirebaseAuth,
            authGoogleCallback: AuthGoogleCallBack
        ) {

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(context.activity()!!) { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                            authGoogleCallback.onRequest(user)
                        }else{
                            authGoogleCallback.onRequestFailed()
                        }
                    } else {
                        authGoogleCallback.onRequestFailed()
                    }
                }

        }


    }
}