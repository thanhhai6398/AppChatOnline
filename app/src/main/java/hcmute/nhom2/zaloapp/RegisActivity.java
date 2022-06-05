package hcmute.nhom2.zaloapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import hcmute.nhom2.zaloapp.utilities.Constants;

public class RegisActivity extends AppCompatActivity {

    EditText edtName, edtPhoneRegis, edtBirth, edtPassRegis, edtRePassRegis;
    Button btnRegis;
    RadioButton rbtnMale, rbtnFeMale;
    FirebaseFirestore db;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        AnhXa();

        //Init Firebase
        db = FirebaseFirestore.getInstance();

        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Regis();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void AnhXa(){
        //Anh xa
        edtName = findViewById(R.id.edtName);
        edtPhoneRegis = findViewById(R.id.edtPhoneRegis);
        edtBirth = findViewById(R.id.edtBirth);
        edtPassRegis = findViewById(R.id.edtPassRegis);
        edtRePassRegis = findViewById(R.id.edtRePassRegis);

        btnRegis = findViewById(R.id.btnRegis);

        rbtnMale = findViewById(R.id.rbtnMale);
        rbtnFeMale = findViewById(R.id.rbtnFemale);

        back = findViewById(R.id.back_main2);
    }
    private void Regis(){
        if(edtName.getText().toString().equals("")) {
            Toast.makeText(RegisActivity.this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();

        }else if(edtPhoneRegis.getText().toString().equals("")) {
            Toast.makeText(RegisActivity.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();

        }else if(!rbtnFeMale.isChecked() && !rbtnMale.isChecked()){
            Toast.makeText(RegisActivity.this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();

        }else if(edtPassRegis.getText().toString().equals("")){
            Toast.makeText(RegisActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();

        }else if(!edtRePassRegis.getText().toString().equals(edtPassRegis.getText().toString())){
            Toast.makeText(RegisActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();

        }else{
            db.collection(Constants.KEY_COLLECTION_USERS).document(edtPhoneRegis.getText().toString())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()){
                        Toast.makeText(RegisActivity.this, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();

                    }else {

                        Map<String, Object> reg_entry = new HashMap<>();
                        reg_entry.put(Constants.KEY_Name, edtName.getText().toString());
                        if(rbtnFeMale.isChecked()){
                            reg_entry.put(Constants.KEY_Gender, "Female");
                        }
                        else{
                            reg_entry.put(Constants.KEY_Gender, "Male");
                        }
                        reg_entry.put(Constants.KEY_Birth, edtBirth.getText().toString());
                        reg_entry.put(Constants.KEY_Image, "avt.jpg");
                        reg_entry.put(Constants.KEY_CoverImage, "bg.jpg");
                        reg_entry.put(Constants.KEY_Active,false);

                        db.collection(Constants.KEY_COLLECTION_USERS).document(edtPhoneRegis.getText().toString())
                                .set(reg_entry).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(RegisActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                Intent login = new Intent(RegisActivity.this, LoginActivity.class);
                                startActivity(login);
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Lỗi đăng ký", e.getMessage());
                                    }
                                });

                        Map<String, Object> data = new HashMap<>();
                        data.put(Constants.KEY_Password, edtPassRegis.getText().toString());
                        db.collection(Constants.KEY_COLLECTION_USERS).document(edtPhoneRegis.getText().toString())
                                .collection(Constants.KEY_SUB_COLLECTION_PrivateData).document(edtPhoneRegis.getText().toString())
                                .set(data);

                    }
                }
            });
        }
    }
}
