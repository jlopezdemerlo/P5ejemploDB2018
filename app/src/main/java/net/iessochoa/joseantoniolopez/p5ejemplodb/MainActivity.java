package net.iessochoa.joseantoniolopez.p5ejemplodb;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import net.iessochoa.joseantoniolopez.p5ejemplodb.model.Alumno;
import net.iessochoa.joseantoniolopez.p5ejemplodb.model.DBAlumno;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * Ejemplo de trabajo con Bases de datos y carga de datos en un listView
 * mediante un CursorAdapter. No se han controlado los errores. Sería necesario
 * añadirlo.
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.et_Nombre)
    EditText etNombre;
    @BindView(R.id.et_Grupo)
    EditText etGrupo;
    @BindView(R.id.et_FotoUri)
    EditText etFotoUri;
    @BindView(R.id.lv_ListaAlumnos)
    ListView lvListaAlumnos;
    //contiene toda la lógica de trabajo con la base de datos
    DBAlumno dbAlumno;
    //cursorAdapter que permite mostrar en el ListView la filas de un cursor de base de datos
    AlumnoAdapter alumnoAdapter;
    //permitiremos restringir la búsqueda por grupo
    @BindView(R.id.spn_Grupos)
    Spinner spnGrupos;
    //ArrayAdapter<String> adaptadorSpinner;
    static public String TAG_ERROR="P5EjemploDB-Error:";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //asignamos la toolbar personalizada con un spinner. Para más información podéis
        //consultar http://www.sgoliver.net/blog/actionbar-appbar-toolbar-en-android-iii/
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        //desactivamos el título de la aplicación
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //el spinner queda bastante feo. Para corregir la apariencia consultar el artículo
        //propuesto

        //iniciamos la base de datos, esta clase, mediante SQLiteOpenHelper, crea y actuliza la base
        //de datos. En la misma clase le hemos metido todos los métodos que hemos necesitado para la
        //manipulación de la mismo
        try {
            dbAlumno = new DBAlumno(this);
            dbAlumno.open();
        }catch (android.database.sqlite.SQLiteException e){
            mostrarMensajeError(e);
        }

        //cargamos los grupos si los hay en el spinner
        //Al cargar los grupos en el spinner tambien se llamará a la carga de la lista
        //el onItemSelected del Spinner
        iniciaDatosGrupoSpinner();


    }

    /**
     * carga el adaptador de la lista de alumnos
     */
    private void iniciaDatosListaAlumnos() {
        try {
            Cursor cursor = dbAlumno.getCursorAlumnos(null);
            alumnoAdapter = new AlumnoAdapter(this, cursor);
            lvListaAlumnos.setAdapter(alumnoAdapter);
        }catch (android.database.sqlite.SQLiteException e){
            mostrarMensajeError(e);
        }


    }

    /**
     * Inicia el spinner con los grupos
     */
    private void iniciaDatosGrupoSpinner() {

        try {
            //buscamos los grupos en la base de datos
            ArrayList<String> al_Grupos = dbAlumno.getGrupos();
            //si el usuario selecciona Todos, mostraremos todos los alumnos
            al_Grupos.add(0, "-TODOS-");
            ArrayAdapter<String> adaptadorSpinner = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, al_Grupos);
            adaptadorSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnGrupos.setAdapter(adaptadorSpinner);

        }catch (android.database.sqlite.SQLiteException e){
            mostrarMensajeError(e);
        }
    }

    @OnClick({R.id.btn_add, R.id.btn_del, R.id.btn_update})
    public void onClick(View view) {
        Alumno alumno = creaAlumno();
        try {//comprobamos posibles errores en la base de datos
            switch (view.getId()) {
                case R.id.btn_add:
                    dbAlumno.insertaAlumno(alumno);
                    break;
                case R.id.btn_del:
                    dbAlumno.borraAlumno(alumno);
                    break;
                case R.id.btn_update:
                    dbAlumno.actualizaAlumno(alumno);
                    break;
            }
            //comprobamo errores de inserccion..
        }catch (android.database.sqlite.SQLiteConstraintException e){
            mostrarMensajeError(e);
        }
        catch (android.database.sqlite.SQLiteException e){
            mostrarMensajeError(e);
        }

        //mostramos todos los alumnos para simplificar
        iniciaDatosGrupoSpinner();



    }

    private void mostrarMensajeError(Exception e) {
        Log.e(TAG_ERROR,e.getMessage());
        Toast.makeText(this, getString(R.string.error_leerBD)+": "+e.getMessage(),Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbAlumno.close();
    }

    /**
     * Crea un alumno con los datos de la Activity
     * @return
     */
    private Alumno creaAlumno() {
        return (new Alumno(etNombre.getText().toString(), etGrupo.getText().toString(), etFotoUri.getText().toString()));
    }

    /**
     * Cuando se selecciona un grupo del spinner, recuperamos los alumnos según el grupo
     * elegido. Si es "-TODOS-", mostramos todos
     */
    @OnItemSelected(R.id.spn_Grupos)
    public void onItemSelected(Spinner spinner, int position) {
        String grupo = null;
        if (position != 0)//si no se ha seleccionado -TODOS-
            grupo = (String) spnGrupos.getSelectedItem();
        try {
            //buscamos los alumnos, si es grupo==null, busca todos
            Cursor cursor = dbAlumno.getCursorAlumnos(grupo);
            //actualizamos el cursor
            if (alumnoAdapter == null) {//si es la primera vez creamos el adaptador
                alumnoAdapter = new AlumnoAdapter(this, cursor);
                lvListaAlumnos.setAdapter(alumnoAdapter);
            } else {//en otro caso lo sustituimos
                alumnoAdapter.changeCursor(cursor);
                alumnoAdapter.notifyDataSetChanged();
            }
        }catch (android.database.sqlite.SQLiteException e){
            mostrarMensajeError(e);
        }


    }
}
