package com.loopeer.codereader.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopeer.codereader.CodeReaderApplication;
import com.loopeer.codereader.event.DownloadProgressEvent;
import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.R;
import com.loopeer.codereader.coreader.db.CoReaderDbHelper;
import com.loopeer.codereader.model.MainHeaderItem;
import com.loopeer.codereader.model.Repo;
import com.loopeer.codereader.ui.view.ForegroundProgressRelativeLayout;
import com.loopeer.codereader.utils.RxBus;
import com.loopeer.itemtouchhelperextension.Extension;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class MainLatestAdapter extends RecyclerViewAdapter<Repo> {
    private static final String TAG = "MainLatestAdapter";

    public MainLatestAdapter(Context context) {
        super(context);
    }

    private final CompositeSubscription mAllSubscription = new CompositeSubscription();

    @Override
    public void setData(List<Repo> data) {
        ArrayList list = new ArrayList();
        list.add(null);
        list.addAll(data);
        super.setData(list);
    }

    @Override
    public void bindView(Repo var1, int var2, RecyclerView.ViewHolder var3) {
        if (var3 instanceof RepoViewHolder) {
            RepoViewHolder viewHolder = (RepoViewHolder) var3;
            Subscription subscription = viewHolder.bind(var1);
            if (subscription != null) {
                mAllSubscription.add(subscription);
            }
            viewHolder.mProgressRelativeLayout.setOnClickListener(view -> {
                if (!var1.isDownloading() && !var1.isUnzip)
                    Navigator.startCodeReadActivity(getContext(), var1);
            });
            viewHolder.mActionDeleteView.setOnClickListener(view -> doRepoDelete(var3));
            viewHolder.mActionSyncView.setOnClickListener(view ->
                    Navigator.startDownloadRepoService(getContext(), var1)
            );
        }
        if (var3 instanceof MainHeaderHolder) {
            MainHeaderHolder viewHolder = (MainHeaderHolder) var3;
            viewHolder.bind();
        }

    }

    private void doRepoDelete(RecyclerView.ViewHolder var3) {
        int position = var3.getAdapterPosition();
        Repo repo = mData.get(position);
        CoReaderDbHelper.getInstance(getContext()).deleteRepo(Long.parseLong(repo.id));
        if (repo.downloadId > 0) Navigator.startDownloadRepoServiceRemove(getContext(), repo.downloadId);
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public void clearSubscription() {
        mAllSubscription.clear();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = getLayoutInflater();
        View view;
        switch (viewType) {
            case R.layout.list_item_main_top_header:
                view = inflater.inflate(R.layout.list_item_main_top_header, parent, false);
                return new MainHeaderHolder(view);
            default:
                view = inflater.inflate(R.layout.list_item_repo, parent, false);
                return new RepoViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return R.layout.list_item_main_top_header;
        return R.layout.list_item_repo;
    }

    public class RepoViewHolder extends RecyclerView.ViewHolder implements Extension {

        @BindView(R.id.img_repo_type)
        ImageView mImgRepoType;
        @BindView(R.id.text_repo_name)
        TextView mTextRepoName;
        @BindView(R.id.text_repo_time)
        TextView mTextRepoTime;
        @BindView(R.id.view_progress_list_repo)
        ForegroundProgressRelativeLayout mProgressRelativeLayout;
        @BindView(R.id.view_list_repo_action_delete)
        View mActionDeleteView;
        @BindView(R.id.view_list_repo_action_update)
        View mActionSyncView;
        @BindView(R.id.view_list_repo_action_container)
        View mActionContainer;
        @BindView(R.id.img_list_repo_cloud)
        View mCloud;
        @BindView(R.id.img_list_repo_phone)
        View mLocalPhone;

        Subscription mSubscription;

        public RepoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public Subscription bind(Repo repo) {
            mImgRepoType.setBackgroundResource(repo.isFolder ? R.drawable.shape_circle_folder : R.drawable.shape_circle_document);
            mImgRepoType.setImageResource(repo.isFolder ? R.drawable.ic_repo_white : R.drawable.ic_document_white);
            mTextRepoName.setText(repo.name);
            mTextRepoTime.setText(DateUtils.getRelativeTimeSpanString(itemView.getContext(), repo.lastModify));
            mActionSyncView.setVisibility(repo.isNetRepo() ? View.VISIBLE : View.GONE);
            mCloud.setVisibility(repo.isNetRepo() ? View.VISIBLE : View.GONE);
            mLocalPhone.setVisibility(repo.isLocalRepo() ? View.VISIBLE : View.GONE);
            resetSubscription(repo);
            if (repo.isDownloading()) {
                mProgressRelativeLayout.setInitProgress(repo.factor);
            } else {
                mProgressRelativeLayout.setInitProgress(1f);
            }
            mProgressRelativeLayout.setUnzip(repo.isUnzip);
            return mSubscription;
        }

        private void resetSubscription(Repo repo) {
            if (mSubscription != null && !mSubscription.isUnsubscribed()) {
                mSubscription.unsubscribe();
            }
            mSubscription = RxBus.getInstance()
                    .toObservable()
                    .filter(o -> o instanceof DownloadProgressEvent)
                    .map(o -> (DownloadProgressEvent) o)
                    .filter(o -> (o.downloadId == repo.downloadId) || repo.id.equals(o.repoId))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(o -> {
                        if (repo.downloadId == 0) repo.downloadId = o.downloadId;
                    })
                    .doOnNext(o -> mProgressRelativeLayout.setProgressCurrent(o.factor))
                    .filter(o -> o.factor == 1f)
                    .doOnNext(o -> repo.isUnzip = o.isUnzip)
                    .doOnNext(o -> mProgressRelativeLayout.setUnzip(o.isUnzip))
                    .filter(o -> o.isUnzip == false)
                    .doOnNext(o -> CoReaderDbHelper.getInstance(
                            CodeReaderApplication.getAppContext()).resetRepoDownloadId(repo.downloadId))
                    .doOnNext(o -> repo.downloadId = 0)
                    .subscribe();
        }

        @Override
        public float getActionWidth() {
            return mActionContainer.getWidth();
        }

    }

    class MainHeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.grid_main)
        GridView mGridView;
        private MainHeaderAdapter mMainHeaderAdapter;

        public MainHeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mMainHeaderAdapter = new MainHeaderAdapter(itemView.getContext());
            mGridView.setAdapter(mMainHeaderAdapter);
        }

        public void bind() {
            List<MainHeaderItem> items = new ArrayList<>();
            items.add(new MainHeaderItem(R.drawable.ic_github, R.string.header_item_github_search
                    , itemView.getContext().getString(R.string.header_item_github_search_link)));
            items.add(new MainHeaderItem(R.drawable.ic_trending, R.string.header_item_trending
                    , itemView.getContext().getString(R.string.header_item_trending_link)));
            mMainHeaderAdapter.updateData(items);
        }
    }
}
