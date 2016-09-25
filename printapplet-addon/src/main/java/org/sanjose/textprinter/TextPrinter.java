// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TextPrinter.java

package org.sanjose.textprinter;

import java.io.UnsupportedEncodingException;
import java.util.*;

// Referenced classes of package com.java4less.textprinter:
//            Command, TextProperties, TextPrinterException, CommandParamValue, 
//            CommandCharSetValue, JobProperties, PaperSize, PrinterPort

public abstract class TextPrinter
{

    public TextPrinter()
    {
        commands = new Hashtable();
        fontValues = new Hashtable();
        charSetValues = new Hashtable();
        pitchValues = new Hashtable();
        interspacingValues = new Hashtable();
        paperSizeValues = new Hashtable();
        resolutionValues = new Hashtable();
        cmdDependencies = new Hashtable();
        bLFfterCR = true;
        pageData = null;
        rtrimLines = false;
        leftMarginPerSoftware = false;
        topMarginPerSoftware = false;
        feedFormPerSoftware = true;
        hLineChar = '-';
        vLineChar = '|';
        vrLineChar = '+';
        vlLineChar = '+';
        htLineChar = '+';
        hbLineChar = '+';
        crossLineChar = '+';
        tlCornerChar = '+';
        trCornerChar = '+';
        blCornerChar = '+';
        brCornerChar = '+';
        linesCharSet = null;
        performCharSetConversion = true;
        defaultCharSet = "";
        debug = true;
        calculatePitchAndHMI = false;
        calculateLineSpacing = false;
        emptyCharater = '\0';
        currentFont = "Courier";
        currentPitch ="12";
        javaEncoding = System.getProperty("file.encoding");
        map = new HashMap<>();
        map.put("/B", TextPrinter.CMD_BOLD_ON);
        map.put("/b", TextPrinter.CMD_BOLD_OFF);
        map.put("/I", TextPrinter.CMD_ITALIC_ON);
        map.put("/i", TextPrinter.CMD_ITALIC_OFF);
        map.put("/W", TextPrinter.CMD_DOUBLEWIDE_ON);
        map.put("/w", TextPrinter.CMD_DOUBLEWIDE_OFF);
        map.put("/U", TextPrinter.CMD_UNDERLINED_ON);
        map.put("/u", TextPrinter.CMD_UNDERLINED_OFF);
        map.put("/C", TextPrinter.CMD_CONDENSED_ON);
        map.put("/c", TextPrinter.CMD_CONDENSED_OFF);
        map.put("/P", TextPrinter.CMD_PROPORTIONAL_ON);
        map.put("/p", TextPrinter.CMD_PROPORTIONAL_OFF);

        //map.put("/Fr", TextPrinter.CMD_SELECT_FONT);
        /*fontsMap = new HashMap<>();
        fontsMap.put("r", "Roman");
        fontsMap.put("s", "SansSerif");
        fontsMap.put("c", "Courier");
        fontsMap.put("p", "Prestige");
        fontsMap.put("t", "Script");
        fontsMap.put("o", "Orator");
*/
  /*      for (String key : fontsMap.keySet()) {
            map.put("/F" + key, TextPrinter.CMD_SELECT_FONT);
        }
  */
        pitchMap = new HashMap<>();
        pitchMap.put("0", "10");
        pitchMap.put("2", "12");
        pitchMap.put("5", "15");
        for (String key : pitchMap.keySet()) {
            map.put("/F" + key, TextPrinter.CMD_PITCH);
        }

    }

    public void setCommand(Command cmd)
    {
        if(!commands.containsKey(cmd.getName()))
            commands.put(cmd.getName(), cmd);
    }


    protected void addFont(String name, String valueOrCommand)
    {
        addParamValue(fontValues, new CommandParamValue(name, valueOrCommand));
    }

    protected void addParamValue(Hashtable table, CommandParamValue val)
    {
        if(table.containsKey(val.name))
            table.remove(val.name);
        val.value = Command.parseCommand(val.value);
        table.put(val.name, val);
    }



