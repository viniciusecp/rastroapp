package com.gps.rastroapp.Model;

import org.json.JSONArray;
import org.json.JSONException;

public class User {

    private int id;
    private String email;
    private String senha;
    private String nome;
    private String apelido;
    private JSONArray veiculos;

    public User(String id, String email, String senha, String nome, String apelido, JSONArray veiculos) {
        this.id = Integer.parseInt(id);
        this.email = email;
        this.senha = senha;
        this.nome = nome;
        this.apelido = apelido;
        this.veiculos = veiculos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public JSONArray getVeiculos() {
        return veiculos;
    }

    public void setVeiculos(JSONArray veiculos) {
        this.veiculos = veiculos;
    }

    public String toString(){
        String jsonString = "";

        jsonString += "{";
            jsonString += "\"id\":\""+this.id+"\",";
            jsonString += "\"email\":\""+this.email+"\",";
            jsonString += "\"senha\":\""+this.senha+"\",";
            jsonString += "\"nome\":\""+this.nome+"\",";
            jsonString += "\"apelido\":\""+this.apelido+"\",";
            jsonString += "\"veiculos\":[";
                for (int i = 0; i < this.veiculos.length(); i++) {
                    jsonString += "{";
                    try {
                        jsonString += "\"imei\":\""+this.veiculos.getJSONObject(i).getString("imei")+"\",";
                        jsonString += "\"veiculo\":\""+this.veiculos.getJSONObject(i).getString("veiculo")+"\"";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (i == this.veiculos.length()-1){
                        jsonString += "}";
                    } else {
                        jsonString += "},";
                    }

                }
            jsonString += "]";
        jsonString += "}";

        return jsonString;
    }
}
