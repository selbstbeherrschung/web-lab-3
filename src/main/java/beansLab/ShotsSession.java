package beansLab;


import resources.BasesShotCreater;
import resources.DataBaseManagerShots;

import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@ManagedBean
@SessionScoped
public class ShotsSession implements Serializable {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ShotsSession.class);

    public static DataBaseManagerShots dataBase = BasesShotCreater.getDataBase();

    public void addShot(Shot shot, String id) {
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
        return dataBase.insertShot(shot, id);
    }

    private Shot[] getShots(String id) {
        return dataBase.getShots(id);
    }

    public String printShots(String id) {
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

    public String writeShots(String id) {
        Shot[] shots = getShots(id);
        return null;
    }

}
