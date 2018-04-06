/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem.plugin;

import java.io.File;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
public abstract class ExternalResourceFetcher {

    protected final File pluginDirectory;

    public ExternalResourceFetcher(String pluginDirectory) throws GolemResourceUpdateException {
        File f = new File(pluginDirectory);
        if (!f.exists() || !f.isDirectory()) {
            if(!f.mkdir()){
                throw new GolemResourceUpdateException("Could not create plugin directory");
            }
        }
        this.pluginDirectory = f;
    }

    public abstract Object loadExternalResource(PluginResource serviceType) throws GolemResourceUpdateException;
}
