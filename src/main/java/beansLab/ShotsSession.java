package beansLab;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

//@ManagedBean
//@SessionScoped
public class ShotsSession implements Serializable {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ShotsSession.class);

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
        return ShotsStorage.addShot(shot, id);
    }

}
