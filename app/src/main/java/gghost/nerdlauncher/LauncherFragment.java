package gghost.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;
import java.util.List;

public class LauncherFragment extends Fragment {

    private static final String TAG = "LauncherFragment";

    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_launcher, container, false);

        mRecyclerView = v.findViewById(R.id.launcher_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        setupAdapter();

        return v;
    }


    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ResolveInfo mResolveInfo;
        private ImageView mImageView;
        private TextView mTextView;
        public ActivityHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.list_item_app_text_id);
            mImageView = itemView.findViewById(R.id.list_item_app_image_id);

            itemView.setOnClickListener(this);
        }
        public void bindActivity(ResolveInfo info) {
            mResolveInfo = info;
            PackageManager pm = getActivity().getPackageManager();
            String appName = mResolveInfo.loadLabel(pm).toString();
            mTextView.setText(appName);

            //здесь нужно каким-то образом достать image
            mImageView.setImageDrawable(mResolveInfo.loadIcon(pm));
        }

        @Override
        public void onClick(View v) {
            ActivityInfo info = mResolveInfo.activityInfo;
            //По идеи включение действия ACTION_MAIN не обязательно. Но в книге говорят,
            //что некоторые приложения могут вести себя иначе
            Intent i = new Intent(Intent.ACTION_MAIN);
            /* Интент должен знать, какую активность ему запускать. Для этого мы используем
            * setClassName, указывая имя пакета и имя класса. Это определяет единственную
            * активность, способную обработать данный intent */
            i.setClassName(info.applicationInfo.packageName, info.name);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }
    private class ActivityHolderAdapter extends RecyclerView.Adapter<ActivityHolder> {

        private final List<ResolveInfo> mInfoList;

        public ActivityHolderAdapter(List<ResolveInfo> infoList) {
            mInfoList = infoList;
        }

        @NonNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_app, parent, false);
            ActivityHolder holder = new ActivityHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {
            holder.bindActivity(mInfoList.get(position));
        }

        @Override
        public int getItemCount() {
            return mInfoList.size();
        }
    }


    private void setupAdapter() {
        //Создаем неявный интент
        Intent i = new Intent(Intent.ACTION_MAIN);
        //Фильтруем интент по категории, чтобы достать список только тех приложений, которые
        //отображаются на главном экране
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager p = getActivity().getPackageManager();

        List<ResolveInfo> activities = p.queryIntentActivities(i, 0);

        activities.sort(new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo o1, ResolveInfo o2) {
                return String.CASE_INSENSITIVE_ORDER.compare(o1.loadLabel(p).toString(), o2.loadLabel(p).toString());
            }
        });

        Log.i(TAG, "Found " + activities.size() + " activities");
        for (ResolveInfo info : activities) {
            Log.i(TAG, "activity: " + info.loadLabel(p));
        }

        mRecyclerView.setAdapter(new ActivityHolderAdapter(activities));
    }
    public static LauncherFragment newInstance() {

        Bundle args = new Bundle();

        LauncherFragment fragment = new LauncherFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
