package com.example.almacen.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.almacen.R;
import com.example.almacen.models.Insumo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.UUID;

public class NuevoInsumoActivity extends AppCompatActivity {
    public ImageButton imagen;
    private TextInputEditText etNombre, etDescripcion, etMarca, etExistencia, etCantMin;
    private Button bAgregar;
    private TextView tTitulo;
    private DatabaseReference dr;
    private Insumo insumo;
    private int tipo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_insumo);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.purple_500));
        }

        Toolbar toolbar = findViewById(R.id.toolbar_nuevo);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        tTitulo = findViewById(R.id.tTitulo);
        etMarca = findViewById(R.id.etMarca);
        etExistencia = findViewById(R.id.etExistencia);
        etCantMin = findViewById(R.id.etCantMin);
        bAgregar = findViewById(R.id.bAgregar);

        bAgregar.setOnClickListener(v -> agregar());

        imagen = findViewById(R.id.bImagen);
        imagen.setOnClickListener(view -> {
            final PopupMenu popupMenu = new PopupMenu(NuevoInsumoActivity.this, imagen);
            popupMenu.getMenuInflater().inflate(R.menu.menu_foto, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.agregar_foto:
                        if(ActivityCompat.checkSelfPermission(NuevoInsumoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED){
                            abrirGaleria();
                        }else{
                            ActivityCompat.requestPermissions(NuevoInsumoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                        return true;
                    case R.id.tomar_foto:
                        if(ActivityCompat.checkSelfPermission(NuevoInsumoActivity.this, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED){
                            abrirCamara();
                        }else{
                            ActivityCompat.requestPermissions(NuevoInsumoActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
                        }
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        });

        insumo = new Insumo();

        Bundle datos = this.getIntent().getExtras();

        if (datos != null) {
            tipo = datos.getInt("getTipo");

            switch (tipo) {
                case 0:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("");
                    tTitulo.setText(R.string.nuevo_insumo);
                    nuevo();
                    break;
                case 1:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("");
                    modificar(datos);
                    tTitulo.setText(R.string.modificar_insumo);
                    break;
            }
        }

        inicializarFirebase();
    }

    private void inicializarFirebase() {
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        dr = fd.getReference("Insumos");

        dr.child(insumo.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && tipo == 1) {
                    insumo.setNombre(Objects.requireNonNull(snapshot.child("nombre").getValue()).toString());
                    insumo.setDescripcion(Objects.requireNonNull(snapshot.child("descripcion").getValue()).toString());
                    insumo.setMarca(Objects.requireNonNull(snapshot.child("marca").getValue()).toString());
                    insumo.setExistencia(Integer.parseInt(Objects.requireNonNull(snapshot.child("existencia").getValue()).toString()));
                    insumo.setCant_minima(Integer.parseInt(Objects.requireNonNull(snapshot.child("cant_minima").getValue()).toString()));
                    insumo.setImagen(Objects.requireNonNull(snapshot.child("imagen").getValue()).toString());

                    mostrarDatos();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(tipo == 1) {
            getMenuInflater().inflate(R.menu.menu_modificar, menu);
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.animator.from_l, R.animator.to_r);
                return true;
            case R.id.mEliminar:
                eliminar();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void eliminar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirmar");
        builder.setMessage("¿Desea eliminar el insumo?");

        builder.setPositiveButton("SI", (dialog, which) -> {
            StorageReference folder = FirebaseStorage.getInstance().getReference();
            StorageReference desertRef = folder.child("Images/IMG-" + insumo.getId());

            desertRef.delete();

            dr.child(insumo.getId()).removeValue();

            Toast.makeText(this, "Insumo eliminado", Toast.LENGTH_SHORT).show();
            onBackPressed();
        });

        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void mostrarDatos() {
        etNombre.setText(insumo.getNombre());
        etDescripcion.setText(insumo.getDescripcion());
        etMarca.setText(insumo.getMarca());
        etExistencia.setText(String.valueOf(insumo.getExistencia()));
        etCantMin.setText(String.valueOf(insumo.getCant_minima()));
    }

    private void limpiarCampos() {
        etNombre.setText("");
        etDescripcion.setText("");
        etMarca.setText("");
        etExistencia.setText("");
        etCantMin.setText("");
        imagen.setImageResource(R.drawable.cajaherra);
        nuevo();
    }

    private void nuevo() {
        bAgregar.setText(R.string.agregar);

        insumo.setId(UUID.randomUUID().toString());
        insumo.setNombre("");
        insumo.setDescripcion("");
        insumo.setMarca("");
        insumo.setExistencia(0);
        insumo.setCant_minima(0);
        insumo.setImagen("");
    }

    private void modificar(Bundle datos) {
        bAgregar.setText(R.string.modificar);

        insumo.setId(datos.getString("getID"));
        insumo.setNombre(datos.getString("getNombre"));
        insumo.setDescripcion(datos.getString("getDescripcion"));
        insumo.setMarca(datos.getString("getMarca"));
        insumo.setExistencia(datos.getInt("getExistencia"));
        insumo.setCant_minima(datos.getInt("getCantidadMin"));
        insumo.setImagen(datos.getString("getImagen"));

        if(!insumo.getImagen().isEmpty()) {
            Picasso.get().load(insumo.getImagen()).into(imagen);
        }

        mostrarDatos();
    }

    public void agregar() {
        if (Objects.requireNonNull(etNombre.getText()).toString().equals("")) {
            etNombre.setError("Requerido");
            return;
        }

        insumo.setNombre(etNombre.getText().toString());
        insumo.setDescripcion(Objects.requireNonNull(etDescripcion.getText()).toString());
        insumo.setMarca(Objects.requireNonNull(etMarca.getText()).toString());
        insumo.setExistencia(Integer.parseInt(Objects.requireNonNull(etExistencia.getText()).toString()));
        insumo.setCant_minima(Integer.parseInt(Objects.requireNonNull(etCantMin.getText()).toString()));

        switch (tipo) {
            case 0:
                dr.child(insumo.getId()).setValue(insumo);
                Toast.makeText(this, "Insumo agregado", Toast.LENGTH_SHORT).show();
                limpiarCampos();
                break;
            case 1:
                dr.child(insumo.getId()).setValue(insumo);
                Toast.makeText(this, "Insumo modificado", Toast.LENGTH_SHORT).show();
                onBackPressed();
                overridePendingTransition(R.animator.from_l, R.animator.to_r);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2){
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                abrirCamara();
            }else{
                Toast.makeText(this, "No se pudo acceder a la camara: permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 1){
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                abrirGaleria();
            }else{
                Toast.makeText(this, "No se pudo acceder a la galeria: permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void subirImagen(Uri FileUri){
        StorageReference Folder = FirebaseStorage.getInstance().getReference().child("Images");

        final StorageReference file_name = Folder.child("IMG-" + insumo.getId());

        file_name.putFile(FileUri).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {
            insumo.setImagen(String.valueOf(uri));
            Toast.makeText(this, "Se ha subido la imagen", Toast.LENGTH_SHORT).show();
        }));
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2){
            if (resultCode == Activity.RESULT_OK){
                assert data != null;
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imagen.setImageBitmap(bitmap);
                subirImagen(getImageUri(this,bitmap));
            }
        }
        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK){
                assert data != null;
                Uri path = data.getData();
                imagen.setImageURI(path);
                subirImagen(path);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void abrirCamara(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 2);
        }
    }

    @SuppressLint("IntentReset")
    private void abrirGaleria(){
        @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(Intent.createChooser(intent,"Seleccione la Aplicación"), 1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.from_l, R.animator.to_r);
    }

}