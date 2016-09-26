// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CommandCharSetValue.java

package org.sanjose.textprinter;


// Referenced classes of package com.java4less.textprinter:
//            CommandParamValue

public class CommandCharSetValue extends CommandParamValue
{

    public CommandCharSetValue(String n, String v, String c)
    {
        super(n, v);
        charSet = "";
        charSet = c;
    }

    public String charSet;
}
