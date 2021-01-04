package beansLab;

import javax.annotation.ManagedBean;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@ManagedBean
@SessionScoped
public class ShotsSession implements Serializable {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ShotsSession.class);


    private int listNumber = 1;
    private int listSize = 12;
    private int maxAvailablePage = 1;

    private ArrayList<Shot> shotsList = new ArrayList<Shot>();

    private ArrayList<Shot> shotsViewList = new ArrayList<Shot>(listSize);

    private double x;
    private double y;
    private double r = 1.0d;

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


    public int getListNumber() {
        return listNumber;
    }

    public void setListNumber(int listNumber) {

        log.info("Setting: " + listNumber);

        if (listNumber > maxAvailablePage) {
            log.info("Out range list number!");
            return;
        }

        if (listNumber < 1) {
            listNumber = 1;
        }

        int size = listSize * (listNumber - 1);
        shotsViewList.clear();
        for (int i = 0; i < listSize; i++) {

            if (i + size < shotsList.size()) {
                shotsViewList.add(shotsList.get(i + size));
            } else {
                break;
            }
        }
        this.listNumber = listNumber;
    }

    public ArrayList<Shot> getShotsViewList() {
        return shotsViewList;
    }

    public void setShotsViewList(ArrayList<Shot> shotsList) {
        this.shotsViewList = shotsList;
    }

    public ArrayList<Shot> getShotsList() {
        return shotsList;
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
        shotsList.add(0, shot);
        if (shotsList.size() % listSize == 1 && shotsList.size() != 1) {
            maxAvailablePage++;
        }
        setListNumber(getListNumber());

        //return true;
        return ShotsStorage.addShot(shot, id);
    }

    public void selectFirstPage() {
        log.info("First");
        setListNumber(1);
    }

    public void selectPrevPage() {
        log.info("Prev");
        if (getListNumber() > 1) {
            setListNumber(getListNumber() - 1);
        }
    }

    public void selectNextPage() {
        log.info("Next");
        if (getListNumber() + 1 <= maxAvailablePage) {
            setListNumber(getListNumber() + 1);
        }else {
            setListNumber(getListNumber());
        }

    }

    public void selectLastPage() {
        log.info("Last");
        setListNumber(maxAvailablePage);
    }

    private static String head = "<div>\n" +
            "<svg id=\"image-coordinates\" height=\"310px\">\n" +
            "<rect width=\"300\" height=\"300\" fill=\"rgb(255,255,255)\" stroke-width=\"1\"\n" +
            "stroke=\"rgb(0,0,0)\"/>\n" +
            "\n" +
            "<circle cx=\"150\" cy=\"150\" r=\"120\" fill=\"rgb(0,255,255)\" stroke-width=\"1\"\n" +
            "stroke=\"rgb(0,0,0)\"/>\n" +
            "<polyline points=\"0,0 150,0 150,150 300,150 300,300 0,300 0,0\" fill=\"rgb(255,255,255)\"\n" +
            "stroke-width=\"1\"\n" +
            "stroke=\"rgb(0,0,0)\"/>\n" +
            "\n" +
            "<rect x=\"30\" y=\"150\" width=\"120\" height=\"120\" fill=\"rgb(0,255,255)\" stroke-width=\"1\"\n" +
            "stroke=\"rgb(50,50,50)\"/>\n" +
            "\n" +
            "<polyline points=\"270,150 150,270 150,150 270,150\" fill=\"rgb(0,255,255)\" stroke-width=\"1\"\n" +
            "stroke=\"rgb(0,0,0)\"/>\n" +
            "\n" +
            "<line x1=\"150\" y1=\"0\" x2=\"150\" y2=\"500\" stroke-width=\"1\" stroke=\"rgb(0,0,0)\"/>\n" +
            "<line x1=\"0\" y1=\"150\" x2=\"300\" y2=\"150\" stroke-width=\"1\" stroke=\"rgb(0,0,0)\"/>\n" +
            "\n" +
            "<line x1=\"145\" y1=\"30\" x2=\"155\" y2=\"30\" stroke-width=\"1\" stroke=\"rgb(0,0,0)\"/>\n" +
            "<line x1=\"145\" y1=\"90\" x2=\"155\" y2=\"90\" stroke-width=\"1\" stroke=\"rgb(0,0,0)\"/>\n" +
            "\n" +
            "<line x1=\"30\" y1=\"145\" x2=\"30\" y2=\"155\" stroke-width=\"1\" stroke=\"rgb(0,0,0)\"/>\n" +
            "<line x1=\"90\" y1=\"145\" x2=\"90\" y2=\"155\" stroke-width=\"1\" stroke=\"rgb(0,0,0)\"/>\n" +
            "\n" +
            "<line x1=\"210\" y1=\"145\" x2=\"210\" y2=\"155\" stroke-width=\"1\" stroke=\"rgb(0,0,0)\"/>\n" +
            "<line x1=\"270\" y1=\"145\" x2=\"270\" y2=\"155\" stroke-width=\"1\" stroke=\"rgb(0,0,0)\"/>\n" +
            "\n" +
            "<line x1=\"145\" y1=\"210\" x2=\"155\" y2=\"210\" stroke-width=\"1\" stroke=\"rgb(0,0,0)\"/>\n" +
            "<line x1=\"145\" y1=\"270\" x2=\"155\" y2=\"270\" stroke-width=\"1\" stroke=\"rgb(0,0,0)\"/>\n" +
            "\n" +
            "<polyline points=\"150,0 140,15 150,5 160,15 150,0\" fill=\"rgb(249,249,249)\" stroke-width=\"1\"\n" +
            "stroke=\"rgb(0,0,0)\"/>\n" +
            "<polyline points=\"300,150 285,140 295,150 285,160 300,150\" fill=\"rgb(249,249,249)\"\n" +
            "stroke-width=\"1\" stroke=\"rgb(0,0,0)\"/>";

    private static String foot = "</svg>\n" +
            "</div>";

    public String printShots() {
        StringBuilder strBuild = new StringBuilder();
        ArrayList<Shot> shots = shotsList;
        double r = getR();

        strBuild.append(head);

        if (shots != null && shots.size() != 0) {
            shots.stream().map(shot -> {
                double x = shot.getX();
                double y = shot.getY();

                if (shot.isGR()) {
                    return (
                            "<circle cx=\"" +
                                    (150 + 120 * (x / r)) +
                                    "\" cy=\"" +
                                    (150 - 120 * (y / r)) +
                                    "\" r=\"5\" fill=\"rgb(0,255,0)\" stroke-width=\"1\"\n stroke=\"rgb(0,0,0)\"/>"
                    );
                } else {
                    return (
                            "<circle cx=\"" +
                                    (150 + 120 * (x / r)) +
                                    "\" cy=\"" +
                                    (150 - 120 * (y / r)) +
                                    "\" r=\"5\" fill=\"rgb(255,0,0)\" stroke-width=\"1\"\n stroke=\"rgb(0,0,0)\"/>"
                    );
                }

            }).forEachOrdered(str -> strBuild.append(str));
        }

        strBuild.append(foot);

        return strBuild.toString();
    }

}
