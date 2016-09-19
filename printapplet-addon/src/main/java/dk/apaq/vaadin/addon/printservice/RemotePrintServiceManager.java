/*
 * PrintService for Vaadin
 * Copyright (C) 2011 by Apaq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.apaq.vaadin.addon.printservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.attribute.AttributeSet;

import com.vaadin.ui.*;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 */
public class RemotePrintServiceManager extends CustomComponent implements Serializable, RemotePrintExecutor {

    private static Map<AbstractLayout, RemotePrintServiceManager> map = new WeakHashMap<AbstractLayout, RemotePrintServiceManager>();

    private final PrintServiceApplet applet;
    private final List<PrintServiceListChangedListener> listeners = new ArrayList<PrintServiceListChangedListener>();

    public RemotePrintServiceManager(PrintServiceApplet applet) {
        this.applet = applet;
        setCompositionRoot(applet);
    }

    public PrintService getDefaultPrintService(){
        return applet.getDefaultPrintService();
    }

    public List<? extends PrintService> getPrintServices(DocFlavor flavor, AttributeSet attributes) {
        return applet.getPrintServices();
    }

    protected PrintServiceApplet getManagerComponent() {
        return applet;
    }

    public void addListener(PrintServiceListChangedListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PrintServiceListChangedListener listener) {
        listeners.remove(listener);
    }

    protected void fireListChanged() {

        PrintServiceListChangedEvent event = new PrintServiceListChangedEvent(getPrintServices(null, null));

        for(PrintServiceListChangedListener listener: listeners) {
            listener.onPrintServiceListChanged(event);
        }
    }

    public static RemotePrintServiceManager getInstance(AbstractLayout app) {
        RemotePrintServiceManager manager = map.get(app);
        if(manager == null) {
            manager = createManager(app);
            map.put(app, manager);
        }
        return manager;
    }

    private static RemotePrintServiceManager createManager(AbstractLayout app) {
        //Window mainWindow = app.getMainWindow();
        //if(mainWindow == null) {
        //    throw new NullPointerException("Application must have a main window set before adding a Print Manager can be created.");
        //}
        
        //look for already added browsercookies

        Iterator<Component> it = app.getComponentIterator();
        while(it.hasNext()) {
            Component c = it.next();
            if(c instanceof RemotePrintServiceManager) {
                return (RemotePrintServiceManager)c;
            }
        }

        //...else create and add a new one.
        PrintServiceApplet applet = new PrintServiceApplet();
        RemotePrintServiceManager manager = new RemotePrintServiceManager(applet);
        applet.setManager(manager);

        app.addComponent(manager);
        return manager;
    }

    public void executeRemotePrint(RemotePrintService service, byte[] data) {
        applet.print(service, data);
    }
    
    public void executeRemotePrint(RemotePrintService service, byte[] data, String type) {
        applet.print(service, data, type);
    }

}
