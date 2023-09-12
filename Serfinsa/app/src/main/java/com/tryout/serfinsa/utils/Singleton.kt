@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.tryout.serfinsa.utils
import android.content.Context
import android.content.SharedPreferences

object Singleton{
    private const val NAME = "SerfinsaAppPreferences"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    // List of preferences for users
    private val SHOWAUTH = Pair("showauth.serfinsa", false)
    private val VERIFICATIONID = Pair("verificationId.serfinsa", "")
    private val UID = Pair("uid.serfinsa", "")

    //List of preferences for google
    private val DISPLAY_NAME_GOOGLE = Pair("google.dname.serfinsa", "")
    private val EMAIL_GOOGLE = Pair("google.email.serfinsa", "")
    private val FAMILY_NAME_GOOGLE = Pair("google.family.serfinsa", "")
    private val GIVEN_NAME_GOOGLE = Pair("google.given.serfinsa", "")
    private val PHOTO_GOOGLE = Pair("google.photo.serfinsa", "")


    // List of preferences at others
    private val LOGGED = Pair("logged.serfinsa", false)

    //List of preferences for objects
    private val USER = Pair("user.serfinsa", "")




    fun init(context: Context) { preferences = context.getSharedPreferences(NAME, MODE) }

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }


    //Auth preferences
    object Auth {

        //--Show auth activity already showed
        var ShowAuthActivityShowed: Boolean
            get() = preferences.getBoolean(SHOWAUTH.first, SHOWAUTH.second)
            set(value) = preferences.edit { it.putBoolean(SHOWAUTH.first, value) }


        //--Set UID
        var uid: String
            get() = preferences.getString(UID.first, UID.second).toString()
            set(value) = preferences.edit {
                it.putString(UID.first, value)
            }


    }

    object GoogleAuth{
        //--Set Display Name
        var displayName: String
            get() = preferences.getString(DISPLAY_NAME_GOOGLE.first, DISPLAY_NAME_GOOGLE.second).toString()
            set(value) = preferences.edit {
                it.putString(DISPLAY_NAME_GOOGLE.first, value)
            }

        //--Set email
        var email: String
            get() = preferences.getString(EMAIL_GOOGLE.first, EMAIL_GOOGLE.second).toString()
            set(value) = preferences.edit {
                it.putString(EMAIL_GOOGLE.first, value)
            }

        //--Set Given Name
        var givenName: String
            get() = preferences.getString(GIVEN_NAME_GOOGLE.first, GIVEN_NAME_GOOGLE.second).toString()
            set(value) = preferences.edit {
                it.putString(GIVEN_NAME_GOOGLE.first, value)
            }

        //--Set Display Name
        var familyName: String
            get() = preferences.getString(FAMILY_NAME_GOOGLE.first, FAMILY_NAME_GOOGLE.second).toString()
            set(value) = preferences.edit {
                it.putString(FAMILY_NAME_GOOGLE.first, value)
            }

        //--Set Display Name
        var photo: String
            get() = preferences.getString(PHOTO_GOOGLE.first, PHOTO_GOOGLE.second).toString()
            set(value) = preferences.edit {
                it.putString(PHOTO_GOOGLE.first, value)
            }


    }

    object Others{


        //--Set a if logged
        var isLogged: Boolean
            get() = preferences.getBoolean(LOGGED.first, LOGGED.second)
            set(value) = preferences.edit {
                it.putBoolean(LOGGED.first, value)
            }


    }

    object Objects{
        //--Set a class through activities
        var user: String
            get() = preferences.getString(USER.first, USER.second).toString()
            set(value) = preferences.edit {
                it.putString(USER.first, value)
            }



    }



}


//Companion objects


