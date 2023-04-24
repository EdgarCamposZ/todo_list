package com.edgcam.todo_list;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class ModificarTarea extends AppCompatActivity {

    EditText nombre, descripcion;
    TextView fecha, hora;
    CheckBox estatus;
    Button botonAgregar, botonRegresar, botonEliminar, btnSeleccionarHora, btnSeleccionarFecha;
    private Calendar fechaSeleccionada = Calendar.getInstance();
    private Calendar horaSeleccionada = Calendar.getInstance();
    String id, nombre_tarea, descripcion_tarea, fecha_tarea, hora_tarea, estatus_tarea;
    Configuraciones objConfiguracion = new Configuraciones();
    String URL = objConfiguracion.urlWebServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_tarea);

        nombre = findViewById(R.id.txtNombre);
        descripcion = findViewById(R.id.txtDescripcion);
        fecha = findViewById(R.id.txtFechaMod);
        hora = findViewById(R.id.txtHoraMod);
        estatus = findViewById(R.id.cbxEstatusMod);


        botonAgregar = findViewById(R.id.btnGuardarTareaEditar);
        botonRegresar = findViewById(R.id.btnRegresarEditar);
        botonEliminar = findViewById(R.id.btnEliminar);

        btnSeleccionarFecha = findViewById(R.id.btnFechaMod);

        btnSeleccionarFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ModificarTarea.this,
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

        btnSeleccionarHora = findViewById(R.id.btnHoraMod);

        btnSeleccionarHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ModificarTarea.this,
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

        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modificar();
            }
        });

        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regresar();
            }
        });

        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminar();
            }
        });
    }

    private void modificar() {
        try {
            RequestQueue objetoPeticion = Volley.newRequestQueue(ModificarTarea.this);
            StringRequest peticion = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject objJSONResultado = new JSONObject(response.toString());
                        String estado = objJSONResultado.getString("estado");
                        if (estado.equals("1")) {
                            Toast.makeText(ModificarTarea.this, "Tarea modificada con exito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ModificarTarea.this, "Error: " + estado, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ModificarTarea.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accion", "modificar");
                    params.put("id", id);
                    params.put("nombre", nombre.getText().toString());
                    params.put("descripcion", descripcion.getText().toString());
                    params.put("fecha", new SimpleDateFormat("yyyy-MM-dd").format(fechaSeleccionada.getTime()));
                    params.put("hora", new SimpleDateFormat("HH:mm:ss").format(horaSeleccionada.getTime()));
                    params.put("estatus", estatus.getText().toString());
                    String estadoCheckbox = estatus.isChecked() ? "1" : "0";
                    params.put("estatus", estadoCheckbox);
                    return params;
                }
            };
            objetoPeticion.add(peticion);
        } catch (Exception error) {
            Toast.makeText(ModificarTarea.this, "Error en tiempo de ejecucion" + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void regresar() {
        Intent actividad = new Intent(ModificarTarea.this, MainActivity.class);
        startActivity(actividad);
        ModificarTarea.this.finish();
    }

    private void eliminar() {
        try {
            RequestQueue objetoPeticion = Volley.newRequestQueue(ModificarTarea.this);
            StringRequest peticion = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject objJSONResultado = new JSONObject(response.toString());
                        String estado = objJSONResultado.getString("estado");
                        if (estado.equals("1")) {
                            Toast.makeText(ModificarTarea.this, "Tarea eliminada con exito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ModificarTarea.this, "Error: " + estado, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ModificarTarea.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accion", "eliminar");
                    params.put("id", id);
                    return params;
                }
            };
            objetoPeticion.add(peticion);
        } catch (Exception error) {
            Toast.makeText(ModificarTarea.this, "Error en tiempo de ejecucion" + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle valoresAdicionales = getIntent().getExtras();
        if (valoresAdicionales == null) {
            Toast.makeText(ModificarTarea.this, "Debe enviar un ID de tarea", Toast.LENGTH_SHORT).show();
            id = "";
            regresar();
        } else {
            id = valoresAdicionales.getString("id");
            nombre_tarea = valoresAdicionales.getString("nombre");
            descripcion_tarea = valoresAdicionales.getString("descripcion");
            fecha_tarea = valoresAdicionales.getString("fecha");
            hora_tarea = valoresAdicionales.getString("hora");
            estatus_tarea = valoresAdicionales.getString("estatus");
            verTarea();
        }
    }

    private void verTarea() {
        nombre.setText(nombre_tarea);
        descripcion.setText(descripcion_tarea);
        fecha.setText(fecha_tarea);
        hora.setText(hora_tarea);
        estatus.setText(estatus_tarea);

        if (estatus_tarea.equals("1")) {
            estatus.setText("Hecho");
            estatus.setChecked(true);
        } else {
            estatus.setText("Pendiente");
            estatus.setChecked(false);
        }
    }
}