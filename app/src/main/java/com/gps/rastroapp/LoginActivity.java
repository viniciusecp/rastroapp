package com.gps.rastroapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gps.rastroapp.Helper.NetworkManager;
import com.gps.rastroapp.Interface.ServerCallback;
import com.gps.rastroapp.Interface.SomeCustomListener;
import com.gps.rastroapp.Model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmailLogin;
    private EditText edtPasswordLogin;
    private Button btnEntrarLogin;

    private ImageView imgUserIconLogin;
    private ImageView imgLockIconLogin;
    private View login_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NetworkManager.getInstance(this);

        if (getPreferencesSaved() != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        edtEmailLogin = findViewById(R.id.edtEmailLogin);
        edtPasswordLogin = findViewById(R.id.edtPasswordLogin);
        btnEntrarLogin = findViewById(R.id.btnEntrarLogin);

        imgUserIconLogin = findViewById(R.id.imgUserIconLogin);
        imgLockIconLogin = findViewById(R.id.imgLockIconLogin);
        login_progress = findViewById(R.id.login_progress);

        btnEntrarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtEmailLogin.getText().toString();
                final String password = edtPasswordLogin.getText().toString();

                if (email.equals("") || password.equals("")){
                    Toast.makeText(LoginActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!NetworkManager.isNetworkAvailable(LoginActivity.this)){
                    Toast.makeText(LoginActivity.this, "Sem acesso a internet!", Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(LoginActivity.this, "Usuario autenticado", Toast.LENGTH_SHORT).show();
                            saveOnPreferences(user);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            try {
                                if ( result.getString("Erro").equals("Usúario não autenticado") ) {
                                    Toast.makeText(LoginActivity.this, "Usuario não autenticado", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e1) {
                                Toast.makeText(LoginActivity.this, "Houve um erro interno: " + e1, Toast.LENGTH_LONG).show();
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

    }

    public void saveOnPreferences(User user){
        SharedPreferences  mPrefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        String jsonString = "{\"users\":[" + user.toString() + "]}";

        prefsEditor.putString("User", jsonString);

        prefsEditor.commit();
    }

    public User getPreferencesSaved(){
        SharedPreferences mPrefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);

        String jsonString = mPrefs.getString("User","");
        User user = null;

        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONArray jArray= obj.getJSONArray("users");

            // apenas para ver se tem algum registro e então passar para main activity
            JSONObject firstUser = jArray.getJSONObject(0);
            user = new User(
                    firstUser.getString("id"),
                    firstUser.getString("email"),
                    firstUser.getString("senha"),
                    firstUser.getString("nome"),
                    firstUser.getString("apelido"),
                    firstUser.getJSONArray("veiculos")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            edtEmailLogin.setVisibility(show ? View.GONE : View.VISIBLE);
            edtPasswordLogin.setVisibility(show ? View.GONE : View.VISIBLE);
            btnEntrarLogin.setVisibility(show ? View.GONE : View.VISIBLE);
            imgUserIconLogin.setVisibility(show ? View.GONE : View.VISIBLE);
            imgLockIconLogin.setVisibility(show ? View.GONE : View.VISIBLE);

            imgUserIconLogin.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    imgUserIconLogin.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            edtPasswordLogin.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    edtPasswordLogin.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            btnEntrarLogin.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    btnEntrarLogin.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            imgUserIconLogin.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    imgUserIconLogin.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            imgLockIconLogin.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    imgLockIconLogin.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            login_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            login_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    login_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            login_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            edtEmailLogin.setVisibility(show ? View.GONE : View.VISIBLE);
            edtPasswordLogin.setVisibility(show ? View.GONE : View.VISIBLE);
            btnEntrarLogin.setVisibility(show ? View.GONE : View.VISIBLE);
            imgUserIconLogin.setVisibility(show ? View.GONE : View.VISIBLE);
            imgLockIconLogin.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}