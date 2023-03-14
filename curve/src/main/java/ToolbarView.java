import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ToolbarView extends VBox implements IView {
    private Model model;

    private Button pentool = new Button();
    private Button erasetool = new Button();
    private Button selectiontool = new Button();
    private Button pointtypetool = new Button();
    private Button addpointtool = new Button();
    private Button removepointtool = new Button();

    private final ColorPicker colorPicker = new ColorPicker();
    private Button thick1 = new Button();
    private Button thick2 = new Button();
    private Button thick3 = new Button();

    private Button solidline = new Button();
    private Button dashline = new Button();
    private Button dotline = new Button();
    TilePane propertypalette = new TilePane(Orientation.VERTICAL);

    ToolbarView(Model model) {
        // keep track of the model
        this.model = model;

        // setup the view (i.e. group+widget)
        //this.setMinSize(HelloMVC3.WINDOW_WIDTH, HelloMVC3.WINDOW_HEIGHT/2);


        //set all button
        ImageView penimageView = new ImageView(new Image("pen.jpg"));
        penimageView.setFitHeight(25);
        penimageView.setFitWidth(25);
        pentool.setGraphic(penimageView);

        ImageView eraseimageView = new ImageView(new Image("erase.jpg"));
        eraseimageView.setFitHeight(25);
        eraseimageView.setFitWidth(25);
        erasetool.setGraphic(eraseimageView);

        ImageView selectimageView = new ImageView(new Image("select.jpg"));
        selectiontool.setGraphic(selectimageView);
        selectimageView.setFitHeight(25);
        selectimageView.setFitWidth(25);

        ImageView pointimageView = new ImageView(new Image("point.jpg"));
        pointimageView.setFitHeight(25);
        pointimageView.setFitWidth(25);
        pointtypetool.setGraphic(pointimageView);

        ImageView addpointView = new ImageView(new Image("add.jpg"));
        addpointView.setFitHeight(25);
        addpointView.setFitWidth(25);
        addpointtool.setGraphic(addpointView);

        ImageView removepointView = new ImageView(new Image("remove.jpg"));
        removepointView.setFitHeight(25);
        removepointView.setFitWidth(25);
        removepointtool.setGraphic(removepointView);

        ImageView thick1imageView = new ImageView(new Image("thick1.jpg"));
        thick1imageView.setFitHeight(16);
        thick1imageView.setFitWidth(16);
        thick1.setGraphic(thick1imageView);
        thick1.setPadding(new Insets(2, 2, 2, 2));

        ImageView thick2imageView = new ImageView(new Image("thick2.jpg"));
        thick2imageView.setFitHeight(16);
        thick2imageView.setFitWidth(16);
        thick2.setGraphic(thick2imageView);
        thick2.setPadding(new Insets(2, 2, 2, 2));

        ImageView thick3imageView = new ImageView(new Image("thick3.jpg"));
        thick3imageView.setFitHeight(16);
        thick3imageView.setFitWidth(16);
        thick3.setGraphic(thick3imageView);
        thick3.setPadding(new Insets(2, 2, 2, 2));

        ImageView solidlineimageView = new ImageView(new Image("thick1.jpg"));
        solidlineimageView.setFitHeight(16);
        solidlineimageView.setFitWidth(16);
        solidline.setGraphic(solidlineimageView);
        solidline.setPadding(new Insets(2, 2, 2, 2));

        ImageView dashlineimageView = new ImageView(new Image("dashline.jpg"));
        dashlineimageView.setFitHeight(16);
        dashlineimageView.setFitWidth(16);
        dashline.setGraphic(dashlineimageView);
        dashline.setPadding(new Insets(2, 2, 2, 2));

        ImageView dotlineimageView = new ImageView(new Image("dotline.jpg"));
        dotlineimageView.setFitHeight(16);
        dotlineimageView.setFitWidth(16);
        dotline.setGraphic(dotlineimageView);
        dotline.setPadding(new Insets(2, 2, 2, 2));


        //set tool palette
        TilePane toolpalette = new TilePane(Orientation.VERTICAL);
        toolpalette.setPadding(new Insets(20, 0, 0, 5));
        toolpalette.setPrefRows(3);
        toolpalette.setPrefColumns(2);
        toolpalette.setHgap(5);
        toolpalette.setVgap(5);
        toolpalette.getChildren().addAll(pentool,selectiontool,addpointtool,erasetool,pointtypetool,removepointtool);

        //property
        colorPicker.setValue(Color.BLACK);
        model.setstatecolor(colorPicker.getValue());
        colorPicker.getStyleClass().add("button");
        colorPicker.setPrefWidth(85);

        TilePane thicknesspalette = new TilePane(Orientation.VERTICAL);
        thicknesspalette.setPrefRows(1);
        thicknesspalette.setPrefColumns(3);
        thicknesspalette.setPadding(new Insets(20, 0, 0, 5));
        thicknesspalette.setHgap(5);
        thicknesspalette.setVgap(5);
        thicknesspalette.getChildren().addAll(thick1,thick2,thick3);

        TilePane stylepalette = new TilePane(Orientation.VERTICAL);
        stylepalette.setPrefRows(1);
        stylepalette.setPrefColumns(3);
        stylepalette.setPadding(new Insets(20, 0, 0, 5));
        stylepalette.setHgap(5);
        stylepalette.setVgap(5);
        stylepalette.getChildren().addAll(solidline,dotline,dashline);


        // the previous controller code will just be handled here
        // we don't need always need a separate controller class!
        pentool.setOnMouseClicked(mouseEvent -> {
            model.enterdrawing();
            //System.out.println("enter drawing mode");
        });

        selectiontool.setOnMouseClicked(mouseEvent -> {
            model.enterselection();
            model.exiterase();
            //System.out.println("enter selection mode");
            if (model.isPointtype()){
                model.exitpointtype();
            } else if (model.isAddpointmode()){
                model.exitAddpointmode();
            } else if (model.isRemovepointmode()){
                model.exitRemovepointmode();
            }
        });

        erasetool.setOnMouseClicked(mouseEvent -> {
            model.entererase();
            //System.out.println("enter selection mode");
        });

        pointtypetool.setOnMouseClicked(mouseEvent -> {
            model.enterpointtype();
            model.exitAddpointmode();
            model.exitRemovepointmode();
            //System.out.println("enter point change");
        });

        addpointtool.setOnMouseClicked(mouseEvent -> {
            model.enterAddpointmode();
            model.exitRemovepointmode();
            model.exitpointtype();
            //System.out.println("enter add point");
        });

        removepointtool.setOnMouseClicked(mouseEvent -> {
            model.enterRemovepointmode();
            model.exitAddpointmode();
            model.exitpointtype();
            //System.out.println("enter remove point");
        });

        colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                model.setstatecolor(colorPicker.getValue());
            }
        });

        thick1.setOnMouseClicked(mouseEvent -> {
            model.setstatethick(1);
        });

        thick2.setOnMouseClicked(mouseEvent -> {
            model.setstatethick(1.6);
        });

        thick3.setOnMouseClicked(mouseEvent -> {
            model.setstatethick(2.3);
        });

        solidline.setOnMouseClicked(mouseEvent -> {
            model.setStatedash(0);
        });
        dotline.setOnMouseClicked(mouseEvent -> {
            model.setStatedash(3);
        });
        dashline.setOnMouseClicked(mouseEvent -> {
            model.setStatedash(10);
        });

        propertypalette.setPadding(new Insets(20, 0, 0, 5));
        propertypalette.getChildren().addAll(colorPicker,thicknesspalette, stylepalette);
        // add button widget to the pane
        this.setMargin(colorPicker , new Insets( 20,0,20,5));

        this.getChildren().addAll(toolpalette,propertypalette);

        // register with the model when we're ready to start receiving data
        model.addView(this);
    }

    public void updateView() {
        if (model.isDrawing()){
            selectiontool.setDisable(true);
            erasetool.setDisable(true);
            pointtypetool.setDisable(true);
            addpointtool.setDisable(true);
            removepointtool.setDisable(true);
            propertypalette.setDisable(true);
        } else if (model.isSelection()){
            pentool.setDisable(true);
            erasetool.setDisable(true);
            if(model.getcurve()!= null){
                addpointtool.setDisable(false);
                pointtypetool.setDisable(false);
                removepointtool.setDisable(false);
            }
            propertypalette.setDisable(false);
        } else if (model.isEarse()) {
            pentool.setDisable(true);
            pointtypetool.setDisable(true);
            addpointtool.setDisable(true);
            removepointtool.setDisable(true);
            propertypalette.setDisable(true);
        } else {
            pentool.setDisable(false);
            selectiontool.setDisable(false);
            erasetool.setDisable(false);
            pointtypetool.setDisable(true);
            addpointtool.setDisable(true);
            removepointtool.setDisable(true);
            propertypalette.setDisable(false);
        }

    }
}
