package com.ibapps.rspanicbutton;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.ibapps.rspanicbutton.api.RegisterAPI;
import com.ibapps.rspanicbutton.model.Value;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    String idUser, namaUser, noHP, password, konfirmasiPassword;
    public static final String URL = "https://panicbutton.indakabarody.com/";
    @BindView(R.id.editTextIDUser) EditText editTextIDUser;
    @BindView(R.id.editTextNamaUser) EditText editTextNamaUser;
    @BindView(R.id.editTextNoHP) EditText editTextNoHP;
    @BindView(R.id.editTextPassword) EditText editTextPassword;
    @BindView(R.id.editTextKonfirmasiPassword) EditText editTextKonfirmasiPassword;
    @BindView(R.id.validasiIDUser) TextInputLayout validasiIDUser;
    @BindView(R.id.validasiNamaUser) TextInputLayout validasiNamaUser;
    @BindView(R.id.validasiNoHP) TextInputLayout validasiNoHP;
    @BindView(R.id.validasiPassword) TextInputLayout validasiPassword;
    @BindView(R.id.validasiKonfirmasiPassword) TextInputLayout validasiKonfirmasiPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
        String statusLogin = sharedPreferences.getString("status", "Logged Out");
        if (statusLogin.equals("Logged In")) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.buttonRegister)
    protected void register() {
        idUser = editTextIDUser.getText().toString();
        namaUser = editTextNamaUser.getText().toString();
        noHP = editTextNoHP.getText().toString();
        password = editTextPassword.getText().toString();
        konfirmasiPassword = editTextKonfirmasiPassword.getText().toString();
        if (idUser.isEmpty()) {
            validasiIDUser.setError("ID User harus diisi");
        } else if (namaUser.isEmpty()) {
            validasiNamaUser.setError("Nama harus diisi");
        } else if (noHP.isEmpty()) {
            validasiNoHP.setError("Nomor telepon harus diisi");
        } else if (password.isEmpty()) {
            validasiPassword.setError("Password harus diisi");
        } else if (konfirmasiPassword.isEmpty()) {
            validasiKonfirmasiPassword.setError("Konfirmasi password harus diisi");
        } else if (!konfirmasiPassword.equals(password)) {
            validasiKonfirmasiPassword.setError("Konfirmasi password harus sama");
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Tunggu sebentar...");
            progressDialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RegisterAPI registerAPI = retrofit.create(RegisterAPI.class);
            Call<Value> call = registerAPI.register(idUser, namaUser, noHP, password);
            call.enqueue(new Callback<Value>() {
                @Override
                public void onResponse(Call<Value> call, Response<Value> response) {
                    String value = response.body().getValue();
                    String message = response.body().getMessage();
                    progressDialog.dismiss();
                    if (value.equals("1")) {
                        alertDialogBuilder.setTitle("Informasi");
                        alertDialogBuilder
                                .setMessage(message)
                                .setCancelable(false)
                                .setNeutralButton("Oke", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        alertDialogBuilder.setTitle("Informasi");
                        alertDialogBuilder
                                .setMessage(message)
                                .setCancelable(false)
                                .setNeutralButton("Oke", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }

                @Override
                public void onFailure(Call<Value> call, Throwable t) {
                    progressDialog.dismiss();
                    alertDialogBuilder.setTitle("Informasi");
                    alertDialogBuilder
                            .setMessage("Gagal menghubungkan ke server")
                            .setCancelable(false)
                            .setNeutralButton("Oke", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        }
    }

    @OnClick(R.id.textViewAlreadyRegister)
    protected void cancel() {
        finish();
    }
}
