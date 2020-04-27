package com.ibapps.rspanicbutton;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.ibapps.rspanicbutton.api.GetNotifAPI;
import com.ibapps.rspanicbutton.api.SendMessageAPI;
import com.ibapps.rspanicbutton.api.StopAlarmAPI;
import com.ibapps.rspanicbutton.model.Value;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActiveButtonActivity extends AppCompatActivity {

    String latitude, longitude, keluhan, idAlarm, idUser, pesanKhusus;
    boolean notif = false;
    private static final String TAG = "ActiveButtonActivity";
    public static final String URL = "https://rspanicbuttonn.000webhostapp.com/";
    @BindView(R.id.textViewKeluhan) TextView textViewKeluhan;
    @BindView(R.id.textViewKoordinat) TextView textViewKoordinat;
    @BindView(R.id.textViewMatikanAlarm) TextView textViewMatikanAlarm;
    @BindView(R.id.editTextPesanKhusus) EditText editTextPesanKhusus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_button);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(ActiveButtonActivity.this);
        String statusLogin = sharedPreferences.getString("status", "Logged Out");
        if (!statusLogin.equals("Logged In")) {
            finish();
        }
        Bundle bundle = getIntent().getExtras();
        latitude = bundle.getString("latitude");
        longitude = bundle.getString("longitude");
        keluhan = bundle.getString("keluhan");
        idAlarm = bundle.getString("idAlarm");
        textViewKoordinat.setText(latitude + ", " + longitude);
        textViewKeluhan.setText("Keluhan : " + keluhan);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (notif == false) {
                    getNotificationValue();
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    @OnClick(R.id.buttonStop)
    protected void stopAlarm() {
        android.app.AlertDialog.Builder alertDialogBuilder =
                new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Konfirmasi");
        alertDialogBuilder
                .setMessage("Anda yakin ingin menghentikan alarm?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ProgressDialog progressDialog = new ProgressDialog(ActiveButtonActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Tunggu sebentar...");
                        progressDialog.show();
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        StopAlarmAPI stopAlarmAPI = retrofit.create(StopAlarmAPI.class);
                        Call<Value> call = stopAlarmAPI.stopAlarm(idAlarm);
                        call.enqueue(new Callback<Value>() {
                            @Override
                            public void onResponse(Call<Value> call, Response<Value> response) {
                                String value = response.body().getValue();
                                progressDialog.dismiss();
                                if (value.equals("1")) {
                                    idAlarm = "";
                                    finish();
                                } else {
                                    Toast.makeText(ActiveButtonActivity.this,
                                            "Gagal menghentikan alarm", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Value> call, Throwable t) {
                                progressDialog.dismiss();
                                Toast.makeText(ActiveButtonActivity.this,
                                        "Gagal menghubungkan ke server", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @OnClick(R.id.buttonKirimPesan)
    protected void sendMessage() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(ActiveButtonActivity.this);
        idUser = sharedPreferences.getString("idUser", "kosong");
        pesanKhusus = editTextPesanKhusus.getText().toString();
        if (!pesanKhusus.isEmpty()) {
            if (!idUser.equals("kosong")) {
                ProgressDialog progressDialog = new ProgressDialog(ActiveButtonActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Tunggu sebentar...");
                progressDialog.show();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                SendMessageAPI sendMessageAPI = retrofit.create(SendMessageAPI.class);
                Call<Value> call = sendMessageAPI.sendMessage(idAlarm, idUser, pesanKhusus);
                call.enqueue(new Callback<Value>() {
                    @Override
                    public void onResponse(Call<Value> call, Response<Value> response) {
                        String value = response.body().getValue();
                        progressDialog.dismiss();
                        if (value.equals("1")) {
                            Toast.makeText(ActiveButtonActivity.this,
                                    "Berhasil mengirimkan pesan", Toast.LENGTH_SHORT).show();
                            editTextPesanKhusus.setText("");
                        } else {
                            Toast.makeText(ActiveButtonActivity.this,
                                    "Gagal mengirimkan pesan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Value> call, Throwable t) {
                        Toast.makeText(ActiveButtonActivity.this,
                                "Gagal menghubungkan ke server", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(ActiveButtonActivity.this,
                        "Gagal mengirimkan pesan", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ActiveButtonActivity.this,
                    "Pesan harus diisi", Toast.LENGTH_SHORT).show();
        }

    }

    private void getNotificationValue() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetNotifAPI getNotifAPI = retrofit.create(GetNotifAPI.class);
        Call<Value> call = getNotifAPI.getNotif(idAlarm);
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getValue();
                if (value.equals("1")) {
                    showNotification();
                    textViewMatikanAlarm.setText("Alarm telah dikonfirmasi. Silakan matikan alarm.");
                    notif = true;
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                Log.d(TAG, "Failure: " + t);
            }
        });
    }

    private void showNotification() {
        String NOTIFICATION_CHANNEL_ID = "channel_androidnotif";
        Context context = this.getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelName = "Android Notif Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        Intent mIntent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fromnotif", "notif");
        mIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setTicker("notif starting")
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle("Alarm Telah Dikonfirmasi")
                .setContentText("Anda dapat mematikan tombol sekarang.");
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(115, builder.build());
    }

    @Override
    public void onBackPressed() {
        stopAlarm();
    }
}
