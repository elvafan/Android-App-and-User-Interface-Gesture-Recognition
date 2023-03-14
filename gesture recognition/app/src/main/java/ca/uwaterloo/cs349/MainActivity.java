package ca.uwaterloo.cs349;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SharedViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_library, R.id.navigation_addition)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        mViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

    }
    @Override
    protected void onResume() {
        super.onResume();
        Path filepath = Paths.get(getFilesDir().getPath() +"librarysave.json");
        if (Files.exists(filepath)){
            try {
                Gson gson = new Gson();
                Reader reader = Files.newBufferedReader(filepath);

                ArrayList<SharedViewModel.GestureString> Glist = new Gson().fromJson(reader,new TypeToken<ArrayList<SharedViewModel.GestureString>>() {}.getType());
                if (Glist != null){
                    mViewModel.LibraryLoad(Glist);
                }
                reader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        ArrayList<SharedViewModel.GestureString> stringlibrary = null;
        try {
            Gson gson = new Gson();
            Writer writer = Files.newBufferedWriter(Paths.get(getFilesDir().getPath() + "librarysave.json"));
            stringlibrary = mViewModel.LibrarySave();
            gson.toJson(stringlibrary, writer);
            writer.close();
            //System.out.println("Save in onP");
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

}