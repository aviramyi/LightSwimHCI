package com.example.lightswimhci

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.navigation.fragment.findNavController
import com.example.lightswimhci.databinding.FragmentMainBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var alertDialog: AlertDialog

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle("Calibration")
        alertDialog.setMessage("Calibrated successfully!")
        alertDialog.setButton("OK", DialogInterface.OnClickListener(function =
        { dialog: DialogInterface, which: Int -> }))

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun showNotConnectedSnackbar(){
        view?.let {
            Snackbar.make(it, "Arduino not connected", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSettings.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonStartSession.setOnClickListener {
            val isSessionStarted = (binding.buttonStartSession.text != getString(R.string.end_session))
            if (!ArduinoManager.isOpen()){
                showNotConnectedSnackbar()
                return@setOnClickListener
            }
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

        binding.buttonCalibrate.setOnClickListener{
            if (!ArduinoManager.isOpen()){
                showNotConnectedSnackbar()
                return@setOnClickListener
            }
            ArduinoManager.getInstance().arduino.send("@".toByteArray())
            alertDialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}