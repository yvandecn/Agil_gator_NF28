package com.agil_gator_nf28.BddInterne;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.agil_gator_nf28.SousTaches.SousTache;
import com.agil_gator_nf28.Sprint.Sprint;
import com.agil_gator_nf28.Taches.Tache;
import com.agil_gator_nf28.constantes.AndroidConstantes;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe gérant la table des taches
 *
 * Created by Nicolas on 17/05/14.
 */
public class TacheBDD extends GestionnaireBDD {

    private static final int NUM_COL_ID = 0;
    private static final int NUM_COL_TITRE = 1;
    private static final int NUM_COL_DESCRIPTION = 2;
    private static final int NUM_COL_PRIO = 3;
    private static final int NUM_COL_DIFF = 4;
    private static final int NUM_COL_PROJET = 5;

    private Context context;

    public TacheBDD(Context context){
        super(context);
        this.context = context;
    }

    @Override
    public void open() {
        //on ouvre la BDD en écriture
        bdd = maBaseSQLite.getWritableDatabase();
    }

    @Override
    public void close() {
        //on ferme l'accès à la BDD
        bdd.close();
    }

    @Override
    public SQLiteDatabase getBDD() {
        return bdd;
    }

    public long insertTache(Tache tache, Sprint sprint) {
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();

        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(AndroidConstantes.COL_TACHE_NAME, tache.getName());
        values.put(AndroidConstantes.COL_TACHE_DESCRIPTION, tache.getDescription());
        values.put(AndroidConstantes.COL_TACHE_PRIORITE, tache.getPriorite());
        values.put(AndroidConstantes.COL_TACHE_DIFFICULTE, tache.getDifficulte());
        values.put(AndroidConstantes.COL_TACHE_SPRINT, sprint.getId());

        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(AndroidConstantes.TABLE_TACHE, null, values);
    }

    public long insertTache(Tache tache, int sprintid) {
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();

        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(AndroidConstantes.COL_TACHE_NAME, tache.getName());
        values.put(AndroidConstantes.COL_TACHE_DESCRIPTION, tache.getDescription());
        values.put(AndroidConstantes.COL_TACHE_PRIORITE, tache.getPriorite());
        values.put(AndroidConstantes.COL_TACHE_DIFFICULTE, tache.getDifficulte());
        values.put(AndroidConstantes.COL_TACHE_SPRINT, sprintid);

        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(AndroidConstantes.TABLE_TACHE, null, values);
    }


    public Tache getTacheWithId(int id){
        //selectionne project avec ID
       Cursor c = bdd.rawQuery("SELECT * FROM "+AndroidConstantes.TABLE_TACHE+" WHERE "+AndroidConstantes.COL_TACHE_ID+" = '"+id+"';",null);

        return cursorToTache(c);
    }

    public void deleteTacheWithId(int id){
        bdd.delete(AndroidConstantes.TABLE_TACHE,AndroidConstantes.COL_TACHE_ID +" = "+id+" ",null);
    }

    public int updateTache(int id, Tache tache){
        ContentValues values = new ContentValues();
        values.put(AndroidConstantes.COL_TACHE_NAME, tache.getName());
        values.put(AndroidConstantes.COL_TACHE_PRIORITE, tache.getPriorite());
        values.put(AndroidConstantes.COL_TACHE_DIFFICULTE, tache.getDifficulte());
        values.put(AndroidConstantes.COL_TACHE_DESCRIPTION, tache.getDescription());
        return bdd.update(AndroidConstantes.TABLE_TACHE, values, AndroidConstantes.COL_TACHE_ID + " = " +id, null);
    }

    public List<Tache> getTaches(Sprint sprint) {

        String query = "SELECT * "
                + " FROM " + AndroidConstantes.TABLE_TACHE
                + " WHERE " + AndroidConstantes.COL_TACHE_SPRINT + "=" + sprint.getId() + ";";
        Cursor c = bdd.rawQuery(query, null);


        return toList(c);
    }

    public int getSprintIdWithTacheId(int tache_id){
        String query = "SELECT "+AndroidConstantes.COL_TACHE_SPRINT
                +" FROM "+AndroidConstantes.TABLE_TACHE
                +" WHERE "+AndroidConstantes.COL_TACHE_ID+" = '"+tache_id+"';";
        Cursor c = bdd.rawQuery(query,null);

        if (c.getCount() == 0)
            return 0;

        c.moveToFirst();
        int sprint = c.getInt(0);
        c.close();
        return sprint;
    }

    public List<Tache> getTachesWithIdSprint(int id_sprint) {
        String query = "SELECT * FROM " + AndroidConstantes.TABLE_TACHE
                + " WHERE " + AndroidConstantes.COL_TACHE_SPRINT + "=" + id_sprint
                + " ORDER BY " + AndroidConstantes.COL_TACHE_PRIORITE + " DESC;";
        Cursor c = bdd.rawQuery(query, null);

        return toList(c);
    }

    private Tache cursorToTache(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();

        Tache tache = new Tache();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        tache.setId(c.getInt(NUM_COL_ID));
        tache.setName(c.getString(NUM_COL_TITRE));
        tache.setDescription(c.getString(NUM_COL_DESCRIPTION));
        tache.setDifficulte(c.getInt(NUM_COL_DIFF));
        tache.setPriorite(c.getInt(NUM_COL_PRIO));

        c.close();

        return tache;
    }

    private List<Tache> toList(Cursor c) {

        List<Tache> taches = new ArrayList<Tache>();
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return taches;

        //Sinon on se place sur le premier élément
        c.moveToFirst();

        //On créé un SousTache
        while(!c.isAfterLast()) {
            Tache tache = new Tache();
            //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
            tache.setId(c.getInt(NUM_COL_ID));
            tache.setName(c.getString(NUM_COL_TITRE));
            tache.setPriorite(c.getInt(NUM_COL_PRIO));
            tache.setDifficulte(c.getInt(NUM_COL_DIFF));

            tache.setSousTaches(getSousTaches(tache));

            taches.add(tache);
            c.moveToNext();
        }

        //On ferme le cursor
        c.close();

        return taches;
    }

    private List<SousTache> getSousTaches(Tache tache) {
        List<SousTache> sub = new ArrayList<SousTache>();
        SousTacheBDD sousTacheBDD = new SousTacheBDD(context);
        sousTacheBDD.open();
        sub = sousTacheBDD.getSousTaches(tache);
        sousTacheBDD.close();
        return sub;
    }

    public long insertTacheWithId(Tache tache, int sprintid) {
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();

        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(AndroidConstantes.COL_TACHE_ID, tache.getId());
        values.put(AndroidConstantes.COL_TACHE_NAME, tache.getName());
        values.put(AndroidConstantes.COL_TACHE_DESCRIPTION, tache.getDescription());
        values.put(AndroidConstantes.COL_TACHE_PRIORITE, tache.getPriorite());
        values.put(AndroidConstantes.COL_TACHE_DIFFICULTE, tache.getDifficulte());
        values.put(AndroidConstantes.COL_TACHE_SPRINT, sprintid);

        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(AndroidConstantes.TABLE_TACHE, null, values);
    }
}

