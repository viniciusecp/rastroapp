package com.gps.rastroapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.darwindeveloper.horizontalscrollmenulibrary.custom_views.HorizontalScrollMenuView;
import com.darwindeveloper.horizontalscrollmenulibrary.extras.MenuItem;
import com.gps.rastroapp.Fragments.AddAccountFragment;
import com.gps.rastroapp.Fragments.CoordinatesFragment;
import com.gps.rastroapp.Model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private HorizontalScrollMenuView horizontal_menu;
    private ArrayList<String> horizontalMenuItems;
    private ArrayList<User> listUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        horizontal_menu = findViewById(R.id.horizontal_menu);

        horizontalMenuItems = new ArrayList<>();

        listUsers = getPreferencesSaved();

        popularMenu();

        setClickMenu();

//        if (savedInstanceState == null){
//            // adicionar o fragmento inicial
//            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new InitialFragment()).commit();
//        }

    }

    private void popularMenu() {

        for (User user : listUsers){
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
        }

        horizontal_menu.addItem("Adicionar", R.drawable.ic_add_circle_24dp);
        horizontalMenuItems.add("add-account");

        horizontal_menu.showItems();
    }

    private void setClickMenu() {
        horizontal_menu.setOnHSMenuClickListener(new HorizontalScrollMenuView.OnHSMenuClickListener() {
            @Override
            public void onHSMClick(MenuItem menuItem, final int position) {
                if (menuItem.getIcon() == R.drawable.ic_directions_car_24dp){

                    String value = horizontalMenuItems.get(position);
                    if (value.split("-")[0].equals("imei")){

                        Bundle bundle = new Bundle();
                        bundle.putString("email", listUsers.get(0).getEmail());
                        bundle.putString("senha", listUsers.get(0).getSenha());
                        bundle.putString("imei", value.split("-")[1]);

                        CoordinatesFragment coordinatesFragment = new CoordinatesFragment();
                        coordinatesFragment.setArguments(bundle);

                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, coordinatesFragment).commit();

                    } else {
                        Toast.makeText(MainActivity.this, "Houve um erro interno!", Toast.LENGTH_SHORT).show();
                    }

                } else if (menuItem.getIcon() == R.drawable.ic_account_circle_24dp){

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Deseja sair da conta " + horizontal_menu.getItem(position).getText() + "?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    String value = horizontalMenuItems.get(position);
                                    if (value.split("-")[0].equals("id")) {
                                        for (User user : listUsers)
                                            if (user.getId() == Integer.parseInt(value.split("-")[1])) {
                                                listUsers.remove(user);
                                                break;
                                            }

                                        saveOnPreferences(listUsers);
                                        Toast.makeText(MainActivity.this, "Usuário removido com sucesso!", Toast.LENGTH_SHORT).show();

                                        Intent intent;
                                        if (listUsers.isEmpty())
                                            intent = new Intent(MainActivity.this, LoginActivity.class);
                                        else
                                            intent = new Intent(MainActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(MainActivity.this, "Houve um erro interno!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
//                                    Toast.makeText(MainActivity.this, "Não", Toast.LENGTH_SHORT).show();
                                }
                            });
                    builder.create().show();


                } else if (menuItem.getIcon() == R.drawable.ic_add_circle_24dp){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddAccountFragment()).commit();
                }

            }
        });
    }

    public void saveOnPreferences(ArrayList<User> lUsers){
        SharedPreferences mPrefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        String jsonString = "{\"users\":[";
        for (User user : lUsers){
            jsonString += user.toString() + ",";
        }
        jsonString += "]}";

        prefsEditor.putString("User", jsonString);

        prefsEditor.commit();
    }

    public ArrayList<User> getPreferencesSaved(){
        SharedPreferences mPrefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);

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

}
