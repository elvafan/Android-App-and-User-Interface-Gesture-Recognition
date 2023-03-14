package ca.uwaterloo.cs349;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;

@SuppressLint("AppCompatCustomView")
public class DrawingView extends ImageView {

    final String LOGNAME = "panzoom";

    // drawing
    Path path = null;
    ArrayList<Path> paths = new ArrayList();
    ArrayList<Point> pathpoint = new ArrayList<>();
    ArrayList<Point> onboardpoint = new ArrayList<>();
    Paint paintbrush = new Paint(Color.BLUE);
    Bitmap background;
    float xmin = 0; //small
    float xmax = 0;
    float ymax = 0;
    float ymin = 0; //small
    Boolean Outofboard = false;
    // constructor
    public DrawingView(Context context) {
        super(context);
        paintbrush.setStyle(Paint.Style.STROKE);
        paintbrush.setStrokeWidth(5);
    }

    // we save a lot of points because they need to be processed
    // during touch events e.g. ACTION_MOVE
    float x1, y1;
    int p1_id, p1_index;

    // store cumulative transformations
    // the inverse matrix is used to align points with the transformations - see below
    Matrix matrix = new Matrix();
    Matrix inverse = new Matrix();

    // capture touch events (down/move/up) to create a path/stroke that we draw later
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        p1_id = event.getPointerId(0);
        p1_index = event.findPointerIndex(p1_id);

        // invert using the current matrix to account for pan/scale
        // inverts in-place and returns boolean
        inverse = new Matrix();
        matrix.invert(inverse);

        // mapPoints returns values in-place
        float[] inverted = new float[] { event.getX(p1_index), event.getY(p1_index) };
        inverse.mapPoints(inverted);
        x1 = inverted[0];
        y1 = inverted[1];

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path = new Path();
                paths.add(path);
                path.moveTo(x1, y1);
                pathpoint = new ArrayList<>();
                pathpoint.add(new Point(x1,y1));
                break;
            case MotionEvent.ACTION_MOVE:
                if (paths.size() > 1){
                    paths.remove(0);
                }
                if(x1 < 0 || x1 > getWidth()){
                    Outofboard = true;
                }
                if (y1 < 0 || y1 >getHeight()){
                    Outofboard = true;
                }
                if(!Outofboard){
                    path.lineTo(x1, y1);
                    pathpoint.add(new Point(x1,y1));
                }
                break;
            case MotionEvent.ACTION_UP:
                Outofboard = false;
                if(pathpoint.size() > 1){
                    onboardpoint = pathpoint;
                }
                break;
        }
        return true;
    }

    // set image as background
    public void setImage(Bitmap bitmap) {
        this.background = bitmap;
    }


    public void clear(){
        paths.clear();
        onboardpoint.clear();

    }

    public ArrayList<Point> getPathpoint() {
        return onboardpoint;
    }

    public void findmaxmin(ArrayList<Point> lst){
        Point start = lst.get(0);
        xmin = start.x;
        xmax = start.x;
        ymin = start.y;
        ymax = start.y;
        for(Point point: lst){
            if (point.x < xmin){
                xmin = point.x;
            }
            if(point.x > xmax){
                xmax = point.x;
            }
            if (point.y < ymin){
                ymin = point.y;
            }
            if (point.y > ymax){
                ymax = point.y;
            }
        }
    }

    public Bitmap getCurCanvas() {
        int w = getWidth();
        int h = getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        canvas.save();
        findmaxmin(getPathpoint());
        int width = Math.round(xmax - xmin)+4;
        int height = Math.round(ymax - ymin)+4;
        int x = Math.max(Math.round(xmin)-2,0);
        int y = Math.max(Math.round(ymin)-2,0);
        Bitmap bmp = null;
        if (x+width <= w && y+height <= h){
            bmp = Bitmap.createBitmap(bitmap,x,y,width,height);
        } else if (x+width > w && y+height > h){
            height = h;
            width = w;
        } else if(x+width > w){
            width = w;
            bmp = Bitmap.createBitmap(bitmap,0,y,width,height);
        } else {
            height = h;
            bmp = Bitmap.createBitmap(bitmap,x,0,width,height);
        }
        if (width > height){
            height = height*500/width;
            width = 500;
        } else {
            width = width* 500/height;
            height = 500;
        }
        //System.out.println("height + width" + height + " "+ width);
        bmp = bmp.createScaledBitmap(bmp,width,height,false);
        return bmp;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // apply transformations from the event handler above
        canvas.concat(matrix);

        // draw background
        if (background != null) {
            this.setImageBitmap(background);
        }

        // draw lines over it
        for (Path path : paths) {
            canvas.drawPath(path, paintbrush);
        }
    }
}
