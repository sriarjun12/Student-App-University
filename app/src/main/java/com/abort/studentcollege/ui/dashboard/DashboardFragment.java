package com.abort.studentcollege.ui.dashboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.abort.studentcollege.Common.Common;
import com.abort.studentcollege.MainActivity;
import com.abort.studentcollege.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    Unbinder unbinder;
    AlertDialog dialog;
    @BindView(R.id.profile)
    ImageView profile;
    @BindView(R.id.details_name)
    TextView details_name;
    @BindView(R.id.details_email)
    TextView details_email;
    @BindView(R.id.details_mobile)
    TextView details_mobile;
    @BindView(R.id.details_roll)
    TextView details_roll;
    @BindView(R.id.details_department)
    TextView details_department;
    @BindView(R.id.details_class)
    TextView details_class;
    @BindView(R.id.details_parent_name)
    TextView details_parent_name;
    @BindView(R.id.details_parent_mobile)
    TextView details_parent_mobile;
    @BindView(R.id.details_address)
    TextView details_address;
    @BindView(R.id.btn_change_password)
    Button btn_change_password;
    @BindView(R.id.btn_edt)
    Button btn_edt;
    @BindView(R.id.signout)
    Button signout;
    private Uri imageUrl=null;
    FirebaseStorage storage;
    StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1234;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        unbinder = ButterKnife.bind(this,root);
        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        getProfiledata();
        btn_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sowEditDialog();
            }
        });
        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changepasswordDialog();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignoutdialog();
            }
        });
        return root;
    }
    private void getProfiledata() {
        if(Common.currentUserModel.getName()!=null)
            details_name.setText(Common.currentUserModel.getName());
        if(Common.currentUserModel.getEmail()!=null)
            details_email.setText("Email : "+Common.currentUserModel.getEmail());
        if(Common.currentUserModel.getMobile()!=null)
            details_mobile.setText("Mobile : "+Common.currentUserModel.getMobile());
        if(Common.currentUserModel.getAddress()!=null)
            details_address.setText("Address : "+Common.currentUserModel.getAddress());
        if(Common.currentUserModel.getClassid()!=null)
            details_class.setText("Class ID : "+Common.currentUserModel.getClassid());
        if(Common.currentUserModel.getDepartment()!=null)
            details_department.setText("Department : "+ Common.currentUserModel.getDepartment());
        if(Common.currentUserModel.getRollno()!=null)
            details_roll.setText("Roll No : "+Common.currentUserModel.getRollno());
        if(Common.currentUserModel.getParentname()!=null)
            details_parent_name.setText("Parent Name : "+ Common.currentUserModel.getParentname());
        if(Common.currentUserModel.getParentmobile()!=null)
            details_parent_mobile.setText("Parent Number : "+Common.currentUserModel.getParentmobile());


        if(Common.currentUserModel.getProfile()!=null)
            Glide.with(getContext()).load(Common.currentUserModel.getProfile())
                    .into(profile);
        else
            Glide.with(getContext()).load(R.drawable.student)
                    .into(profile);

    }
    private void sowEditDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update Faculty");
        builder.setMessage("Please enter Information");
        View itemView =LayoutInflater.from(getContext()).inflate(R.layout.edit_profile_layout,null);
        EditText edt_department=(EditText)itemView.findViewById(R.id.edt_department);
        EditText edt_address=(EditText)itemView.findViewById(R.id.edt_address);
        EditText edt_parent_name=(EditText)itemView.findViewById(R.id.edt_parent_name);
        EditText edt_parent_mobile=(EditText)itemView.findViewById(R.id.edt_parent_mobile);

        if(Common.currentUserModel.getClassid()!=null) {
            edt_parent_mobile.setText(Common.currentUserModel.getParentmobile());
            edt_parent_name.setText(Common.currentUserModel.getParentname());
            edt_department.setText(Common.currentUserModel.getDepartment());
            edt_address.setText(Common.currentUserModel.getAddress());
        }
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialog.dismiss());
        builder.setPositiveButton("UPDATE", (dialogInterface, i) -> {

            if(!TextUtils.isEmpty(edt_parent_mobile.getText().toString())&&!TextUtils.isEmpty(edt_parent_name.getText().toString())&&!TextUtils.isEmpty(edt_department.getText().toString())&&!TextUtils.isEmpty(edt_address.getText().toString())){

                Common.currentUserModel.setParentmobile(edt_parent_mobile.getText().toString());
                Common.currentUserModel.setParentname(edt_parent_name.getText().toString());
                Common.currentUserModel.setDepartment(edt_department.getText().toString());
                Common.currentUserModel.setAddress(edt_address.getText().toString());
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("parentmobile",edt_parent_mobile.getText().toString());
                updateData.put("parentname",edt_parent_name.getText().toString());
                updateData.put("address",edt_address.getText().toString());
                updateData.put("department",edt_department.getText().toString());

                dialog.show();
                FirebaseDatabase.getInstance().getReference(Common.STUDENTREF)
                        .child(Common.currentUserModel.getClassid())
                        .child(Common.currentUserModel.getId())
                        .updateChildren(updateData)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                getProfiledata();
                                dialog.dismiss();
                            }
                        });
            }

            else
                Toast.makeText(getContext(), "Enter all Details", Toast.LENGTH_SHORT).show();
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog=builder.create();
        builder.show();
    }
    private void changepasswordDialog()  {
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Change Password");
        View itemView =LayoutInflater.from(getContext()).inflate(R.layout.single_text_layout,null);
        EditText edt_password=(EditText)itemView.findViewById(R.id.edt_password);
        EditText edt_password_cfn=(EditText)itemView.findViewById(R.id.edt_password_cfn);
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialog.dismiss());
        builder.setPositiveButton("UPDATE", (dialogInterface, i) -> {
            if(edt_password.getText().toString().equals(edt_password_cfn.getText().toString())){
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("password",edt_password.getText().toString());
                FirebaseDatabase.getInstance().getReference(Common.STUDENTREF)
                        .child(Common.currentUserModel.getClassid())
                        .child(Common.currentUserModel.getId())
                        .updateChildren(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Password Change Success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                Toast.makeText(getContext(), "Password Mismatch", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog=builder.create();
        builder.show();

    }
    private void updateprofileimage() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());
        //Setting message manually and performing action on button click
        builder.setTitle("Profile").setMessage("Sure want to Update Profile") .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogg, int id) {
                        dialog.show();
                        if(imageUrl!=null){
                            dialog.setMessage("Uploading ...");
                            dialog.show();
                            String unique_name= UUID.randomUUID().toString();
                            StorageReference imageFloder=storageReference.child("image/"+unique_name);
                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUrl);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                            byte[] data = baos.toByteArray();

                            imageFloder.putBytes(data)
                                    .addOnFailureListener(e -> {
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }).addOnCompleteListener(task -> {
                                dialog.dismiss();
                                imageFloder.getDownloadUrl().addOnSuccessListener(uri -> {
                                    Map<String, Object> updateData = new HashMap<>();
                                    updateData.put("profile",uri.toString());
                                    Common.currentUserModel.setProfile(uri.toString());
                                    FirebaseDatabase.getInstance().getReference(Common.STUDENTREF)
                                            .child(Common.currentUserModel.getClassid())
                                            .child(Common.currentUserModel.getId())
                                            .updateChildren(updateData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getContext(), "Update Success", Toast.LENGTH_SHORT).show();
                                                    getProfiledata();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                            getProfiledata();
                                        }
                                    });
                                });

                            }).addOnProgressListener(taskSnapshot -> {
                                int progress=(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                                dialog.setMessage(new StringBuilder("Uploading : ").append(progress).append("%"));

                            });
                        }
                        else{
                            Toast.makeText(getContext(), "Select Image", Toast.LENGTH_SHORT).show();
                            getProfiledata();
                            return;
                            //addCategory(categoryModel);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogg, int id) {
                        //  Action for 'NO' Button
                        dialogg.dismiss();
                        getProfiledata();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void showSignoutdialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());
        //Setting message manually and performing action on button click
        builder.setTitle("Signout").setMessage("Sure want to Signout") .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogg, int id) {
                        dialog.show();
                        FirebaseDatabase.getInstance().getReference(Common.STUDENTREF)
                                .child(Common.currentUserModel.getClassid())
                                .child(Common.currentUserModel.getId())
                                .child("uid")
                                .setValue(null)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        dialogg.dismiss();
                                        Toast.makeText(getContext(), "Signout Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                dialogg.dismiss();

                                Toast.makeText(getContext(), "Signout Success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getContext(), MainActivity.class));

                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogg, int id) {
                        //  Action for 'NO' Button
                        dialogg.dismiss();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST &&  resultCode== Activity.RESULT_OK){
            if (data != null && data.getData() != null ){
                imageUrl=data.getData();
                updateprofileimage();
            }
        }
    }
}