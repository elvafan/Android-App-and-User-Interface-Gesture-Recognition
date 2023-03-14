package ca.uwaterloo.cs349;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LibRecyclerViewAdapter extends RecyclerView.Adapter<LibRecyclerViewAdapter.ViewHolder> {

    List<Gesture> mValues;
    Context context;

    public LibRecyclerViewAdapter(List<Gesture> items,Context context) {
        mValues = items;
        this.context = context;
    }

    @Override
    public LibRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.libitem, parent, false);
        return new ViewHolder(view,this);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).name);
        holder.mImageView.setImageBitmap(mValues.get(position).thumbnail);
    }

    public void remove(int position){
        mValues.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,mValues.size());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //public View mview;
        public final TextView mIdView;
        public ImageView mImageView = new ImageView(context);
        public ImageButton deleteButton;
        public ImageButton updateButton;
        public Gesture mItem;
        private LibRecyclerViewAdapter adapter;

        public ViewHolder(View itemView, final LibRecyclerViewAdapter adapter) {
            super(itemView);
            mIdView = (TextView) itemView.findViewById(R.id.textView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            deleteButton = (ImageButton) itemView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener((view)->{
                adapter.remove(mValues.indexOf(mItem));
            });
            updateButton = (ImageButton) itemView.findViewById(R.id.updateButton);
            updateButton.setOnClickListener((view)->{
                NavController controller = Navigation.findNavController(view);
                Bundle bundle = new Bundle();
                bundle.putString("arg", "modify");
                bundle.putInt("position",mValues.indexOf(mItem));
                controller.navigate(R.id.navigation_addition,bundle);
            });
        }
    }
}
