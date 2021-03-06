package com.websbro.funplay.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.websbro.funplay.Adapter.DownloadListAdapter;
import com.websbro.funplay.C;
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

    TextView downloadTextInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.downloads_fragment,container,false);
        context = getActivity();
        downloadList = view.findViewById(R.id.downloaded_shows);
        downloadedFiles = new ArrayList<>();

        downloadTextInfo = view.findViewById(R.id.download_info_text);

        getFiles();



        return view;
    }

    public void getFiles(){
        File downloadFolder = context.getExternalFilesDir("FunPlay");
        File[] files = downloadFolder.listFiles();

        if(files.length<1 ){
            downloadTextInfo.setVisibility(View.VISIBLE);
        }

        downloadedFiles.clear();

        for (int i = 0; i < files.length; i++) {
            File currFile = files[i];
            EpisodeDetails thisFile = new EpisodeDetails(currFile.getName(),null,currFile.getAbsolutePath(),null,null);
            if(!downloadedFiles.contains(thisFile)) {
                downloadedFiles.add(thisFile);
            }


            System.out.println(currFile.getAbsolutePath());


        }

        setAdapter();
    }

    public void setAdapter(){
        downloadListAdapter = new DownloadListAdapter(context,downloadedFiles);
        downloadList.setAdapter(downloadListAdapter);
    }
}
