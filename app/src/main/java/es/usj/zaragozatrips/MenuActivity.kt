package es.usj.zaragozatrips

import android.Manifest
import android.app.AlertDialog
import android.app.Fragment
import android.app.NotificationManager
import android.content.Context
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import es.usj.zaragozatrips.fragments.*
import es.usj.zaragozatrips.models.CustomPlace
import es.usj.zaragozatrips.models.Place
import es.usj.zaragozatrips.services.CustomDataManager
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.app_bar_menu.*
import es.usj.zaragozatrips.services.NotificationHelper
import android.content.Intent
import es.usj.zaragozatrips.services.LocationService


class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        NewPlaceFragment.OnFragmentInteractionListener, NearMeFragment.OnFragmentInteractionListener,
        MyPlacesFragment.OnListFragmentInteractionListener, AboutFragment.OnFragmentInteractionListener,
        PlaceDetailFragment.OnFragmentInteractionListener, MyCustomPlacesFragment.OnListFragmentInteractionListener,
        CustomPlaceDetailFragment.OnFragmentInteractionListener, MediaGridFragment.OnListFragmentInteractionListener,
        MediaPreviewFragment.OnFragmentInteractionListener, VideoGridFragment.OnListFragmentInteractionListener,
        VideoPreviewFragment.OnFragmentInteractionListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        setSupportActionBar(toolbar)

        CustomDataManager.initialize(this.filesDir)

        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        if(savedInstanceState == null) {
            replaceFragment(NearMeFragment())
            nav_view.setCheckedItem(R.id.nav_near_me)
        }

        setupLocationService()

        NotificationHelper.notificationManager =  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        NotificationHelper.createNotificationChannel(getString(R.string.channel_name), getString(R.string.channel_description))
    }

    private fun setupLocationService() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {/* ... */
                        if(report.areAllPermissionsGranted()){
                            startService(Intent(this@MenuActivity, LocationService::class.java))
                        }else{
                            Toast.makeText(this@MenuActivity, "All permissions need to be granted to get location", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?, token: PermissionToken?) {
                        //show alert dialog with permission options
                        AlertDialog.Builder(this@MenuActivity)
                                .setTitle(
                                        "Permissions Error!")
                                .setMessage(
                                        "Please allow permissions to get location")
                                .setNegativeButton(
                                        android.R.string.cancel
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                    token?.cancelPermissionRequest()
                                }
                                .setPositiveButton(android.R.string.ok
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                    token?.continuePermissionRequest()
                                }
                                .setOnDismissListener {
                                    token?.cancelPermissionRequest() }
                                .show()
                    }
                }).check()
    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.slide_out) //animations, this must always be set before the replace
                .replace(R.id.fragment_container, fragment) //replacing current fragment with the new one
                .addToBackStack(null)
                .commit()
    }

    override fun onListFragmentInteraction(place: Place) {
        showPlace(place)
    }

    override fun onMediaFragmentInteraction(item: String) {
        val bundle = Bundle()
        bundle.putString(MediaPreviewFragment.EXTRA_MEDIA_KEY, item)
        val fragment = MediaPreviewFragment()
        fragment.arguments = bundle

        replaceFragment(fragment)
    }

    override fun onVideoFragmentInteraction(item: String) {
        val bundle = Bundle()
        bundle.putString(VideoPreviewFragment.EXTRA_MEDIA_KEY, item)
        val fragment = VideoPreviewFragment()
        fragment.arguments = bundle

        replaceFragment(fragment)
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onResume() {
        super.onResume()

        val place = intent.getParcelableExtra<Place>(NotificationHelper.PLACE_KEY_ID)
        if(place != null) {
            showPlace(place)
        }

        val showNearPlaces = intent.getBooleanExtra(NotificationHelper.SHOW_NEAR_PLACES_KEY_ID, false)
        if(showNearPlaces) {
            showNearPlaces()
        }
    }

    override fun onListFragmentInteraction(customPlace: CustomPlace) {
        val bundle = Bundle()
        bundle.putString(getString(R.string.custom_place_item_key), customPlace.id.toString())
        val fragment = CustomPlaceDetailFragment()
        fragment.arguments = bundle

        replaceFragment(fragment)
    }

    private fun showPlace(place: Place) {
        val bundle = Bundle()
        bundle.putParcelable(getString(R.string.place_item_key), place)
        val fragment = PlaceDetailFragment()
        fragment.arguments = bundle

        replaceFragment(fragment)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_near_me -> {
                showNearPlaces()
            }
            R.id.nav_new_place -> {
                replaceFragment(NewPlaceFragment())
            }
            R.id.nav_places -> {
                replaceFragment(MyPlacesFragment())
            }
            R.id.nav_custom_places -> {
                replaceFragment(MyCustomPlacesFragment())
            }
            R.id.nav_about -> {
                replaceFragment(AboutFragment())
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showNearPlaces() {
        replaceFragment(NearMeFragment())
    }
}
