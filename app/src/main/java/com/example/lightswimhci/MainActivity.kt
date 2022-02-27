package com.example.lightswimhci

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.lightswimhci.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private val helpDescription = """
To start, please navigate to the "Settings" page.
There, select the stations that should be activated during the session. You can also choose vary the Audio feedbacks and the method of alternating stations.

After saving the settings, feel free to click on "Start session" whenever your patient and you are ready to go.
Calibration happens everytime the sensing unit is turned on, but can also be triggered manually using the "Calibrate" button."""
    private val aboutDescription = """Thank you for using the HCI course Light Swim application.
We are happy to hear any feedback and provide support using the "contact us" button, so please feel free to reach out!
        
This application was developed for the Human Computer Interaction course, HUJI in corporation with Alyn Hospital, 2022.
It was designed to help therapists like you to conduct swimming therapy sessions that are interactive and fun, both for the patient and the therapist.
Using visual and audible feedback, the patient is encouraged to swim between stations for physical practice."""
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var arduinoManager: ArduinoManager
    private lateinit var helpDialog: AlertDialog;
    private lateinit var aboutDialog: AlertDialog;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        helpDialog = AlertDialog.Builder(this, R.style.MyDialogTheme).create()
        helpDialog.setTitle("Instructions")
        helpDialog.setMessage(helpDescription)
        helpDialog.setButton("OK", DialogInterface.OnClickListener(function =
        { dialog: DialogInterface, which: Int -> }))
        aboutDialog = AlertDialog.Builder(this, R.style.MyDialogTheme).create()
        aboutDialog.setTitle("Light Swim Therapy Application")
        aboutDialog.setMessage(aboutDescription)
        aboutDialog.setButton("OK", DialogInterface.OnClickListener(function =
        { dialog: DialogInterface, which: Int -> }))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            emailIntent.type = "vnd.android.cursor.item/email"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("LightSwimHCISupport@huji.mail.ac.il"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback / Support on LightSwim")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi! I would you to know about:")
            startActivity(emailIntent)
        }
        arduinoManager = ArduinoManager.getInstance(this,
            Snackbar.make(findViewById<View>(android.R.id.content).rootView, "Arduino not connected", Snackbar.LENGTH_LONG)
                .setAction("Action", null))

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.help_menu_button -> {
                helpDialog.show()
                return true
            }
            R.id.about_menu_button -> {
                aboutDialog.show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}