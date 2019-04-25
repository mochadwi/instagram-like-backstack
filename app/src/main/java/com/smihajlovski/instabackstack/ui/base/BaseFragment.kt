package com.smihajlovski.instabackstack.ui.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment

open class BaseFragment : Fragment() {

    protected var fragmentInteractionCallback: FragmentInteractionCallback? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            fragmentInteractionCallback = context as FragmentInteractionCallback?
        } catch (e: ClassCastException) {
            throw RuntimeException(context!!.toString() + " must implement " + FragmentInteractionCallback::class.java!!.getName())
        }

    }

    override fun onDetach() {
        fragmentInteractionCallback = null
        super.onDetach()
    }

    interface FragmentInteractionCallback {

        fun onFragmentInteractionCallback(bundle: Bundle)
    }

    companion object {
        @JvmStatic
        lateinit var currentTab: String
    }
}
