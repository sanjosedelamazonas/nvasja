/*     */ package dk.apaq.vaadin.addon.printapplet;
/*     */ 
/*     */ public class Base64Coder
/*     */ {
/*  30 */   private static final String systemLineSeparator = System.getProperty("line.separator");
/*     */ 
/*  32 */   private static char[] map1 = new char[64];
/*     */   private static byte[] map2;
/*     */ 
/*     */   public static String encodeString(String s)
/*     */   {
/*  67 */     return new String(encode(s.getBytes()));
/*     */   }
/*     */ 
/*     */   public static String encodeLines(byte[] in)
/*     */   {
/*  77 */     return encodeLines(in, 0, in.length, 76, systemLineSeparator);
/*     */   }
/*     */ 
/*     */   public static String encodeLines(byte[] in, int iOff, int iLen, int lineLen, String lineSeparator)
/*     */   {
/*  90 */     int blockLen = lineLen * 3 / 4;
/*  91 */     if (blockLen <= 0) {
/*  92 */       throw new IllegalArgumentException();
/*     */     }
/*  94 */     int lines = (iLen + blockLen - 1) / blockLen;
/*  95 */     int bufLen = (iLen + 2) / 3 * 4 + lines * lineSeparator.length();
/*  96 */     StringBuilder buf = new StringBuilder(bufLen);
/*  97 */     int ip = 0;
/*  98 */     while (ip < iLen) {
/*  99 */       int l = Math.min(iLen - ip, blockLen);
/* 100 */       buf.append(encode(in, iOff + ip, l));
/* 101 */       buf.append(lineSeparator);
/* 102 */       ip += l;
/*     */     }
/* 104 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public static char[] encode(byte[] in)
/*     */   {
/* 114 */     return encode(in, 0, in.length);
/*     */   }
/*     */ 
/*     */   public static char[] encode(byte[] in, int iLen)
/*     */   {
/* 125 */     return encode(in, 0, iLen);
/*     */   }
/*     */ 
/*     */   public static char[] encode(byte[] in, int iOff, int iLen)
/*     */   {
/* 137 */     int oDataLen = (iLen * 4 + 2) / 3;
/* 138 */     int oLen = (iLen + 2) / 3 * 4;
/* 139 */     char[] out = new char[oLen];
/* 140 */     int ip = iOff;
/* 141 */     int iEnd = iOff + iLen;
/* 142 */     int op = 0;
/* 143 */     while (ip < iEnd) {
/* 144 */       int i0 = in[(ip++)] & 0xFF;
/* 145 */       int i1 = ip < iEnd ? in[(ip++)] & 0xFF : 0;
/* 146 */       int i2 = ip < iEnd ? in[(ip++)] & 0xFF : 0;
/* 147 */       int o0 = i0 >>> 2;
/* 148 */       int o1 = (i0 & 0x3) << 4 | i1 >>> 4;
/* 149 */       int o2 = (i1 & 0xF) << 2 | i2 >>> 6;
/* 150 */       int o3 = i2 & 0x3F;
/* 151 */       out[(op++)] = map1[o0];
/* 152 */       out[(op++)] = map1[o1];
/* 153 */       out[op] = (op < oDataLen ? map1[o2] : '=');
/* 154 */       op++;
/* 155 */       out[op] = (op < oDataLen ? map1[o3] : '=');
/* 156 */       op++;
/*     */     }
/* 158 */     return out;
/*     */   }
/*     */ 
/*     */   public static String decodeString(String s)
/*     */   {
/* 169 */     return new String(decode(s));
/*     */   }
/*     */ 
/*     */   public static byte[] decodeLines(String s)
/*     */   {
/* 181 */     char[] buf = new char[s.length()];
/* 182 */     int p = 0;
/* 183 */     for (int ip = 0; ip < s.length(); ip++) {
/* 184 */       char c = s.charAt(ip);
/* 185 */       if ((c != ' ') && (c != '\r') && (c != '\n') && (c != '\t')) {
/* 186 */         buf[(p++)] = c;
/*     */       }
/*     */     }
/* 189 */     return decode(buf, 0, p);
/*     */   }
/*     */ 
/*     */   public static byte[] decode(String s)
/*     */   {
/* 200 */     return decode(s.toCharArray());
/*     */   }
/*     */ 
/*     */   public static byte[] decode(char[] in)
/*     */   {
/* 211 */     return decode(in, 0, in.length);
/*     */   }
/*     */ 
/*     */   public static byte[] decode(char[] in, int iOff, int iLen)
/*     */   {
/* 224 */     if (iLen % 4 != 0) {
/* 225 */       throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
/*     */     }
/* 227 */     while ((iLen > 0) && (in[(iOff + iLen - 1)] == '=')) {
/* 228 */       iLen--;
/*     */     }
/* 230 */     int oLen = iLen * 3 / 4;
/* 231 */     byte[] out = new byte[oLen];
/* 232 */     int ip = iOff;
/* 233 */     int iEnd = iOff + iLen;
/* 234 */     int op = 0;
/* 235 */     while (ip < iEnd) {
/* 236 */       int i0 = in[(ip++)];
/* 237 */       int i1 = in[(ip++)];
/* 238 */       int i2 = ip < iEnd ? in[(ip++)] : 65;
/* 239 */       int i3 = ip < iEnd ? in[(ip++)] : 65;
/* 240 */       if ((i0 > 127) || (i1 > 127) || (i2 > 127) || (i3 > 127)) {
/* 241 */         throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
/*     */       }
/* 243 */       int b0 = map2[i0];
/* 244 */       int b1 = map2[i1];
/* 245 */       int b2 = map2[i2];
/* 246 */       int b3 = map2[i3];
/* 247 */       if ((b0 < 0) || (b1 < 0) || (b2 < 0) || (b3 < 0)) {
/* 248 */         throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
/*     */       }
/* 250 */       int o0 = b0 << 2 | b1 >>> 4;
/* 251 */       int o1 = (b1 & 0xF) << 4 | b2 >>> 2;
/* 252 */       int o2 = (b2 & 0x3) << 6 | b3;
/* 253 */       out[(op++)] = (byte)o0;
/* 254 */       if (op < oLen) {
/* 255 */         out[(op++)] = (byte)o1;
/*     */       }
/* 257 */       if (op < oLen) {
/* 258 */         out[(op++)] = (byte)o2;
/*     */       }
/*     */     }
/* 261 */     return out;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  35 */     int i = 0;
/*  36 */     for (char c = 'A'; c <= 'Z'; c = (char)(c + '\001')) {
/*  37 */       map1[(i++)] = c;
/*     */     }
/*  39 */     for (char c = 'a'; c <= 'z'; c = (char)(c + '\001')) {
/*  40 */       map1[(i++)] = c;
/*     */     }
/*  42 */     for (char c = '0'; c <= '9'; c = (char)(c + '\001')) {
/*  43 */       map1[(i++)] = c;
/*     */     }
/*  45 */     map1[(i++)] = '+';
/*  46 */     map1[(i++)] = '/';
/*     */ 
/*  49 */     map2 = new byte[''];
/*     */ 
/*  52 */     for (int j = 0; j < map2.length; j++) {
/*  53 */       map2[j] = -1;
/*     */     }
/*  55 */     for (int j = 0; j < 64; j++)
/*  56 */       map2[map1[j]] = (byte)j;
/*     */   }
/*     */ }

/* Location:           /home/pol/Pulpit/printapplet/
 * Qualified Name:     dk.apaq.vaadin.addon.printapplet.Base64Coder
 * JD-Core Version:    0.6.0
 */