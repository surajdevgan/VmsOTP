package com.suraj.vmsotp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    RequestQueue requestQueue;
    String number="";
    String name = "";
    String phone ="";
    String count="";
    int id=0;
    ProgressBar pb;
    Handler handler;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar a = getSupportActionBar();
        a.hide();
        pb = findViewById(R.id.progressbar);
        pb.setVisibility(View.VISIBLE);
        preferences = getSharedPreferences(Util.AcuPrefs, MODE_PRIVATE);
        editor = preferences.edit();
        requestQueue = Volley.newRequestQueue(this);
        number = preferences.getString(Util.Phone, "");
        handler = new Handler();
        if (ConnectionCheck.isConnected(connectivityManager, networkInfo, this)) {
            if (number.length() > 0 && !(number.isEmpty())) {
                loginUser();

            } else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashScreen.this,LoginActivity.class));
                        finish();
                        pb.setVisibility(View.GONE);
                    }
                }, 3000);
            }
        } else {
            Toast.makeText(this, "Internet Connection Required", Toast.LENGTH_LONG).show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finishAffinity();
                }
            }, 2000);
        }
    }



    public void loginUser(){
        StringRequest request=new StringRequest(Request.Method.POST, Util.login1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object=new JSONObject(response);
                    JSONArray array=object.getJSONArray("students");
                    String message=object.getString("message");
                    if (message.contains("Sucessful")){
                        for (int i=0;i<array.length();i++){
                            JSONObject object1=array.getJSONObject(i);
                            id = object1.getInt("User_ID");
                            name = object1.getString("User_name");
                            phone = object1.getString("Phone");
                            count = object1.getString("Count");
                        }
                        editor.putString(Util.Name,name);
                        editor.putString(Util.Phone,phone);
                        editor.putString(Util.count,count);
                        editor.putInt(Util.id,id);
                        editor.apply();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                startActivity(new Intent(SplashScreen.this,MainActivity.class));
                                finish();
                                pb.setVisibility(View.GONE);
                            }
                        },4000);
                    }else {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                startActivity(new Intent(SplashScreen.this,LoginActivity.class));
                                finish();
                                pb.setVisibility(View.GONE);
                            }
                        },3000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<>();
                map.put("Phone",number);
                map.put("Flag","on");
                return map;
            }
        };
        requestQueue.add(request);
    }

}