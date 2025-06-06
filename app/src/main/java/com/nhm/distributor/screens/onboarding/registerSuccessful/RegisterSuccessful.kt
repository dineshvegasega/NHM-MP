package com.nhm.distributor.screens.onboarding.registerSuccessful

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.nhm.distributor.R
import com.nhm.distributor.databinding.RegisterSuccessfulBinding
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.utils.firstCharIfItIsLowercase
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterSuccessful : Fragment() {
    private var _binding: RegisterSuccessfulBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RegisterSuccessfulBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(0)
        binding.apply {
            btSignIn.singleClick {
                requireView().findNavController().navigate(R.id.action_registerSuccessful_to_loginPassword)
            }
            val text =
                "<font color="+ ResourcesCompat.getColor(resources, R.color.black, null)+">"+resources.getString(R.string.congratulations)+"</font> <br/>" +
                        "<font color="+ ResourcesCompat.getColor(resources, R.color._E79D46, null)+">"+arguments?.getString("key")?.firstCharIfItIsLowercase()+"</font>"+
                        "<font color="+ ResourcesCompat.getColor(resources, R.color.black, null)+">,</font> "+
                        "<font color="+ ResourcesCompat.getColor(resources, R.color.black, null)+">"+resources.getString(R.string.your_initial_registration_is_complete)+"</font>"

//            val text =
//                "<font color="+ ResourcesCompat.getColor(resources, R.color.black, null)+">"+resources.getString(R.string.congratulations)+"</font> " +
//                        "<font color="+ ResourcesCompat.getColor(resources, R.color._E79D46, null)+">"+arguments?.getString("key")+"</font>"+
//                        "<font color="+ ResourcesCompat.getColor(resources, R.color.black, null)+">,</font> <br/>"+
//                        "<font color="+ ResourcesCompat.getColor(resources, R.color.black, null)+">"+resources.getString(R.string.your_initial_registration_is_complete)+"</font>"

                        textHeaderTxt312.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)

        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}