    protected String replaceParameter(String command, String value)
        throws TextPrinterException
    {
        int p = command.indexOf("#CHAR#");
        if(p >= 0)
        {
            if(value.length() == 0)
                value = "0";
            byte b[] = new byte[1];
            b[0] = (byte)(0xff & (int)Math.floor(Double.parseDouble(value)));
            try
            {
                command = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(command.substring(0, p))))).append(new String(b, javaEncoding)).append(command.substring(p + 6))));
            }
            catch(Exception e)
            {
                throw new TextPrinterException(e.getMessage());
            }
        }
        p = command.indexOf("#");
        if(p >= 0)
            command = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(command.substring(0, p))))).append(value).append(command.substring(p + 1))));
        return command;
    }

    protected void addPitch(String name, String valueOrCommand)
    {
        addParamValue(pitchValues, new CommandParamValue(name, valueOrCommand));
    }

    protected void addSpacing(String name, String valueOrCommand)
    {
        addParamValue(interspacingValues, new CommandParamValue(name, valueOrCommand));
    }

    protected void addCharSet(String name, String valueOrCommand, String charSet)
    {
        addParamValue(charSetValues, new CommandCharSetValue(name, valueOrCommand, charSet));
    }


    protected final void executeCommandInternal(String name, String param)
            throws TextPrinterException
    {
        Command cmd = getCommand(name);
        if(cmd != null)
        {
            addToBuffer(replaceParameter(cmd.getCommand(), param));
            if(debug)
                System.out.println(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(cmd.getName())))).append(": ").append(param))));
            //executeDependencies(name);
        }
    }


    protected Result convertPart(String s) throws TextPrinterException {
        int pos=s.length()-1;
        String keyFound = null;
        for (String key : map.keySet()) {
            if (s.indexOf(key)>=0 && s.indexOf(key)<pos) {
                pos = s.indexOf(key);
                keyFound = key;
            }
        }
        if (keyFound!=null) {
            addToBuffer(s.substring(0,pos));
            if (keyFound.startsWith("/F")) {
                //executeCommandInternal(map.get(keyFound), fontsMap.get(new Character(keyFound.charAt(2)).toString()));
                processCommandsForPitch(currentPitch, pitchMap.get(new Character(keyFound.charAt(2)).toString()));
                //processCommandsForFont(currentFont, fontsMap.get(new Character(keyFound.charAt(2)).toString()));
            } else {
                executeCommand(map.get(keyFound));
            }
        }
        if (pos<s.length()-1) {
            return new Result(s.substring(pos+keyFound.length()), false);
        }
        else
            return new Result(s, true);
    }

    public String convert(String conv) throws TextPrinterException {
        buffer = new byte[conv.length()+20];
        Result result;
        String str = conv;
        boolean notFound = false;
        do {
            result = convertPart(str);
            str = result.getResStr();
            notFound = result.isNotFound();
        }
        while (!notFound);
        addToBuffer(result.getResStr());
        String resStr = new String(buffer).substring(0,bufferPointer);
        flushBuffer();
        return resStr;
    }


    protected void addToBuffer(String s)
            throws TextPrinterException
    {
        try
        {
            addToBuffer(s.getBytes(javaEncoding));
        }
        catch(UnsupportedEncodingException e)
        {
            throw new TextPrinterException(e.getMessage());
        }
    }

    protected void addToBuffer(byte b[])
            throws TextPrinterException
    {
        boolean isCommand = false;
        if(b == null)
            return;
        for(int i = 0; i < b.length; i++)
        {
            if(debug)
                if(i == 0)
                {
                    if(b[i] == 10)
                        System.out.println("LF ");
                    else
                    if(b[i] == 12)
                        System.out.print("FF ");
                    else
                    if(b[i] == 13)
                        System.out.print("CR ");
                    else
                    if(b[i] == 27)
                    {
                        System.out.print("ESC ");
                        isCommand = true;
                    } else
                    {
                        System.out.print(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(Integer.toHexString(b[i]).toUpperCase())))).append("(").append(new String(b)).append(") "))));
                    }
                } else
                if(isCommand)
                    System.out.print(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(Integer.toHexString(b[i]).toUpperCase())))).append("(").append(new String(b, i, 1)).append(") "))));
                else
                    System.out.print(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(Integer.toHexString(b[i]).toUpperCase())))).append("(").append(new String(b)).append(") "))));
            try
            {
                buffer[bufferPointer++] = b[i];
                if(bufferPointer == bufferSize)
                    flushBuffer();
                continue;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return;
        }

    }

    protected void flushBuffer()
            throws TextPrinterException
    {
        //if(bufferPointer > 0)
            //port.write(buffer, bufferPointer);
        bufferPointer = 0;
    }


    public Command getCommand(String name)
    {
        if(commands.containsKey(name))
            return (Command)commands.get(name);
        else
            return null;
    }

    protected void executeCommand(String name)
            throws TextPrinterException
    {
        executeCommandInternal(name, null);
    }

    protected String mapTableLookup(Hashtable table, String value)
    {
        if(table == null)
            return null;
        for(Enumeration e = table.elements(); e.hasMoreElements();)
        {
            CommandParamValue p = (CommandParamValue)e.nextElement();
            if(p.name.equals(value))
                return p.value;
            if(p.name.equals("*"))
                return p.value;
        }

        return null;
    }

    protected void processCommandsForFont(String currentFont, String newFont)
            throws TextPrinterException
    {
        if(!currentFont.equals(newFont) && !newFont.equals(""))
        {
            String mappedFont = mapTableLookup(fontValues, newFont);
            if(mappedFont != null)
                executeCommandInternal(CMD_SELECT_FONT, mappedFont);
        }
    }

    protected void processCommandsForPitch(String currentPitch, String newPitch)
            throws TextPrinterException
    {
        if(!currentPitch.equals(newPitch) && !newPitch.equals(""))
        {
            String mappedPitch = getBestPitchCommand(newPitch);
            if(mappedPitch == null)
                return;
            mappedPitch = replaceParameter(mappedPitch, newPitch);
            executeCommandInternal(CMD_PITCH, mappedPitch);
        }
    }

    protected String getBestPitchCommand(String pitch)
    {
        int bestPitch = 0;
        String bestPitchCommand = null;
        double iPitch = Double.parseDouble(pitch);
        Enumeration e = pitchValues.elements();
        do
        {
            if(!e.hasMoreElements())
                break;
            CommandParamValue p = (CommandParamValue)e.nextElement();
            if(p.name.equals("*"))
                return p.value;
            if(Double.parseDouble(p.name) == iPitch)
                return p.value;
            if(Double.parseDouble(p.name) > iPitch && Double.parseDouble(p.name) < (double)bestPitch)
            {
                bestPitch = Integer.parseInt(p.name);
                bestPitchCommand = p.value;
            }
        } while(true);
        return bestPitchCommand;
    }


    protected final char ESC = '\033';
    protected final char LF = '\n';
    protected final char CR = '\r';
    protected final char FF = '\f';
    protected Hashtable commands;
    protected static String CMD_RESET = "CMD_RESET";
    protected static String CMD_BOLD_ON = "CMD_BOLD_ON";
    protected static String CMD_BOLD_OFF = "CMD_BOLD_OFF";
    protected static String CMD_HORIZONTAL_POSITIONING = "CMD_HORIZONTAL_POSITIONING";
    protected static String CMD_ITALIC_ON = "CMD_ITALIC_ON";
    protected static String CMD_ITALIC_OFF = "CMD_ITALIC_OFF";
    protected static String CMD_DOUBLESTRIKE_ON = "CMD_DOUBLESTRIKE_ON";
    protected static String CMD_DOUBLESTRIKE_OFF = "CMD_DOUBLESTRIKE_OFF";
    protected static String CMD_UNDERLINED_ON = "CMD_UNDERLINED_ON";
    protected static String CMD_UNDERLINED_OFF = "CMD_UNDERLINED_OFF";
    protected static String CMD_DOUBLEWIDE_ON = "CMD_DOUBLEWIDE_ON";
    protected static String CMD_DOUBLEWIDE_OFF = "CMD_DOUBLEWIDE_OFF";
    protected static String CMD_CONDENSED_ON = "CMD_CONDENSED_ON";
    protected static String CMD_CONDENSED_OFF = "CMD_CONDENSED_OFF";
    protected static String CMD_SUBSCRIPT_ON = "CMD_SUBSCRIPT_ON";
    protected static String CMD_SUBSCRIPT_OFF = "CMD_SUBSCRIPT_OFF";
    protected static String CMD_SUPERSCRIPT_ON = "CMD_SUPERSCRIPT_ON";
    protected static String CMD_SUPERSCRIPT_OFF = "CMD_SUPERSCRIPT_OFF";
    protected static String CMD_PAGE_LENGTH_LINES = "CMD_PAGE_LENGTH_LINES";
    protected static String CMD_PAGE_LENGTH_INCHES = "CMD_PAGE_LENGTH_INCHES";
    protected static String CMD_PAGE_WIDTH = "CMD_PAGE_WIDTH";
    protected static String CMD_TOP_MARGIN = "CMD_TOP_MARGIN";
    protected static String CMD_BOTTOM_MARGIN = "CMD_BOTTOM_MARGIN";
    protected static String CMD_LEFT_MARGIN = "CMD_LEFT_MARGIN";
    protected static String CMD_RIGHT_MARGIN = "CMD_RIGHT_MARGIN";
    protected static String CMD_SELECT_FONT = "CMD_SELECT_FONT";
    protected static String CMD_SELECT_CHAR_SET = "CMD_SELECT_CHAR_SET";
    protected static String CMD_PITCH = "CMD_PITCH";
    protected static String CMD_PROPORTIONAL_ON = "CMD_PROPORTIONAL_ON";
    protected static String CMD_PROPORTIONAL_OFF = "CMD_PROPORTIONAL_OFF";
    protected static String CMD_PORTRAIT = "CMD_PORTRAIT";
    protected static String CMD_LANDSCAPE = "CMD_LANDSCAPE";
    protected static String CMD_INTERSPACE = "CMD_INTERSPACE";
    protected static String CMD_QUALITY = "CMD_QUALITY";
    protected static String CMD_DRAFT = "CMD_DRAFT";
    protected static String CMD_HMI = "CMD_HMI";
    protected static String CMD_PAPER_SIZE = "CMD_PAPER_SIZE";
    protected static String CMD_RESOLUTION = "CMD_RESOLUTION";
    protected Hashtable fontValues;
    protected Hashtable charSetValues;
    protected Hashtable pitchValues;
    protected Hashtable interspacingValues;
    protected Hashtable paperSizeValues;
    protected Hashtable resolutionValues;
    protected Hashtable cmdDependencies;
    protected boolean bLFfterCR;
    protected char pageData[][];
    public boolean rtrimLines;
    protected int currentRow;
    protected int currentCol;
    protected int bufferSize;
    protected int bufferPointer;
    protected byte buffer[];
    public boolean leftMarginPerSoftware;
    public boolean topMarginPerSoftware;
    public boolean feedFormPerSoftware;
    public char hLineChar;
    public char vLineChar;
    public char vrLineChar;
    public char vlLineChar;
    public char htLineChar;
    public char hbLineChar;
    public char crossLineChar;
    public char tlCornerChar;
    public char trCornerChar;
    public char blCornerChar;
    public char brCornerChar;
    public String linesCharSet;
    public boolean performCharSetConversion;
    public String defaultCharSet;
    public boolean debug;
    public boolean calculatePitchAndHMI;
    public boolean calculateLineSpacing;
    public String currentFont;
    public String currentPitch;
    char emptyCharater;
    String javaEncoding;
    public Map<String, String> map;
    public Map<String, String> fontsMap;
    public Map<String, String> pitchMap;


    public class Result {

        String resStr;
        boolean notFound;

        public Result(String resStr, boolean notFound) {
            this.resStr = resStr;
            this.notFound = notFound;
        }

        public String getResStr() {
            return resStr;
        }

        public void setResStr(String resStr) {
            this.resStr = resStr;
        }

        public boolean isNotFound() {
            return notFound;
        }

        public void setNotFound(boolean notFound) {
            this.notFound = notFound;
        }
    }
}
