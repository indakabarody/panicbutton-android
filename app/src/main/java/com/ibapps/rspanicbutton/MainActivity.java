package com.ibapps.rspanicbutton;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ibapps.rspanicbutton.api.SendHelpRequestAPI;
import com.ibapps.rspanicbutton.model.Value;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    LocationManager locationManager;
    String latitude, longitude, keluhan;
    private static final String TAG = "MainActivity";
    public static final String URL = "https://panicbutton.indakabarody.com/";
    @BindView(R.id.spinnerKeluhan) Spinner spinnerKeluhan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String statusLogin = sharedPreferences.getString("status", "Logged Out");
        if (!statusLogin.equals("Logged In")) {
            finish();
        }
    }

    @OnClick(R.id.buttonSOS)
    protected void SOS() {
        keluhan = spinnerKeluhan.getSelectedItem().toString();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableGPS();
            getLocation();
            activateSOS();
        } else {
            getLocation();
            activateSOS();
        }
    }

    @OnClick(R.id.imageViewPengaturan)
    protected void goToSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsMenuActivity.class);
        startActivity(intent);
    }

    private void enableGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Aktifkan GPS?").setCancelable(false).
                setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.
                    GPS_PROVIDER);
            Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.
                    NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.
                    PASSIVE_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
            } else if (locationNetwork != null) {
                double lat = locationNetwork.getLatitude();
                double longi = locationNetwork.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
            } else if (locationPassive != null) {
                double lat = locationPassive.getLatitude();
                double longi = locationPassive.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
            } else {
                Toast.makeText(this, "Tidak dapat mendapatkan lokasi Anda",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void inputKeluhanLainnya() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Keluhan");
        EditText inputKeluhan = new EditText(this);
        inputKeluhan.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        builder.setView(inputKeluhan);
        builder.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                keluhan = inputKeluhan.getText().toString();
                activateSOS();
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void activateSOS() {
        if (latitude != null && longitude != null) {
            Bundle bundle = new Bundle();
            Intent intent = new Intent(MainActivity.this, ActiveButtonActivity.class);
            if (keluhan.equals("Lainnya") || keluhan.equals("lainnya") || keluhan.isEmpty()) {
                inputKeluhanLainnya();
            } else {
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Tunggu sebentar...");
                progressDialog.show();
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                String idUser = sharedPreferences.getString("idUser", "kosong");
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                SendHelpRequestAPI sendHelpRequestAPI = retrofit.create(SendHelpRequestAPI.class);
                Call<Value> call = sendHelpRequestAPI.sendRequest(idUser, keluhan, latitude, longitude);
                call.enqueue(new Callback<Value>() {
                    @Override
                    public void onResponse(Call<Value> call, Response<Value> response) {
                        String value = response.body().getValue();
                        String idAlarm = response.body().getIdAlarm();
                        progressDialog.dismiss();
                        if (value.equals("1")) {
                            bundle.putString("idAlarm", idAlarm);
                            bundle.putString("keluhan", keluhan);
                            bundle.putString("latitude", latitude);
                            bundle.putString("longitude", longitude);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Gagal mengirimkan permintaan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Value> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this,
                                "Gagal menghubungkan ke server", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan atau swipe sekali lagi untuk keluar",
                Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
