package com.gps.rastroapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.gps.rastroapp.R;
import com.gps.rastroapp.Model.Coordinate;
import java.util.ArrayList;

public class CoordinatesListAdapter extends ArrayAdapter<Coordinate> {

    private Context mContext;
    int mResource;

    /**
     * @param context
     * @param resource
     * @param objects
     */
    public CoordinatesListAdapter(Context context, int resource, ArrayList<Coordinate> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView,ViewGroup parent) {

        String date = getItem(position).getDate();
        String latitude = getItem(position).getLatitude();
        String longitude = getItem(position).getLongitude();
        String speed = getItem(position).getSpeed();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView txtDate = convertView.findViewById(R.id.txtDate);
        TextView txtLatitude = convertView.findViewById(R.id.txtLatitude);
        TextView txtLongitude = convertView.findViewById(R.id.txtLongitude);
        TextView txtSpeed = convertView.findViewById(R.id.txtSpeed);

        txtDate.setText(mountDate(date));
        try {
            txtLatitude.setText(latitude.substring(0, 9));
            txtLongitude.setText(longitude.substring(0, 9));
        } catch (Exception e){
            txtLatitude.setText(latitude);
            txtLongitude.setText(longitude);
        }

        if (speed.equals("0")){
            txtSpeed.setText("0 km/h");
        } else {
            txtSpeed.setText(speed.split("\\.")[0] + " km/h");
        }

        return convertView;
    }

    public String mountDate(String date){

        String data = date.substring(0, 10);
        String ano = data.split("-")[0];
        String mes = data.split("-")[1];
        String dia = data.split("-")[2];

        String horario = date.substring(11, 16);
        String hora = horario.split(":")[0];
        String minuto = horario.split(":")[1];

        return dia + "/" + mes + "/" + ano + " ás " + hora + ":" + minuto;
    }
}
