package android.example.uas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.example.uas.mypaket.AdapterMyData;
import android.example.uas.mypaket.MyData;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addData;
    RecyclerView recyclerView;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("My Data");
    List<MyData> list = new ArrayList<>();
    AdapterMyData adapterMyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addData = findViewById(R.id.addData);
        recyclerView = findViewById(R.id.recView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogTambahData();

            }
        });

        bacaData();
    }

    private void bacaData() {
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    MyData value = snapshot.getValue(MyData.class);
                    list.add(value);
                }
                adapterMyData = new AdapterMyData(MainActivity.this, list);
                recyclerView.setAdapter(adapterMyData);

                setClick();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    private void setClick() {
        adapterMyData.setOnCallBack(new AdapterMyData.OnCallBack() {
            @Override
            public void onTblHapus(MyData myData) {
                hapusData(myData);
            }
        });
    }

    private void hapusData(MyData myData) {
        myRef.child(myData.getKunci()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(MainActivity.this, myData.getIsi() + " Dihapus", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogTambahData() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.tambah_data);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        ImageButton tblKeluar = dialog.findViewById(R.id.tblKeluar);
        tblKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        EditText txt_tambah = dialog.findViewById(R.id.txt_tambah);
        Button tblTambah = dialog.findViewById(R.id.tbl_Tambah);

        tblTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(txt_tambah.getText())){
                    tblTambah.setError("Silahkan Isi Data");
                }else {
                    simpanData(txt_tambah.getText().toString());
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void simpanData(String s) {

        String kunci = myRef.push().getKey();
        MyData myData = new MyData(kunci, s);

        myRef.child(kunci).setValue(myData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "Berhasil Disimpan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}