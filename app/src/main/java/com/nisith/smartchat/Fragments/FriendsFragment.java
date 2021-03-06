package com.nisith.smartchat.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.nisith.smartchat.Adapters.MyFriendFragmentRecyclerAdapter;
import com.nisith.smartchat.Adapters.MyFriendRequestFragmentRecyclerAdapter;
import com.nisith.smartchat.Constant;
import com.nisith.smartchat.DialogBox.FriendImageClickDialog;
import com.nisith.smartchat.FriendsProfileActivity;
import com.nisith.smartchat.Model.Friend;
import com.nisith.smartchat.R;

public class FriendsFragment extends Fragment implements MyFriendFragmentRecyclerAdapter.OnFriendFragmentViewsClickListener {

    private RecyclerView recyclerView;
    private MyFriendFragmentRecyclerAdapter adapter;
    //Firebase
    private DatabaseReference friendsDatabaseRef;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //This method is called after the onCreate() method is executed of activity i.e. in this case HomeActivity
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        friendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("friends").child(currentUserId);
        setUpRecyclerViewWithAdapter();

    }

    private void setUpRecyclerViewWithAdapter(){
        Query query =friendsDatabaseRef.orderByKey();
        FirebaseRecyclerOptions<Friend> recyclerOptions = new FirebaseRecyclerOptions.Builder<Friend>()
                .setQuery(query, Friend.class)
                .build();
        adapter = new MyFriendFragmentRecyclerAdapter(recyclerOptions, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null){
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null){
            adapter.stopListening();
        }
    }

    @Override
    public void onFriendViewsClick(View view, String friendUid) {
        //called when each row of Friend Fragment is clicked
        switch (view.getId()){
            case R.id.profile_image_view:
                showImageDialog(friendUid);
                break;

            case R.id.root_view:
                Intent intent = new Intent(getContext(), FriendsProfileActivity.class);
                intent.putExtra(Constant.FRIEND_UID, friendUid);
                startActivity(intent);
        }
    }

    private void showImageDialog(String friendUid){
        FriendImageClickDialog dialog = new FriendImageClickDialog(friendUid);
        dialog.show(getActivity().getSupportFragmentManager(),"smart chat");
    }

}