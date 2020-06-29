package com.ibapps.rspanicbutton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.ibapps.rspanicbutton.api.ChangePasswordAPI;
import com.ibapps.rspanicbutton.model.Value;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePasswordActivity extends AppCompatActivity {

    String passwordLama, password, konfirmasiPassword;
    public static final String URL = "https://panicbutton.indakabarody.com/";
    @BindView(R.id.editTextPasswordLama) EditText editTextPasswordLama;
    @BindView(R.id.editTextPassword) EditText editTextPassword;
    @BindView(R.id.editTextKonfirmasiPassword) EditText editTextKonfirmasiPassword;
    @BindView(R.id.validasiPasswordLama) TextInputLayout validasiPasswordLama;
    @BindView(R.id.validasiPassword) TextInputLayout validasiPassword;
    @BindView(R.id.validasiKonfirmasiPassword) TextInputLayout validasiKonfirmasiPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(ChangePasswordActivity.this);
        String statusLogin = sharedPreferences.getString("status", "Logged Out");
        if (!statusLogin.equals("Logged In")) {
            finish();
        }
    }

    @OnClick(R.id.buttonSimpan)
    protected void changePassword() {
        passwordLama = editTextPasswordLama.getText().toString();
        password = editTextPassword.getText().toString();
        konfirmasiPassword = editTextKonfirmasiPassword.getText().toString();
        if (passwordLama.isEmpty()) {
            validasiPasswordLama.setError("Password lama harus diisi");
        } else if (password.isEmpty()) {
            validasiPassword.setError("Password baru harus diisi");
        } else if (konfirmasiPassword.isEmpty()) {
            validasiKonfirmasiPassword.setError("Konfirmasi password harus diisi");
        } else if (!password.equals(konfirmasiPassword)) {
            validasiKonfirmasiPassword.setError("Password dan konfirmasi password harus sama");
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Konfirmasi");
            alertDialogBuilder.setMessage("Simpan perubahan?");
            alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ProgressDialog progressDialog = new ProgressDialog(ChangePasswordActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Tunggu sebentar...");
                    progressDialog.show();
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(ChangePasswordActivity.this);
                    String idUser = sharedPreferences.getString("idUser", "kosong");
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    ChangePasswordAPI changePasswordAPI = retrofit.create(ChangePasswordAPI.class);
                    Call<Value> call = changePasswordAPI.change(idUser, passwordLama, password);
                    call.enqueue(new Callback<Value>() {
                        @Override
                        public void onResponse(Call<Value> call, Response<Value> response) {
                            String value = response.body().getValue();
                            String message = response.body().getMessage();
                            progressDialog.dismiss();
                            if (value.equals("1")) {
                                Toast.makeText(ChangePasswordActivity.this, message,
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, message,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Value> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(ChangePasswordActivity.this,
                                    "Gagal menghubungkan ke server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            alertDialogBuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    @OnClick(R.id.textViewBatal)
    protected void cancel() {
        finish();
    }
}
