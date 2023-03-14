import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import java.util.ArrayList;

public class Model {
    private boolean drawing = false;
    private boolean selection = false;
    private boolean erase = false;
    private boolean pointtype = false;
    private boolean addpointmode = false;
    private boolean removepointmode = false;
    private ArrayList<cubiccurve> curcurve = null;
    private ArrayList<ArrayList<cubiccurve>> allcurve  = new ArrayList<>();
    private ArrayList<ArrayList<curvestring>> allcurvestring  = new ArrayList<>();
    private ArrayList<ArrayList<curvestring>> savedcurve  = new ArrayList<>();
    double startx  = -1;
    double starty = -1;
    boolean needchange = false;
    public double pointsize = 6;
    private int currentindex = -1;
    private double d = 50;
    private String segtype = "-1";
    private String connecttype = "-1";
    private Color statecolor;
    private double statethick = 1;
    private double statedash = 0;

    // all views of this model
    private ArrayList<IView> views = new ArrayList<IView>();

    // method that the views can use to register themselves with the Model
    // once added, they are told to update and get state from the Model
    public void addView(IView view) {
        views.add(view);
        view.updateView();
    }

    public void enterdrawing (){
        curcurve = new ArrayList<>();
        drawing = true;
        notifyObservers();
    }

    public void exitdrawing (){
        drawing = false;
        startx  = -1;
        starty = -1;
        for(cubiccurve curve: curcurve){
            curve.unsetSelect();
        }
        allcurve.add(curcurve);
        curcurve = new ArrayList<>();
        curcurve = null;
        notifyObservers();
    }

    public boolean isDrawing(){
        return drawing;
    }

    public void enterselection (){
        selection = true;
        notifyObservers();
    }
    public void exitselection (){
        selection = false;
        exitpointtype();
        exitAddpointmode();
        exitRemovepointmode();
        notifyObservers();
    }
    public boolean isSelection(){
        return selection;
    }

    public void entererase(){
        erase = true;
        notifyObservers();
    }
    public boolean isEarse(){
        return erase;
    }
    public void exiterase(){
        erase = false;
        notifyObservers();
    }

    public void enterpointtype(){
        pointtype = true;
        notifyObservers();
    }
    public boolean isPointtype(){
        return pointtype;
    }
    public void exitpointtype(){
        pointtype = false;
        notifyObservers();
    }

    public void enterAddpointmode(){
        addpointmode = true;
        notifyObservers();
    }
    public boolean isAddpointmode(){
        return addpointmode;
    }
    public void exitAddpointmode(){
        addpointmode = false;
        notifyObservers();
    }

    public void enterRemovepointmode(){
        removepointmode = true;
        notifyObservers();
    }
    public boolean isRemovepointmode(){
        return removepointmode;
    }
    public void exitRemovepointmode(){
        removepointmode = false;
        notifyObservers();
    }

    public void drawpoint(double x, double y){
        cubiccurve newcurve = new cubiccurve();
        if ((startx == -1)&&(starty == -1)) {
            newcurve = new cubiccurve(x, y, x, y,statecolor,statethick,statedash);
            needchange = true;
        } else {
            if (needchange){
                curcurve.clear();
            }
            newcurve = new cubiccurve(startx, starty, x, y,statecolor,statethick,statedash);
            if (curcurve.size()>0){
                cubiccurve previous = curcurve.get(curcurve.size()-1);
                previous.setEndtype("smooth");
                newcurve.connect(previous);
                newcurve.setStarttype("smooth");
            }
            needchange = false;
        }
        curcurve.add(newcurve);
        startx = x;
        starty = y;
        notifyObservers();
    }

    public CubicCurve preview(double x, double y){
        CubicCurve precur = new CubicCurve();
        if ((startx == -1)&&(starty == -1)){
            precur.setStartX(x);
            precur.setStartY(y);
            precur.setControlX1(x);
            precur.setControlY1(y+d);
        } else{
            precur.setStartX(startx);
            precur.setStartY(starty);
            precur.setControlX1(startx);
            precur.setControlY1(starty+d);
        }
        precur.setEndX(x);
        precur.setEndY(y);
        precur.setControlX2(x);
        precur.setControlY2(y-d);
        return precur;
    }

    public ArrayList<cubiccurve> getcurve (){
        return curcurve;
    }

