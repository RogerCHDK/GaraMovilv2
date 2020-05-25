package com.example.pruebafirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.pruebafirebase.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;

public class Sesion extends AppCompatActivity {

    EditText correo, password;
    Button login;
    String correoPersona=" ",passwordPersona=" ";
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sesion);
        inicializarFirebase();
        auth=FirebaseAuth.getInstance();

        correo = findViewById(R.id.txt_correo);
        password = findViewById(R.id.txt_password);
        login = findViewById(R.id.btn_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correoPersona = correo.getText().toString();
                passwordPersona = password.getText().toString();

                if (!correoPersona.isEmpty() && !passwordPersona.isEmpty()){
                    loginUser(correoPersona,passwordPersona);
                }else{
                    Toast.makeText(Sesion.this,"Debes llenar todos los campos",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loginUser(final String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
           if (task.isSuccessful()){

               String id= auth.getCurrentUser().getUid();
               databaseReference.child("Users").child(id).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       if (dataSnapshot.exists()) {
                           String tipo_usuario = dataSnapshot.child("tipo_usuario").getValue().toString();
                           if (tipo_usuario.equals("cliente")){
                               startActivity(new Intent(Sesion.this,MenuCliente.class));
                               finish();
                           }else{
                               startActivity(new Intent(Sesion.this,Vendedor.class));
                               finish();
                           }
                       }
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });

           }else{
               Toast.makeText(Sesion.this,"Comprueba los datos",Toast.LENGTH_LONG).show();
           }
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
}
