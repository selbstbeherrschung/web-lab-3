package beansLab;

import resources.BasesShotCreater;
import resources.DataBaseManagerShots;

import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

@ManagedBean
@SessionScoped
public class ShotsSession implements Serializable {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ShotsSession.class);

    public static DataBaseManagerShots dataBase = BasesShotCreater.getDataBase();

    private LinkedList<Shot> shotLinkedList = new LinkedList<>();

    private double x;
    private double y;
    private double r;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public void addShot(String id) {
        Shot shot = new Shot();
        shot.setX(x);
        shot.setY(y);
        shot.setR(r);
        shot.calculateShot();
        if (!saveShot(shot, id)) {
            log.warn("Exception with saving in data base with shot: " + "\n" +
                    "   X: " + shot.getX() + "\n" +
                    "   Y: " + shot.getY() + "\n" +
                    "   R: " + shot.getR() + "\n" +
                    "   GR: " + shot.isGR() + "\n" +
                    "   Start: " + shot.getStart() + "\n" +
                    "   Script time: " + shot.getScriptTime() + "\n"
            );
        }

    }

    private boolean saveShot(Shot shot, String id) {
        shotLinkedList.addFirst(shot);
        return dataBase.insertShot(shot, id);
    }

    private Shot[] getShots(String id) {
        return dataBase.getShots(id);
    }

    public String printShots(String id) {
        StringBuilder strBuild = new StringBuilder();
        ArrayList<Shot> shots = new ArrayList<>();
        Collections.addAll(shots, getShots(id));
        shots.stream().map(shot -> {
            double x = shot.getX();
            double y = shot.getY();
            double r = shot.getR();

            return (
                    "<circle cx=\"" + (150 + 120 * (x / r)) + "\" cy=\"" + (150 - 120 * (y / r)) + "\" r=\"5\" fill=\"rgb(255,0,255)\" stroke-width=\"1\"\n stroke=\"rgb(0,0,0)\"/>"
            );
        }).forEachOrdered(str -> strBuild.append(str));

        return strBuild.toString();
    }

    public String writeShots(String id, double r) {
        ArrayList<Shot> shots = new ArrayList<>();
        Collections.addAll(shots, getShots(id));
        StringBuilder strBuild = new StringBuilder();
        shots.stream().map(shot -> {
            String trS = "<tr>";
            String trE = "</tr>";
            String tdS = "<td>";
            String tdE = "</td>";
            return (trS +
                    tdS + shot.getX() + tdE +
                    tdS + shot.getY() + tdE +
                    tdS + shot.getR() + tdE +
                    tdS + shot.isGR() + tdE +
                    tdS + shot.getStart() + tdE +
                    tdS + shot.getScriptTime() + tdE +
                    trE
            );
        }).forEachOrdered(str -> strBuild.append(str));

        return strBuild.toString();
    }

}
