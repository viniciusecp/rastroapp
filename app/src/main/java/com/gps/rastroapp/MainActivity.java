package com.gps.rastroapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.darwindeveloper.horizontalscrollmenulibrary.custom_views.HorizontalScrollMenuView;
import com.darwindeveloper.horizontalscrollmenulibrary.extras.MenuItem;
import com.gps.rastroapp.Fragments.CoordinatesFragment;
import com.gps.rastroapp.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private HorizontalScrollMenuView horizontal_menu;
    private ArrayList<String> horizontalMenuItems;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        horizontal_menu = findViewById(R.id.horizontal_menu);

        horizontalMenuItems = new ArrayList<>();

        user = getPreferencesSaved();

        popularMenu();

        setClickMenu();

//        if (savedInstanceState == null){
//            // adicionar o fragmento inicial
//            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new InitialFragment()).commit();
//        }

    }

    private void popularMenu() {
        horizontal_menu.addItem(user.getNome(), R.drawable.ic_account_circle_24dp);
        horizontalMenuItems.add("id-" + user.getId());

        for (int i = 0; i < user.getVeiculos().length(); i++) {
            try {
                horizontal_menu.addItem(user.getVeiculos().getJSONObject(i).getString("veiculo"), R.drawable.ic_directions_car_24dp);
                horizontalMenuItems.add("imei-" + user.getVeiculos().getJSONObject(i).getString("imei"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        horizontal_menu.addItem("Adicionar", R.drawable.ic_add_circle_24dp);
        horizontalMenuItems.add("add-account");

        horizontal_menu.showItems();
    }

    private void setClickMenu() {
        // setando clique no item do menu
        horizontal_menu.setOnHSMenuClickListener(new HorizontalScrollMenuView.OnHSMenuClickListener() {
            @Override
            public void onHSMClick(MenuItem menuItem, int position) {
                if (menuItem.getIcon() == R.drawable.ic_directions_car_24dp){

                    String value = horizontalMenuItems.get(position);
                    if (value.split("-")[0].equals("imei")){

                        Bundle bundle = new Bundle();
                        bundle.putString("email", user.getEmail());
                        bundle.putString("senha", user.getSenha());
                        bundle.putString("imei", value.split("-")[1]);

                        CoordinatesFragment coordinatesFragment = new CoordinatesFragment();
                        coordinatesFragment.setArguments(bundle);

                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, coordinatesFragment).commit();

                    } else {
                        Toast.makeText(MainActivity.this, "Houve um erro interno!", Toast.LENGTH_SHORT).show();
                    }

                } else if (menuItem.getIcon() == R.drawable.ic_account_circle_24dp){

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Deseja sair desta conta?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deletePreferences();
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
//                                    Toast.makeText(MainActivity.this, "Não", Toast.LENGTH_SHORT).show();
                                }
                            });
                    builder.create().show();


                } else if (menuItem.getIcon() == R.drawable.ic_add_circle_24dp){
                    Toast.makeText(MainActivity.this, "Ainda estamos em desenvolvimento =)", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void deletePreferences(){
        SharedPreferences  mPrefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        prefsEditor.putString("User", "");
        prefsEditor.commit();
    }

    public User getPreferencesSaved(){
        SharedPreferences  mPrefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);

        String jsonString = mPrefs.getString("User", "");
        User user = null;

        try {
            JSONObject obj = new JSONObject(String.valueOf(jsonString));
            user = new User(
                    obj.getString("id"),
                    obj.getString("email"),
                    obj.getString("senha"),
                    obj.getString("nome"),
                    obj.getString("apelido"),
                    obj.getJSONArray("veiculos")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

}
