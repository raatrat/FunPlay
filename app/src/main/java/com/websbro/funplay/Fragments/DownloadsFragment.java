package com.websbro.funplay.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.websbro.funplay.Adapter.DownloadListAdapter;
import com.websbro.funplay.EpisodeDetails;
import com.websbro.funplay.R;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class DownloadsFragment extends Fragment {

    Context context;
    ArrayList<EpisodeDetails> downloadedFiles;
    DownloadListAdapter downloadListAdapter;

    ListView downloadList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.downloads_fragment,container,false);
        context = getActivity();
        downloadList = view.findViewById(R.id.downloaded_shows);
        downloadedFiles = new ArrayList<>();

        getFiles();




        return view;
    }

    public void getFiles(){
        File downloadFolder = context.getFilesDir();
        File[] files = downloadFolder.listFiles();

        downloadedFiles.clear();

        for (int i = 0; i < files.length; i++) {
            File currFile = files[i];
            EpisodeDetails thisFile = new EpisodeDetails(currFile.getName(),null,currFile.getAbsolutePath(),null);
            downloadedFiles.add(thisFile);


            System.out.println(currFile.getAbsolutePath());


        }

        setAdapter();
    }

    public void setAdapter(){
        downloadListAdapter = new DownloadListAdapter(context,downloadedFiles);
        downloadList.setAdapter(downloadListAdapter);
    }
}