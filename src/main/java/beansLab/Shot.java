package beansLab;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;


public class Shot implements Serializable {

    private double x;
    private double y;
    private double r;

    private boolean GR;

    private LocalDateTime start;
    private long scriptTime;

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getR() {
        return r;
    }

    public boolean isGR() {
        return GR;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public long getScriptTime() {
        return scriptTime;
    }

    public Shot(){}
    public Shot(double xIn, double yIn, double rIn, boolean grIn, LocalDateTime startIn, long scriptTimeIn){
        x = xIn;
        y = yIn;
        r = rIn;
        GR = grIn;
        start = startIn;
        scriptTime = scriptTimeIn;
    }

    public void calculateShot() {
        start = LocalDateTime.now();
        scriptTime = System.nanoTime();
        GR = check(x, y, r);
        scriptTime = System.nanoTime() - scriptTime;
    }

    public boolean check(double x, double y, double r) {
        if ((y == 0 && Math.abs(x) <= r) || (x == 0 && Math.abs(y) <= r)) {
            return true;
        }
        if (y > 0) {
            if (x > 0) {
                return (x * x + y * y <= r * r);
            } else {
                return false;
            }
        } else {
            if (x > 0) {
                return ((x + (-y)) <= r);
            } else {
                return ((-x <= r) && (-y <= r));
            }
        }
    }

}
