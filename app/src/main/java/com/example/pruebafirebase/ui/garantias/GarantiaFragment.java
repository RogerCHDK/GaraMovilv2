package com.example.pruebafirebase.ui.garantias;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.pruebafirebase.Login;
import com.example.pruebafirebase.MainActivity;
import com.example.pruebafirebase.Perfil;
import com.example.pruebafirebase.R;
import com.example.pruebafirebase.model.Garantia;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GarantiaFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ListView lv_garantia;
    EditText edt_producto, edt_tienda, edt_tiempo, edt_condiciones;
    ImageView img_producto;

    Garantia personaSelect;
    private List<Garantia> listPersona = new ArrayList<>();
    ArrayAdapter<Garantia> arrayAdapterPersona;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       /* galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);

        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        setHasOptionsMenu(true);
        lv_garantia = root.findViewById(R.id.lv_datosGarantiaF);

        edt_producto = root.findViewById(R.id.txtP_productoF);
        edt_tiempo = root.findViewById(R.id.txtP_tiempoF);
        edt_tienda = root.findViewById(R.id.txtP_tiendaF);
        edt_condiciones = root.findViewById(R.id.txtP_condicionesF);
        img_producto = root.findViewById(R.id.img_prod);

        inicializarFirebase();
        listarDatos();
        lv_garantia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSelect = (Garantia) parent.getItemAtPosition(position);
                edt_producto.setText(personaSelect.getProducto());
                edt_tiempo.setText(personaSelect.getTiempo());
                edt_tienda.setText(personaSelect.getTienda());
                edt_condiciones.setText(personaSelect.getCondiciones());
                if(personaSelect.getProducto().equals("control")){
                    //img_producto.setImageResource(R.drawable.control);
                    //aqui pones tu imagen
                }else if(personaSelect.getProducto().equals("laptop")){
                    //img_producto.setImageResource(R.drawable.laptop);
                }
            }
        });



        return root;
    }

    private void inicializarFirebase() {
        auth=FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();

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
                        arrayAdapterPersona = new ArrayAdapter<Garantia>(getActivity(),android.R.layout.simple_list_item_1,listPersona);
                        lv_garantia.setAdapter(arrayAdapterPersona);}
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
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
                    Toast.makeText(getActivity(),"Actualizado",Toast.LENGTH_LONG).show();
                    limpiarCajas();
                }

                break;
            }
            case R.id.icon_delete: {
                Garantia g = new Garantia();
                g.setUid(personaSelect.getUid());
                databaseReference.child("Garantia").child(g.getUid()).removeValue();
                Toast.makeText(getActivity(),"Eliminado",Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            }

            default:break;
        }
        return true;
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
