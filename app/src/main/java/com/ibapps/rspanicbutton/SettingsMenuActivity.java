package com.ibapps.rspanicbutton;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.ibapps.rspanicbutton.api.LogoutAPI;
import com.ibapps.rspanicbutton.api.ShowProfileInfoAPI;
import com.ibapps.rspanicbutton.model.Value;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingsMenuActivity extends AppCompatActivity {

    public static final String URL = "https://rspanicbuttonn.000webhostapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(SettingsMenuActivity.this);
        String statusLogin = sharedPreferences.getString("status", "Logged Out");
        if (!statusLogin.equals("Logged In")) {
            finish();
        }
    }

    @OnClick(R.id.textViewProfil)
    protected void editProfile() {
        ProgressDialog progressDialog = new ProgressDialog(SettingsMenuActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Tunggu sebentar...");
        progressDialog.show();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(SettingsMenuActivity.this);
        String idUser = sharedPreferences.getString("idUser", "kosong");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ShowProfileInfoAPI showProfileInfoAPI = retrofit.create(ShowProfileInfoAPI.class);
        Call<Value> call = showProfileInfoAPI.show(idUser);
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getValue();
                String idUser = response.body().getIdUser();
                String namaUser = response.body().getNamaUser();
                String noHP = response.body().getNoHP();
                if (value.equals("1")) {
                    progressDialog.dismiss();
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(SettingsMenuActivity.this,
                            ProfileMenuActivity.class);
                    bundle.putString("idUser", idUser);
                    bundle.putString("namaUser", namaUser);
                    bundle.putString("noHP", noHP);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SettingsMenuActivity.this,
                            "Gagal menghubungkan ke server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SettingsMenuActivity.this,
                        "Gagal menghubungkan ke server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.textViewUbahPassword)
    protected void changePassword() {
        Intent intent = new Intent(SettingsMenuActivity.this,
                ChangePasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.textViewLogOut)
    protected void logout() {
        android.app.AlertDialog.Builder alertDialogBuilder =
                new android.app.AlertDialog.Builder(this);
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(getApplicationContext());
        alertDialogBuilder.setTitle("Konfirmasi");
        alertDialogBuilder
                .setMessage("Anda yakin ingin log out?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String idUser = sharedPreferences.getString("idUser", "kosong");
                        ProgressDialog progress = new ProgressDialog(SettingsMenuActivity.this);
                        progress.setCancelable(false);
                        progress.setMessage("Tunggu sebentar...");
                        progress.show();
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        LogoutAPI api = retrofit.create(LogoutAPI.class);
                        Call<Value> call = api.logout(idUser);
                        call.enqueue(new Callback<Value>() {
                            @Override
                            public void onResponse(Call<Value> call, Response<Value> response) {
                                String value = response.body().getValue();
                                progress.dismiss();
                                if (value.equals("1")) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("idUser", "");
                                    editor.putString("status", "");
                                    editor.apply();
                                    Intent intent = new Intent(
                                            SettingsMenuActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                } else {
                                    Toast.makeText(SettingsMenuActivity.this,
                                            "Gagal log out", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Value> call, Throwable t) {
                                progress.dismiss();
                                Toast.makeText(SettingsMenuActivity.this,
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
}
