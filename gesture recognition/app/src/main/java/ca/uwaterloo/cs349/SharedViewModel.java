package ca.uwaterloo.cs349;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    public ArrayList<Gesture> library = new ArrayList<>();
    private float N = 128;

    public SharedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is shared model");
    }

    public LiveData<String> getText() {
        return mText;
    }

    private ArrayList<Point> standardize (ArrayList<Point> lst) {

        lst = resample(lst);
        lst = rotate(lst);
        lst = to0(lst);
        lst = scale(lst);
        /*System.out.println("repos: ");
        print(lst);
        System.out.println("list: ");
        print(lst);
        System.out.println("scale: ");
        print(lst);*/
        return lst;
    }

    private void print(ArrayList<Point> lst){
        for(Point point: lst){
            System.out.print("("+point.x+"," + point.y+")"+", ");
        }
        System.out.println(" ");
    }

    private float interval(ArrayList<Point> lst) {
        float total = 0;
        Point pre = lst.get(0);
        for (int i = 1; i < lst.size();i++){
            Point cur = lst.get(i);
            float d = (float) Math.sqrt(Math.pow(ftod(pre.x) - ftod(cur.x),2) + Math.pow((ftod(pre.y) - ftod(cur.y)),2));
            total += d;
            pre = cur;
        }
        return total / N;
    }
    private double ftod (float f){
        BigDecimal b = new BigDecimal(String.valueOf(f));
        double d = b.doubleValue();
        return d;
    }

    private ArrayList<Point> resample(ArrayList<Point> lst) {
        ArrayList<Point> sampledlst = new ArrayList<>();
        float interval = interval(lst);
        Point pre = lst.get(0);
        sampledlst.add(lst.get(0));
        float curpath = 0;
        float samplepath = interval;
        for (int i = 1; i < lst.size();i++){
            Point cur = lst.get(i);
            float d = (float) Math.sqrt(Math.pow(ftod(pre.x) - ftod(cur.x),2) + Math.pow((ftod(pre.y) - ftod(cur.y)),2));
            while (curpath + d >= samplepath){
                float r = samplepath - curpath;
                Point newp =  new Point();
                newp.x = (cur.x - pre.x)*r/d  + pre.x;
                newp.y = (cur.y - pre.y)*r/d  + pre.y;
                sampledlst.add(newp);
                samplepath += interval;
            }
            curpath += d;
            pre = cur;
        }
        if(sampledlst.size() != N){
            if(sampledlst.size() > N){
                sampledlst.remove(sampledlst.size()-1);
            }
        }
        return sampledlst;
    }

    private Point center (ArrayList<Point> lst) {
        float totalx = 0;
        float totaly = 0;
        float totalnum = 0;
        for(Point point: lst){
            totalnum += 1;
            totalx += point.x;
            totaly += point.y;
        }
        Point centroid = new Point();
        centroid.x = totalx/totalnum;
        centroid.y = totaly/totalnum;
        return centroid;
    }

    private ArrayList<Point>  to0 (ArrayList<Point> lst) {
        ArrayList<Point> to0lst = new ArrayList<>();
        Point centroid = center(lst);
        for(Point point: lst){
            Point newp = new Point();
            newp.x = point.x - centroid.x;
            newp.y = point.y - centroid.y;
            to0lst.add(newp);
        }
        return to0lst;
    }

    private ArrayList<Point>  rotate (ArrayList<Point> lst) {
        ArrayList<Point> rolst = new ArrayList<>();
        Point start = lst.get(0);
        Point centroid = center(lst);
        start.x = start.x - centroid.x;
        start.y = start.y - centroid.y;
        double angle = 0;
        angle = Math.atan2(ftod(start.y),ftod(start.x)) *180 / Math.PI;
        //System.out.println("angle: "+ angle);
        angle = angle*Math.PI/180;
        angle = -angle;
        start.x = start.x + centroid.x;
        start.y = start.y + centroid.y;
        for(Point point: lst){
            Point newp = new Point();
            newp.x = (float) (Math.cos(angle)*ftod(point.x-centroid.x) -Math.sin(angle)*ftod(point.y-centroid.y) + centroid.x);
            newp.y = (float) (Math.sin(angle)*ftod(point.x-centroid.x) +Math.cos(angle)*ftod(point.y-centroid.y) + centroid.y);
            rolst.add(newp);
        }
        return rolst;
    }

    private ArrayList<Point>  scale (ArrayList<Point> lst) {
        ArrayList<Point> scalelst = new ArrayList<>();
        Point pre = lst.get(0);
        Point cur = lst.get(1);
        float d = (float) Math.sqrt(Math.pow(ftod(pre.x) - ftod(cur.x),2) + Math.pow((ftod(pre.y) - ftod(cur.y)),2));
        float ratio = 30/d;
        for(Point point : lst){
            Point newp = new Point();
            newp.x = ratio * point.x;
            newp.y = ratio * point.y;
            scalelst.add(newp);
        }
        return scalelst;
    }

    public void savegesture(String name, ArrayList<Point> original, Bitmap bmp){
        ArrayList<Point> standard = standardize(original);
        Gesture gst = new Gesture(name, original,standard,bmp);
        library.add(gst);
    }

    public void modifygesture(int index,ArrayList<Point> original, Bitmap bmp) {
        Gesture gst = library.get(index);
        gst.original = original;
        gst.standard = standardize(original);
        gst.thumbnail = bmp;
        library.set(index,gst);
    }

    public ArrayList<Gesture> getTop3(ArrayList<Point> lst){
        lst = standardize(lst);
        ArrayList<Gesture> top3lst = null;
        if (lst != null && lst.size()>1){
            top3lst = new ArrayList<>();
            ArrayList<Double> top3score = new ArrayList<>();
            for(Gesture gesture : library){
                double d = score(lst, gesture.standard);
                if (top3lst.size() == 0){
                    top3lst.add(gesture);
                    top3score.add(d);
                } else{
                    Boolean added = false;
                    for (int i = 0; i < top3score.size(); i++ ){
                        if (d < top3score.get(i)){
                            top3score.add(i, d);
                            top3lst.add(i,gesture);
                            added = true;
                            break;
                        }
                    }
                    if (!added){
                        top3lst.add(gesture);
                        top3score.add(d);
                    }
                }
            }
            if (top3lst.size() > 3) {
                ArrayList<Gesture> temp = new ArrayList<>();
                temp.add(top3lst.get(0));
                temp.add(top3lst.get(1));
                temp.add(top3lst.get(2));
                top3lst = temp;
            }
            /*for(Double d: top3score){
                System.out.print(" "+ d);
            }
            System.out.println("d ");*/
        }
        return top3lst;
    }

    public double score(ArrayList<Point> s, ArrayList<Point> t){
        double total = 0;
        double d;
        if (s.size() != t.size()){
            System.out.println("size: "+s.size() + " "+ t.size());
        }
        for(int i = 0; i < N; i++){
            d = Math.sqrt(Math.pow((ftod(s.get(i).x) - ftod(t.get(i).x)),2) + Math.pow((ftod(s.get(i).y) - ftod(t.get(i).y)),2));
            total += d;
        }
        return total/s.size();
    }

    public ArrayList<GestureString> LibrarySave(){
        //System.out.println("save  "+ library.size());
        ArrayList<GestureString> gestureStringAList = new ArrayList<>();
        if (!library.isEmpty()){
            for(Gesture gst: library){
                float[] originalx = new float[gst.original.size()];
                float[] originaly = new float[gst.original.size()];
                for(int i = 0; i < gst.original.size(); i++){
                    Point point = gst.original.get(i);
                    originalx[i] = point.x;
                    originaly[i] = point.y;
                }
                float[] standardx = new float[gst.standard.size()];
                float[] standardy = new float[gst.standard.size()];
                for(int i = 0; i < gst.standard.size(); i++){
                    Point point = gst.standard.get(i);
                    standardx[i] = point.x;
                    standardy[i] = point.y;
                }
                Bitmap b = gst.thumbnail;
                /*int bytes = b.getByteCount();
                ByteBuffer buffer = ByteBuffer.allocate(bytes);
                b.copyPixelsFromBuffer(buffer);
                byte[] bm = buffer.array();*/
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG,100,bs);
                byte[] data = bs.toByteArray();
                GestureString gstString = new GestureString(gst.name,originalx,originaly,standardx,standardy,data);
                gestureStringAList.add(gstString);
            }
        }
        //System.out.println("save string: "+ gestureStringAList.size());
        return gestureStringAList;
    }

    public void LibraryLoad(ArrayList<GestureString> gstlist){
        //System.out.println("load "+ gstlist.size());
        if (!gstlist.isEmpty()){
            ArrayList<Gesture> tempLibrary = new ArrayList<>();
            for (GestureString gstString : gstlist){
                ArrayList<Point> tempori = new ArrayList<>();
                for(int i = 0; i < gstString.originalx.length; i++){
                    tempori.add(new Point(gstString.originalx[i],gstString.originaly[i]));
                }
                ArrayList<Point> tempst = new ArrayList<>();
                for(int i = 0; i < gstString.standardx.length; i++){
                    tempst.add(new Point(gstString.standardx[i],gstString.standardy[i]));
                }
                Bitmap bitmap = null;
                if (gstString.bitmap.length != 0){
                    bitmap = BitmapFactory.decodeByteArray(gstString.bitmap,0,gstString.bitmap.length);
                }
                Gesture newsgt = new Gesture(gstString.gname, tempori,tempst,bitmap);
                tempLibrary.add(newsgt);
            }
            library = tempLibrary;
        }
    }

    public class GestureString {
        public String gname;
        public float[] originalx;
        public float[] originaly;
        public float[] standardx;
        public float[] standardy;
        public byte[] bitmap;

        public GestureString(String sname,float[] originalx, float[] originaly,float[] standardx,float[] standardy, byte[] bitmap){
            this.gname = sname;
            this.originalx = originalx;
            this.originaly = originaly;
            this.standardx = standardx;
            this.standardy = standardy;
            this.bitmap = bitmap;
        }

    }
}
