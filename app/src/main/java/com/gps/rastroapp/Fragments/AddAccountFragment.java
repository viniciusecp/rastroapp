package com.gps.rastroapp.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    private ImageView imgUserIconAddAccount;
    private ImageView imgLockIconAddAccount;
    private View add_account_progress;

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

        imgUserIconAddAccount = view.findViewById(R.id.imgUserIconAddAccount);
        imgLockIconAddAccount = view.findViewById(R.id.imgLockIconAddAccount);
        add_account_progress = view.findViewById(R.id.add_account_progress);

        listUsers = getPreferencesSaved();

        btnEntrarAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtEmailAddAccount.getText().toString();
                final String password = edtPasswordAddAccount.getText().toString();

                if (email.equals("") || password.equals("")){
                    Toast.makeText(getActivity(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!NetworkManager.isNetworkAvailable(getActivity())){
                    Toast.makeText(getActivity(), "Sem acesso a internet!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String path = "authentication";

                Map<String, String> jsonParams = new HashMap<>();
                jsonParams.put("email", email);
                jsonParams.put("senha", password);

                showProgress(true);

                NetworkManager.getInstance().somePostRequestReturningString(path, jsonParams, new SomeCustomListener<String>() {
                    @Override
                    public void getResult(JSONObject result) {
                        try {
                            User user = new User(
                                    result.getString("id"),
                                    result.getString("email"),
                                    password,
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
                                }
                            } catch (JSONException e1) {
                                Toast.makeText(getActivity(), "Houve um erro interno: " + e1, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        showProgress(false);
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            edtEmailAddAccount.setVisibility(show ? View.GONE : View.VISIBLE);
            edtPasswordAddAccount.setVisibility(show ? View.GONE : View.VISIBLE);
            btnEntrarAddAccount.setVisibility(show ? View.GONE : View.VISIBLE);
            imgUserIconAddAccount.setVisibility(show ? View.GONE : View.VISIBLE);
            imgLockIconAddAccount.setVisibility(show ? View.GONE : View.VISIBLE);

            edtEmailAddAccount.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    edtEmailAddAccount.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            edtPasswordAddAccount.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    edtPasswordAddAccount.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            btnEntrarAddAccount.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    btnEntrarAddAccount.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            imgUserIconAddAccount.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    imgUserIconAddAccount.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            imgLockIconAddAccount.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    imgLockIconAddAccount.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            add_account_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            add_account_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    add_account_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            add_account_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            edtEmailAddAccount.setVisibility(show ? View.GONE : View.VISIBLE);
            edtPasswordAddAccount.setVisibility(show ? View.GONE : View.VISIBLE);
            btnEntrarAddAccount.setVisibility(show ? View.GONE : View.VISIBLE);
            imgUserIconAddAccount.setVisibility(show ? View.GONE : View.VISIBLE);
            imgLockIconAddAccount.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
