package com.abort.studentcollege.ui.timetable;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.abort.studentcollege.Common.Common;
import com.abort.studentcollege.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TimeTableFragment extends Fragment {

    private TimeTableViewModel mViewModel;
    Unbinder unbinder;
    AlertDialog dialog;
    @BindView(R.id.time_table)
    ImageView time_table;
    @BindView(R.id.btn_timetaable)
    Button btn_timetaable;
    public static TimeTableFragment newInstance() {
        return new TimeTableFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.time_table_fragment, container, false);
        unbinder = ButterKnife.bind(this,root);

        FirebaseDatabase.getInstance().getReference(Common.TIMETABLEREF)
                .child(Common.currentUserModel.getClassid())
                .child(Common.TIMETABLEREF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue()!=null)
                            Glide.with(getContext()).load(snapshot.getValue().toString())
                                    .into(time_table);
                        else
                            Glide.with(getContext()).load(R.drawable.timetable)
                                    .into(time_table);
                        btn_timetaable.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                downloadImageNew(Common.TIMETABLEREF,snapshot.getValue().toString());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return root;
    }
    private void downloadImageNew(String filename, String downloadUrlOfImage){
        try{
            DownloadManager dm = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename + ".jpg");
            dm.enqueue(request);
            Toast.makeText(getContext(), "Image download started.", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getContext(), "Image download failed.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TimeTableViewModel.class);
        // TODO: Use the ViewModel
    }

}