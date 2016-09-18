/*    */ package dk.apaq.vaadin.addon.printapplet;
/*    */ 
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Image;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.awt.print.PageFormat;
/*    */ import java.awt.print.Printable;
/*    */ import java.awt.print.PrinterException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class ImagePrintable
/*    */   implements Printable
/*    */ {
/* 34 */   private List<BufferedImage> images = new ArrayList();
/*    */ 
/*    */   public void addImage(BufferedImage image) {
/* 37 */     this.images.add(image);
/*    */   }
/*    */ 
/*    */   public void removeImages(int index) {
/* 41 */     this.images.remove(index);
/*    */   }
/*    */ 
/*    */   public int getImagesCount() {
/* 45 */     return this.images.size();
/*    */   }
/*    */ 
/*    */   public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
/* 49 */     if (pageIndex >= this.images.size()) {
/* 50 */       return 1;
/*    */     }
/*    */ 
/* 53 */     Graphics2D g2 = (Graphics2D)graphics;
/* 54 */     BufferedImage image = (BufferedImage)this.images.get(pageIndex);
/*    */ 
/* 56 */     double imageWidth = image.getWidth();
/* 57 */     double pageWidth = pageFormat.getWidth();
/* 58 */     double scale = pageWidth / imageWidth;
/* 59 */     g2.scale(scale, scale);
/* 60 */     graphics.drawImage((Image)this.images.get(pageIndex), 0, 0, null);
/*    */ 
/* 62 */     return 0;
/*    */   }
/*    */ }

/* Location:           /home/pol/Pulpit/printapplet/
 * Qualified Name:     dk.apaq.vaadin.addon.printapplet.ImagePrintable
 * JD-Core Version:    0.6.0
 */