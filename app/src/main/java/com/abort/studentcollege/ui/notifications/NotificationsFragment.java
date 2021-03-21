package com.abort.studentcollege.ui.notifications;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.abort.studentcollege.Common.Common;
import com.abort.studentcollege.Model.FacultyModel;
import com.abort.studentcollege.Model.NotesModel;
import com.abort.studentcollege.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

import static android.Manifest.permission.CALL_PHONE;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
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
    @BindView(R.id.details_tutor)
    TextView details_tutor;
    @BindView(R.id.details_designation)
    TextView details_designation;
    @BindView(R.id.details_class)
    TextView details_class;
    @BindView(R.id.details_subject)
    TextView details_subject;
    @BindView(R.id.details_address)
    TextView details_address;
    @BindView(R.id.call_tutor)
    Button call_tutor;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        unbinder = ButterKnife.bind(this,root);
        getProfiledata();

        return root;
    }
    private void getProfiledata() {

        List<FacultyModel> tempList = new ArrayList<>();
        DatabaseReference staffref = FirebaseDatabase.getInstance()
                .getReference(Common.FACULTYREF);
        final long[] itemCount = {1};
        staffref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot itemSnapShot:dataSnapshot.getChildren())
                {
                    FacultyModel facultyModel = itemSnapShot.getValue(FacultyModel.class);
                    if(facultyModel.getClassid().equals(Common.currentUserModel.getClassid())) {
                        tempList.add(facultyModel);
                        addFacultyData(facultyModel);
                        break;
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



    }

    private void addFacultyData(FacultyModel facultyModel) {
        Common.tutormodel=facultyModel;
        call_tutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+Common.tutormodel.getMobile()));//change the number
                if (ContextCompat.checkSelfPermission(getContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                } else {
                    requestPermissions(new String[]{CALL_PHONE}, 1);
                }
            }
        });
        if(Common.tutormodel.getName()!=null)
            details_name.setText(Common.tutormodel.getName());
        if(Common.tutormodel.getEmail()!=null)
            details_email.setText("Email : "+Common.tutormodel.getEmail());
        if(Common.tutormodel.getMobile()!=null)
            details_mobile.setText("Mobile : "+Common.tutormodel.getMobile());
        if(Common.tutormodel.getAddress()!=null)
            details_address.setText("Address : "+Common.tutormodel.getAddress());
        if(Common.tutormodel.getClassid()!=null)
            details_class.setText("Class ID : "+Common.tutormodel.getClassid());
        if(Common.tutormodel.getDesignation()!=null)
            details_designation.setText("Designation : "+ Common.tutormodel.getDesignation());
        if(Common.tutormodel.getDepartment()!=null)
            details_subject.setText("Subject : "+Common.tutormodel.getDepartment());
        if(Common.tutormodel.isTutor())
            details_tutor.setText("Tutor : Yes");
        else
            details_tutor.setText("Tutor : No");

        if(Common.tutormodel.getProfile()!=null)
            Glide.with(getContext()).load(Common.tutormodel.getProfile())
                    .into(profile);
        else
            Glide.with(getContext()).load(R.drawable.teacher)
                    .into(profile);
    }
}