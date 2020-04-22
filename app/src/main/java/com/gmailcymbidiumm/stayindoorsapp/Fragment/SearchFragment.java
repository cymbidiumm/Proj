package com.gmailcymbidiumm.stayindoorsapp.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.gmailcymbidiumm.stayindoorsapp.Adapter.UserAdapter;
import com.gmailcymbidiumm.stayindoorsapp.Model.User;
import com.gmailcymbidiumm.stayindoorsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    EditText search_bar;

    private DatabaseReference usersDb;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search,container,false);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        usersDb = FirebaseDatabase.getInstance().getReference("users");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search_bar = view.findViewById(R.id.search_bar);

        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(),mUsers);
        recyclerView.setAdapter(userAdapter);

        readUsers();
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return view;
    }

    private void searchUsers(final String s){

        Query query = usersDb.orderByValue();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getKey() != null && snapshot.getValue() instanceof Map) {
                        Map<String, String> map = (HashMap<String, String>) snapshot.getValue();
                        String userName = map.get("fName");
                        if(userName != null && userName.startsWith(s)) {
                            constructUserObjectFromSnapshot(map);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void constructUserObjectFromSnapshot(Map<String, String> userH) {
        if(userH == null) return;
        String fName = userH.get("fName");
        String email = userH.get("email");
        String imageUrl = userH.get("imageurl");
        User user = new User("random", fName, imageUrl, email);
        mUsers.add(user);
    }

    private void readUsers(){

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(search_bar.getText().toString().equals("")){
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        mUsers.add(user);
                    }

//                    userAdapter.notifyDataSetChanged();
                    userAdapter = new UserAdapter(getContext(),mUsers);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }


}
