package com.example.myaudiobook.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myaudiobook.HomepageNew;
import com.example.myaudiobook.databinding.FragmentInfoLayoutBinding;

public class InfoFragment extends Fragment {

    private FragmentInfoLayoutBinding binding = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInfoLayoutBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View mView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(mView, savedInstanceState);

        binding.ivBack.setOnClickListener(view->{
            try{
                Activity activity = getActivity();
                if(activity instanceof HomepageNew){
                    ((HomepageNew)activity).takeToMainPage();
                }
            }catch (Exception ignored){}
        });
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if(activity instanceof HomepageNew){
            try{
                //((HomepageNew) activity).setDataPassListener(3,null,null,null,this);
            }catch (Exception ignored){}
        }
    }

}
