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
import com.ibapps.rspanicbutton.api.SaveProfileInfoAPI;
import com.ibapps.rspanicbutton.model.Value;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileMenuActivity extends AppCompatActivity {

    String idUser, idUserOld, namaUser, noHP;
    public static final String URL = "https://rspanicbuttonn.000webhostapp.com/";
    @BindView(R.id.editTextIDUser) EditText editTextIDUser;
    @BindView(R.id.editTextNamaUser) EditText editTextNamaUser;
    @BindView(R.id.editTextNoHP) EditText editTextNoHP;
    @BindView(R.id.validasiIDUser) TextInputLayout validasiIDUser;
    @BindView(R.id.validasiNamaUser) TextInputLayout validasiNamaUser;
    @BindView(R.id.validasiNoHP) TextInputLayout validasiNoHP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_menu);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(ProfileMenuActivity.this);
        String statusLogin = sharedPreferences.getString("status", "Logged Out");
        if (!statusLogin.equals("Logged In")) {
            finish();
        }
        Bundle bundle = getIntent().getExtras();
        idUser = bundle.getString("idUser");
        namaUser = bundle.getString("namaUser");
        noHP = bundle.getString("noHP");
        editTextIDUser.setText(idUser);
        editTextNamaUser.setText(namaUser);
        editTextNoHP.setText(noHP);
    }

    @OnClick(R.id.buttonSimpan)
    protected void save() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(ProfileMenuActivity.this);
        idUser = editTextIDUser.getText().toString();
        idUserOld = sharedPreferences.getString("idUser", "kosong");
        namaUser = editTextNamaUser.getText().toString();
        noHP = editTextNoHP.getText().toString();
        if (idUser.isEmpty()) {
            validasiIDUser.setError("ID User harus diisi");
        } else if (namaUser.isEmpty()) {
            validasiNamaUser.setError("Nama harus diisi");
        } else if (noHP.isEmpty()) {
            validasiNoHP.setError("Nomor telepon harus diisi");
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Konfirmasi");
            alertDialogBuilder.setMessage("Simpan perubahan?");
            alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ProgressDialog progressDialog = new ProgressDialog(ProfileMenuActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Tunggu sebentar...");
                    progressDialog.show();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    SaveProfileInfoAPI saveProfileInfoAPI = retrofit.create(SaveProfileInfoAPI.class);
                    Call<Value> call = saveProfileInfoAPI.save(idUser, idUserOld, namaUser, noHP);
                    call.enqueue(new Callback<Value>() {
                        @Override
                        public void onResponse(Call<Value> call, Response<Value> response) {
                            String value = response.body().getValue();
                            String message = response.body().getMessage();
                            progressDialog.dismiss();
                            if (value.equals("1")) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("idUser", idUser);
                                editor.apply();
                                Toast.makeText(ProfileMenuActivity.this,
                                        message, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ProfileMenuActivity.this,
                                        message, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Value> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileMenuActivity.this,
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
