package tech.tanaysinghania.interviewapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private Button interviewer_add,interviewee_submit;
    private boolean marked_or_not;
    private Spinner spin;
    private ArrayList<DateData> availableDates = new ArrayList<DateData>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_schedule,container,false);
        marked_or_not = false;
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

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        spin = (Spinner) getView().findViewById(R.id.spinner);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                updateUI(currentUser);
            }
        });
        updateUI(currentUser);
        interviewer_add = getView().findViewById(R.id.interviewerSubmit);
        interviewer_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.interviewerSubmit){
                    String date = selectedDate.getDay()+"-"+selectedDate.getMonth()+"-"+selectedDate.getYear();
                    int time = seekBar.getProgress();
                    date = date + "-"+time;
                    if(mAuth.getCurrentUser()!=null) {
                        final DocumentReference ref = db.collection("availableSlots").document(date);
                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        ref.update("emails", FieldValue.arrayUnion(mAuth.getCurrentUser().getEmail().toString()));
                                        Log.d(TAG, "Document exists!");
                                    } else {
                                        Map<String,Object> map = new HashMap<>();
                                        List<String> ems = new ArrayList<>();
                                        ems.add(mAuth.getCurrentUser().getEmail().toString());
                                        map.put("emails",ems);
                                        map.put("monthyear",""+selectedDate.getMonth()+""+selectedDate.getYear());
                                        ref.set(map);
                                        Log.d(TAG, "Document does not exist!");
                                    }
                                } else {
                                    Log.d(TAG, "Failed with: ", task.getException());
                                }
                            }
                        });


                    }
                }
            }
        });
        interviewee_submit = getView().findViewById(R.id.intervieweeSubmit);
        interviewee_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.intervieweeSubmit){
                    String date = selectedDate.getDay()+"-"+selectedDate.getMonth()+"-"+selectedDate.getYear();
                    Log.d("TAG",""+spin.getSelectedItem());
                    final Map<String,Object> map = new HashMap<>();
                    int time = Integer.parseInt(spin.getSelectedItem()+"");
                    date = date + "-"+time;
                    final String finalDate = date;
                    if(mAuth.getCurrentUser()!=null) {
                        final DocumentReference r = db.collection("availableSlots").document(date);
                        r.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                List<String> emails = (List<String>) documentSnapshot.get("emails");
                                map.put("erEmail",emails.get(0));
                                emails.remove(0);
                                if(emails.size()==0){
                                    db.collection("availableSlots").document(finalDate)
                                            .delete();
                                }else {
                                    Map<String, Object> m2 = new HashMap<>();
                                    m2.put("emails", emails);
                                    r.set(m2);
                                }

                                //ADD in eeSlots
                                final DocumentReference ref = db.collection("eeSlots").document(mAuth.getCurrentUser().getEmail());
                                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                ref.update("erEmail", FieldValue.arrayUnion(map.get("erEmail").toString()));
                                                ref.update("time",FieldValue.arrayUnion(finalDate));
                                                Log.d(TAG, "Document exists!");
                                            } else {
                                                List<String> ems = new ArrayList<>();
                                                ems.add(map.get("erEmail").toString());
                                                List<String> times = new ArrayList<>();
                                                times.add(finalDate);
                                                final Map<String,Object> m2 = new HashMap<>();
                                                m2.put("erEmail",ems);
                                                m2.put("time",times);
                                                ref.set(m2);
                                                Log.d(TAG, "Document does not exist!");
                                            }
                                        } else {
                                            Log.d(TAG, "Failed with: ", task.getException());
                                        }
                                    }
                                });

                                //ADD in erSlots
                                final DocumentReference ref2 = db.collection("erSlots").document(map.get("erEmail").toString());
                                ref2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                ref2.update("eeEmail", FieldValue.arrayUnion(mAuth.getCurrentUser().getEmail().toString()));
                                                ref2.update("time",FieldValue.arrayUnion(finalDate));
                                                Log.d(TAG, "Document exists!");
                                            } else {
                                                List<String> ems = new ArrayList<>();
                                                ems.add(mAuth.getCurrentUser().getEmail().toString());
                                                List<String> times = new ArrayList<>();
                                                times.add(finalDate);
                                                final Map<String,Object> m2 = new HashMap<>();
                                                m2.put("eeEmail",ems);
                                                m2.put("time",times);
                                                ref2.set(m2);
                                                Log.d(TAG, "Document does not exist!");
                                            }
                                        } else {
                                            Log.d(TAG, "Failed with: ", task.getException());
                                        }
                                    }
                                });


                            }
                        });
                    }
                }
            }
        });


    }

    private void updateUI(final FirebaseUser currentUser) {
        if(currentUser!=null) {
            String email = currentUser.getEmail().toString();
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
        seekBar.setVisibility(View.VISIBLE);
        interviewer_add.setVisibility(View.VISIBLE);
    }

    private void updateUIInterviewee(FirebaseUser currentUser) {
        //mark available slots
        spin.setVisibility(View.VISIBLE);
        interviewee_submit.setVisibility(View.VISIBLE);
        if(!marked_or_not) {

            db.collection("availableSlots").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String[] date = document.getId().toString().split("-");
                                    int year = Integer.parseInt(date[2]);
                                    int mon = Integer.parseInt(date[1]);
                                    int day = Integer.parseInt(date[0]);
                                    DateData thisDate = new DateData(year,mon,day);
                                    thisDate.setHour(Integer.parseInt(date[3]));
                                    if(!availableDates.isEmpty() && !marked_or_not)
                                        availableDates = new ArrayList<DateData>();
                                    availableDates.add(thisDate);
                                    mCalendarView.markDate(thisDate);

                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            marked_or_not = true;
        }
        ArrayList<String> times = new ArrayList<>();
        for(DateData i : availableDates){
            if(i.getYear()==selectedDate.getYear() && i.getMonth()==selectedDate.getMonth() && i.getDay()==selectedDate.getDay()){
                //show available time slots
                times.add(i.getHour()+"");
            }
        }
        if(times!=null && times.size()!=0) {
            ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, times.toArray(new String[times.size()]));
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(aa);
        }
    }

}
