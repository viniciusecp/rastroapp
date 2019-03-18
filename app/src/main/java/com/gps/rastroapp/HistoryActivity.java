package com.gps.rastroapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gps.rastroapp.Adapter.CoordinatesListAdapter;
import com.gps.rastroapp.Helper.NetworkManager;
import com.gps.rastroapp.Interface.ServerCallback;
import com.gps.rastroapp.Interface.SomeCustomListener;
import com.gps.rastroapp.Model.Coordinate;
import com.gps.rastroapp.Model.Coordinates;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class HistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private TextView edtDataInicio;
    private TextView edtHoraInicio;
    private TextView edtDataFinal;
    private TextView edtHoraFinal;
    private Button btnBuscar;
    private ListView listViewHistory;
    private ProgressBar history_progress;

    String itemSelected = null;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        String email = extras.getString("email");
        String senha = extras.getString("senha");
        String imei = extras.getString("imei");

        edtDataInicio = findViewById(R.id.edtDataInicio);
        edtHoraInicio = findViewById(R.id.edtHoraInicio);
        edtDataFinal = findViewById(R.id.edtDataFinal);
        edtHoraFinal = findViewById(R.id.edtHoraFinal);
        btnBuscar = findViewById(R.id.btnBuscar);
        listViewHistory = findViewById(R.id.listViewHistory);
        history_progress = findViewById(R.id.history_progress);

        edtDataInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemSelected = "edtDataInicio";
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        HistoryActivity.this,
                        HistoryActivity.this,
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        });
        edtHoraInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemSelected = "edtHoraInicio";
                Calendar c = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        HistoryActivity.this,
                        HistoryActivity.this,
                        c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(HistoryActivity.this)
                );
                timePickerDialog.show();
            }
        });
        edtDataFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemSelected = "edtDataFinal";
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        HistoryActivity.this,
                        HistoryActivity.this,
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        });

        edtHoraFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemSelected = "edtHoraFinal";
                Calendar c = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        HistoryActivity.this,
                        HistoryActivity.this,
                        c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(HistoryActivity.this)
                );
                timePickerDialog.show();
            }
        });


        // Faz nova chamada a API
        final String path = "getcoordinates/" + imei;

        final Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("email", email);
        jsonParams.put("senha", senha);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!NetworkManager.isNetworkAvailable(HistoryActivity.this)){
                    Toast.makeText(HistoryActivity.this, "Sem acesso a internet!", Toast.LENGTH_SHORT).show();
                } else {
                    showProgress(true);

                    NetworkManager.getInstance().somePostRequestReturningString(path, jsonParams, new SomeCustomListener<String>() {
                        @Override
                        public void getResult(JSONObject result) {
                            try {
                                final Coordinates coordinates = new Coordinates(
                                        result.getJSONArray("coordinates")
                                );

                                final ArrayList<Coordinate> coordinateArrayList = new ArrayList<Coordinate>();
                                for (int i = 0; i < coordinates.getCoordinates().length(); i++) {
                                    Coordinate coordinate = new Coordinate(
                                            coordinates.getCoordinates().getJSONObject(i).getString("date"),
                                            coordinates.getCoordinates().getJSONObject(i).getString("latitudeDecimalDegrees"),
                                            coordinates.getCoordinates().getJSONObject(i).getString("longitudeDecimalDegrees"),
                                            coordinates.getCoordinates().getJSONObject(i).getString("speed")

                                    );
                                    coordinateArrayList.add(coordinate);
                                }

                                CoordinatesListAdapter adapter = new CoordinatesListAdapter(HistoryActivity.this, R.layout.adapter_coordinates_view, coordinateArrayList);

                                listViewHistory.setAdapter(adapter);
                                listViewHistory.setDivider(null);

                                listViewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent = new Intent(HistoryActivity.this, MapsActivity.class);
                                        intent.putExtra("latitudeDecimalDegrees", coordinateArrayList.get(position).getLatitude());
                                        intent.putExtra("longitudeDecimalDegrees", coordinateArrayList.get(position).getLongitude());
                                        startActivity(intent);
                                    }
                                });

                            } catch (JSONException e) {
                                try {
                                    if (result.getString("Erro").equals("Usúario não autenticado")) {
                                        Toast.makeText(HistoryActivity.this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e1) {
                                    Toast.makeText(HistoryActivity.this, "Houve um erro interno: " + e1, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }, new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject response) { showProgress(false); }
                    });
                }
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        month = month + 1;
        String monthFinal = month < 10 ? '0' + String.valueOf(month) : String.valueOf(month);
        String dayFinal = dayOfMonth < 10 ? '0' + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);

        switch (itemSelected) {
            case "edtDataInicio":
                edtDataInicio.setText(dayFinal + "/" + monthFinal + "/" + year);
                break;
            case "edtDataFinal":
                edtDataFinal.setText(dayFinal + "/" + monthFinal + "/" + year);
                break;
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        String hourFinal = hourOfDay < 10 ? '0' + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
        String minFinal = minute < 10 ? '0' + String.valueOf(minute) : String.valueOf(minute);

        switch (itemSelected) {
            case "edtHoraInicio":
                edtHoraInicio.setText(hourFinal + ":" + minFinal);
                break;
            case "edtHoraFinal":
                edtHoraFinal.setText(hourFinal + ":" + minFinal);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            listViewHistory.setVisibility(show ? View.GONE : View.VISIBLE);

            listViewHistory.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listViewHistory.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            history_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            history_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    history_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            history_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            listViewHistory.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
