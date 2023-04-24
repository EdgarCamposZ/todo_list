package com.edgcam.todo_list;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.edgcam.todo_list.clases.Configuraciones;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrarTarea extends AppCompatActivity {

    EditText nombre, descripcion, estatus;
    TextView fecha, hora;
    private Calendar fechaSeleccionada = Calendar.getInstance();
    private Calendar horaSeleccionada = Calendar.getInstance();
    Button botonAgregar, botonRegresar, btnSeleccionarFecha, btnSeleccionarHora;
    Configuraciones objConfiguracion = new Configuraciones();
    String URL = objConfiguracion.urlWebServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_tarea);

        nombre = findViewById(R.id.txtNombre);
        descripcion = findViewById(R.id.txtDescripcion);
        fecha = findViewById(R.id.txtFecha);
        hora = findViewById(R.id.txtHora);
        estatus = findViewById(R.id.txtEstatus);

        botonAgregar = findViewById(R.id.btnGuardarTarea);
        botonRegresar = findViewById(R.id.btnRegresar);

        btnSeleccionarFecha = findViewById(R.id.btnFecha);

        btnSeleccionarFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegistrarTarea.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Actualizar la fecha seleccionada
                                fechaSeleccionada.set(Calendar.YEAR, year);
                                fechaSeleccionada.set(Calendar.MONTH, monthOfYear);
                                fechaSeleccionada.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                // Mostrar la fecha seleccionada en un TextView (opcional)
                                fecha.setText(new SimpleDateFormat("dd/MM/yyyy").format(fechaSeleccionada.getTime()));
                            }
                        },
                        // Establecer la fecha mínima y máxima (opcional)
                        fechaSeleccionada.get(Calendar.YEAR),
                        fechaSeleccionada.get(Calendar.MONTH),
                        fechaSeleccionada.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        btnSeleccionarHora = findViewById(R.id.btnHora);

        btnSeleccionarHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(RegistrarTarea.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Actualizar la hora seleccionada
                                horaSeleccionada.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                horaSeleccionada.set(Calendar.MINUTE, minute);

                                // Mostrar la hora seleccionada en un TextView (opcional)
                                hora.setText(new SimpleDateFormat("hh:mm a").format(horaSeleccionada.getTime()));
                            }
                        },
                        // Establecer la hora inicial (opcional)
                        horaSeleccionada.get(Calendar.HOUR_OF_DAY),
                        horaSeleccionada.get(Calendar.MINUTE),
                        false);

                timePickerDialog.show();
            }
        });

        //Evento (CLICK)
        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Registrar
                registrar();
            }
        });

        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Regresar
                regresar();
            }
        });
    }

    private void regresar(){
        Intent actividad = new Intent(RegistrarTarea.this,MainActivity.class);
        startActivity(actividad);
        RegistrarTarea.this.finish();
    }

    private void registrar(){
        try {
            RequestQueue objetoPeticion = Volley.newRequestQueue(RegistrarTarea.this);
            StringRequest peticion = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject objJSONResultado = new JSONObject(response.toString());
                        String estado = objJSONResultado.getString("estado");
                        if (estado.equals("1")) {
                            Toast.makeText(RegistrarTarea.this, "Contacto registrado con exito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegistrarTarea.this, "Error: " + estado, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RegistrarTarea.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accion", "agregar");
                    params.put("nombre", nombre.getText().toString());
                    params.put("descripcion", descripcion.getText().toString());
                    params.put("fecha", new SimpleDateFormat("yyyy-MM-dd").format(fechaSeleccionada.getTime()));
                    params.put("hora", new SimpleDateFormat("HH:mm:ss").format(horaSeleccionada.getTime()));
                    params.put("estatus", estatus.getText().toString());
                    return params;
                }
            };
            objetoPeticion.add(peticion);
        } catch (Exception error) {
            Toast.makeText(RegistrarTarea.this, "Error en tiempo de ejecucion" + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}