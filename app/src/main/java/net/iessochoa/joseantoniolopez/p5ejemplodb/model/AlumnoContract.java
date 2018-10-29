package net.iessochoa.joseantoniolopez.p5ejemplodb.model;

import android.provider.BaseColumns;

/**
 * Esta clase nos permitira mantener aislado la base de datos del código
 * Es conveniente crearla y utilizar sus propiedades en vez de los nombres
 * directos de la base de datos en el código
 */

public class AlumnoContract {

    public static abstract class AlumnoEntry {
        //Nombre de la base de datos
        public static final String TABLE_NAME = "ALUMNO";

        //nombre de las columnas
        public static final String _ID = BaseColumns._ID;//esta columna es necesaria para Android
        public static final String NOMBRE = "nombre";
        public static final String GRUPO = "grupo";
        public static final String FOTO_URI = "fotoUri";


    }
}
