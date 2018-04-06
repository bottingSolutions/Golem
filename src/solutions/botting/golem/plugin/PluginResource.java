/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem.plugin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
public class PluginResource {

    private final String location;
    private final String name;
    private final String checksum;

    public PluginResource(String checksum, String location, String name) {

        this.checksum = checksum;
        this.location = location;
        this.name = name;
    }

    public static String getMD5CheckSum(byte[] b) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] arr = md.digest(b);
        return Base64.getEncoder().encodeToString(arr);
    }



    public String getName() {
        return name;
    }


}
