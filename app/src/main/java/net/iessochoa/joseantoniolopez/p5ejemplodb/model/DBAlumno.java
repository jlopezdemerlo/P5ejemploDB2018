package net.iessochoa.joseantoniolopez.p5ejemplodb.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import net.iessochoa.joseantoniolopez.p5ejemplodb.model.AlumnoContract.*;

import java.util.ArrayList;

/**
 * Created by JoseA on 01/11/2016.
 */

public class DBAlumno {
    //si cambiamos la versión, en SQLiteOpenHelper se llamará a onUpdate
    private static final int DATABASE_VERSION = 2;
    //nombre del archivo
    private static final String DATABASE_NAME = "Alumnos.db";
    //creamos las sentencias que nos serán útiles en la clase. Muchas de ellas parametrizadasw
    private static final String CREATE_TABLE = "CREATE TABLE if not exists " + AlumnoEntry.TABLE_NAME + " ("
            + AlumnoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + AlumnoEntry.NOMBRE + " TEXT NOT NULL,"
            + AlumnoEntry.GRUPO + " TEXT NOT NULL,"
            + AlumnoEntry.FOTO_URI + " TEXT, "
            + "UNIQUE (" + AlumnoEntry.NOMBRE + ")"
            + ")";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + AlumnoEntry.TABLE_NAME;
    //En este ejemplo no se utilizan pero si la he puesto para que veais otra alternativa para manipula la b.d.
    private static final String SQL_INSERT = "INSERT INTO " + AlumnoEntry.TABLE_NAME +
            " VALUES(" + AlumnoEntry.NOMBRE +
            "," + AlumnoEntry.GRUPO +
            "," + AlumnoEntry.FOTO_URI +
            ") VALUES (?,?,?)";
    private static final String SQL_UPDATE = "UPDATE " + AlumnoEntry.TABLE_NAME +
            " SET " +
            AlumnoEntry.GRUPO + "=? , " +
            AlumnoEntry.FOTO_URI + "=?  " +
            " WHERE " + AlumnoEntry.NOMBRE + " = ?";
    private static final String SQL_GRUPOS="SELECT DISTINCT "+AlumnoEntry.GRUPO
            +" FROM "+AlumnoEntry.TABLE_NAME
            +" ORDER BY "+AlumnoEntry.GRUPO+" ASC";
    //Nos permitira abrir la base de datos
    private AlumnosDbHelper dbH;
    private SQLiteDatabase db;

    public DBAlumno(Context context) {
        dbH = new AlumnosDbHelper(context);
    }

    /**
     * Abre la base de datos
     */
    public void open() throws SQLiteException {
        if ((db == null) || (!db.isOpen()))
            db = dbH.getWritableDatabase();
    }

    public void close() throws SQLiteException {
        if (db.isOpen())
            db.close();
    }

    /**
     * Con esta clase le diremos a android que cree la base de datos on que
     * tiene que hacer cuando se modifica la version de la base de datos
     */
    private class AlumnosDbHelper extends SQLiteOpenHelper {


