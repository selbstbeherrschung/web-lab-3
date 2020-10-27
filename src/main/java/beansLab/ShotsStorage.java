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

    public static boolean addShot(Shot shot, String id) {
        if (shotMapList.containsKey(id)) {
            shotMapList.get(id).addFirst(shot);
        } else {
            LinkedList<Shot> ll = new LinkedList<Shot>();
            shotMapList.put(id, ll);
            ll.addFirst(shot);
        }
        return dataBase.insertShot(shot, id);
    }

    private static Shot[] getShots(String id) {
        return dataBase.getShots(id);
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

    public static String printShots(String id, double r) {
        StringBuilder strBuild = new StringBuilder();
        LinkedList<Shot> shots = shotMapList.get(id);

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

    private static String headWr = "<div id=\"result\" class=\"custom_scrollbar\">\n" +
            "        <table class=\"page-table-T\" border=\"1\">\n" +
            "            <thead class=\"header\" id=\"headerTable\">\n" +
            "            <tr>\n" +
            "                <th id=\"x-id\" class=\"stickyTh\"><center>X</center></th>\n" +
            "                <th class=\"y-class stickyTh\"><center>Y</center></th>\n" +
            "                <th class=\"r-class stickyTh\"><center>R</center></th>\n" +
            "                <th class=\"som-class stickyTh\"><center>Y/N</center></th>\n" +
            "                <th class=\"stt-class stickyTh\"><center>Start time</center></th>\n" +
            "                <th class=\"sct-class stickyTh\"><center>Script time</center></th>\n" +
            "            </tr>\n" +
            "            </thead>\n" +
            "            <tbody>";

    private static String footWr = "</tbody>\n" +
            "        </table>\n" +
            "    </div>";

    public static String writeShots(String id) {
        LinkedList<Shot> shots = shotMapList.get(id);
        StringBuilder strBuild = new StringBuilder();

        strBuild.append(headWr);

        if (shots != null && shots.size() != 0) {
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
        }

        strBuild.append(footWr);

        return strBuild.toString();
    }

}
