package ca.uwaterloo.cs349;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import java.lang.reflect.Field;

public class AdditionFragment extends Fragment {

    private SharedViewModel mViewModel;
    DrawingView pageView;
    private Boolean modify = false;
    private int modifyindex = -1;
    private View view;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addition, container, false);
        view = root;
        Bundle bundle = getArguments();
        if (bundle!= null){
            if (bundle.getString("arg").contentEquals("modify")){
                modify = true;
            }
            int index = bundle.getInt("position");
            if (index < 0 && index >= mViewModel.library.size()){
                System.err.println("switch fragment in modify thumbnail passing wrong index");
                modify = false;
            } else {
                modifyindex = index;
            }
        }
        pageView = new DrawingView(getActivity());
        pageView.setMinimumWidth(300);
        pageView.setMinimumHeight(500);
        pageView.setMaxHeight(1000);
        pageView.setMaxWidth(2000);

        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.white);
        pageView.setImage(image);

        LinearLayout linearLayout = (LinearLayout) root.findViewById(R.id.drawing);
        linearLayout.addView(pageView);

        Button cancel = (Button) root.findViewById(R.id.cancelbutton);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pageView.clear();
            }
        });

        Button ok = (Button) root.findViewById(R.id.okbutton);
        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                 if (pageView.getPathpoint().size()> 1){
                    showAlterDialog();
                }
            }
        });
        return root;
    }

    private void showAlterDialog(){
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(getActivity());
        final Bitmap bmp = pageView.getCurCanvas();
        ImageView image = new ImageView(getActivity());
        image.setImageBitmap(bmp);
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(image);
        final EditText editText = new EditText(getActivity());
        if (modify){
            alterDiaglog.setTitle("modify");
            String gstname =  mViewModel.library.get(modifyindex).name;
            alterDiaglog.setMessage("replace gesture for: " + gstname);
            alterDiaglog.setPositiveButton("save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mViewModel.modifygesture(modifyindex,pageView.getPathpoint(),bmp);
                    pageView.clear();
                    modifyindex = -1;
                    modify = false;
                    canCloseDialog(dialog,true);
                    NavController controller = Navigation.findNavController(view);
                    controller.navigate(R.id.navigation_library);
                }
            });
        } else {
            alterDiaglog.setTitle("save");
            alterDiaglog.setMessage("please enter a name for this gesture:");
            layout.addView(editText);
            alterDiaglog.setPositiveButton("save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (editText.getText().toString().isEmpty()){
                        EmptyDialog();
                        canCloseDialog(dialog,false);
                    } else {
                        mViewModel.savegesture(editText.getText().toString(),pageView.getPathpoint(),bmp);
                        pageView.clear();
                        canCloseDialog(dialog,true);
                    }
                }
            });
        }
        alterDiaglog.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                canCloseDialog(dialog,true);
            }
        });
        alterDiaglog.setView(layout);
        alterDiaglog.show();
    }

    private void EmptyDialog() {
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(getActivity());
        alterDiaglog.setTitle("Invaild name");
        alterDiaglog.setMessage("name is empty");
        alterDiaglog.setNeutralButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alterDiaglog.show();
    }
    private void canCloseDialog(DialogInterface dialogInterface, boolean close) {
        Field field = null;
        try {
            field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface, close);
        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}