/*    */ package dk.apaq.vaadin.addon.printapplet;
/*    */ 
/*    */ import java.awt.print.PrinterException;
/*    */ import java.io.InputStream;
/*    */ import java.security.PrivilegedAction;
/*    */ import javax.swing.JOptionPane;
/*    */ 
/*    */ public class PrintAction
/*    */   implements PrivilegedAction<Boolean>
/*    */ {
/*    */   private String flavour;
/*    */   private InputStream is;
/*    */   private Printer printer;
/*    */ 
/*    */   public PrintAction(String flavour, InputStream is, Printer printer)
/*    */   {
/* 35 */     this.printer = printer;
/* 36 */     this.is = is;
/* 37 */     this.flavour = flavour;
/*    */   }
/*    */ 
/*    */   public Boolean run() {
/*    */     try {
/* 42 */       this.printer.print(this.flavour, this.is);
/*    */     } catch (PrinterException ex) {
/* 44 */       JOptionPane.showMessageDialog(null, "Printing failed: " + ex.getClass().getName() + ", " + ex.getMessage());
/*    */     }
/* 46 */     return Boolean.valueOf(true);
/*    */   }
/*    */ }

/* Location:           /home/pol/Pulpit/printapplet/
 * Qualified Name:     dk.apaq.vaadin.addon.printapplet.PrintAction
 * JD-Core Version:    0.6.0
 */