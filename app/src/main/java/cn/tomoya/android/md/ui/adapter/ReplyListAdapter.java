package cn.tomoya.android.md.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.tomoya.android.md.R;
import cn.tomoya.android.md.model.entity.Reply;
import cn.tomoya.android.md.presenter.contract.IReplyPresenter;
import cn.tomoya.android.md.presenter.implement.ReplyPresenter;
import cn.tomoya.android.md.ui.activity.LoginActivity;
import cn.tomoya.android.md.ui.activity.UserDetailActivity;
import cn.tomoya.android.md.ui.view.ICreateReplyView;
import cn.tomoya.android.md.ui.view.IReplyView;
import cn.tomoya.android.md.ui.widget.ContentWebView;
import cn.tomoya.android.md.util.FormatUtils;

public class ReplyListAdapter extends BaseAdapter {

    private final Activity activity;
    private final LayoutInflater inflater;
    private final List<Reply> replyList = new ArrayList<>();
    private final Map<String, Integer> positionMap = new HashMap<>();
    private final ICreateReplyView createReplyView;

    public ReplyListAdapter(@NonNull Activity activity, @NonNull ICreateReplyView createReplyView) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        this.createReplyView = createReplyView;
    }

    @NonNull
    public List<Reply> getReplyList() {
        return replyList;
    }

    public void setReplyList(@NonNull List<Reply> replyList) {
        this.replyList.clear();
        this.replyList.addAll(replyList);
        positionMap.clear();
        for (int n = 0; n < replyList.size(); n++) {
            Reply reply = replyList.get(n);
            positionMap.put(reply.getId(), n);
        }
    }

    public void addReply(@NonNull Reply reply) {
        replyList.add(reply);
        positionMap.put(reply.getId(), replyList.size() - 1);
    }

    @Override
    public int getCount() {
        return replyList.size();
    }

    @Override
    public Object getItem(int position) {
        return replyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_reply, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.update(position);
        return convertView;
    }

    public class ViewHolder implements IReplyView {

        @BindView(R.id.img_avatar)
        protected ImageView imgAvatar;

        @BindView(R.id.tv_login_name)
        protected TextView tvLoginName;

        @BindView(R.id.tv_index)
        protected TextView tvIndex;

        @BindView(R.id.tv_create_time)
        protected TextView tvCreateTime;

        @BindView(R.id.tv_target_position)
        protected TextView tvTargetPosition;

        @BindView(R.id.web_content)
        protected ContentWebView webContent;

        @BindView(R.id.icon_deep_line)
        protected View iconDeepLine;

        @BindView(R.id.icon_shadow_gap)
        protected View iconShadowGap;

        private final IReplyPresenter replyPresenter;

        private Reply reply;

        public ViewHolder(@NonNull View itemView) {
            ButterKnife.bind(this, itemView);
            replyPresenter = new ReplyPresenter(activity, this);
        }

        public void update(int position) {
            reply = replyList.get(position);
            updateReplyViews(reply, position, null);
        }

        public void updateReplyViews(@NonNull Reply reply, int position, @Nullable Integer targetPosition) {
            Glide.with(activity).load(reply.getAvatar()).placeholder(R.drawable.image_placeholder).dontAnimate().into(imgAvatar);
            tvLoginName.setText(reply.getAuthor());
            tvIndex.setText(position + 1 + "楼");
            tvCreateTime.setText(FormatUtils.getRelativeTimeSpanString(reply.getInTime()));
            if (targetPosition == null) {
                tvTargetPosition.setVisibility(View.GONE);
            } else {
                tvTargetPosition.setVisibility(View.VISIBLE);
                tvTargetPosition.setText("回复：" + (targetPosition + 1) + "楼");
            }

            // 这里直接使用WebView，有性能问题
            webContent.loadRenderedContent(reply.getContent());

            iconDeepLine.setVisibility(position == replyList.size() - 1 ? View.GONE : View.VISIBLE);
            iconShadowGap.setVisibility(position == replyList.size() - 1 ? View.VISIBLE : View.GONE);
        }

        @OnClick(R.id.img_avatar)
        protected void onBtnAvatarClick() {
            UserDetailActivity.startWithTransitionAnimation(activity, reply.getAuthor(), imgAvatar, reply.getAvatar());
        }

        @OnClick(R.id.btn_at)
        protected void onBtnAtClick() {
            if (LoginActivity.startForResultWithLoginCheck(activity)) {
                createReplyView.onAt(reply, positionMap.get(reply.getId()));
            }
        }

        @Override
        public void onUpReplyOk(@NonNull Reply reply) {

        }
    }

}
