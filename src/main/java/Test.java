import beansLab.Shot;
import resources.BasesShotCreater;
import resources.DataBaseManagerShots;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class Test
{
    public static void main(String[] args) {
        DataBaseManagerShots dataBaseManagerShots = BasesShotCreater.getDataBase();
        Connection connection = dataBaseManagerShots.connection;
        String str = "kdsgnsdkg";
        Shot[] shots = dataBaseManagerShots.getShots(str);
        int l = shots.length;
        for (int i = 0; i < l; i++) {
            System.out.print(shots[i].getX() + "  ");
            System.out.print(shots[i].getY() + "  ");
            System.out.print(shots[i].getR() + "  ");
            System.out.print(shots[i].isGR() + "  ");
            System.out.print(shots[i].getStart() + "  ");
            System.out.println(shots[i].getScriptTime() + "  ");
        }

    }
}
