package tech.tanaysinghania.interviewapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUpFragment extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private FirebaseAuth mAuth;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private CheckBox mInterviewer;
    private CheckBox mInterviewee;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mNameField;
    private FirebaseFirestore db;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_signup, container, false);



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getView().findViewById(R.id.createAccount).setOnClickListener(this);
//        getView().findViewById(R.id.signOutButton).setOnClickListener(this);
//        getView().findViewById(R.id.verifyEmailButton).setOnClickListener(this);
//        getView().findViewById(R.id.reloadButton).setOnClickListener(this);
        mStatusTextView = getView().findViewById(R.id.status);
        mDetailTextView = getView().findViewById(R.id.detail);
        mEmailField = getView().findViewById(R.id.signup_email);
        mPasswordField = getView().findViewById(R.id.signup_pass);
        mNameField = getView().findViewById(R.id.signup_name);
        mInterviewee = getView().findViewById(R.id.checkbox_interviewee);
        mInterviewer = getView().findViewById(R.id.checkbox_interviewer);
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setProgressBar(R.id.progressBar);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }


    //[START on_start_check_user]

// [END on_start_check_user]

    private void createAccount(final String email, final String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }


        showProgressBar();

// [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Map<String, Object> data = new HashMap<>();
                            data.put("Name", mNameField.getText().toString());
                            data.put("email", email);
                            data.put("password", password);

                            data.put("Interviewer", mInterviewer.isChecked());
                            data.put("Interviewee", mInterviewee.isChecked());

                            // Add a new document with a generated ID
                            db.collection("USERS")
                                    .add(data)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        hideProgressBar();
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressBar();

// [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
// Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
// If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
// [START_EXCLUDE]
//                            checkForMultiFactorFailure(task.getException());
// [END_EXCLUDE]
                        }

// [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressBar();
// [END_EXCLUDE]
                    }
                });
// [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
// Disable button
//        getView().findViewById(R.id.verifyEmailButton).setEnabled(false);

// Send verification email
// [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
// [START_EXCLUDE]
// Re-enable button
//                        getView().findViewById(R.id.verifyEmailButton).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(getActivity(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
// [END_EXCLUDE]
                    }
                });
// [END send_email_verification]
    }

    private void reload() {
        mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateUI(mAuth.getCurrentUser());
                    Toast.makeText(getActivity(),
                            "Reload successful!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "reload", task.getException());
                    Toast.makeText(getActivity(),
                            "Failed to reload user.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressBar();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

//            getView().findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
//            getView().findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
//            getView().findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);

//            if (user.isEmailVerified()) {
//                getView().findViewById(R.id.verifyEmailButton).setVisibility(View.GONE);
//
//            } else {
//                getView().findViewById(R.id.verifyEmailButton).setVisibility(View.VISIBLE);
//            }
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

//            getView().findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
//            getView().findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
//            getView().findViewById(R.id.signedInButtons).setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.createAccount) {
            Log.d(TAG, "button:pressed");
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
//        } else if (i == R.id.emailSignInButton) {
//            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
//        } else if (i == R.id.signOutButton) {
//            signOut();
//        } else if (i == R.id.verifyEmailButton) {
//            sendEmailVerification();
//        } else if (i == R.id.reloadButton) {
//            reload();
//        }
    }

}
