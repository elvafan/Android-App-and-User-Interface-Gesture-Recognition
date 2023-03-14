import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import static javafx.scene.paint.Color.LIGHTGRAY;

public class Main extends Application {
    int framecount = 0;
    Boolean [][] array = new Boolean[75][50];
    public void initialize(){
        for (int i = 0; i<75;i++){
            for(int j = 0; j<50; j++){
                array[i][j] = false;
            }
        }
    }
    public int index (double x) {
        return (int)(x /12);
    }
    public void addcell(int x, int y){
        if(x>=0&&x<=74&&y>=0&&y<=49){
            array[x][y] = true;
        }
    }
    public int nei (int x, int y){
        int n = 0;
        if ((x>0)&&(y>0)){
            if (array[x-1][y-1]){
                n++;
            }
        } if ((x<74)&&(y>0)) {
            if (array[x+1][y-1]){
                n++;
            }
        }
        if ((x>0)&&(y<49)){
            if (array[x-1][y+1]){
                n++;
            }
        }
        if ((x<74)&&(y<49)){
            if (array[x+1][y+1]){
                n++;
            }
        }
        if (y > 0){
            if (array[x][y-1]){
                n++;
            }
        }
        if (x>0){
            if (array[x-1][y]){
                n++;
            }
        }
        if (x<74){
            if (array[x+1][y]){
                n++;
            }
        }
        if (y<49){
            if (array[x][y+1]){
                n++;
            }
        }
        return n;
    }

    public void update(){
        framecount++;
        Boolean [][] temp = new Boolean[75][50];
        for (int i = 0; i<75;i++){
            for(int j = 0; j<50; j++){
                int n = nei(i,j);
                if ((array[i][j])&&((n < 2)||(n>3))){
                    temp[i][j] = false;
                } else if ((!array[i][j])&&(n==3)){
                    temp[i][j] = true;
                } else {
                    temp[i][j] = array[i][j];
                }
            }
        }
        array = temp;
    }

