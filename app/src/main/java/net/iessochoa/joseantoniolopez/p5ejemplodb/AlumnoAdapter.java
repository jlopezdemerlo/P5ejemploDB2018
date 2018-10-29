package net.iessochoa.joseantoniolopez.p5ejemplodb;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iessochoa.joseantoniolopez.p5ejemplodb.model.Alumno;
import net.iessochoa.joseantoniolopez.p5ejemplodb.model.DBAlumno;

/**
 * Es el adaptador para el ListView. Nos basamos en un cursor de la base de datos, por lo que los métodos
 * cambian respecto a un ArrayAdpater.
 */

public class AlumnoAdapter extends CursorAdapter {
    public AlumnoAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * crea un item nuevo
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.item_alumno, parent, false);

    }

    /**
     * Este método carga los valores en cada item del ListView. El cursor
     * que se pasa como parámetro está en la posición del elemento que se tiene
     * que mostrar.
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Alumno alumno= DBAlumno.deCursorAAlumno(cursor);

        TextView tvNombre = (TextView) view.findViewById(R.id.tv_Nombre);
        TextView tvId = (TextView) view.findViewById(R.id.tv_id);
        TextView tvGrupo = (TextView) view.findViewById(R.id.tv_Grupo);


        //Asignamos los valor de la posición actual del cursor
        tvNombre.setText(alumno.getNombre());
        tvGrupo.setText(alumno.getGrupo());
        tvId.setText(alumno.getId());
    }


}
