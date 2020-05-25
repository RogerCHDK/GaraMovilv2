package com.example.pruebafirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pruebafirebase.model.Garantia;
import com.example.pruebafirebase.model.Persona;
import com.example.pruebafirebase.model.Usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Perfil extends AppCompatActivity {

    Button logout;
    TextView nombre,ap_p,ap_m,correo;
    ListView lv_garantia;
    EditText edt_producto, edt_tienda, edt_tiempo, edt_condiciones;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Garantia personaSelect;
    private List<Garantia> listPersona = new ArrayList<>();
    ArrayAdapter<Garantia> arrayAdapterPersona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        logout = findViewById(R.id.btn_logout);
        nombre = findViewById(R.id.lbl_nombre);
        ap_p = findViewById(R.id.lbl_ap_p);
        ap_m = findViewById(R.id.lbl_ap_m);
        correo = findViewById(R.id.lbl_correo);
        lv_garantia = findViewById(R.id.lv_datosGarantia);

        edt_producto = findViewById(R.id.txtP_producto);
        edt_tiempo = findViewById(R.id.txtP_tiempo);
        edt_tienda = findViewById(R.id.txtP_tienda);
        edt_condiciones = findViewById(R.id.txtP_condiciones);

        inicializarFirebase();



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(Perfil.this,Login.class));
                finish();
            }
        });

        getUserInfo();
        listarDatos();

        lv_garantia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSelect = (Garantia) parent.getItemAtPosition(position);
                edt_producto.setText(personaSelect.getProducto());
                edt_tiempo.setText(personaSelect.getTiempo());
                edt_tienda.setText(personaSelect.getTienda());
                edt_condiciones.setText(personaSelect.getCondiciones());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String producto = edt_producto.getText().toString();
        String tienda = edt_tienda.getText().toString();
        String tiempo = edt_tiempo.getText().toString();
        String condiciones = edt_condiciones.getText().toString();

        switch (item.getItemId()){
            case  R.id.logout_usuario:{
                auth.signOut();
                startActivity(new Intent(Perfil.this,Login.class));
                finish();
            }
            case R.id.icon_save: {
                if (producto.equals("")||tienda.equals("")||tiempo.equals("")||condiciones.equals("")){
                    validar();
                }else{
                    String id= auth.getCurrentUser().getUid();
                    Garantia g = new Garantia();
                    g.setUid(personaSelect.getUid());
                    g.setProducto(producto.trim());
                    g.setTiempo(tiempo.trim());
                    g.setTienda(tienda.trim());
                    g.setCondiciones(condiciones.trim());
                    g.setId_usuario(id.trim());
                    databaseReference.child("Garantia").child(g.getUid()).setValue(g);
                    Toast.makeText(Perfil.this,"Actualizado",Toast.LENGTH_LONG).show();
                    limpiarCajas();
                }

                break;
            }
            case R.id.icon_delete: {
                Garantia g = new Garantia();
                g.setUid(personaSelect.getUid());
                databaseReference.child("Garantia").child(g.getUid()).removeValue();
                Toast.makeText(Perfil.this,"Eliminado",Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            }

            default:break;
        }
        return true;
    }

    private void listarDatos() {


        databaseReference.child("Garantia").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPersona.clear();
                String id= auth.getCurrentUser().getUid();

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Garantia g = objSnapshot.getValue(Garantia.class);
                    if (g.getId_usuario().equals(id)){
                    listPersona.add(g);
                    arrayAdapterPersona = new ArrayAdapter<Garantia>(Perfil.this,android.R.layout.simple_list_item_1,listPersona);
                    lv_garantia.setAdapter(arrayAdapterPersona);}
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getUserInfo() {
        String id= auth.getCurrentUser().getUid();

        databaseReference.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String nombreP = dataSnapshot.child("nombre").getValue().toString();
                    String correoP = dataSnapshot.child("correo").getValue().toString();
                    String ap_paternoP = dataSnapshot.child("apellidoPaterno").getValue().toString();
                    String ap_maternoP = dataSnapshot.child("apellidoMaterno").getValue().toString();

                    //nombre.setText(nombreP);
                    ap_p.setText(ap_paternoP);
                    ap_m.setText(ap_maternoP);
                    correo.setText(correoP);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    private void limpiarCajas() {
        edt_producto.setText("");
        edt_tienda.setText("");
        edt_tiempo.setText("");
        edt_condiciones.setText("");
    }

    public void validar(){
        String producto = edt_condiciones.getText().toString();
        String tienda = edt_tienda.getText().toString();
        String tiempo = edt_tiempo.getText().toString();
        String condiciones = edt_condiciones.getText().toString();

        if (producto.equals("")){
            edt_condiciones.setError("Campo requerido");
        }else if (tienda.equals("")){
            edt_tienda.setError("Campo requerido");
        }else if (tiempo.equals("")){
            edt_tiempo.setError("Campo requerido");
        }else if (condiciones.equals("")){
            edt_condiciones.setError("Campo requerido");
        }
    }
}
