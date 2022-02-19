package com.example.lightswimhci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lightswimhci.databinding.FragmentMainBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSettings.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonStartSession.setOnClickListener {
            val isSessionStarted = (binding.buttonStartSession.text != getString(R.string.end_session))
            if (!isSessionStarted) {
                binding.buttonStartSession.text = getString(R.string.start_session)
                binding.buttonSettings.visibility = View.VISIBLE
                binding.buttonCalibrate.visibility = View.VISIBLE
                binding.textCalibrate.visibility = View.VISIBLE
            } else {
                binding.buttonStartSession.text = getString(R.string.end_session)
                binding.buttonSettings.visibility = View.INVISIBLE
                binding.buttonCalibrate.visibility = View.INVISIBLE
                binding.textCalibrate.visibility = View.INVISIBLE
            }
            ArduinoManager.getInstance().arduino.send(("!$isSessionStarted").toByteArray())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}