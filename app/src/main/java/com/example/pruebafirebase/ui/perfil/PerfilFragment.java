package com.example.pruebafirebase.ui.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pruebafirebase.Login;
import com.example.pruebafirebase.MenuCliente;
import com.example.pruebafirebase.Perfil;
import com.example.pruebafirebase.R;
import com.example.pruebafirebase.gps;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerfilFragment extends Fragment {

    private HomeViewModel homeViewModel;
    TextView nombre,ap_p,ap_m,correo;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button logout, mapa;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //homeViewModel =
          //      ViewModelProviders.of(this).get(HomeViewModel.class);
        //View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
       /* homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        nombre = root.findViewById(R.id.lbl_nombreFP);
        ap_p = root.findViewById(R.id.lbl_ap_pFP);
        ap_m = root.findViewById(R.id.lbl_ap_mFP);
        correo = root.findViewById(R.id.lbl_correoFP);
        logout = root.findViewById(R.id.btn_logoutF);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getActivity(),Login.class));
                getActivity().finish();
            }
        });
        mapa = root.findViewById(R.id.mapa);
        inicializarFirebase();

        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), gps.class);
                getActivity().startActivity(intent);
            }
        });
        inicializarFirebase();

        getUserInfo();

        return root;

    }

    private void inicializarFirebase() {
        auth=FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();

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

                    nombre.setText(nombreP);
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


}
