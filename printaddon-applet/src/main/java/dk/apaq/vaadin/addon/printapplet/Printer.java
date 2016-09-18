package dk.apaq.vaadin.addon.printapplet;

import java.awt.print.PrinterException;
import java.io.InputStream;

public abstract interface Printer
{
  public abstract void print(String paramString, InputStream paramInputStream)
    throws PrinterException;
}

/* Location:           /home/pol/Pulpit/printapplet/
 * Qualified Name:     dk.apaq.vaadin.addon.printapplet.Printer
 * JD-Core Version:    0.6.0
 */