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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.ColorSupported;
import javax.print.event.PrintServiceAttributeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class RemotePrintService implements PrintService {

    private static final Logger LOG = LoggerFactory.getLogger(RemotePrintService.class);
    private static final DocFlavor[] SUPPORTED_FLAVORS = new DocFlavor[]{DocFlavor.BYTE_ARRAY.PNG,
        DocFlavor.BYTE_ARRAY.GIF,
        DocFlavor.BYTE_ARRAY.JPEG,
        DocFlavor.SERVICE_FORMATTED.PAGEABLE,
        DocFlavor.SERVICE_FORMATTED.PRINTABLE,
        DocFlavor.SERVICE_FORMATTED.RENDERABLE_IMAGE,
        DocFlavor.INPUT_STREAM.PNG,
        DocFlavor.INPUT_STREAM.GIF,
        DocFlavor.INPUT_STREAM.JPEG,
        DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_8};
    private final RemotePrintExecutor printExecutor;
    private final String id;
    private final String name;
    private final int resolution;
    private final boolean colorSupported;
    private final boolean defaultPrinter;
    private final HashPrintServiceAttributeSet attributeSet = new HashPrintServiceAttributeSet();

    public RemotePrintService(RemotePrintExecutor printExecutor, String id, String name, int resolution, boolean colorSupported, boolean defaultPrinter) {
        this.printExecutor = printExecutor;
        this.id = id;
        this.name = name;
        this.resolution = resolution;
        this.colorSupported = colorSupported;
        this.defaultPrinter = defaultPrinter;

        attributeSet.add((colorSupported ? ColorSupported.SUPPORTED : ColorSupported.NOT_SUPPORTED));

    }

    public void print(RemoteDocPrintJob printJob, Doc doc, PrintRequestAttributeSet pras) throws PrintException {
        try {
            String flavorClassName = doc.getDocFlavor().getRepresentationClassName();
            float scaleTo72Dpi = (float) resolution / 72;

            if (doc.getDocFlavor().getMimeType().contains("text/plain")) {            	
            	byte[] b = (byte[])doc.getPrintData();
            	LOG.info("RemotePrintService got print job text size: " + b.length);
                printExecutor.executeRemotePrint(this, b, "text/plain");
            }            
            if (flavorClassName.equals(Pageable.class.getCanonicalName())) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ZipOutputStream zout = new ZipOutputStream(os);
                Pageable pageable = (Pageable) doc.getPrintData();
                try {
                    for (int i = 0; i == 0 || i < pageable.getNumberOfPages(); i++) {
                        try {
                            Printable p = pageable.getPrintable(i);
                            PageFormat pf = pageable.getPageFormat(i);

                            int width = (int) (pf.getWidth() * scaleTo72Dpi);
                            int height = (int) (pf.getHeight() * scaleTo72Dpi);

                            boolean hasMorePages = true;
                            int pageCount = 0;
                            do {
                                long start = System.currentTimeMillis();
                                try {
                                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                                    Graphics2D gfx = image.createGraphics();
                                    gfx.scale(scaleTo72Dpi, scaleTo72Dpi);

                                    if (p.print(gfx, pf, pageCount++) == Printable.PAGE_EXISTS) {
                                        ZipEntry entry = new ZipEntry(pageCount + ".png");
                                        zout.putNextEntry(entry);
                                        entry.setExtra("image/png".getBytes("utf-8"));
                                        ImageIO.write(image, "png", zout);
                                        zout.flush();
                                        zout.closeEntry();
                                        image.flush();
                                        LOG.info("Page printed in " + (System.currentTimeMillis() - start) + "ms.");
                                    } else {
                                        hasMorePages = false;
                                    }


                                } catch (PrinterException ex) {
                                    hasMorePages = false;
                                    LOG.error("Error while printing.", ex);
                                }
                            } while (hasMorePages);
                        } catch (IndexOutOfBoundsException ex) {
                            break;
                        }
                    }

                } catch (Exception ex) {
                    throw new PrintException(ex);

                } finally {
                    zout.flush();
                    os.flush();
                    zout.close();
                }

                printExecutor.executeRemotePrint(this, os.toByteArray());
            }
            
        } catch (IOException ex) {
            throw new PrintException("Unable to print.", ex);
        }
    }

    public int getResolution() {
        return resolution;
    }

    public boolean isColorSupported() {
        return colorSupported;
    }

    public boolean isDefaultPrinter() {
        return defaultPrinter;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public DocPrintJob createPrintJob() {
        return new RemoteDocPrintJob(this);
    }

    public void addPrintServiceAttributeListener(PrintServiceAttributeListener pl) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removePrintServiceAttributeListener(PrintServiceAttributeListener pl) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public PrintServiceAttributeSet getAttributes() {
        return attributeSet;
    }

    public <T extends PrintServiceAttribute> T getAttribute(Class<T> type) {
        return null;
    }

    public DocFlavor[] getSupportedDocFlavors() {
        return SUPPORTED_FLAVORS;
    }

    public boolean isDocFlavorSupported(DocFlavor df) {
        for (DocFlavor current : SUPPORTED_FLAVORS) {
            if (current.equals(df)) {
                return true;
            }
        }
        return false;//"application/zip".equals(df.getMimeType());
    }

    public Class<?>[] getSupportedAttributeCategories() {
        return new Class[0];
    }

    public boolean isAttributeCategorySupported(Class<? extends Attribute> type) {
        return false;
    }

    public Object getDefaultAttributeValue(Class<? extends Attribute> type) {
        return null;
    }

    public Object getSupportedAttributeValues(Class<? extends Attribute> type, DocFlavor df, AttributeSet as) {
        return null;
    }

    public boolean isAttributeValueSupported(Attribute atrbt, DocFlavor df, AttributeSet as) {
        return false;
    }

    public AttributeSet getUnsupportedAttributes(DocFlavor df, AttributeSet as) {
        return new HashAttributeSet();
    }

    public ServiceUIFactory getServiceUIFactory() {
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
