package com.gps.rastroapp.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.darwindeveloper.horizontalscrollmenulibrary.custom_views.HorizontalScrollMenuView;
import com.gps.rastroapp.Helper.NetworkManager;
import com.gps.rastroapp.Interface.ServerCallback;
import com.gps.rastroapp.Interface.SomeCustomListener;
import com.gps.rastroapp.LoginActivity;
import com.gps.rastroapp.MainActivity;
import com.gps.rastroapp.Model.User;
import com.gps.rastroapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class AddAccountFragment extends Fragment {

    private EditText edtEmailAddAccount;
    private EditText edtPasswordAddAccount;
    private Button btnEntrarAddAccount;

    ArrayList<User> listUsers;

    public AddAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_account, container, false);

        edtEmailAddAccount = view.findViewById(R.id.edtEmailAddAccount);
        edtPasswordAddAccount = view.findViewById(R.id.edtPasswordAddAccount);
        btnEntrarAddAccount = view.findViewById(R.id.btnEntrarAddAccount);

        listUsers = getPreferencesSaved();

        btnEntrarAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String path = "authentication";

                Map<String, String> jsonParams = new HashMap<>();
                jsonParams.put("email", edtEmailAddAccount.getText().toString());
                jsonParams.put("senha", edtPasswordAddAccount.getText().toString());

                NetworkManager.getInstance().somePostRequestReturningString(path, jsonParams, new SomeCustomListener<String>() {
                    @Override
                    public void getResult(JSONObject result) {
                        try {
                            User user = new User(
                                    result.getString("id"),
                                    result.getString("email"),
                                    edtPasswordAddAccount.getText().toString(),
                                    result.getString("nome"),
                                    result.getString("apelido"),
                                    result.getJSONArray("veiculos")
                            );

                            listUsers.add(user);

                            Toast.makeText(getActivity(), "Usuario autenticado", Toast.LENGTH_SHORT).show();
                            saveOnPreferences(listUsers);

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();

                        } catch (JSONException e) {
                            try {
                                if ( result.getString("Erro").equals("Usúario não autenticado") ) {
                                    Toast.makeText(getActivity(), "Usuario não autenticado", Toast.LENGTH_SHORT).show();
//                                    mPasswordView.setError(getString(R.string.error_incorrect_password));
//                                    mPasswordView.requestFocus();
                                }
                            } catch (JSONException e1) {
                                Toast.makeText(getActivity(), "Houve um erro interno: " + e1, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {

                    }
                });

            }
        });

        return view;
    }

    public ArrayList<User> getPreferencesSaved(){
        SharedPreferences mPrefs = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);

        String jsonString = mPrefs.getString("User","");
        ArrayList<User> lUsers = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONArray jArray= obj.getJSONArray("users");

            for ( int i = 0; i < jArray.length(); i++){
                User user = new User(
                        jArray.getJSONObject(i).getString("id"),
                        jArray.getJSONObject(i).getString("email"),
                        jArray.getJSONObject(i).getString("senha"),
                        jArray.getJSONObject(i).getString("nome"),
                        jArray.getJSONObject(i).getString("apelido"),
                        jArray.getJSONObject(i).getJSONArray("veiculos")
                );
                lUsers.add(user);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lUsers;
    }

    public void saveOnPreferences(ArrayList<User> lUsers){
        SharedPreferences mPrefs = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        String jsonString = "{\"users\":[";
        for (User user : lUsers){
            jsonString += user.toString() + ",";
        }
        jsonString += "]}";

        prefsEditor.putString("User", jsonString);

        prefsEditor.commit();
    }

}
