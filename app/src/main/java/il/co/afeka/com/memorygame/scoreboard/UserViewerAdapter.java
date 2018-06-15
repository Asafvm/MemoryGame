package il.co.afeka.com.memorygame.scoreboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import il.co.afeka.com.memorygame.R;

public class UserViewerAdapter extends RecyclerView.Adapter<UserViewerAdapter.ViewHolder> {
    private List<UserItem> list;
    private Context context;

    public UserViewerAdapter(List<UserItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);

        return new UserViewerAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewerAdapter.ViewHolder holder, int position) {
        final UserItem item = list.get(position);
        if(position==0)
            holder.itemView.setBackgroundResource(R.drawable.shape_item_frame_gold);
        else if(position==1)
            holder.itemView.setBackgroundResource(R.drawable.shape_item_frame_silver);
        else if(position==2)
            holder.itemView.setBackgroundResource(R.drawable.shape_item_frame_bronze);
        else
            holder.itemView.setBackgroundResource(R.drawable.shape_item_frame);

        holder.age.setText(item.getAge());
        holder.name.setText(item.getName());
        holder.score.setText(item.getScore());
    }

    @Override
    public int getItemCount() {
        if(list!=null)
            return list.size();
        else
            return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView age;
        TextView name;
        TextView score;

        ViewHolder(View itemView) {
            super(itemView);
            age = itemView.findViewById(R.id.item_age);
            name = itemView.findViewById(R.id.item_name);
            score = itemView.findViewById(R.id.item_score);

        }


    }
}

