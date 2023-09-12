package com.tryout.serfinsa.network.auth

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider


// This is our <--Networking Callback-->, which will be help us to retrieve either Success or Failure state

//Auth

//--Google
interface AuthGoogleCallBack {
    fun onRequest(firebaseUser: FirebaseUser)
    fun onRequestFailed()
}
