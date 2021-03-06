package tech.tanaysinghania.interviewapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.text.TextUtils;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


public class HomeFragment extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private FirebaseAuth mAuth;
    private TextView mStatusTextView;
    private TextView mDetailTextView;

    private EditText mEmailField;
    private EditText mPasswordField;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getView().findViewById(R.id.emailSignInButton).setOnClickListener(this);
        getView().findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        getView().findViewById(R.id.signOutButton).setOnClickListener(this);
        getView().findViewById(R.id.verifyEmailButton).setOnClickListener(this);
        getView().findViewById(R.id.reloadButton).setOnClickListener(this);
        mStatusTextView = getView().findViewById(R.id.status);
        mDetailTextView = getView().findViewById(R.id.detail);
        mEmailField = getView().findViewById(R.id.fieldEmail);
        mPasswordField = getView().findViewById(R.id.fieldPassword);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setProgressBar(R.id.progressBar);

        // Views


        // Buttons


        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }


    //[START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
// [END on_start_check_user]

    private void createAccount(String email, String password) {
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
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
// If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

// [START_EXCLUDE]
                        hideProgressBar();
// [END_EXCLUDE]
                    }
                });
// [END create_user_with_email]
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
        getView().findViewById(R.id.verifyEmailButton).setEnabled(false);

// Send verification email
// [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
// [START_EXCLUDE]
// Re-enable button
                       getView().findViewById(R.id.verifyEmailButton).setEnabled(true);

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

            getView().findViewById(R.id.emailPasswordButtons1).setVisibility(View.GONE);
            getView().findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
            getView().findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);

            if (user.isEmailVerified()) {
                getView().findViewById(R.id.verifyEmailButton).setVisibility(View.GONE);

            } else {
                getView().findViewById(R.id.verifyEmailButton).setVisibility(View.VISIBLE);
            }

        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            getView().findViewById(R.id.emailPasswordButtons1).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.signedInButtons).setVisibility(View.GONE);
        }
    }

//    private void checkForMultiFactorFailure(Exception e) {
//        // Multi-factor authentication with SMS is currently only available for
//        // Google Cloud Identity Platform projects. For more information:
//        // https://cloud.google.com/identity-platform/docs/android/mfa
//        if (e instanceof FirebaseAuthMultiFactorException) {
//            Log.w(TAG, "multiFactorFailure", e);
//            Intent intent = new Intent();
//            MultiFactorResolver resolver = ((FirebaseAuthMultiFactorException) e).getResolver();
//            intent.putExtra("EXTRA_MFA_RESOLVER", resolver);
//            setResult(MultiFactorActivity.RESULT_NEEDS_MFA_SIGN_IN, intent);
//            finish();
//        }
//    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
//            Fragment selectedFragment = new SignUpFragment();
            SignUpFragment nextFrag= new SignUpFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                    .addToBackStack(null)
                    .commit();
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    selectedFragment).commit();
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.emailSignInButton) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.signOutButton) {
            signOut();
        } else if (i == R.id.verifyEmailButton) {
            sendEmailVerification();
        } else if (i == R.id.reloadButton) {
            reload();
        }
    }


}
