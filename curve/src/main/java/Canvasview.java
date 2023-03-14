import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import java.util.ArrayList;


public class Canvasview extends Pane implements IView {
    private final Canvas canvas = new Canvas(getWidth(),getHeight());
    GraphicsContext gc = canvas.getGraphicsContext2D();
    private Model model;
    private CubicCurve preview = null;
    private Model.cubiccurve preview1 = null;
    private Model.cubiccurve preview2 = null;
    private Boolean needchange = false;
    private Boolean isseg = false;
    private Boolean isconnect = false;

    Canvasview(Model model) {
        this.model = model;
        canvas.setOnMouseClicked(mouseEvent -> {
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();
            if(model.isDrawing()){
                model.drawpoint(x,y);
            } else if(model.isSelection()){
                if (model.getcurve()==null){
                    Boolean iscon =model.iscurvecontain(x,y);
                    if (iscon){
                        model.selectcurve();
                    }
                }
                if(model.isPointtype()&&(model.getcurve()!=null)){
                    //System.out.println("call change type");
                    model.changetype(x,y);
                } else if (model.isAddpointmode()&&(model.getcurve()!=null)){
                    //System.out.println("add point");
                    model.addpoint(x,y);
                } else if (model.isRemovepointmode()&&(model.getcurve()!=null)) {
                    if (model.getcurve().size() <= 1){
                        model.exitRemovepointmode();
                        System.out.println("can't remove any point on curve now");
                    }
                    model.removepoint(x, y);
                }
            } else if(model.isEarse()){
                Boolean iscon =model.iscurvecontain(x,y);
                if (iscon){
                    model.deletecurve();
                    model.exiterase();
                }
            }
        });

        canvas.setOnMouseMoved(mouseEvent -> {
            if(model.isDrawing()){
                double x = mouseEvent.getX();
                double y = mouseEvent.getY();
                preview = model.preview(x,y);
                updateView();
            }
        });

        canvas. setOnMousePressed(mouseEvent -> {
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();
            if (model.isSelection() && ! model.isPointtype()){
                if (model.segementpress(x,y)){
                    isseg = true;
                    canvas.setCursor(Cursor.NONE);
                } else if (model.connectpress(x,y)){
                    isconnect = true;
                    canvas.setCursor(Cursor.NONE);
                }
            }
        });
        canvas.setOnMouseDragged(mouseEvent -> {
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();
            ArrayList<Model.cubiccurve> twocurve = null;
            if (model.isSelection() && !model.isPointtype()){
                if ( isseg){
                    needchange = true;
                    preview1 = null;
                    preview2 = null;
                    twocurve = model.segementdrag(x, y);
                } else if (isconnect){
                    needchange = true;
                    preview1 = null;
                    preview2 = null;
                    twocurve = model.connectdrag(x, y);
                }
                if (twocurve != null) {
                    preview1 = twocurve.get(0);
                    preview2 = twocurve.get(1);
                }
            }
            updateView();
        });

        canvas.setOnMouseReleased(mouseEvent -> {
            if(model.isSelection() && !model.isPointtype()){
                if (needchange && isseg) {
                    canvas.setCursor(Cursor.DEFAULT);
                    model.segementrelease(preview1, preview2);
                    preview1 = null;
                    preview2 = null;
                    needchange = false;
                    isseg = false;
                } else if (needchange && isconnect){
                    canvas.setCursor(Cursor.DEFAULT);
                    model.connectrelease(preview1, preview2);
                    preview1 = null;
                    preview2 = null;
                    needchange = false;
                    isconnect = false;
                } else if (isseg || isconnect){
                    canvas.setCursor(Cursor.DEFAULT);
                    needchange = false;
                    isseg =false;
                    isconnect = false;
                }
            }
            updateView();
        });

        this.getChildren().add(canvas);
        canvas.widthProperty().bind(
                this.widthProperty());
        canvas.heightProperty().bind(
                this.heightProperty());
        canvas.widthProperty().addListener(event->draw());
        canvas.heightProperty().addListener(event->draw());
        model.addView(this);
    }

