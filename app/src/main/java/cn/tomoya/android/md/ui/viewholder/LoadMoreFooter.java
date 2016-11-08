package cn.tomoya.android.md.ui.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.tomoya.android.md.R;
import cn.tomoya.android.md.ui.widget.ListView;

public class LoadMoreFooter {

    public enum State {
        disable, loading, nomore, endless, fail
    }

    public interface OnLoadMoreListener {

        void onLoadMore();

    }

    @BindView(R.id.icon_loading)
    protected View iconLoading;

    @BindView(R.id.tv_text)
    protected TextView tvText;

    private State state = State.disable;
    private final OnLoadMoreListener loadMoreListener;

    public LoadMoreFooter(@NonNull Context context, @NonNull ListView listView, @NonNull OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
        View footerView = LayoutInflater.from(context).inflate(R.layout.footer_load_more, listView, false);
        ButterKnife.bind(this, footerView);
        listView.addFooterView(footerView, null, false);
        listView.addOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    checkLoadMore();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

        });
    }

    private void checkLoadMore() {
        if (getState() == State.endless || getState() == State.fail) {
            setState(State.loading);
            loadMoreListener.onLoadMore();
        }
    }

    @NonNull
    public State getState() {
        return state;
    }

    public void setState(@NonNull State state) {
        if (this.state != state) {
            this.state = state;
            switch (state) {
                case disable:
                    iconLoading.setVisibility(View.GONE);
                    tvText.setVisibility(View.GONE);
                    break;
                case loading:
                    iconLoading.setVisibility(View.VISIBLE);
                    tvText.setVisibility(View.GONE);
                    break;
                case nomore:
                    iconLoading.setVisibility(View.GONE);
                    tvText.setVisibility(View.VISIBLE);
                    tvText.setText(R.string.load_more_nomore);
                    break;
                case endless:
                    iconLoading.setVisibility(View.GONE);
                    tvText.setVisibility(View.VISIBLE);
                    tvText.setText(R.string.load_more_endless);
                    break;
                case fail:
                    iconLoading.setVisibility(View.GONE);
                    tvText.setVisibility(View.VISIBLE);
                    tvText.setText(R.string.load_more_fail);
                    break;
                default:
                    throw new AssertionError("Unknow state.");
            }
        }
    }

    @OnClick(R.id.tv_text)
    protected void onBtnTextClick() {
        checkLoadMore();
    }

}
