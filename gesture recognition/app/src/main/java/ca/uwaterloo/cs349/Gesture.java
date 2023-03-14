package ca.uwaterloo.cs349;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Gesture {
    String name;
    ArrayList<Point> original = new ArrayList<>();
    ArrayList<Point> standard = new ArrayList<>();
    Bitmap  thumbnail = null;

    public Gesture(String name, ArrayList<Point> original, ArrayList<Point> standard, Bitmap thumbnail){
        this.name = name;
        this.original = original;
        this.standard =standard;
        this.thumbnail = thumbnail;
    }
}
