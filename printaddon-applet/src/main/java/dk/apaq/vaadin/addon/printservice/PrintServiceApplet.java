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


import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import dk.apaq.vaadin.addon.printapplet.Base64Coder;
import dk.apaq.vaadin.addon.printapplet.PrintApplet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;
import org.vaadin.applet.AppletIntegration;
import org.vaadin.applet.client.ui.VAppletIntegration;

/**
 *
 */
public class PrintServiceApplet extends AppletIntegration {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(PrintServiceApplet.class);
    private static final JSONParser parser = new JSONParser();
    private List<RemotePrintService> printServices = new ArrayList<RemotePrintService>();
    private int defaultIndex;
    private int defaultPrinterResolution = 300;
    private RemotePrintServiceManager manager;
    private String[] arhives = {"printapplet-0.0.5.jar"
    };

    protected PrintServiceApplet() {
        this.setAppletClass(PrintApplet.class.getCanonicalName());
        this.setAppletArchives(Arrays.asList(arhives));

        this.setWidth(1, Unit.PIXELS);
        this.setHeight(1, Unit.PIXELS);

        LOG.debug("PrintApplet constructed");
    }

    public void setManager(RemotePrintServiceManager manager) {
        this.manager = manager;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        // Applet class
        if (getAppletClass() == null) {
            // Do not paint anything of class is missing
            return;
        }

        LOG.debug("Painting PrintApplet.");
        target.addAttribute(VAppletIntegration.ATTR_APPLET_NAME, "Printer");
        target.addVariable(this, VAppletIntegration.ATTR_APPLET_ACTION, "dummy");
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);

        if (variables.containsKey("printers")) {
            try {
                LOG.debug("PrintApplet data recieved from client.");
                if (!printServices.isEmpty()) {
                    LOG.debug("Clearing existing printer data.");
                    printServices.clear();
                    manager.fireListChanged();
                }

                String data = (String)variables.get("printers");
                LOG.debug("Data reteieved from client: {}.", data);
                
                JSONArray jsonarray = (JSONArray) parser.parse(data);

                LOG.debug("Adding printers");
                for (int i = 0; i < jsonarray.size(); i++) {
                    JSONObject json = (JSONObject) jsonarray.get(i);
                    String id = (String) json.get("id");
                    String name = (String) json.get("name");

                    boolean defaultPrinter = false;
                    if(json.containsKey("default")) {
                        defaultPrinter = (Boolean) json.get("default");
                    }

                    int resolution = defaultPrinterResolution;
                    if(json.containsKey("printer-resolution")) {
                        resolution = ((Long) json.get("printer-resolution")).intValue();
                    }
                    
                    boolean colorSupported = false;
                    if(json.containsKey("color-supported")) {
                        colorSupported = (Boolean) json.get("color-supported");
                    }

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Printer: " + name);
                    }
                    if (name != null) {
                        printServices.add(new RemotePrintService(manager, id, name, resolution, colorSupported, defaultPrinter));
                    }
                }
                manager.fireListChanged();
            } catch (ParseException ex) {
                LOG.error("Unable to parse data retrieved from Printer applet.", ex);
            }
            
        }
    }

    public List<RemotePrintService> getPrintServices() {
        return Collections.unmodifiableList(printServices);
    }

    public RemotePrintService getDefaultPrintService() {
        if (defaultIndex >= 0 && defaultIndex < printServices.size()) {
            return printServices.get(defaultIndex);
        } else {
            return null;
        }
    }

    protected void print(RemotePrintService service, byte[] data) {
        String base64Encoded = new String(Base64Coder.encode(data));
        String[] params = new String[]{service.getName(), base64Encoded};

        if(LOG.isDebugEnabled()) {
            LOG.debug("Executing printjob [printservice= " + service.getName() + "]");
        }
        executeCommand("print", params);
    }
    
    protected void print(RemotePrintService service, byte[] data, String type) {
    	LOG.info("Received print job: " + service.getName() + " type: " + type + " " + data.length);
        String base64Encoded = new String(Base64Coder.encode(data));
        String[] params = new String[]{service.getName(), base64Encoded, type};

        if(LOG.isDebugEnabled()) {
            LOG.debug("Executing printjob [printservice= " + service.getName() + "]");
        }
        executeCommand("print", params);
    }
}
