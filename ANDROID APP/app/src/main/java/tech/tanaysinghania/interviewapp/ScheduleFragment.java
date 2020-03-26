package tech.tanaysinghania.interviewapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ScheduleFragment extends BaseActivity {



    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView mTitle,mText,mSeekbarText,mDateText;
    private DateData selectedDate;
    private MCalendarView mCalendarView;
    private SeekBar seekBar;
    private Button interviewer_add;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_schedule,container,false);

        mSeekbarText = rootView.findViewById(R.id.seekBar_selected);
        seekBar = rootView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(    SeekBar seekBar,    int progress,    boolean fromUser){
                int temp = progress+1;
                mSeekbarText.setText(""+progress+"00 hrs - "+temp+"00 hrs");

            }
            public void onStartTrackingTouch(    SeekBar seekBar){
            }
            public void onStopTrackingTouch(    SeekBar seekBar){
            }
        }
);
        return rootView;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setProgressBar(R.id.progressBar_schedule);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // [END initialize_auth]
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mTitle = getView().findViewById(R.id.title_schedule);
        mText = getView().findViewById(R.id.text_schedule);
        mSeekbarText = getView().findViewById(R.id.seekBar_selected);
        mDateText = getView().findViewById(R.id.date_selected);
        mCalendarView = ((MCalendarView) getView().findViewById(R.id.calendarView));

        mCalendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                mDateText.setText(""+date.getDay()+"-"+date.getMonthString());
                selectedDate = date;

            }
        });
        interviewer_add = getView().findViewById(R.id.interviewerSubmit);
        interviewer_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.interviewerSubmit){
                    String date = selectedDate.getDayString()+"-"+selectedDate.getMonthString()+"-"+selectedDate.getYear();
                    int time = seekBar.getProgress();
                    date = date + "-"+time;
                    if(mAuth.getCurrentUser()!=null) {
                        List<String> emailss = new ArrayList<>();
                        emailss.add(mAuth.getCurrentUser().getEmail().toString());
                        Map<String, Object> map = new HashMap<>();
                        map.put("count", 1);
                        map.put("emails", emailss);
                        db.collection("availableSlots").document(date).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot added with ID: ");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
                    }
                }
            }
        });
        FirebaseUser currentUser = mAuth.getCurrentUser();


        updateUI(currentUser);

    }

    private void updateUI(final FirebaseUser currentUser) {
        if(currentUser!=null) {
            String email = currentUser.getEmail().toString();
//            mEmail.setText(email);
            db.collection("USERS").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        Log.w("TAG", "USER"+ doc);

//                        mName.setText(doc.get("Name").toString());
                        if (doc.get("Interviewee")!=null && (boolean) doc.get("Interviewee")) {
                            mText.setText("So you are a Interviewee");
                            updateUIInterviewee(currentUser);
                        }
                        if (doc.get("Interviewer")!=null && (boolean) doc.get("Interviewer")) {
                            mText.setText("So you are a Interviewer");
                            updateUIInterviewer(currentUser);

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

    private void updateUIInterviewer(FirebaseUser currentUser) {


    }

    private void updateUIInterviewee(FirebaseUser currentUser) {

    }

}
