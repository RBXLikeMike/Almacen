package com.example.almacen.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.Toast;
import com.example.almacen.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient googleApiClient;
    private SignInButton signInButton;
    public static final int SIGN_IN_CORE = 777;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private Pattern patron1 = Pattern.compile("([a-z0-9]+(\\.?[a-z0-9])*)+@(([a-z]+)\\.([a-z]+))+");
    private Pattern patron2 = Pattern.compile("^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#*.$%^&+=])"
                + "(?=\\S+$).{8,20}$");
    private Matcher match1;
    private Matcher match2;
    private Switch switchRecordar;
    TextInputEditText etCorreo, etPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.purple_500));
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        auth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

        etCorreo = findViewById(R.id.etCorreo);
        etPass = findViewById(R.id.etPass);
        switchRecordar = findViewById(R.id.switch_recordar);
        cargarPreferencias();
    }

    private void cargarPreferencias() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        etCorreo.setText(preferences.getString("user",""));
        etPass.setText(preferences.getString("pass",""));
        switchRecordar.setChecked(preferences.getBoolean("switch",false));
    }

    public void inicioMenu(View u){
        String email = etCorreo.getText().toString();
        final String contra = etPass.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this, "Introduce tu correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(contra)){
            Toast.makeText(LoginActivity.this, "Introduce tu contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        //Validación de correo
        match1 = patron1.matcher(email);
        if (!match1.find()){
            Toast.makeText(LoginActivity.this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        //Validación de contraseña
        match2 = patron2.matcher(contra);
        if (!match2.find()){
            Toast.makeText(LoginActivity.this, "Contraseña inválida, debe contener: minimo 8 caracteres y máximo 20, " +
                    "un dígito, una letra mayúscula, una letra minúscula y un caracter especial", Toast.LENGTH_SHORT).show();
            return;
        }

        //Autenticación de usuario
        auth.signInWithEmailAndPassword(email, contra).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Falló la autenticación, revise sus credenciales", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(switchRecordar.isChecked()) {
                        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("user", email);
                        editor.putString("pass", "");
                        editor.putBoolean("switch",true);

                        editor.apply();
                    } else {
                        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("user", "");
                        editor.putString("pass", "");
                        editor.putBoolean("switch",false);

                        editor.apply();
                    }

                    Toast.makeText(LoginActivity.this, "Bienvenido "+ email, Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(getApplicationContext(), MenuLateralActivity.class);
                    i.putExtra("Usuario", email);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    public void inicioGoogle() {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}