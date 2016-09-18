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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.Doc;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobListener;

/**
 *
 * @author michaelzachariassenkrog
 */
public class RemoteDocPrintJob implements DocPrintJob {

    private final RemotePrintService printService;

    public RemoteDocPrintJob(RemotePrintService printService) {
        this.printService = printService;
    }


    public PrintService getPrintService() {
        return printService;
    }

    public PrintJobAttributeSet getAttributes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addPrintJobListener(PrintJobListener pl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removePrintJobListener(PrintJobListener pl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addPrintJobAttributeListener(PrintJobAttributeListener pl, PrintJobAttributeSet pjas) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removePrintJobAttributeListener(PrintJobAttributeListener pl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void print(Doc doc, PrintRequestAttributeSet pras) throws PrintException {
        printService.print(this, doc, pras);
    }

}
