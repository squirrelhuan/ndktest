package cn.demomaster.huan.ndktest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import cn.demomaster.huan.ndktest.R;
import cn.demomaster.huan.ndktest.helper.DBHelper;
import cn.demomaster.huan.ndktest.model.ChatRecode;
import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;
import cn.demomaster.huan.quickdeveloplibrary.view.drawable.QDRoundButtonDrawable;
import cn.demomaster.huan.quickdeveloplibrary.widget.SquareImageView;


/**
 * Created by Squirrel桓 on 2018/11/11.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolderMessage> {


    public List<ChatRecode> lists = null;
    private Context context;

    public ChatAdapter(Context context, List<ChatRecode> lists) {
        this.context = context;
        this.lists = lists;
    }

    @Override
    public int getItemViewType(int position) {
        ChatRecode chatRecode = lists.get(position);
        String id = SharedPreferencesHelper.getInstance().getString("UserId", "");

        return chatRecode.getSenderId().equals(id.trim()) ? 1 : 0;//0left，1right
    }

    //创建View,被LayoutManager所用
    @Override
    public ViewHolderMessage onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        ViewHolderMessage holder;
        QDRoundButtonDrawable bg = new QDRoundButtonDrawable();
        bg.setCornerRadius(2);
        if (viewType == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_record_left, parent, false);
            bg.setColor(context.getResources().getColor(R.color.white));
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_record_right, parent, false);
            bg.setColor(context.getResources().getColor(R.color.greenyellow));
        }
        holder  = new ViewHolderMessage(view,viewType);

        holder.tv_content.setBackground(bg);
        return holder;
    }

    //数据的绑定
    @Override
    public void onBindViewHolder(final ViewHolderMessage holder, int position) {
        ViewHolderMessage holder1 = holder;
        holder1.tv_content.setText(lists.get(position).getContent());
        holder1.bind(context,lists,position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(null, null, holder.getAdapterPosition(), holder.getAdapterPosition());
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onItemLongClick(null, null, holder.getAdapterPosition(), holder.getAdapterPosition());
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    //自定义ViewHolder,包含item的所有界面元素
    public static class ViewHolderMessage extends RecyclerView.ViewHolder {
        public TextView tv_content;
        public SquareImageView iv_head;
        private int viewType;

        public ViewHolderMessage(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            tv_content = itemView.findViewById(R.id.tv_content);
            iv_head = itemView.findViewById(R.id.iv_header);
        }
        public void bind(Context context, List<ChatRecode> lists, int position){
            String url = "";
            if(viewType==0){
                url = DBHelper.getUserById(lists.get(position).getSenderId()).getFace();
            }else {
                url = SharedPreferencesHelper.getInstance().getString("headerUrl","");
            }
            RequestOptions mRequestOptions = RequestOptions.circleCropTransform()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                    .skipMemoryCache(false);//不做内存缓存
            Glide.with(context).load(url).apply(mRequestOptions).into(iv_head);
        }
    }


    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemLongClickListener onItemLongClickListener;

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
}

