package com.example.pruebafirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
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

public class Login extends AppCompatActivity {

    EditText nomP,app_p,app_m, correoP, passwordP;
    TextView aux, aux2;
    RadioButton rb_cliente,rb_vendedor;
    Button registro,login, crear;

    //variables de los datos que voy a capturar
    String nombre = " ",ap_Paterno= " ",ap_Materno = " ", correo = " ",password = " ",tipo_usuario=" ";

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nomP = findViewById(R.id.txt_nombrePersona);
        app_p = findViewById(R.id.txt_ap_pPersona);
        app_m = findViewById(R.id.txt_ap_mPersona);
        correoP = findViewById(R.id.txt_correoPersona);
        passwordP = findViewById(R.id.txt_passwordPersona);
        aux = findViewById(R.id.textView2);
        aux2 = findViewById(R.id.textView6);
        rb_cliente = findViewById(R.id.rb_cliente);
        rb_vendedor = findViewById(R.id.rb_vendedor);
        registro = findViewById(R.id.register);
        login = findViewById(R.id.login);
        crear = findViewById(R.id.btn_crear);


        inicializarFirebase();

        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomP.setVisibility(View.GONE);
                nomP.setVisibility(View.VISIBLE);
                app_p.setVisibility(View.VISIBLE);
                app_m.setVisibility(View.VISIBLE);
                correoP.setVisibility(View.VISIBLE);
                passwordP.setVisibility(View.VISIBLE);
                aux.setVisibility(View.VISIBLE);
                rb_cliente.setVisibility(View.VISIBLE);
                rb_vendedor.setVisibility(View.VISIBLE);
                registro.setVisibility(View.VISIBLE);

            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                nombre = nomP.getText().toString();
                ap_Paterno = app_p.getText().toString();
                ap_Materno = app_m.getText().toString();
                correo = correoP.getText().toString();
                password = passwordP.getText().toString();
                if (rb_cliente.isChecked() == true){
                    tipo_usuario = "cliente";
                }else if (rb_vendedor.isChecked() == true){
                    tipo_usuario = "vendedor";
                }
             if (!nombre.isEmpty() && !ap_Paterno.isEmpty() && !ap_Materno.isEmpty() && !correo.isEmpty()
                     && !password.isEmpty()){

                 if (password.length() >= 6){
                     registroUsuario(nombre,ap_Paterno,ap_Materno,correo,password,tipo_usuario);

                 }else{
                     Toast.makeText(Login.this,"La contraseña debe contener minimo 6 caracteres",Toast.LENGTH_LONG).show();
                 }

             }
             else {
                 Toast.makeText(Login.this,"Debes de agregar todos los campos",Toast.LENGTH_LONG).show();
             }

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Sesion.class));
                //finish();
            }
        });


    }

    private void registroUsuario(final String nombre, final String ap_Paterno, final String ap_Materno,
                                 final String correo, final String password, final String tipo_usuario) {
        auth.createUserWithEmailAndPassword(correo,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Usuario u= new Usuario();
                    String id = auth.getCurrentUser().getUid();
                    u.setUid(id);
                    u.setNombre(nombre);
                    u.setApellidoPaterno(ap_Paterno);
                    u.setApellidoMaterno(ap_Materno);
                    u.setCorreo(correo);
                    u.setPassword(password);
                    u.setTipo_usuario(tipo_usuario);

                    databaseReference.child("Users").child(u.getUid()).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()){
                                startActivity(new Intent(Login.this,MenuCliente.class));
                                finish();
                            }else {
                                Toast.makeText(Login.this,"No se pudieron crear los datos",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(Login.this,"No se pudo registrar",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        auth=FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null){

            String id= auth.getCurrentUser().getUid();
            databaseReference.child("Users").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String var = dataSnapshot.child("tipo_usuario").getValue().toString();
                        if (var.equals("cliente")){
                            startActivity(new Intent(Login.this,MenuCliente.class));
                            finish();
                        }else if(var.equals("vendedor")) {
                            startActivity(new Intent(Login.this,Vendedor.class));
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
