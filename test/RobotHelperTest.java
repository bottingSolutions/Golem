
import java.awt.AWTException;
import java.awt.Robot;
import java.util.logging.Level;
import java.util.logging.Logger;
import solutions.botting.golem.core.RobotHelper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
public class RobotHelperTest {
    public static void main(String[] args) {
        try {
            RobotHelper r = new RobotHelper( new Robot());
            r.clickMouse(500, 500, 1);
            r.sendKeys("THIS IS A tYpIngTest And Can Be Disregarded... ", 50,50);
                
                
             
        } catch (AWTException ex) {
            Logger.getLogger(RobotHelperTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
