package com.example.lightswimhci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lightswimhci.databinding.FragmentSettingsBinding

enum class StationOrder {
    CIRCULAR, RANDOM, MANUAL, OPPOSITE
}

enum class AudioFeedback {
    INSTRUMENTAL, VERBAL, AUDIOBOOK, NONE
}

class Settings() {
    private var delimiter = "$"

    var activeStations: BooleanArray = booleanArrayOf(false, false, false, false, false, false)
    var orderOfStations: StationOrder = StationOrder.CIRCULAR
    var audioFeedback: AudioFeedback = AudioFeedback.INSTRUMENTAL

    init {
    }
    fun Serialize() : String {
        var result = "$"

        for (i in 0..5) {
            if (activeStations[i]) {
                result += i.toString()
            }
        }

        result += delimiter
        result += orderOfStations.ordinal.toString()
        result += delimiter
        result += audioFeedback.ordinal.toString()

        return result
    }
}

private var savedSettings: Settings = Settings()

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillFormWithSavedSettings()

        binding.buttonSaveSettings.setOnClickListener {
            ArduinoManager.getInstance().arduino.send(extractSettings())
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    fun fillFormWithSavedSettings() {
        binding.radioButtonStation1.isChecked = savedSettings.activeStations[0]
        binding.radioButtonStation2.isChecked = savedSettings.activeStations[1]
        binding.radioButtonStation3.isChecked = savedSettings.activeStations[2]
        binding.radioButtonStation4.isChecked = savedSettings.activeStations[3]
        binding.radioButtonStation5.isChecked = savedSettings.activeStations[4]
        binding.radioButtonStation6.isChecked = savedSettings.activeStations[5]

        binding.spinnerOrderOfStations.setSelection(savedSettings.orderOfStations.ordinal)
        binding.spinnerAudioFeedback.setSelection(savedSettings.audioFeedback.ordinal)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun extractSettings() : ByteArray{
        if (binding.radioButtonStation1.isChecked){
            savedSettings.activeStations[0] = true
        }
        if (binding.radioButtonStation2.isChecked){
            savedSettings.activeStations[1] = true
        }
        if (binding.radioButtonStation3.isChecked){
            savedSettings.activeStations[2] = true
        }
        if (binding.radioButtonStation4.isChecked){
            savedSettings.activeStations[3] = true
        }
        if (binding.radioButtonStation5.isChecked){
            savedSettings.activeStations[4] = true
        }
        if (binding.radioButtonStation6.isChecked){
            savedSettings.activeStations[5] = true
        }

        savedSettings.orderOfStations = StationOrder.valueOf(binding.spinnerOrderOfStations.selectedItem.toString().uppercase())
        savedSettings.audioFeedback = AudioFeedback.valueOf(binding.spinnerAudioFeedback.selectedItem.toString().uppercase())

        return savedSettings.Serialize().toByteArray()
    }


}