        public AlumnosDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        /* Alternativa para que la base de datos la cree en la tarjeta SD para que podais copiarla en dispositivos
    que no esten rooteados
    En el AndroidManifest teneis que añadir el permiso para leer en la tajeta SD
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"> </uses-permission>
    No realizamos comprobaciones del estado de la memoria(si exites, si permite lectura....). En un aplicacion
    real tendriamos que hacerlo*/
        /*public AlumnosDbHelper(Context context) {
        //Guardamos la base de datos en la tarjeta SD, pero no realizamos ningun tipo de comprobacion previa.
            super(context, context.getExternalFilesDir("databases").getAbsolutePath()+File.separator+DATABASE_NAME, null, DATABASE_VERSION);
        }*/
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE);
            db.execSQL(CREATE_TABLE);

        }
    }



    /**
     * Insterta el alumno pasado por parametro
     *
     * @param alumno
     */
    public void insertaAlumno  (Alumno alumno) throws SQLiteException,SQLiteConstraintException {
        //********alternativa 1******** para insertar. Evita inyección de sql
        ContentValues values = new ContentValues();
        values.put(AlumnoEntry.NOMBRE, alumno.getNombre());
        values.put(AlumnoEntry.GRUPO, alumno.getGrupo());
        if (!alumno.getFotoUri().equals(""))
            values.put(AlumnoEntry.FOTO_URI, alumno.getFotoUri());
        //Si queremos que salte la excepción en caso de problemas en la insercción
        //tenemos que utilizar insertOrThrow, en otro caso podemos utilizar insert
        db.insertOrThrow(AlumnoEntry.TABLE_NAME, null, values);
        //db.insert(AlumnoEntry.TABLE_NAME, null, values);
        //*******alternativa 2*********
        /*String[] arg=new String[]{alumno.getNombre(),alumno.getGrupo(),alumno.getFotoUri()};
        db.execSQL(SQL_INSERT,arg);*/

    }

    public void actualizaAlumno(Alumno alumno) throws SQLiteException,SQLiteConstraintException {
        //valores a modificar
        ContentValues values = new ContentValues();
        values.put(AlumnoEntry.NOMBRE, alumno.getNombre());
        values.put(AlumnoEntry.GRUPO, alumno.getGrupo());
        if (!alumno.getFotoUri().equals(""))
            values.put(AlumnoEntry.FOTO_URI, alumno.getFotoUri());
        //Parte where de la sentencia
        String where = AlumnoEntry.NOMBRE + "=?";
        String[] arg = new String[]{alumno.getNombre()};
        //actualizamos
        db.update(AlumnoEntry.TABLE_NAME, values, where, arg);
        //alternativa 2
       /* String[] arg=new String[]{alumno.getGrupo(),alumno.getFotoUri(),alumno.getNombre()};
        db.execSQL(SQL_UPDATE,arg);*/
    }

    public void borraAlumno(Alumno alumno) throws SQLiteException,SQLiteConstraintException {
        db.delete(AlumnoEntry.TABLE_NAME, AlumnoEntry.NOMBRE + "= ?", new String[]{alumno.getNombre()});
    }

    /**
     * Devolvemos un cursor con toda la tabla de alumnos
     * @param grupo: si n es nulo busca todos en otro caso devuelve los alumnos del grupo
     */
    public Cursor getCursorAlumnos(String grupo) throws SQLiteException {


        String where=null;
        String[] argWhere=null;
        if(grupo!=null){
            where=AlumnoEntry.GRUPO+"= ?";//"grupo=?"
            argWhere=new String[]{grupo};
        }
        Cursor cursor = db.query(AlumnoEntry.TABLE_NAME, null, where, argWhere, null, null, null);
        //close();
        return cursor;
        //otra forma
        //return db.rawQuery("SELECT * FROM ALUMNOS "+where,argWhere);
    }

    /**
     * Dada una posición del cursor, nos devuelve un objeto Alumno
     */
    public static Alumno deCursorAAlumno(Cursor cursor) {
        int indiceColumna;
        //obtenemos la posicion de la columna id
        indiceColumna = cursor.getColumnIndex(AlumnoEntry._ID);
        //obtenemos el valor del id
        String id = cursor.getString(indiceColumna);
        indiceColumna = cursor.getColumnIndex(AlumnoEntry.NOMBRE);
        String nombre = cursor.getString(indiceColumna);
        indiceColumna = cursor.getColumnIndex(AlumnoEntry.GRUPO);
        String grupo = cursor.getString(indiceColumna);
        indiceColumna = cursor.getColumnIndex(AlumnoEntry.FOTO_URI);
        String foto = cursor.getString(indiceColumna);

        return new Alumno(id, nombre, grupo, foto);

    }
    /**
     * Devuelve un array list con los los grupos de alumnos
     */
    public ArrayList<String> getGrupos()throws SQLiteException {
        Cursor cursor=db.rawQuery(SQL_GRUPOS,null);
        ArrayList<String> listaGrupos=new ArrayList<String>();
        if(cursor.moveToFirst()){
            do{
                listaGrupos.add(cursor.getString(cursor.getColumnIndex(AlumnoEntry.GRUPO)));
            }while(cursor.moveToNext());
        }
        return listaGrupos;
    }


}
