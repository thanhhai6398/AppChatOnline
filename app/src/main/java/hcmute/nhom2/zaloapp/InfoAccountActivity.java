package hcmute.nhom2.zaloapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.nhom2.zaloapp.utilities.Constants;
import hcmute.nhom2.zaloapp.utilities.PreferenceManager;

public class InfoAccountActivity extends AppCompatActivity {
    Button btnUpdateInfo;
    TextView txtName, txtGender, txtBirth, txtPhone;
    CircleImageView imgAvt;
    ImageView imgCoverImage, back;
    FirebaseFirestore db;
    FirebaseStorage storage;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_account);

        AnhXa();

        //Init Firebase
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        btnUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent update = new Intent(InfoAccountActivity.this, UpdateAccountActivity.class);
                startActivity(update);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        String PhoneNum = preferenceManager.getString("PhoneNum");

        db.collection(Constants.KEY_COLLECTION_USERS).document(PhoneNum)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.getResult().exists()) {
                    DocumentSnapshot doc = task.getResult();

                    assert doc != null;
                    String name = doc.getString(Constants.KEY_Name);
                    String gender = doc.getString(Constants.KEY_Gender);
                    String birth = doc.getString(Constants.KEY_Birth);
                    String image = doc.getString(Constants.KEY_Image);
                    String coverImage = doc.getString(Constants.KEY_CoverImage);

                    loadImage(image, imgAvt);
                    loadImage(coverImage, imgCoverImage);
                    txtName.setText(name);
                    txtBirth.setText(birth);
                    if(gender.equals("Female")){
                        txtGender.setText("Nữ");
                    }else{
                        txtGender.setText("Nam");
                    }

                    txtPhone.setText(PhoneNum);
                }else{
                    Toast.makeText(InfoAccountActivity.this, "Không tồn tại thông tin", Toast.LENGTH_SHORT).show();
                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    public  void AnhXa(){
        btnUpdateInfo = findViewById(R.id.btnUpdateInfo);
        txtName = findViewById(R.id.txtName);
        txtGender = findViewById(R.id.txtGender);
        txtBirth = findViewById(R.id.txtBirth);
        txtPhone = findViewById(R.id.txtPhone);
        imgAvt = findViewById(R.id.imgAvt);
        imgCoverImage = findViewById(R.id.imgCoverImage);
        back = findViewById(R.id.back_setting1);
    }

    private void loadImage(String imageName, ImageView imageView){
        StorageReference storageReference = storage.getReference()
                .child(Constants.KEY_COLLECTION_USERS)
                .child(Constants.KEY_STORAGE_FOLDER_UserImages)
                .child(imageName);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                final String downloadUrl = uri.toString();
                Glide.with(getApplicationContext()).load(downloadUrl).into(imageView);
            }
        });
    }
}
