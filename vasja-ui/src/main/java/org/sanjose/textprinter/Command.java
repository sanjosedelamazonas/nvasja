// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Command.java

package org.sanjose.textprinter;


public class Command
{

    public Command(String sName, String sControl)
    {
        name = "";
        controlString = "";
        controlString = parseCommand(sControl);
        name = sName;
    }

    public String getName()
    {
        return name;
    }

    public String getCommand()
    {
        return controlString;
    }

    public static String parseCommand(String s)
    {
        for(int p = s.toUpperCase().indexOf("ESC"); p >= 0; p = s.toUpperCase().indexOf("ESC"))
            s = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(s.substring(0, p))))).append('\033').append(s.substring(p + 3))));

        for(int p = s.toUpperCase().indexOf("CRTL+"); p >= 0; p = s.toUpperCase().indexOf("CRTL+"))
            s = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(s.substring(0, p))))).append((char)((byte)s.charAt(p + 5) - 64)).append(s.substring(p + 6))));

        return s;
    }

    protected static final char ESC = '\033';
    String name;
    String controlString;
}
