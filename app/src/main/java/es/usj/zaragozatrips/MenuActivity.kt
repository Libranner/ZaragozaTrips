package es.usj.zaragozatrips

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import es.usj.zaragozatrips.fragments.*
import es.usj.zaragozatrips.fragments.dummy.DummyContent
import es.usj.zaragozatrips.models.Coordinate
import es.usj.zaragozatrips.models.Place
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.app_bar_menu.*


class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        NewPlaceFragment.OnFragmentInteractionListener, NearMeFragment.OnFragmentInteractionListener,
        MyPlacesFragment.OnListFragmentInteractionListener, AboutFragment.OnFragmentInteractionListener,
        PlaceDetailFragment.OnFragmentInteractionListener{

    override fun onListFragmentInteraction(item: DummyContent.DummyItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        setSupportActionBar(toolbar)

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
            val bundle = Bundle()
            val images = arrayOf("https://cdn.traveler.es/uploads/images/thumbs/es/trav/3/s/2018/35/el_calamar_bravo_3337_745x559.jpg",
                    "https://media-cdn.tripadvisor.com/media/photo-s/06/b1/52/33/el-calamar-bravo.jpg")
            val place = Place("El Calamar Bravo", "Comida & Bebida", "Desde las 9AM hasta las 10AM",
                    Coordinate(41.6520855, -0.883574), "https://www.youtube.com/watch?v=cmkV-vWx04o", images,
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nu")

            bundle.putParcelable("test", place)

            val fragment =  PlaceDetailFragment()
            fragment.arguments = bundle

            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
            nav_view.setCheckedItem(R.id.nav_about)
        }
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
        return false //TODO: Determine if this is neccesary option buttons in the toolbar
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
                fragmentManager.beginTransaction().replace(R.id.fragment_container, NearMeFragment()).commit()
            }
            R.id.nav_new_place -> {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, NewPlaceFragment()).commit()
            }
            R.id.nav_my_places -> {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, MyPlacesFragment()).commit()
            }
            R.id.nav_about -> {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, PlaceDetailFragment()).commit()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
