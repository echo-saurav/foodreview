package com.example.productreview.userManagement;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.productreview.Helper;
import com.example.productreview.MainActivity;
import com.example.productreview.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpFragment extends Fragment implements View.OnClickListener {
    EditText name, email, password, password2;
    Button signUpButton;
    ProgressDialog progressDialog;
    UserMngInterface userMngInterface;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = view.findViewById(R.id.name_sign_up);
        email = view.findViewById(R.id.email_sign_up);
        password = view.findViewById(R.id.password_sign_up);
        password2 = view.findViewById(R.id.password_two_sign_up);
        signUpButton = view.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(this);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading, Please wait");
        progressDialog.setCancelable(false);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        userMngInterface = (UserManagement) context;
        this.context = context;

    }

    @Override
    public void onClick(View v) {
        signUpButton();
    }

    void signUpButton() {
        String name = this.name.getText().toString().trim();
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        String password2 = this.password2.getText().toString().trim();

        if (!name.isEmpty()
                && !password.isEmpty()
                && !password2.isEmpty()
                && !email.isEmpty()) {
            //if password don't match
            if (!password.equals(password2)) {
                Helper.customToast(getActivity(), "Password didn't match");
            } else {
                //if password match
                signUp(name, email, password);
            }

        }
    }

    public void signUp(final String name, final String email, String password) {
        progressDialog.show();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            setProfile(name, email);
                        } else {
                            signUpFailed(task);
                        }
                    }
                });
    }

    void setProfile(final String name, final String email) {
        // Sign in is successful
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            setUserToDatabase(name, email);
                        } else {
                            signUpFailed(task);
                        }

                    }
                });

    }

    void setUserToDatabase(String name, String email) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("DataType",Helper.USER);
        user.put("name", name);
        user.put("email", email);

        user.put("searchName", name.toLowerCase());
        user.put("searchEmail", email.toLowerCase());
        user.put("timeStamp", FieldValue.serverTimestamp());

        addSearchTerm(name,email,uid);

        DocumentReference documentReference = db.collection("users").document(uid);
        documentReference.set(user)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        signUpSuccessful();
                    }else {
                        Helper.customToast(getActivity(),"Sign Up Failed");
                    }
                });

    }

    void addSearchTerm(String userName,String email,String uid){
        DatabaseReference userNameSearchTag= FirebaseDatabase.getInstance().getReference("searchTerms").push();

        userNameSearchTag.child("category").setValue("userName");
        userNameSearchTag.child("tag").setValue(userName.toLowerCase());
        userNameSearchTag.child("id").setValue(uid);

        DatabaseReference emailSearchTag= FirebaseDatabase.getInstance().getReference("searchTerms").push();

        emailSearchTag.child("category").setValue("userEmail");
        emailSearchTag.child("tag").setValue(email.toLowerCase());
        emailSearchTag.child("id").setValue(uid);

    }

    void signUpFailed(Task task) {
        if (task.getException() != null) {
            String error = task.getException().getMessage();
            Helper.customToast(getActivity(), error);
        }
    }
    void signUpSuccessful(){
        progressDialog.dismiss();
        Helper.customToast(getActivity(),"Sign Up successful");
        context.startActivity(new Intent(context, MainActivity.class));
        getActivity().finish();

    }

}

