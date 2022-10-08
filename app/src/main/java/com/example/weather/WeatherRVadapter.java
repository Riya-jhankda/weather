package com.example.weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.util.Date;

//for using the dependency picasso
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherRVadapter extends RecyclerView.Adapter<WeatherRVadapter.ViewHolder> {

    public WeatherRVadapter(Context context, ArrayList<WeatherRVmodel> weatherRVmodelArrayList) {
        this.context = context;
        this.weatherRVmodelArrayList = weatherRVmodelArrayList;
    }

    private Context context;
    private ArrayList<WeatherRVmodel> weatherRVmodelArrayList;

    @NonNull
    @Override
    public WeatherRVadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WeatherRVmodel model = weatherRVmodelArrayList.get(position);
        holder.TemperatureTv.setText(model.getTemperature()+" degree C");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.conditionTv);
        holder.windTv.setText(model.getWindspeed()+"km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat(" hh:mm  aa");

        try{
            Date t =input.parse(model.getTime());
            holder.timeTv.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return weatherRVmodelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView windTv,TemperatureTv,timeTv;
        private ImageView conditionTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTv=itemView.findViewById(R.id.TVWindspeed);
            TemperatureTv=itemView.findViewById(R.id.TVTemperature);
            timeTv=itemView.findViewById(R.id.TVtime);
            conditionTv=itemView.findViewById(R.id.TVcondition);

        }
    }
}
