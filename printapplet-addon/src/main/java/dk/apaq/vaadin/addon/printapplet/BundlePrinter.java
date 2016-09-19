/*    */ package dk.apaq.vaadin.addon.printapplet;
/*    */ 
/*    */ import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.print.PrintService;

/*    */ 
/*    */ public class BundlePrinter
/*    */   implements Printer
/*    */ {
/*    */   private PrintService ps;
/*    */ 
/*    */   public BundlePrinter(PrintService ps)
/*    */   {
/* 57 */     this.ps = ps;
/*    */   }
/*    */ 
/*    */   public void print(String flavour, InputStream in)
/*    */     throws PrinterException
/*    */   {
/* 64 */     Printable printable = null;
/* 65 */     Dimension dimension = null;
/* 66 */     String contenttype = null;
/* 67 */     SvgPrintable svgPrintable = null;
/* 68 */     ImagePrintable imagePrintable = null;
/*    */     try
/*    */     {
/* 71 */       contenttype = flavour;
/*    */ 
/* 73 */       if ("application/zip".equals(contenttype))
/*    */       {
/* 75 */         ZipInputStream zin = new ZipInputStream(in);
/* 76 */         CloseIgnoringInputStream cin = new CloseIgnoringInputStream(zin);
/*    */         ZipEntry entry;
/* 78 */         while ((entry = zin.getNextEntry()) != null) {
/* 79 */           String mimetype = "image/png";
/* 80 */           if (entry.getExtra() != null) {
/* 81 */             mimetype = new String(entry.getExtra(), "utf-8");
/*    */           }
/*    */ 
/* 84 */           if (("image/png".equals(mimetype)) || ("image/png".equals(mimetype)) || ("image/png".equals(mimetype))) {
/* 85 */             BufferedImage image = ImageIO.read(zin);
/*    */ 
/* 87 */             if (dimension == null) {
/* 88 */               dimension = new Dimension(image.getWidth(), image.getHeight());
/*    */             }
/*    */ 
/* 92 */             if (imagePrintable == null) {
/* 93 */               imagePrintable = new ImagePrintable();
/* 94 */               printable = imagePrintable;
/*    */             }
/* 96 */             imagePrintable.addImage(image);
/*    */           }
/*    */ 
/*    */         }
/*    */       //ZipEntry entry;
/* 126 */       PrinterJob job = PrinterJob.getPrinterJob();
/* 127 */       job.setPrintService(this.ps);
/* 128 */       PageFormat pf = new PageFormat();
/*    */ 
/* 130 */       Paper paper = new Paper();
/* 131 */       paper.setSize(dimension.width, dimension.height);
/* 132 */       paper.setImageableArea(0.0D, 0.0D, dimension.width, dimension.height);
/* 133 */       pf.setPaper(paper);
/* 134 */       System.out.println(pf.getWidth() + "X" + pf.getHeight());
/* 135 */       pf = job.validatePage(pf);
/* 136 */       System.out.println(pf.getWidth() + "X" + pf.getHeight());
/*    */ 
/* 138 */       job.setPrintable(printable, pf);
/* 139 */       job.print();
/*    */ 
/*    */       }
/*    */       else if (flavour.contains("text/plain")) {
					String strLine;
					String strLine1="";
					try {
						DataInputStream dIn = new DataInputStream(in);
						BufferedReader br = new BufferedReader(new InputStreamReader(dIn, "UTF-8"));
						while ((strLine = br.readLine()) != null) {
							strLine1 = strLine1 + strLine + "\r\n";
						}
						
						byte[] b = strLine1.getBytes("ISO-8859-1");
						in.close();
						
						strLine1 = new String(b);
						System.out.println("Imprimiendo comprobante");
					}catch (Exception e){//Catch exception if any
						System.err.println("Error: " + e.getMessage());
					}
					try {
						String temp = System.getProperty("java.io.tmpdir");
						String fs = System.getProperty("file.separator");
						FileWriter out = new FileWriter(temp + fs + "comprobante.txt");
						out.write(strLine1);
						out.write("\f"); // CR
						out.close();
						
						out = new FileWriter("lpt1");
						out.write(strLine1);
						out.write("\f"); // CR
						out.close();
					} catch (IOException e) {
					}
			   } else
/*    */       {
/* 122 */         throw new PrinterException("Contenttype of input was no a supported type. [contenttype=" + flavour + "]");
/*    */       }
/*    */     } catch (Exception ex) {
/* 141 */       throw new PrinterException(ex.toString());
/*    */     }
/*    */   }
/*    */ 
/*    */   private class CloseIgnoringInputStream extends InputStream
/*    */   {
/*    */     private InputStream inputStream;
/*    */ 
/*    */     public CloseIgnoringInputStream(InputStream is)
/*    */     {
/* 41 */       this.inputStream = is;
/*    */     }
/*    */ 
/*    */     public int read()
/*    */       throws IOException
/*    */     {
/* 47 */       return this.inputStream.read();
/*    */     }
/*    */ 
/*    */     public void close()
/*    */       throws IOException
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/pol/Pulpit/printapplet/
 * Qualified Name:     dk.apaq.vaadin.addon.printapplet.BundlePrinter
 * JD-Core Version:    0.6.0
 */