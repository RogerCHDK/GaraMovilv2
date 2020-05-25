package com.example.pruebafirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pruebafirebase.model.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText nomP,appP, correoP, passwordP;
    ListView listV_personas;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Persona personaSelect;

    private List<Persona> listPersona = new ArrayList<>();
    ArrayAdapter<Persona> arrayAdapterPersona;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nomP = findViewById(R.id.txt_nombrePersona);
        appP = findViewById(R.id.txt_appPersona);
        correoP = findViewById(R.id.txt_correoPersona);
        passwordP = findViewById(R.id.txt_passwordPersona);

        listV_personas = findViewById(R.id.lv_datosPersona);

        inicializarFirebase();
        listarDatos();

        listV_personas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSelect = (Persona)parent.getItemAtPosition(position);
                nomP.setText(personaSelect.getNombre());
                appP.setText(personaSelect.getApellido());
                correoP.setText(personaSelect.getCorreo());
                passwordP.setText(personaSelect.getPassword());
            }
        });
    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPersona.clear();

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Persona p = objSnapshot.getValue(Persona.class);
                    listPersona.add(p);

                    arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this,android.R.layout.simple_list_item_1,listPersona);
                    listV_personas.setAdapter(arrayAdapterPersona);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String nombre = nomP.getText().toString();
        String apellido = appP.getText().toString();
        String correo = correoP.getText().toString();
        String password = passwordP.getText().toString();

        switch (item.getItemId()){
            /*case R.id.icon_add: {

                if (nombre.equals("")||apellido.equals("")||correo.equals("")||password.equals("")){
                    validar();
                }else{
                    Persona p = new Persona();
                    p.setUid(UUID.randomUUID().toString());
                    p.setNombre(nombre);
                    p.setApellido(apellido);
                    p.setCorreo(correo);
                    p.setPassword(password);
                    databaseReference.child("Persona").child(p.getUid()).setValue(p);

                Toast.makeText(this,"Agregado",Toast.LENGTH_LONG).show();
                limpiarCajas();
                }
                break;
            }*/
            case R.id.icon_save: {
                if (nombre.equals("")||apellido.equals("")||correo.equals("")||password.equals("")){
                    validar();
                }else{
                    Persona p = new Persona();
                    p.setUid(personaSelect.getUid());
                    p.setNombre(nombre.trim());
                    p.setApellido(apellido.trim());
                    p.setCorreo(correo.trim());
                    p.setPassword(password.trim());
                    databaseReference.child("Persona").child(p.getUid()).setValue(p);
                    Toast.makeText(this,"Actualizado",Toast.LENGTH_LONG).show();
                    limpiarCajas();
                }

                break;
            }
            case R.id.icon_delete: {
                Persona p = new Persona();
                p.setUid(personaSelect.getUid());
                databaseReference.child("Persona").child(p.getUid()).removeValue();
                Toast.makeText(this,"Eliminado",Toast.LENGTH_LONG).show();
                limpiarCajas();
               break;
            }

            default:break;
        }
            return true;
    }

    private void limpiarCajas() {
        nomP.setText("");
        appP.setText("");
        correoP.setText("");
        passwordP.setText("");
    }

    public void validar(){
        String nombre = nomP.getText().toString();
        String apellido = appP.getText().toString();
        String correo = correoP.getText().toString();
        String password = passwordP.getText().toString();

        if (nombre.equals("")){
            nomP.setError("Campo requerido");
        }else if (apellido.equals("")){
            appP.setError("Campo requerido");
        }else if (correo.equals("")){
            correoP.setError("Campo requerido");
        }else if (password.equals("")){
            passwordP.setError("Campo requerido");
        }
    }
}
