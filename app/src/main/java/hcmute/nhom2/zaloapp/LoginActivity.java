package hcmute.nhom2.zaloapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import hcmute.nhom2.zaloapp.utilities.Constants;
import hcmute.nhom2.zaloapp.utilities.PreferenceManager;


public class LoginActivity extends AppCompatActivity {

    EditText edtPhone, edtPass;
    TextView txtResetPass;
    Button btnLogin;
    FirebaseFirestore db;
    PreferenceManager preferenceManager ;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AnhXa();

        //Init
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgetPass = new Intent(LoginActivity.this, ForgotPassActivity.class);
                startActivity(forgetPass);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }
    private  void AnhXa(){
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtPass = (EditText) findViewById(R.id.edtPass);
        txtResetPass = (TextView) findViewById(R.id.txtResetPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        back = (ImageView) findViewById(R.id.back_main);
    }
    private void login(){
        if (edtPhone.getText().toString().equals("")) {
            Toast.makeText(LoginActivity.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
        } else if (edtPass.getText().toString().equals("")) {
            Toast.makeText(LoginActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
        }

        db.collection(Constants.KEY_COLLECTION_USERS).document(edtPhone.getText().toString())
                .collection(Constants.KEY_SUB_COLLECTION_PrivateData)
                .document(edtPhone.getText().toString())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    String b = doc.getString(Constants.KEY_Password);
                    String b1 = edtPass.getText().toString().trim();
                    if (Objects.equals(b, b1)) {

                        updateActive(edtPhone.getText().toString());
                        SaveUser(edtPhone.getText().toString());

                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(LoginActivity.this, "Lỗi đăng nhập. Mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void SaveUser(String PhoneNum){

        db.collection(Constants.KEY_COLLECTION_USERS).document(PhoneNum)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                String Phone = PhoneNum.toString().trim();
                if (task.getResult().exists()) {
                    DocumentSnapshot doc = task.getResult();
                    assert doc != null;
                    String name = doc.getString(Constants.KEY_Name);
                    String image = doc.getString(Constants.KEY_Image);
                    List<String> listFriends = (List<String>) doc.get("ListFriends");

                    Set<String> friends = new HashSet<String>();
                    if( listFriends != null)
                    {
                        friends.addAll(listFriends);
                    }else{
                        friends.addAll(Collections.EMPTY_SET);
                    }

                    preferenceManager.putString("Name", name);
                    preferenceManager.putString("Image", image);
                    preferenceManager.putString("PhoneNum", Phone);
                    preferenceManager.putStringSet("ListFriends", friends);

                    Intent home = new Intent(LoginActivity.this, ListChatAndContactActivity.class);
                    startActivity(home);
                }
            }
        });
    }
    private void updateActive(String PhoneNum){
        db.collection(Constants.KEY_COLLECTION_USERS).document(PhoneNum).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> data = new HashMap<>();
                data.put(Constants.KEY_Active, true);
                db.collection(Constants.KEY_COLLECTION_USERS).document(PhoneNum).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
            }
        });
    }

}