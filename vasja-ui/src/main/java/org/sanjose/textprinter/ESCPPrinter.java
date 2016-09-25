// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ESCPPrinter.java

package org.sanjose.textprinter;


public class ESCPPrinter extends TextPrinter
{

    public ESCPPrinter()
    {
        super.hLineChar = '\u2500';
        super.vLineChar = '\u2502';
        super.tlCornerChar = '\u250C';
        super.trCornerChar = '\u2510';
        super.blCornerChar = '\u2514';
        super.brCornerChar = '\u2518';
        super.vrLineChar = '\u251C';
        super.vlLineChar = '\u2524';
        super.htLineChar = '\u2534';
        super.hbLineChar = '\u252C';
        super.crossLineChar = '\u253C';
        super.linesCharSet = "437";
        setCommand(new Command(TextPrinter.CMD_RESET, "\033@"));
        setCommand(new Command(TextPrinter.CMD_BOLD_ON, "EscE"));
        setCommand(new Command(TextPrinter.CMD_BOLD_OFF, "EscF"));
        setCommand(new Command(TextPrinter.CMD_ITALIC_ON, "Esc4"));
        setCommand(new Command(TextPrinter.CMD_ITALIC_OFF, "Esc5"));
        setCommand(new Command(TextPrinter.CMD_DOUBLESTRIKE_ON, "EscG"));
        setCommand(new Command(TextPrinter.CMD_DOUBLESTRIKE_OFF, "EscH"));
        setCommand(new Command(TextPrinter.CMD_UNDERLINED_ON, "\033-\001"));
        setCommand(new Command(TextPrinter.CMD_UNDERLINED_OFF, "\033-\0"));
        setCommand(new Command(TextPrinter.CMD_DOUBLEWIDE_ON, "\033W\001"));
        setCommand(new Command(TextPrinter.CMD_DOUBLEWIDE_OFF, "\033W\0"));
        setCommand(new Command(TextPrinter.CMD_CONDENSED_ON, "Esc\017"));
        setCommand(new Command(TextPrinter.CMD_CONDENSED_OFF, "\022"));
        setCommand(new Command(TextPrinter.CMD_SUBSCRIPT_ON, "EscS\001"));
        setCommand(new Command(TextPrinter.CMD_SUBSCRIPT_OFF, "EscT"));
        setCommand(new Command(TextPrinter.CMD_SUPERSCRIPT_ON, "EscS\0"));
        setCommand(new Command(TextPrinter.CMD_SUPERSCRIPT_OFF, "EscT"));
        setCommand(new Command(TextPrinter.CMD_PAGE_LENGTH_LINES, "EscC#CHAR#"));
        setCommand(new Command(TextPrinter.CMD_PAGE_LENGTH_INCHES, "EscC\000#CHAR#"));
        setCommand(new Command(TextPrinter.CMD_PAGE_WIDTH, ""));
        setCommand(new Command(TextPrinter.CMD_TOP_MARGIN, ""));
        setCommand(new Command(TextPrinter.CMD_BOTTOM_MARGIN, "\033N#CHAR#"));
        setCommand(new Command(TextPrinter.CMD_SELECT_FONT, "\033k#"));
        setCommand(new Command(TextPrinter.CMD_SELECT_CHAR_SET, "#"));
        setCommand(new Command(TextPrinter.CMD_PITCH, "#"));
        setCommand(new Command(TextPrinter.CMD_PROPORTIONAL_ON, "\033p1"));
        setCommand(new Command(TextPrinter.CMD_PROPORTIONAL_OFF, "\033p2"));
        setCommand(new Command(TextPrinter.CMD_PORTRAIT, ""));
        setCommand(new Command(TextPrinter.CMD_LANDSCAPE, ""));
        setCommand(new Command(TextPrinter.CMD_INTERSPACE, "#"));
        setCommand(new Command(TextPrinter.CMD_QUALITY, "\033x1"));
        setCommand(new Command(TextPrinter.CMD_DRAFT, "\033x0"));
        setCommand(new Command(TextPrinter.CMD_HMI, "Escc"));
        setCommand(new Command(TextPrinter.CMD_HORIZONTAL_POSITIONING, "Esc$"));
        super.addFont("Roman", "\0");
        super.addFont("SansSerif", "\001");
        super.addFont("Courier", "\002");
        super.addFont("Prestige", "\003");
        super.addFont("Script", "\004");
        super.addFont("OCR-B", "\005");
        super.addFont("OCR-A", "\006");
        super.addFont("Orator", "\007");
        super.addFont("Orator-S", "\b");
        super.addFont("Script-C", "\t");
        super.addCharSet("437", "Esct1\0", "USA");
        super.addCharSet("ISO-IR-69", "EsctCRTL+@EscR\001", "France");
        super.addCharSet("ISO-IR-21", "EsctCRTL+@EscR\002", "Germany");
        super.addCharSet("ISO-IR-4", "EsctCRTL+@EscR\003", "England");
        super.addCharSet("Denmark", "EsctCRTL+@EscR\004", "Denmark");
        super.addCharSet("Sweden", "EsctCRTL+@EscR\005", "Sweden");
        super.addCharSet("ISO-IR-15", "EsctCRTL+@EscR\006", "Italy");
        super.addCharSet("ISO-IR-17", "EsctCRTL+@EscR\007", "Spain");
        super.addCharSet("Japan", "EsctCRTL+@EscR\b", "Japan");
        super.addCharSet("ISO-IR-60", "EsctCRTL+@EscR\t", "Norway");
        super.addCharSet("Denmark II", "EsctCRTL+@EscR\n", "Denmark II");
        super.addCharSet("ISO-IR-85", "EsctCRTL+@EscR\013", "Spain II");
        super.addCharSet("Latin America", "EsctCRTL+@EscR\f", "Latin America");
        super.addCharSet("Korea", "EsctCRTL+@EscR\r", "Korea");
        super.addPitch("10", "EscP");
        super.addPitch("12", "EscM");
        super.addPitch("15", "Escg");
        super.addSpacing("6", "Esc2");
        super.addSpacing("8", "Esc0");
        super.addSpacing("60", "EscA#CHAR#");
        super.addSpacing("180", "Esc3#CHAR#");
        super.addSpacing("360", "Esc+#CHAR#");
        super.defaultCharSet = "437";
    }

    protected void addCharSet(String name, String valueOrCommand, String charSet)
    {
        addParamValue(charSetValues, new CommandCharSetValue(name, valueOrCommand, charSet));
    }

    protected void executeCommand(String name, String param)
        throws TextPrinterException
    {
        if(name.equals(TextPrinter.CMD_LEFT_MARGIN))
        {
            int m = Integer.parseInt(param);
            if(m == 0)
                m = 1;
            executeCommandInternal(TextPrinter.CMD_LEFT_MARGIN, "".concat(String.valueOf(String.valueOf(m))));
        } else
        {
            super.executeCommandInternal(name, param);
        }
    }
}
