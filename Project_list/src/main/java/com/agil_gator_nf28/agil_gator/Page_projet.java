package com.agil_gator_nf28.agil_gator;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.agil_gator_nf28.BddInterne.TacheBDD;
import com.agil_gator_nf28.BddInterne.ProjetBDD;
import com.agil_gator_nf28.BddInterne.SprintBDD;
import com.agil_gator_nf28.Projet.Projet;
import com.agil_gator_nf28.SousTaches.SousTache;
import com.agil_gator_nf28.SousTaches.SousTacheEtat;
import com.agil_gator_nf28.Taches.Tache;
import com.agil_gator_nf28.Taches.TacheAdapter;
import com.agil_gator_nf28.constantes.AndroidConstantes;

import java.util.ArrayList;
import java.util.List;

/**
 * Activité gérant le tableau scul d'un projet
 */
public class Page_projet extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */


    private static final int  MENU_EDIT = Menu.FIRST;
    private static final int  MENU_DELETE = Menu.FIRST + 1;

    private CharSequence mTitle;
    private int id_project;

    private Projet project;
    private TacheAdapter adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String EXTRA_ID = "user_login";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_projet);
        ListView ListeTaches = (ListView)findViewById(R.id.ListeTaches);
        TextView titre = (TextView)findViewById(R.id.projectPageTitle);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Intent intent = getIntent();
        if (intent != null) {
            ProjetBDD projetBDD = new ProjetBDD(this);
            projetBDD.open();
            int projectId = Integer.valueOf(intent.getStringExtra(EXTRA_ID));

            project = projetBDD.getProjetById(projectId);

            projetBDD.close();

            titre.setText(project.getName());

            SprintBDD sprintBDD = new SprintBDD(this);

           /* TacheBDD tbdd = new  TacheBDD(this);
            tbdd.open();
            Tache task = tbdd.getTacheWithId(1);
            tbdd.close();

            List<Tache> ltache = new ArrayList<Tache>();
            ltache.add(task);*/

        // Mise en place de la liste des tâches
        SousTache sub = new SousTache("lolilol");
        sub.setEtat(SousTacheEtat.AFAIRE);

        id_project = Integer.parseInt(intent.getStringExtra(EXTRA_ID));

        TacheBDD tacheBDD = new TacheBDD(this);
        tacheBDD.open();
        //tacheBDD.insertTache(new Tache("test tache","ceci est un test de tache",1,2,3),new Sprint(null,1));
        Tache tache = tacheBDD.getTacheWithId(1);
       // List<Tache> taches = tacheBDD.getTachesWithIdSprint(3);
        List<Tache> taches = new ArrayList<Tache>();
            taches.add(tache);
        tacheBDD.close();

        //on definit la vue associé au menu floattant
        //this.registerForContextMenu(ListeTaches);

         adapter = new TacheAdapter(this,getApplicationContext(), taches);


            // On dit à la ListView de se remplir via cet adapter
            ListeTaches.setAdapter(adapter);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_add_task);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.page_projet, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.add_task_menu) {
            Intent intent = new Intent(this, Add_Task.class);
            intent.putExtra(AndroidConstantes.PROJECT_ID, String.valueOf(project.getId()));
            startActivity(intent);
            return true;
        }
        if (id == R.id.edit_project) {
            Intent intent = new Intent(Page_projet.this, EditProjet.class);
            intent.putExtra(AndroidConstantes.PROJECT_ID, String.valueOf(project.getId()));
            intent.putExtra(AndroidConstantes.PROJECT_EDIT_FROM, String.valueOf(AndroidConstantes.PROJECT_EDIT_FROM_DETAIL));
            startActivity(intent);
            return true;
        }
        if (id == R.id.archives) {
            Intent intent = new Intent(Page_projet.this, ArchivedSprint.class);
            intent.putExtra(AndroidConstantes.PROJECT_ID, String.valueOf(project.getId()));
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, MENU_EDIT, Menu.NONE, R.string.modif_task);
        menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, R.string.description_task);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Tache selectedTache = (Tache) adapter.getItem(item.getItemId()-1);
        System.out.println("id de la tache selectionne "+ selectedTache.getId());

        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent(this,Modif_task.class);
                intent.putExtra("ID_TA", selectedTache.getId());
                this.startActivity(intent);
                return true;
            case R.id.description_task:
                //editNote(info.id);
                return true;
            case R.id.add_sub__task:
                //editNote(info.id);
                return true;
            case R.id.supp_task:
                //deleteNote(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_page_projet, container, false);

            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));*/
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Page_projet) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
