package com.example.pruebafirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pruebafirebase.model.Garantia;
import com.example.pruebafirebase.model.Persona;
import com.example.pruebafirebase.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Vendedor extends AppCompatActivity {

    EditText producto,tiempo, condiciones, tienda,id_usuario;
    ListView listV_usuarios;
    Button btn_foto;
    ImageView img_producto;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;

    Usuario personaSelect;
    private List<Usuario> listPersona = new ArrayList<>();
    ArrayAdapter<Usuario> arrayAdapterPersona;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor);

        producto = findViewById(R.id.txt_producto);
        tiempo = findViewById(R.id.txt_tiempo);
        condiciones = findViewById(R.id.txt_condiciones);
        tienda = findViewById(R.id.txt_tienda);
        id_usuario = findViewById(R.id.txt_idUsuario);
        btn_foto = findViewById(R.id.btn_foto);
        img_producto = findViewById(R.id.img_producto);

        listV_usuarios = findViewById(R.id.lv_datosUsuario);

        inicializarFirebase();
        listarDatos();

        listV_usuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSelect = (Usuario) parent.getItemAtPosition(position);
                id_usuario.setText(personaSelect.getUid());
            }
        });

        btn_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            img_producto.setImageBitmap(imageBitmap);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());

            // Creamos una referencia a la carpeta y el nombre de la imagen donde se guardara
            StorageReference mountainImagesRef = storageRef.child("camara/" + timeStamp + ".jpg");
            //Pasamos la imagen a un array de byte
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] datas = baos.toByteArray();

            // Empezamos con la subida a Firebase
            UploadTask uploadTask = mountainImagesRef.putBytes(datas);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getBaseContext(), "Hubo un error", Toast.LENGTH_LONG);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Vendedor.this, "Subida con exito", Toast.LENGTH_LONG);

                }
            });

        }
    }

    private void listarDatos() {
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPersona.clear();

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Usuario u = objSnapshot.getValue(Usuario.class);

                    if (u.getTipo_usuario().equals("cliente")) {
                        listPersona.add(u);
                        arrayAdapterPersona = new ArrayAdapter<Usuario>(Vendedor.this, android.R.layout.simple_list_item_1, listPersona);
                        listV_usuarios.setAdapter(arrayAdapterPersona);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        auth= FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vendedor,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String producto_garatia = producto.getText().toString();
        String tiempo_garantia = tiempo.getText().toString();
        String condiciones_garantia = condiciones.getText().toString();
        String tienda_garantia = tienda.getText().toString();
        String idUsuario_garantia = id_usuario.getText().toString();
        switch (item.getItemId()){

            case R.id.guardar:{
                if (producto_garatia.equals("")||tiempo_garantia.equals("")||
                        condiciones_garantia.equals("")||tienda_garantia.equals("") || idUsuario_garantia.equals("")){
                    validar();
                }else{
                    Garantia g = new Garantia();
                    g.setUid(UUID.randomUUID().toString());
                    g.setProducto(producto_garatia);
                    g.setTiempo(tiempo_garantia);
                    g.setCondiciones(condiciones_garantia);
                    g.setTienda(tienda_garantia);
                    g.setId_usuario(idUsuario_garantia);

                    databaseReference.child("Garantia").child(g.getUid()).setValue(g);
                    Toast.makeText(Vendedor.this,"Agregado",Toast.LENGTH_LONG).show();
                    limpiarCajas();
                }

                break;
            }

            case  R.id.salir:{
                auth.signOut();
                startActivity(new Intent(Vendedor.this,Login.class));
                finish();
            }
            default:break;
        }

        return  true;
    }

    private void limpiarCajas() {
        producto.setText("");
        tiempo.setText("");
        condiciones.setText("");
        tienda.setText("");
        id_usuario.setText("");
    }

    public void validar(){
        String producto_garatia = producto.getText().toString();
        String tiempo_garantia = tiempo.getText().toString();
        String condiciones_garantia = condiciones.getText().toString();
        String tienda_garantia = tienda.getText().toString();
        String idUsuario_garantia = id_usuario.getText().toString();

        if (producto_garatia.equals("")){
            producto.setError("Campo requerido");
        }else if (tiempo_garantia.equals("")){
            tiempo.setError("Campo requerido");
        }else if (condiciones_garantia.equals("")){
            condiciones.setError("Campo requerido");
        }else if (tienda_garantia.equals("")){
            tienda.setError("Campo requerido");
        }else if (idUsuario_garantia.equals("")){
            id_usuario.setError("Campo requerido");
        }
    }
}
