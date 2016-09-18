/*     */ package dk.apaq.vaadin.addon.printapplet;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Arrays;
/*     */ import javax.print.PrintService;
/*     */ import javax.print.PrintServiceLookup;
/*     */ import javax.print.attribute.Attribute;
/*     */ import javax.print.attribute.PrintServiceAttributeSet;
/*     */ import javax.print.attribute.standard.ColorSupported;
/*     */ import javax.print.attribute.standard.PrinterResolution;
/*     */ import javax.swing.JOptionPane;
/*     */ import org.json.simple.JSONArray;
/*     */ import org.json.simple.JSONObject;
/*     */ import org.vaadin.applet.AbstractVaadinApplet;
/*     */ 
/*     */ public class PrintApplet extends AbstractVaadinApplet
/*     */ {
/*     */   public void init()
/*     */   {
/*  45 */     super.init();
/*     */ 
/*  48 */     PrintService[] printServices = getPrintServices();
/*  49 */     PrintService defaultService = getDefaultPrintService();
/*  50 */     JSONArray jsonArray = buildJsonFromPrintServices(printServices, defaultService);
/*     */ 
/*  52 */     vaadinUpdateVariable("printers", jsonArray.toString(), true);
/*     */   }
/*     */ 
/*     */   protected void doExecute(String command, Object[] params)
/*     */   {
/*  59 */     System.out.println("doExecute [command=" + command + "; params=" + Arrays.toString(params) + "]");
/*     */ 
/*  61 */     if ("print".equals(command)) {
/*  62 */       String printerName = (String)params[0];
/*  63 */       String data = (String)params[1];
				String type = null;
				if (params.length>2) type = (String)params[2];
/*     */ 
/*  65 */       PrintService printService = getPrintService(printerName);
/*  66 */       if (printService == null) {
/*  67 */         JOptionPane.showMessageDialog(null, "Unable to find printService [PrinterName=" + printerName + "]");
/*  68 */         return;
/*     */       }
/*     */ 
/*  71 */       printBundleData(type, Base64Coder.decode(data), printService);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void printBundleData(String type, byte[] data, PrintService printService) {
/*  76 */     Printer printer = new BundlePrinter(printService);
/*  77 */     if (type==null) doPrintFromBuffer("application/zip", data, printer);
			  else doPrintFromBuffer(type, data, printer);
/*     */   }
/*     */ 
/*     */   private void doPrintFromBuffer(String flavour, byte[] data, Printer printer) {
/*     */     try {
/*  82 */       InputStream is = new ByteArrayInputStream(data);
/*  83 */       AccessController.doPrivileged(new PrintAction(flavour, is, printer));
/*     */     }
/*     */     catch (Throwable ex) {
/*  86 */       JOptionPane.showMessageDialog(this, "Cannot print: " + ex.getClass().getName() + ", " + ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   private PrintService getPrintService(String name) {
/*  91 */     PrintService[] services = getPrintServices();
/*  92 */     for (PrintService current : services) {
/*  93 */       if (current.getName().equals(name)) {
/*  94 */         return current;
/*     */       }
/*     */     }
/*  97 */     return null;
/*     */   }
/*     */ 
/*     */   private PrintService getDefaultPrintService() {
/* 101 */     PrintService printService = (PrintService)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public PrintService run()
/*     */       {
/*     */         try {
/* 106 */           return PrintServiceLookup.lookupDefaultPrintService(); } catch (Throwable e) {
/*     */         }
/* 108 */         return null;
/*     */       }
/*     */     });
/* 113 */     return printService;
/*     */   }
/*     */ 
/*     */   private PrintService[] getPrintServices() {
/* 117 */     PrintService[] printServices = (PrintService[])AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public PrintService[] run()
/*     */       {
/*     */         try {
/* 122 */           return PrintServiceLookup.lookupPrintServices(null, null); } catch (Throwable e) {
/*     */         }
/* 124 */         return null;
/*     */       }
/*     */     });
/* 129 */     return printServices;
/*     */   }
/*     */ 
/*     */   private JSONArray buildJsonFromPrintServices(PrintService[] services, PrintService defaultService) {
/* 133 */     JSONArray jsonarray = new JSONArray();
/* 134 */     for (PrintService ps : services) {
/* 135 */       JSONObject json = new JSONObject();
/* 136 */       json.put("id", ps.getName());
/* 137 */       json.put("name", ps.getName());
/*     */ 
/* 140 */       PrinterResolution resolution = (PrinterResolution)ps.getDefaultAttributeValue(PrinterResolution.class);
/* 141 */       int dpi = 300;
/* 142 */       if (resolution != null) {
/* 143 */         resolution.getCrossFeedResolution(100);
/*     */       }
/* 145 */       json.put("printer-resolution", Integer.valueOf(dpi));
/*     */ 
/* 147 */       PrintServiceAttributeSet psas = ps.getAttributes();
/* 148 */       for (Attribute attr : psas.toArray())
/*     */       {
/* 150 */         if ((attr instanceof ColorSupported)) {
/* 151 */           ColorSupported colorSupported = (ColorSupported)attr;
/* 152 */           boolean color = colorSupported.getValue() == ColorSupported.SUPPORTED.getValue();
/* 153 */           json.put("color-supported", Boolean.valueOf(color));
/*     */         }
/*     */       }
/*     */ 
/* 157 */       json.put("default", Boolean.valueOf(ps == defaultService));
/*     */ 
/* 159 */       jsonarray.add(json);
/*     */     }
/*     */ 
/* 162 */     return jsonarray;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws IOException
/*     */   {
/* 179 */     PrintApplet app = new PrintApplet();
/*     */ 
/* 221 */     JSONArray json = app.buildJsonFromPrintServices(app.getPrintServices(), app.getDefaultPrintService());
/* 222 */     System.out.println(json);
/*     */   }
/*     */ }

/* Location:           /home/pol/Pulpit/printapplet/
 * Qualified Name:     dk.apaq.vaadin.addon.printapplet.PrintApplet
 * JD-Core Version:    0.6.0
 */