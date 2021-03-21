package com.abort.studentcollege.ui.notes;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abort.studentcollege.Common.Common;
import com.abort.studentcollege.Model.NotesModel;
import com.abort.studentcollege.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NotesFragment extends Fragment {

    private NotesViewModel mViewModel;
    Unbinder unbinder;
    @BindView(R.id.edt_title)
    EditText edt_title;
    @BindView(R.id.edt_content)
    EditText edt_content;
    @BindView(R.id.update_note)
    Button update_note;
    public static NotesFragment newInstance() {
        return new NotesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.notes_fragment, container, false);
        unbinder = ButterKnife.bind(this,root);
        loadNotes();


        update_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(edt_title.getText().toString())&&!TextUtils.isEmpty(edt_content.getText().toString())) {
                    NotesModel notesModel = new NotesModel();
                    notesModel.setTitle(edt_title.getText().toString());
                    notesModel.setContent(edt_content.getText().toString());
                    updateNotes(notesModel);
                }
                else{
                    Toast.makeText(getContext(), "Enter Title And Content", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NotesViewModel.class);
        // TODO: Use the ViewModel
    }
    private void loadNotes() {
        FirebaseDatabase.getInstance().getReference(Common.STUDENTREF)
                .child(Common.currentUserModel.getClassid())
                .child(Common.currentUserModel.getId())
                .child(Common.NOTES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        NotesModel notesModel=snapshot.getValue(NotesModel.class);

                        if (notesModel!=null)
                            edt_title.setText(notesModel.getTitle());
                        if (notesModel!=null)
                            edt_content.setText(notesModel.getContent());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateNotes(NotesModel notesModel) {
        FirebaseDatabase.getInstance().getReference(Common.STUDENTREF)
                .child(Common.currentUserModel.getClassid())
                .child(Common.currentUserModel.getId())
                .child(Common.NOTES)
                .setValue(notesModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Notes Updated", Toast.LENGTH_SHORT).show();
                        loadNotes();
                    }
                });
    }
}