    public void drawcubic(CubicCurve curve){
        gc.beginPath();
        gc.moveTo(curve.getStartX(),curve.getStartY());
            /*System.out.println(curve.getStartX() + " "+ curve.getStartY()
                + " "+ curve.getControlX1() + " "+ curve.getControlY1()+ " "+
                curve.getControlX2()+ " "+ curve.getControlY2()+ " "+curve.getEndX()+ " "+curve.getEndY());*/
        gc.bezierCurveTo(curve.getControlX1(),curve.getControlY1(),curve.getControlX2(),curve.getControlY2(),
                curve.getEndX(),curve.getEndY());
        gc.stroke();
    }

    public void drawcurve(Model.cubiccurve cubiccurve){
        CubicCurve curve = cubiccurve.getcubic();
        drawcubic(curve);
        if(cubiccurve.isSelect()){
            //line
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.setLineDashes(0);
            Line line = cubiccurve.getL1();
            gc.strokeLine(line.getStartX(),line.getStartY(),line.getEndX(),line.getEndY());
            line = cubiccurve.getL2();
            gc.strokeLine(line.getStartX(),line.getStartY(),line.getEndX(),line.getEndY());
            //control point
            gc.setFill(Color.BLACK);
            if((cubiccurve.getStarttype().equals("smooth"))||(cubiccurve.getStarttype().equals("start"))){
                Circle control = cubiccurve.getC1();
                gc.fillRect(control.getCenterX(), control.getCenterY(), model.pointsize-2, model.pointsize-2);
            }
            if((cubiccurve.getEndtype().equals("smooth"))||(cubiccurve.getEndtype().equals("end"))){
                Circle control = cubiccurve.getC2();
                gc.fillRect(control.getCenterX(), control.getCenterY(), model.pointsize-2, model.pointsize-2);
            }
            //segament point
            Circle circle = cubiccurve.getStart();
            gc.setFill(circle.getFill());
            gc.fillOval(circle.getCenterX(),circle.getCenterY(),circle.getRadius(),circle.getRadius());
            circle = cubiccurve.getEnd();
            gc.setFill(circle.getFill());
            gc.fillOval(circle.getCenterX(),circle.getCenterY(),circle.getRadius(),circle.getRadius());
        }
    }


    public void draw(){
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        gc.beginPath();
        if (model.getcurvelist() != null &&!model.getcurvelist().isEmpty()){
            for(ArrayList<Model.cubiccurve> curvelist: model.getcurvelist()) {
                for(Model.cubiccurve curve: curvelist){
                    gc.setStroke(curve.getColor());
                    gc.setLineWidth(curve.getThick());
                    gc.setLineDashes(curve.getdash());
                    drawcurve(curve);
                }
            }
        }
        if (model.isDrawing()||model.isSelection()){
            if ((model.getcurve()!=null) && !model.getcurve().isEmpty()){
                for(Model.cubiccurve curve: model.getcurve()){
                    gc.setStroke(curve.getColor());
                    gc.setLineWidth(curve.getThick());
                    gc.setLineDashes(curve.getdash());
                    drawcurve(curve);
                }
            }
            if (model.isDrawing()&& (preview != null)){
                gc.setStroke(Color.LIGHTGRAY);
                gc.setLineWidth(1);
                gc.setLineDashes(0);
                drawcubic(preview);
                preview = null;
            }
            if(preview1 != null){
                gc.setStroke(Color.LIGHTGRAY);
                gc.setLineWidth(1);
                gc.setLineDashes(0);
                drawcurve(preview1);
            }
            if(preview2 != null){
                gc.setStroke(Color.LIGHTGRAY);
                gc.setLineWidth(1);
                gc.setLineDashes(0);
                drawcurve(preview2);
            }
        }
    }

    public void updateView() {
        if(model.isEarse()){
            Image cursor = new Image("cursor.jpg");
            canvas.setCursor(new ImageCursor(cursor,cursor.getWidth()/5,cursor.getHeight()/5));
        } else {
            canvas.setCursor(Cursor.DEFAULT);
        }
        draw();
    }
}
