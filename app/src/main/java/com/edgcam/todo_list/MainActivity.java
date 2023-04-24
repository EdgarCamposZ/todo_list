package com.edgcam.todo_list;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.edgcam.todo_list.clases.Configuraciones;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button botonAgregar, botonTaskPendientes, botonTaskCompletadas;
    ListView listaTareas;
    Configuraciones objConfiguracion = new Configuraciones();
    String URL = objConfiguracion.urlWebServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonAgregar = findViewById(R.id.btnAgregar);
        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ventana = new Intent(MainActivity.this, RegistrarTarea.class);
                startActivity(ventana);
            }
        });

        listaTareas = findViewById(R.id.lvlTareas);

        botonTaskPendientes = findViewById(R.id.btnPendientes);
        botonTaskPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llenarLista(getFiltroTaskPendientes());
            }
        });

        botonTaskCompletadas = findViewById(R.id.btnCompletadas);
        botonTaskCompletadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llenarLista(getFiltroTaskCompletadas());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            llenarLista(null);
        } catch (Exception error) {
            Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String filtroActual = null;

    private String getFiltroTaskPendientes() {
        if (filtroActual == null || filtroActual.equals("1")) {
            filtroActual = "0";
        } else {
            filtroActual = null;
        }
        return filtroActual;
    }

    private String getFiltroTaskCompletadas() {
        if (filtroActual == null || filtroActual.equals("0")) {
            filtroActual = "1";
        } else {
            filtroActual = null;
        }
        return filtroActual;
    }

    private void FiltrarTareas(String filtro) {
        RequestQueue objetoPeticion = Volley.newRequestQueue(MainActivity.this);
        StringRequest peticion = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objJSONResultado = new JSONObject(response.toString());
                    JSONArray aDatosResultado = objJSONResultado.getJSONArray("resultado");
                    AdaptadorListaTarea miAdaptador = new AdaptadorListaTarea();
                    miAdaptador.arregloDatos = aDatosResultado;
                    listaTareas.setAdapter(miAdaptador);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accion", "listar_tareas");
                if (filtro != null) {
                    params.put("estatus", filtro);
                }
                return params;
            }
        };
        objetoPeticion.add(peticion);
    }

    public void llenarLista(String filtro) {
        FiltrarTareas(filtro);
    }


    class AdaptadorListaTarea extends BaseAdapter {
        public JSONArray arregloDatos;

        public AdaptadorListaTarea() {
            super();
        }

        @Override
        public int getCount() {
            return arregloDatos.length();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            v = getLayoutInflater().inflate(R.layout.fila_tareas, null);
            TextView txtNombre = v.findViewById(R.id.tvNombreFilaTarea);
            TextView txtDescripcion = v.findViewById(R.id.tvDescripcionFilaTarea);
            TextView txtFecha = v.findViewById(R.id.tvFechaFilaTarea);
            TextView txtHora = v.findViewById(R.id.tvHoraFilaTarea);
            Button btnVer = v.findViewById(R.id.btnEditarTarea);
            CheckBox cbxEstatus = v.findViewById(R.id.cbxEstatus);

            JSONObject objJSON = null;
            try {
                objJSON = arregloDatos.getJSONObject(position);
                final String id, nombre, descripcion, fecha, hora, estatus;
                id = objJSON.getString("id");
                nombre = objJSON.getString("nombre");
                descripcion = objJSON.getString("descripcion");
                fecha = objJSON.getString("fecha");
                hora = objJSON.getString("hora");
                estatus = objJSON.getString("estatus");

                txtNombre.setText(nombre);
                txtDescripcion.setText(descripcion);
                txtFecha.setText("Fecha: " + fecha);
                txtHora.setText("Hora: " + hora);

                if(estatus.equals("1")){
                    cbxEstatus.setChecked(true);
                    cbxEstatus.setText("Hecho");
                }else{
                    cbxEstatus.setChecked(false);
                    cbxEstatus.setText("Pendiente");
                }


                btnVer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent ventanaModificar = new Intent(MainActivity.this, ModificarTarea.class);
                        ventanaModificar.putExtra("id", id);
                        ventanaModificar.putExtra("nombre", nombre);
                        ventanaModificar.putExtra("descripcion", descripcion);
                        ventanaModificar.putExtra("fecha", fecha);
                        ventanaModificar.putExtra("hora", hora);
                        ventanaModificar.putExtra("estatus", estatus);
                        startActivity(ventanaModificar);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return v;
        }

    }
}