    public void clear(GraphicsContext gc){
        gc.clearRect(0, 0, 900, 600);
    }
    public void draw(GraphicsContext gc){
        gc.setFill(Color.BLACK);
        for (int i = 0; i<75;i++){
            for(int j = 0; j<50; j++){
                if (array[i][j]){
                    gc.fillRect(i*12,j*12,12,12);
                }
            }
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        //create button items
        Button Block = new Button("Block");
        Image Blockimage = new Image("block.jpg");
        ImageView BlockimageView = new ImageView(Blockimage);
        BlockimageView.setFitHeight(25);
        BlockimageView.setFitWidth(25);
        Block.setGraphic(BlockimageView);

        Button Beehive = new Button("Beehive");
        Image Beehiveimage = new Image("beehive.jpg");
        ImageView BeehiveimageView = new ImageView(Beehiveimage);
        BeehiveimageView.setFitHeight(25);
        BeehiveimageView.setFitWidth(38);
        Beehive.setGraphic(BeehiveimageView);

        Button Blinker = new Button("Blinker");
        Image Blinkerimage = new Image("blinker.jpg");
        ImageView BlinkerimageView = new ImageView(Blinkerimage);
        BlinkerimageView.setFitHeight(25);
        BlinkerimageView.setFitWidth(30);
        Blinker.setGraphic(BlinkerimageView);

        Button Toad = new Button("Toad");
        Image Toadimage = new Image("toad.jpg");
        ImageView ToadimageView = new ImageView(Toadimage);
        ToadimageView.setFitHeight(25);
        ToadimageView.setFitWidth(40);
        Toad.setGraphic(ToadimageView);

        Button Glider = new Button("Glider");
        Image Gliderimage = new Image("glider.jpg");
        ImageView GliderimageView = new ImageView(Gliderimage);
        GliderimageView.setFitHeight(25);
        GliderimageView.setFitWidth(25);
        Glider.setGraphic(GliderimageView);

        Button Clear = new Button("Clear");
        Image Clearimage = new Image("clear.jpg");
        ImageView ClearimageView = new ImageView(Clearimage);
        ClearimageView.setFitHeight(25);
        ClearimageView.setFitWidth(25);
        Clear.setGraphic(ClearimageView);

        //put together
        ToolBar toolbar = new ToolBar(Block, Beehive, new Separator(), new Separator(),
                Blinker, Toad, Glider, new Separator(), new Separator(),
                Clear);

        // status bar
        Label fc = new Label();
        Label instr = new Label("   press  M: manual mode   N: next frame   E: exit manual mode");
        BorderPane status = new BorderPane();
        status.setLeft(instr);
        status.setRight(fc);

        // grid
        Group grid = new Group();
        // Use the graphics context to draw on a canvas draw gridline
        final Canvas gridcanvas = new Canvas(900, 600);
        GraphicsContext gridgc = gridcanvas.getGraphicsContext2D();
        /*gridgc.setFill(Color.WHITE);
        gridgc.fillRect(0,0,900,600);*/
        gridgc.setLineWidth(1);
        gridgc.setStroke(LIGHTGRAY);
        double mult = 12;
        for (double i = 0; i <= 50; i++) {
            double y = i * mult;
            gridgc.strokeLine(0, y, 900, y);
        }
        for (double i = 0; i <= 75; i++) {
            double x = i * mult;
            gridgc.strokeLine(x, 0, x, 600);
        }

        // block canvas
        final Canvas canvas = new Canvas(900, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        initialize();

        // Add the canvas to the scene
        grid.getChildren().add(canvas);
        grid.getChildren().add(gridcanvas);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            update();
            clear(gc);
            draw(gc);
            String f = "frame " + framecount + "  ";
            fc.setText(f);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        //setup handler
        Block.setOnAction(event -> {
            grid.setOnMouseClicked(mouseEvent->{
            int x = index(mouseEvent.getX());
            int y = index(mouseEvent.getY());
            //gc.fillRect(x,y,24,24);
            addcell(x,y);
            addcell(x+1,y);
            addcell(x,y+1);
            addcell(x+1,y+1);
            draw(gc); });});

        Beehive.setOnAction(event->{
            grid.setOnMouseClicked(mouseEvent->{
                int x = index(mouseEvent.getX());
                int y = index(mouseEvent.getY());
                addcell(x+1,y);
                addcell(x+2,y);
                addcell(x,y+1);
                addcell(x+3,y+1);
                addcell(x+1,y+2);
                addcell(x+2,y+2);
                draw(gc); });});

        Blinker.setOnAction(event->{
            grid.setOnMouseClicked(mouseEvent->{
            int x = index(mouseEvent.getX());
            int y = index(mouseEvent.getY());
            addcell(x,y+1);
            addcell(x+1,y+1);
            addcell(x+2,y+1);
            draw(gc); });});

        Toad.setOnAction(event->{
            grid.setOnMouseClicked(mouseEvent->{
                int x = index(mouseEvent.getX());
                int y = index(mouseEvent.getY());
                addcell(x+1,y);
                addcell(x+2,y);
                addcell(x+3,y);
                addcell(x,y+1);
                addcell(x+1,y+1);
                addcell(x+2,y+1);
                draw(gc); });});

        Glider.setOnAction(event->{
            grid.setOnMouseClicked(mouseEvent->{
            int x = index(mouseEvent.getX());
            int y = index(mouseEvent.getY());
            addcell(x+2,y);
            addcell(x,y+1);
            addcell(x+2,y+1);
            addcell(x+1,y+2);
            addcell(x+2,y+2);
            draw(gc);
            });});

        Clear.setOnAction(event->{ clear(gc);
        initialize();});


        //display on the scene
        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(grid);
        root.setBottom(status);

        Scene scene = new Scene(root, 900, 660);
        scene.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.M)){
                timeline.pause();
            } else if(event.getCode().equals(KeyCode.N)){
                update();
                clear(gc);
                draw(gc);
                String f = "frame " + framecount + "  ";
                fc.setText(f);
            } else if (event.getCode().equals(KeyCode.E)){
                timeline.play();
            }
        });

        stage.setResizable(false);
        stage.setTitle("Conway's Game of Life (x55fan)");
        stage.setScene(scene);
        stage.show();
    }
}
