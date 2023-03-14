package ca.uwaterloo.cs349;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private SharedViewModel mViewModel;
    DrawingView pageView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        pageView = new DrawingView(getActivity());
        pageView.setMinimumWidth(300);
        pageView.setMinimumHeight(500);
        pageView.setMaxHeight(1000);
        pageView.setMaxWidth(2000);
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.white);
        pageView.setImage(image);
        LinearLayout linearLayout = (LinearLayout) root.findViewById(R.id.regdraw);
        linearLayout.addView(pageView);

        Button reggst = (Button) root.findViewById(R.id.regbutton);
        reggst.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (pageView.getPathpoint().size()> 1){
                    ArrayList<Gesture> toplist = mViewModel.getTop3(pageView.getPathpoint());
                    if (toplist!= null){
                        if (toplist.size() == 3){
                            TextView text3= (TextView) root.findViewById(R.id.nametop3);
                            text3.setText(toplist.get(2).name);
                            ImageView image3 = (ImageView) root.findViewById(R.id.imagetop3);
                            image3.setImageBitmap(toplist.get(2).thumbnail);
                            toplist.remove(2);
                        }
                        if (toplist.size() == 2){
                            TextView text3= (TextView) root.findViewById(R.id.nametop2);
                            text3.setText(toplist.get(1).name);
                            ImageView image3 = (ImageView) root.findViewById(R.id.imagetop2);
                            image3.setImageBitmap(toplist.get(1).thumbnail);
                            toplist.remove(1);
                        }
                        if (toplist.size() == 1){
                            TextView text3= (TextView) root.findViewById(R.id.nametop1);
                            text3.setText(toplist.get(0).name);
                            ImageView image3 = (ImageView) root.findViewById(R.id.imagetop1);
                            image3.setImageBitmap(toplist.get(0).thumbnail);
                            toplist.remove(0);
                        }
                    }
                }
            }
        });
        return root;
    }
}