package com.gps.rastroapp.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gps.rastroapp.Adapter.CoordinatesListAdapter;
import com.gps.rastroapp.Helper.NetworkManager;
import com.gps.rastroapp.Interface.ServerCallback;
import com.gps.rastroapp.Interface.SomeCustomListener;
import com.gps.rastroapp.MapsActivity;
import com.gps.rastroapp.R;
import com.gps.rastroapp.Model.Coordinate;
import com.gps.rastroapp.Model.Coordinates;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CoordinatesFragment extends Fragment {

    private ListView listViewCoordinatesFragment;
    private View mProgressView;


    public CoordinatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_coordinates, container, false);

        listViewCoordinatesFragment = view.findViewById(R.id.listViewCoordinatesFragment);
        mProgressView = view.findViewById(R.id.coordinates_progress);

        String email = getArguments().getString("email");
        String senha = getArguments().getString("senha");
        String imei = getArguments().getString("imei");

        // Faz nova chamada a API
        String path = "getcoordinates/" + imei;

        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("email", email);
        jsonParams.put("senha", senha);


        if(!NetworkManager.isNetworkAvailable(getActivity())){
            Toast.makeText(getActivity(), "Sem acesso a internet!", Toast.LENGTH_SHORT).show();
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

                        CoordinatesListAdapter adapter = new CoordinatesListAdapter(getActivity(), R.layout.adapter_coordinates_view, coordinateArrayList);

                        listViewCoordinatesFragment.setAdapter(adapter);
                        listViewCoordinatesFragment.setDivider(null);

                        listViewCoordinatesFragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getActivity(), MapsActivity.class);
                                intent.putExtra("latitudeDecimalDegrees", coordinateArrayList.get(position).getLatitude());
                                intent.putExtra("longitudeDecimalDegrees",coordinateArrayList.get(position).getLongitude());
                                startActivity(intent);
                            }
                        });

                    } catch (JSONException e) {
                        try {
                            if ( result.getString("Erro").equals("Usúario não autenticado") ) {
                                Toast.makeText(getActivity(), "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
//                            finish();
                            }
                        } catch (JSONException e1) {
                            Toast.makeText(getActivity(), "Houve um erro interno: " + e1, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }, new ServerCallback() {
                @Override
                public void onSuccess(JSONObject response) { showProgress(false); }
            });
        }

        return view;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            listViewCoordinatesFragment.setVisibility(show ? View.GONE : View.VISIBLE);
            listViewCoordinatesFragment.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listViewCoordinatesFragment.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            listViewCoordinatesFragment.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
