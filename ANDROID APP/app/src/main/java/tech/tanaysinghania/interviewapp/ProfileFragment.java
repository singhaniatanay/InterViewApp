package tech.tanaysinghania.interviewapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileFragment extends BaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView mName, mEmail, mInterviewee,mInterviewer,mTitle;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setProgressBar(R.id.progressBar_profile);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // [END initialize_auth]
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mName = getView().findViewById(R.id.profile_name);
        mEmail = getView().findViewById(R.id.profile_email);
        mInterviewee = getView().findViewById(R.id.profile_interviewee);
        mInterviewer = getView().findViewById(R.id.profile_interviewer);
        mTitle = getView().findViewById(R.id.profileTitle);

        ScheduledData[] myListData = new ScheduledData[] {
                new ScheduledData("Email", "vsv",""),
                new ScheduledData("Blah", "sfadfaff",""),
                new ScheduledData("BLue", "sfaaff",""),
                new ScheduledData("asd", "sffaff",""),
                new ScheduledData("asssd", "sfaff",""),
                new ScheduledData("dadsad", "sffaf",""),
                new ScheduledData("dadadasef", "sfafvsddfaff","")
        };

        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        ScheduledAdapter adapter = new ScheduledAdapter(myListData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);




        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null) {
            String email = currentUser.getEmail().toString();
            mEmail.setText(email);
            db.collection("USERS").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        mName.setText(doc.get("Name").toString());
                        if ((boolean) doc.get("Interviewee")) {
                            mInterviewee.setVisibility(View.VISIBLE);
                        }
                        if ((boolean) doc.get("Interviewer")) {
                            mInterviewer.setVisibility(View.VISIBLE);
                        }

//                    mInterviewee.setText(""+doc.get("Interviewee"));
//                    mInterviewee.setV(doc.get("Name").toString());
                    } else {
                        Log.w("TAG", "Error getting documents.", task.getException());
                    }
                }
            });

        }else{
            mTitle.setText("Sign In first !!!!");
        }
    }
}