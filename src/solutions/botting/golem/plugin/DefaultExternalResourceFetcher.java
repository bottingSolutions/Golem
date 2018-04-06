/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
public class DefaultExternalResourceFetcher extends ExternalResourceFetcher {

    public DefaultExternalResourceFetcher(String pluginDirectory) throws GolemResourceUpdateException {
        super(pluginDirectory);

    }

    public void downloadResource(PluginResource pr) throws GolemResourceUpdateException {
        try {
            URL website = new URL("http://www.website.com/information.asp");
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream("information.html");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DefaultExternalResourceFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DefaultExternalResourceFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Object loadExternalResource(PluginResource pr) throws GolemResourceUpdateException {
        if (requiresUpdates(pr)) {
            downloadResource(pr);
            if (requiresUpdates(pr)) {
                throw new GolemResourceUpdateException("Checksum failure after download attempt");
            }
        }

        File f = new File(this.pluginDirectory + File.separator + pr.getName());
        URL[] urls = new URL[1];
        try {
            urls[0] = f.toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new GolemResourceUpdateException(ex);
        }
        URLClassLoader ucl = new URLClassLoader(urls);

        ServiceLoader<PluginLibrary> sl = ServiceLoader.load(PluginLibrary.class, ucl);

        Iterator<PluginLibrary> apit = sl.iterator();
        if (apit.hasNext()) {
            return apit.next();
        }
        throw new GolemResourceUpdateException("Service not found!");

    }

    private boolean requiresUpdates(PluginResource pr) {
        return false;
    }

}