    public ArrayList<ArrayList<cubiccurve>> getcurvelist (){
        return allcurve;
    }

    public void selectcurve(){
        for(cubiccurve curve: curcurve){
            curve.setSelect();
        }
        notifyObservers();
    }

    public void unselectcurve(){
        for(cubiccurve curve: curcurve){
            curve.unsetSelect();
        }
        allcurve.add(curcurve);
        curcurve = null;
        notifyObservers();
    }

    public void deletecurve(){
        if (curcurve != null){
            curcurve.clear();
            curcurve = null;
        }else{
            System.out.println("remove null");
        }
        notifyObservers();
    }

    public Boolean iscurvecontain(double x, double y){
        for(ArrayList<cubiccurve> curlist: allcurve){
            for(cubiccurve curve: curlist){
                for (double i = -2.5; i <= 2.5; i = i + 0.5){
                    for(double j = -2.5; j< 2.5; j= j + 0.5){
                        if (curve.getcubic().contains(x+j,y+i)){
                            curcurve = curlist;
                            boolean result = allcurve.remove(curcurve);
                            if (! result){
                                System.out.println("remove wrong");
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public String issegement(double x, double y){
        String segpos = "-1";
        if (curcurve != null){
            for(cubiccurve curve: curcurve){
                if (curve.getStart().contains(x,y) ){
                    currentindex = curcurve.indexOf(curve);
                    segpos = "start";
                    break;
                } if (curve.getEnd().contains(x,y)) {
                    currentindex = curcurve.indexOf(curve);
                    segpos = "end";
                    break;
                }
            }
        } else {
            //System.out.println("no curve select");
        }

        if (segpos.equals("start")){
            return curcurve.get(currentindex).getStarttype();
        } else if (segpos.equals("end")) {
            return curcurve.get(currentindex).getEndtype();
        } else {
            currentindex = -1;
            return segpos;
        }
    }


    public void changetype (double x, double y){
        String type = issegement(x,y);
        //System.out.println("type" + type);
        if (currentindex != -1){
            cubiccurve thiscurve = curcurve.get(currentindex);
            if ((type.equals("smooth"))||(type.equals("sharp"))) {
                thiscurve.changeendtype();
                cubiccurve next = curcurve.get(currentindex+1);
                next.connect(thiscurve);
                next.changestarttype();
            }
        }
        notifyObservers();
    }

    public Boolean segementpress (double x, double y){
        String type = issegement(x,y);
        if (currentindex != -1) {
            segtype = type;
            return true;
        }else {
            return false;
        }
    }

    public ArrayList<cubiccurve> segementdrag (double x, double y) {
        cubiccurve thiscurve = curcurve.get(currentindex);
        ArrayList<cubiccurve> twocurve = new ArrayList<>();
        if(segtype.equals("start")){
            cubiccurve newc =thiscurve.updatestart(x,y);
            twocurve.add(0,newc);
            twocurve.add(1,null);
        } else if(segtype.equals("end")){
            cubiccurve newc = thiscurve.updateend(x,y);
            twocurve.add(0,null);
            twocurve.add(1,newc);
        } else{
            cubiccurve newc = thiscurve.updateend(x,y);
            twocurve.add(0,newc);
            cubiccurve next = curcurve.get(currentindex+1).updatestart(x,y);
            next.connect(twocurve.get(0));
            twocurve.add(1,next);
        }
        return twocurve;
    }

    public void segementrelease (cubiccurve p1, cubiccurve p2) {
        cubiccurve thiscurve = curcurve.get(currentindex);
        if (thiscurve != null) {
            if (segtype.equals("start")) {
                curcurve.set(currentindex,p1);
            } else if (segtype.equals("end")) {
                curcurve.set(currentindex,p2);
            } else {
                curcurve.set(currentindex,p1);
                p2.connect(curcurve.get(currentindex));
                curcurve.set(currentindex+1,p2);
            }
            /*for (cubiccurve curve : curcurve){
                System.out.println(curve.getStarttype());
                System.out.println(curve.getEndtype());
            }*/
        }
    }

    public String isconnect(double x, double y) {
        String segpos = "-1";
        if (curcurve != null) {
            for (cubiccurve curve : curcurve) {
                if (curve.getC1().contains(x, y)) {
                    currentindex = curcurve.indexOf(curve);
                    segpos = "c1";
                    break;
                }
                if (curve.getC2().contains(x, y)) {
                    currentindex = curcurve.indexOf(curve);
                    segpos = "c2";
                    break;
                }
            }
        } else {
            //System.out.println("no curve select");
        }
        if (segpos.equals("-1")){
            currentindex = -1;
        } else if (currentindex == 0 && segpos.equals("c1")){
            segpos = "start";
        } else if (currentindex == curcurve.size()-1 && segpos.equals("c2")){
            segpos = "end";
        }
        return segpos;
    }

    public Boolean connectpress (double x, double y){
        connecttype = isconnect(x,y);
        return currentindex != -1;
    }

    class pos{
        double x;
        double y;

        public pos (){
            this.x = -1;
            this.y = -1;
        }
    }

    private pos calculate (double startx, double starty, double x, double y,double Bx, double By){
        pos newc2 = new pos();
        double dx = Bx - startx;
        double dy = By - starty;
        double Bd =Math.sqrt(Math.pow(Math.abs(dx),2)+Math.pow(Math.abs(dy),2));

        dx = x - startx;
        dy = y - starty;
        double Ad = Math.sqrt(Math.pow(Math.abs(dx),2)+Math.pow(Math.abs(dy),2));

        double r = Bd/Ad;

        newc2.x = r*(startx - x) + startx;
        newc2.y = r*(starty - y) + starty;

        return newc2;
    }

    public ArrayList<cubiccurve> connectdrag (double x, double y) {
        cubiccurve thiscurve = curcurve.get(currentindex);
        ArrayList<cubiccurve> twocurve = new ArrayList<>();
        if(connecttype.equals("start")){
            cubiccurve newc = thiscurve.updatec1(x,y);
            twocurve.add(0,null);
            twocurve.add(1,newc);
        } else if(connecttype.equals("end")){
            cubiccurve newc = thiscurve.updatec2(x,y);
            twocurve.add(0,newc);
            twocurve.add(1,null);
        } else if(connecttype.equals("c1")){
            cubiccurve precurve  = curcurve.get(currentindex -1);
            pos newc2 = calculate(thiscurve.curve.getStartX(),thiscurve.curve.getStartY(),x,y,
                    precurve.getcubic().getControlX2(),precurve.getcubic().getControlY2());
            cubiccurve newc = precurve.updatec2(newc2.x,newc2.y);
            twocurve.add(0, newc);
            newc = thiscurve.updatec1(x,y);
            newc.connect(twocurve.get(0));
            twocurve.add(1,newc);
        } else if (connecttype.equals("c2")){
            cubiccurve newc = thiscurve.updatec2(x,y);
            twocurve.add(0,newc);
            cubiccurve nextcurve  = curcurve.get(currentindex + 1);
            pos newc1 = calculate(nextcurve.curve.getStartX(),nextcurve.curve.getStartY(),x,y,
                    nextcurve.getcubic().getControlX1(),nextcurve.getcubic().getControlY1());
            newc = nextcurve.updatec1(newc1.x,newc1.y);
            newc.connect(twocurve.get(0));
            twocurve.add(1,newc);
        } else {
            twocurve.add(0,null);
            twocurve.add(1,null);
            System.out.println("wrong connect type"+ connecttype);
        }
        return twocurve;
    }

    public void connectrelease (cubiccurve p1, cubiccurve p2) {
        cubiccurve thiscurve = curcurve.get(currentindex);
        if (thiscurve != null) {
            if (connecttype.equals("start")) {
                curcurve.set(currentindex,p2);
            } else if (connecttype.equals("end")) {
                curcurve.set(currentindex,p1);
            } else if(connecttype.equals("c1")){
                curcurve.set(currentindex-1,p1);
                curcurve.set(currentindex,p2);
            } else if (connecttype.equals("c2")){
                curcurve.set(currentindex,p1);
                curcurve.set(currentindex+1,p2);
            }
        }
    }

    public Color getstatecolor (){
        return statecolor;
    }

    public void setstatecolor (Color c){
        statecolor = c;
        if(isSelection()){
            for(cubiccurve curve: curcurve){
                curve.setColor(statecolor);
            }
        }
        notifyObservers();
    }

    public double getstatethick (){
        return statethick;
    }

    public void setstatethick (double t){
        statethick = t;
        if(isSelection()){
            for(cubiccurve curve: curcurve){
                curve.setThick(statethick);
            }
        }
        notifyObservers();
    }

    public double getStatedash (){
        return statedash;
    }

    public void setStatedash (double d){
        statedash = d;
        if(isSelection()){
            for(cubiccurve curve: curcurve){
                curve.setdash(statedash);
            }
        }
        notifyObservers();
    }

    public int iscurcurvecontain (double x, double y){
        int contain = -1;
        for(cubiccurve curve: curcurve){
            for (double i = -2.5; i <= 2.5; i = i + 0.5) {
                for(double j = -2.5; j< 2.5; j= j + 0.5) {
                    if (curve.getcubic().contains(x+j, y + i)) {
                        contain = curcurve.indexOf(curve);
                        break;
                    }
                }
            }
        }
        return contain;
    }

    public void addpoint(double x, double y){
        int index = iscurcurvecontain(x,y);
        if (index != -1){
            ArrayList<cubiccurve> newlist = new ArrayList<>();
            cubiccurve thiscurve = curcurve.get(index);
            for(int i = 0; i<curcurve.size();i++){
                if (index == i){
                    cubiccurve new1 = new cubiccurve(thiscurve.getcubic().getStartX(),thiscurve.getcubic().getStartY(),
                            x,y,thiscurve.getColor(),thiscurve.getThick(),thiscurve.getdash());
                    new1.copystart(thiscurve);
                    new1.setEndtype("smooth");
                    cubiccurve new2 = new cubiccurve(x,y,thiscurve.getcubic().getEndX(),thiscurve.getcubic().getEndY(),
                            thiscurve.getColor(),thiscurve.getThick(),thiscurve.getdash());
                    new2.copyend(thiscurve);
                    new2.setStarttype("smooth");
                    newlist.add(new1);
                    new2.connect(newlist.get(index));
                    newlist.add(new2);
                } else {
                    newlist.add(curcurve.get(i));
                }
            }
            curcurve = newlist;
            /*for (cubiccurve curve : curcurve){
                System.out.println(curve.getStarttype());
                System.out.println(curve.getEndtype());
            }*/
            notifyObservers();
        }
    }

    public void removepoint (double x, double y){
        String segtype = issegement(x,y);
        if (currentindex != -1){
            ArrayList<cubiccurve> newlist = new ArrayList<>();
            if (segtype.equals("start")){
                curcurve.get(currentindex+1).setStarttype("start");
                curcurve.remove(currentindex);
            } else if (segtype.equals("end")){
                curcurve.get(currentindex-1).setEndtype("end");
                curcurve.remove(currentindex);
            } else{
                cubiccurve thiscurve = curcurve.get(currentindex);
                cubiccurve next = curcurve.get(currentindex+1);
                cubiccurve newcurve = new cubiccurve(thiscurve.getcubic().getStartX(),thiscurve.getcubic().getStartY(),
                        next.getcubic().getEndX(),next.getcubic().getEndY(),
                        thiscurve.getColor(),thiscurve.getThick(),thiscurve.getdash());
                newcurve.copystart(thiscurve);
                newcurve.copyend(next);
                curcurve.set(currentindex,newcurve);
                curcurve.remove(currentindex+1);

            }
            /*for (cubiccurve curve : curcurve){
                System.out.println(curve.getStarttype());
                System.out.println(curve.getEndtype());
            }*/
            notifyObservers();
        }
    }

    // the model uses this method to notify all of the Views that the data has changed
    // the expectation is that the Views will refresh themselves to display new data when appropriate
    private void notifyObservers() {
        for (IView view : this.views) {
            view.updateView();
        }
    }

    public class curvestring {
        private double[] curve; // 8
        private String starttype = "start";
        private String endtype = "end";
        private String color ; //String color = Color.RED.toString();
        private double thick;
        private double dash;

        public curvestring() {
        }

        public curvestring(double[] curve,String starttype, String endtype,
                           String color,double thick,double dash) {
            this.curve =  curve; // 8
            this.starttype = starttype;
            this.endtype = endtype;
            this.color = color;
            this.thick = thick;
            this.dash = dash;
        }

        public double[] getCurve(){
            return curve;
        }
        public String getStarttype(){
            return starttype;
        }
        public String getEndtype(){
            return endtype;
        }
        public String getColor(){
            return color;
        }
        public double getThick() {
            return thick;
        }
        public double getDash() {
            return dash;
        }
    }

    public Boolean samecurve(curvestring c1, curvestring c2){
        if(!c1.getStarttype().equals(c1.getStarttype())){
            //System.out.println(c1.getStarttype() +" "+c2.getStarttype());
            return false;
        }
        if (!c1.getEndtype().equals(c2.getEndtype())){
            //System.out.println(c1.getEndtype() +" "+c2.getEndtype());
            return false;
        }
        if (!c1.getColor().equals(c2.getColor())){
            //System.out.println(c1.getColor().toString() +" "+c2.getColor().toString());
            return false;
        }
        if (c1.getThick() != c2.getThick()){
            //System.out.println("thick");
            return false;
        }
        if (c1.getDash() != c2.getDash()){
            //System.out.println("dash");
            return false;
        }
        if (c1.getCurve().length != c2.getCurve().length){
            //System.out.println("curve");
            return false;
        } else{
            for (int i = 0; i < c1.getCurve().length; i++){
                if (c1.getCurve()[i] != c2.getCurve()[i]){
                    return false;
                }
            }
        }
        return true;
    }

    public Boolean save(){
        if (isSelection() || isDrawing()||isEarse()){
            exitselection();
            exiterase();
            exitdrawing();
        }
        Boolean needsave = false;
        if (!allcurve.isEmpty()){
            ArrayList<ArrayList<curvestring>> allcurlist = new ArrayList<>();
            for(ArrayList<cubiccurve> curlist: allcurve){
                ArrayList<curvestring> curstringlist = new ArrayList<>();
                for(cubiccurve cur: curlist){
                    CubicCurve cubic = cur.getcubic();
                    double[] cubiclist = new double[]{cubic.getStartX(),cubic.getStartY(),cubic.getEndX(),cubic.getEndY(),
                            cubic.getControlX1(),cubic.getControlY1(), cubic.getControlX2(), cubic.getControlY2()};
                    curvestring cs = new curvestring(cubiclist,cur.getStarttype(),cur.getEndtype(),
                            cur.getColor().toString(),cur.getThick(),cur.getdash());
                    curstringlist.add(cs);
                }
                allcurlist.add(curstringlist);
            }
            if (allcurlist.size() == savedcurve.size()){
                //System.out.println("same size");
                for(ArrayList<curvestring> curstring: allcurlist){
                    Boolean contain = false;
                    for(ArrayList<curvestring> savedstring: savedcurve){
                        if (curstring.size() == savedstring.size()){
                            boolean same = true;
                            for (int i = 0; i<curstring.size(); i++){
                                if (!samecurve(curstring.get(i),savedstring.get(i))){
                                    //System.out.println("curstring.get(i)"+ curstring.get(i));
                                    //System.out.println("savedstring.get(i)"+ savedstring.get(i));
                                    //System.out.println("same");
                                    same = false;
                                    break;
                                }
                            }
                            if(same){
                                contain = true;
                                break;
                            }
                        }
                    }
                    if (!contain){
                        needsave = true;
                        break;
                    }
                }
            } else {
                needsave = true;
            }
            if (needsave){
                allcurvestring = allcurlist;
            }
        }
        return needsave;
    }

    public ArrayList<ArrayList<curvestring>> getAllcurvestring (){
        return allcurvestring;
    }

    public void updatesave(ArrayList<ArrayList<curvestring>> list){
        savedcurve = list;
    }

    public void load (ArrayList<ArrayList<curvestring>> list){
        savedcurve = list;
        ArrayList<ArrayList<cubiccurve>> allcurlist = new ArrayList<>();
        for(ArrayList<curvestring> curlist: list){
            ArrayList<cubiccurve> curcubiclist = new ArrayList<>();
            for(curvestring curstring: curlist){
                double[] curve = curstring.getCurve();
                Color color = Color.valueOf(curstring.getColor());
                cubiccurve cubic = new cubiccurve(curve[0],curve[1],curve[2],curve[3], color,curstring.getThick(),curstring.getDash());
                cubic.updatecontrol(curve[4],curve[5],curve[6],curve[7]);
                cubic.setStarttype(curstring.getStarttype());
                cubic.setEndtype(curstring.getEndtype());
                cubic.updatefill();
                cubic.unsetSelect();
                if (curcubiclist.size() > 0) {
                    cubic.connect(curcubiclist.get(curcubiclist.size()-1));
                }
                curcubiclist.add(cubic);
            }
            allcurlist.add(curcubiclist);
        }
        allcurve = allcurlist;
        /*for(ArrayList<cubiccurve> lst : allcurve){
            for (cubiccurve curve : lst){
                System.out.println(curve.getStarttype());
                System.out.println(curve.getEndtype());
            }
        }*/
        notifyObservers();
    }

    public void clear(){
        curcurve = null;
        allcurve  = new ArrayList<>();
        allcurvestring  = new ArrayList<>();
        savedcurve  = new ArrayList<>();
        notifyObservers();
    }

    static class cubiccurve extends Shape {
        private double d = 50;
        private double correction = -2;
        private double pointsize = 6;
        private CubicCurve  curve = new CubicCurve();
        private boolean select = true;
        private Circle start = new Circle();
        private String starttype = "start";
        private Circle c1 = new Circle();
        private Line l1 = new Line();
        private Circle end = new Circle();
        private String endtype = "end";
        private Circle c2 = new Circle();
        private Line l2 = new Line();
        private Color color ;
        private double thick;
        private double dash;

        public cubiccurve(){}

        public cubiccurve (double startx, double starty, double endx, double endy, Color color,double thick,double dash){
            this.curve.setStartX(startx);
            this.curve.setStartY(starty);
            this.curve.setControlX1(startx);
            this.curve.setControlY1(starty+ d);
            this.curve.setEndX(endx);
            this.curve.setEndY(endy);
            this.curve.setControlX2(endx);
            this.curve.setControlY2(endy-d);

            this.start.setCenterX(startx + correction);
            this.start.setCenterY(starty + correction);
            this.start.setRadius(pointsize);

            this.c1.setCenterX(curve.getControlX1() + correction);
            this.c1.setCenterY(curve.getControlY1() + correction);
            this.c1.setRadius(pointsize);

            this.l1.setStartX(startx);
            this.l1.setStartY(starty);
            this.l1.setEndX(this.curve.getControlX1());
            this.l1.setEndY(this.curve.getControlY1());

            this.end.setCenterX(endx + correction);
            this.end.setCenterY(endy + correction);
            this.end.setRadius(pointsize);

            this.c2.setCenterX(curve.getControlX2() + correction);
            this.c2.setCenterY(curve.getControlY2() + correction);
            this.c2.setRadius(pointsize);

            this.l2.setStartX(endx);
            this.l2.setStartY(endy);
            this.l2.setEndX(this.curve.getControlX2());
            this.l2.setEndY(this.curve.getControlY2());

            this.color = color;
            this.thick = thick;
            this.dash = dash;
        }
        public CubicCurve getcubic() {
            return curve;
        }

        public Color getColor(){
            return color;
        }

        public void setColor(Color c){
            color = c;
        }
        public double getThick(){
            return thick;
        }

        public void setThick(double t){
            thick = t;
        }

        public double getdash(){
            return dash;
        }

        public void setdash(double d){
            dash = d;
        }

        public Circle getStart() {
            return start;
        }

        public void setStart(Circle c){
            start = c;
        }

        public String getStarttype(){
            return starttype;
        }

        public void setStarttype(String type){
            starttype = type;
        }

        public Circle getC1() {
            return c1;
        }

        public void setC1(Circle c){
            c1 = c;
        }

        public Line getL1() {
            return l1;
        }

        public void setL1(Line l){
            l1 = l;
        }

        public Circle getEnd() {
            return end;
        }

        public void setEnd(Circle c){
            end = c;
        }

        public String getEndtype(){
            return endtype;
        }

        public void setEndtype(String type){
            endtype = type;
        }

        public Circle getC2() {
            return c2;
        }

        public void setC2(Circle c){
            c2 = c;
        }

        public Line getL2() {
            return l2;
        }

        public void setL2(Line l){
            l2 = l;
        }

        public void connect (cubiccurve c){
            start = c.getEnd();
        }

        public void changeendtype (){
            if (endtype.equals("smooth")){
                curve.setControlX2(curve.getEndX());
                curve.setControlY2(curve.getEndY());
                end.setFill(Color.BLUE);
                c2 = end;
                l2.setEndX(curve.getControlX2());
                l2.setEndY(curve.getControlY2());
                endtype = "sharp";
            } else if (endtype.equals("sharp")){
                curve.setControlX2(curve.getEndX());
                curve.setControlY2(curve.getEndY() - d);
                end.setFill(Color.BLACK);
                c2 = new Circle();
                c2.setCenterX(curve.getControlX2() + correction);
                c2.setCenterY(curve.getControlY2() + correction);
                c2.setRadius(pointsize);
                l2.setEndX(curve.getControlX2());
                l2.setEndY(curve.getControlY2());
                endtype = "smooth";
            } else {
                System.out.println("change end wrong type");
            }
        }

        public void changestarttype() {
            if (starttype.equals("smooth")){
                curve.setControlX1(curve.getStartX());
                curve.setControlY1(curve.getStartY());
                c1 = start;
                l1.setEndX(curve.getControlX1());
                l1.setEndY(curve.getControlY1());
                starttype = "sharp";
            } else if (starttype.equals("sharp")){
                curve.setControlX1(curve.getStartX());
                curve.setControlY1(curve.getStartY()+d);
                c1 = new Circle();
                c1.setCenterX(curve.getControlX1() + correction);
                c1.setCenterY(curve.getControlY1() + correction);
                c1.setRadius(pointsize);
                l1.setEndX(curve.getControlX1());
                l1.setEndY(curve.getControlY1());
                starttype = "smooth";
            } else {
                System.out.println("change end wrong type");
            }
        }

        public cubiccurve updatestart(double x, double y){
            cubiccurve newc = new cubiccurve(x,y,curve.getEndX(),curve.getEndY(),color,thick,dash);
            newc.setStarttype(starttype);
            if (starttype.equals("sharp")){
                newc.getStart().setFill(Color.BLUE);
            } else {
                newc.getStart().setFill(Color.BLACK);
            }
            newc.setEndtype(endtype);
            if (endtype.equals("sharp")){
                newc.getEnd().setFill(Color.BLUE);
            } else {
                newc.getEnd().setFill(Color.BLACK);
            }
            newc.setEnd(end);
            newc.setC2(c2);
            newc.setL2(l2);
            newc.curve.setControlX2(curve.getControlX2());
            newc.curve.setControlY2(curve.getControlY2());

            double deltax = curve.getControlX1() - curve.getStartX();
            double deltay = curve.getControlY1() - curve.getStartY();
            newc.curve.setControlX1(x + deltax);
            newc.curve.setControlY1(y + deltay);
            newc.c1.setCenterX(newc.curve.getControlX1() + correction);
            newc.c1.setCenterY(newc.curve.getControlY1() + correction);
            newc.l1.setEndX(newc.curve.getControlX1());
            newc.l1.setEndY(newc.curve.getControlY1());

            select = false;
            return newc;
        }

        public cubiccurve updateend(double x, double y){
            cubiccurve newc = new cubiccurve(curve.getStartX(),curve.getStartY(),x,y,color,thick,dash);

            newc.setStarttype(starttype);
            if (starttype.equals("sharp")){
                newc.getStart().setFill(Color.BLUE);
            } else {
                newc.getStart().setFill(Color.BLACK);
            }
            newc.setEndtype(endtype);
            if (endtype.equals("sharp")){
                newc.getEnd().setFill(Color.BLUE);
            } else {
                newc.getEnd().setFill(Color.BLACK);
            }
            newc.setStart(start);
            newc.setC1(c1);
            newc.setL1(l1);
            newc.curve.setControlX1(curve.getControlX1());
            newc.curve.setControlY1(curve.getControlY1());

            double deltax = curve.getControlX2() - curve.getEndX();
            double deltay = curve.getControlY2() - curve.getEndY();
            newc.curve.setControlX2(x + deltax);
            newc.curve.setControlY2(y + deltay);
            newc.c2.setCenterX(newc.curve.getControlX2() + correction);
            newc.c2.setCenterY(newc.curve.getControlY2() + correction);
            newc.l2.setEndX(newc.curve.getControlX2());
            newc.l2.setEndY(newc.curve.getControlY2());


            select = false;
            return newc;
        }

        public cubiccurve updatec1 (double x, double y){
            cubiccurve newc = new cubiccurve(curve.getStartX(),curve.getStartY(),curve.getEndX(),curve.getEndY(),color,thick,dash);
            newc.setStarttype(starttype);
            if (starttype.equals("sharp")){
                newc.getStart().setFill(Color.BLUE);
            } else {
                newc.getStart().setFill(Color.BLACK);
            }
            newc.setEndtype(endtype);
            if (endtype.equals("sharp")){
                newc.getEnd().setFill(Color.BLUE);
            } else {
                newc.getEnd().setFill(Color.BLACK);
            }
            newc.setStart(start);
            newc.setEnd(end);
            newc.setC2(c2);
            newc.setL2(l2);
            newc.curve.setControlX2(curve.getControlX2());
            newc.curve.setControlY2(curve.getControlY2());

            newc.curve.setControlX1(x);
            newc.curve.setControlY1(y);
            newc.c1.setCenterX(newc.curve.getControlX1()+ correction);
            newc.c1.setCenterY(newc.curve.getControlY1() + correction);
            newc.l1.setEndX(newc.curve.getControlX1());
            newc.l1.setEndY(newc.curve.getControlY1());

            select = false;
            return newc;
        }

        public cubiccurve updatec2 (double x, double y){
            cubiccurve newc = new cubiccurve(curve.getStartX(),curve.getStartY(),curve.getEndX(),curve.getEndY(),color,thick,dash);
            newc.setStarttype(starttype);
            if (starttype.equals("sharp")){
                newc.getStart().setFill(Color.BLUE);
            } else {
                newc.getStart().setFill(Color.BLACK);
            }
            newc.setEndtype(endtype);
            if (endtype.equals("sharp")){
                newc.getEnd().setFill(Color.BLUE);
            } else {
                newc.getEnd().setFill(Color.BLACK);
            }
            newc.setStart(start);
            newc.setEnd(end);
            newc.setC1(c1);
            newc.setL1(l1);
            newc.curve.setControlX1(curve.getControlX1());
            newc.curve.setControlY1(curve.getControlY1());

            newc.curve.setControlX2(x);
            newc.curve.setControlY2(y);
            newc.c2.setCenterX(newc.curve.getControlX2() + correction);
            newc.c2.setCenterY(newc.curve.getControlY2() + correction);
            newc.l2.setEndX(newc.curve.getControlX2());
            newc.l2.setEndY(newc.curve.getControlY2());

            select = false;
            return newc;
        }

        public void copystart (cubiccurve c) {
            curve.setControlX1(c.getcubic().getControlX1());
            curve.setControlY1(c.getcubic().getControlY1());
            start = c.getStart();
            starttype = c.getStarttype();
            c1 = c.getC1();
            l1 = c.getL1();
        }

        public void copyend(cubiccurve c){
            curve.setControlX2(c.getcubic().getControlX2());
            curve.setControlY2(c.getcubic().getControlY2());
            end = c.getEnd();
            endtype = c.getEndtype();
            c2 = c.getC2();
            l2 = c.getL2();
        }

        public void updatecontrol(double c1x, double c1y, double c2x, double c2y){
            curve.setControlX1(c1x);
            curve.setControlY1(c1y);
            curve.setControlX2(c2x);
            curve.setControlY2(c2y);

            c1.setCenterX(curve.getControlX1() + correction);
            c1.setCenterY(curve.getControlY1() + correction);

            l1.setEndX(curve.getControlX1());
            l1.setEndY(curve.getControlY1());

            c2.setCenterX(curve.getControlX2() + correction);
            c2.setCenterY(curve.getControlY2() + correction);

            l2.setEndX(this.curve.getControlX2());
            l2.setEndY(this.curve.getControlY2());

        }

        public void updatefill (){
            if (starttype.equals("sharp")){
                start.setFill(Color.BLUE);;
            } else if (endtype.equals("sharp")){
                end.setFill(Color.BLUE);
            }
        }

        public void setSelect(){
            select = true;
        }
        public void unsetSelect(){
            select = false;
        }
        public Boolean isSelect(){
            return select;
        }
    }
}

