package beansLab;

import resources.BasesShotCreater;
import resources.DataBaseManagerShots;


import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.util.HashMap;
import java.util.LinkedList;

//@ManagedBean
//@ApplicationScoped
public class ShotsStorage {

    public static DataBaseManagerShots dataBase = BasesShotCreater.getDataBase();
    private static HashMap<String, LinkedList<Shot>> shotMapList = new HashMap<>();

    public static boolean addShot(Shot shot, String id){
        if(shotMapList.containsKey(id)){
            shotMapList.get(id).addFirst(shot);
        }else {
            LinkedList<Shot> ll = new LinkedList<Shot>();
            shotMapList.put(id, ll);
            ll.addFirst(shot);
        }
        return dataBase.insertShot(shot, id);
    }

    private static Shot[] getShots(String id) {
        return dataBase.getShots(id);
    }

    public static String printShots(String id, double r) {
        StringBuilder strBuild = new StringBuilder();
        LinkedList<Shot> shots = shotMapList.get(id);

        shots.stream().map(shot -> {
            double x = shot.getX();
            double y = shot.getY();

            if(shot.isGR()){
                return (
                        "<circle cx=\"" +
                                (150 + 120 * (x / r)) +
                                "\" cy=\"" +
                                (150 - 120 * (y / r)) +
                                "\" r=\"5\" fill=\"rgb(0,255,0)\" stroke-width=\"1\"\n stroke=\"rgb(0,0,0)\"/>"
                );
            }else{
                return (
                        "<circle cx=\"" +
                                (150 + 120 * (x / r)) +
                                "\" cy=\"" +
                                (150 - 120 * (y / r)) +
                                "\" r=\"5\" fill=\"rgb(255,0,0)\" stroke-width=\"1\"\n stroke=\"rgb(0,0,0)\"/>"
                );
            }

        }).forEachOrdered(str -> strBuild.append(str));

        return strBuild.toString();
    }

    public static String writeShots(String id, double r) {
        LinkedList<Shot> shots = shotMapList.get(id);
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
