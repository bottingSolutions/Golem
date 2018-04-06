/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem.plugin;

import java.net.MalformedURLException;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
public class GolemResourceUpdateException extends Exception {

    GolemResourceUpdateException(String error) {
        super(error);
    }

    GolemResourceUpdateException(Throwable cause) {
        super(cause);
    }

}
