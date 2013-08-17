/*
 * @(#)Cursor.java	1.22 98/10/29
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package java.awt;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Toolkit;

import java.io.File;
import java.io.FileInputStream;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import java.security.AccessController;

/**
 * A class to encapsulate the bitmap representation of the mouse cursor.
 *
 * @see Component#setCursor
 * @version 	1.22, 10/29/98
 * @author 	Amy Fowler
 */
public class Cursor implements java.io.Serializable {

    /**
     * The default cursor type (gets set if no cursor is defined).
     */
    public static final int	DEFAULT_CURSOR   		= 0;

    /**
     * The crosshair cursor type.
     */
    public static final int	CROSSHAIR_CURSOR 		= 1;

    /**
     * The text cursor type.
     */
    public static final int	TEXT_CURSOR 	 		= 2;

    /**
     * The wait cursor type.
     */
    public static final int	WAIT_CURSOR	 		= 3;

    /**
     * The south-west-resize cursor type.
     */
    public static final int	SW_RESIZE_CURSOR	 	= 4;

    /**
     * The south-east-resize cursor type.
     */
    public static final int	SE_RESIZE_CURSOR	 	= 5;

    /**
     * The north-west-resize cursor type.
     */
    public static final int	NW_RESIZE_CURSOR		= 6;

    /**
     * The north-east-resize cursor type.
     */
    public static final int	NE_RESIZE_CURSOR	 	= 7;

    /**
     * The north-resize cursor type.
     */
    public static final int	N_RESIZE_CURSOR 		= 8;

    /**
     * The south-resize cursor type.
     */
    public static final int	S_RESIZE_CURSOR 		= 9;

    /**
     * The west-resize cursor type.
     */
    public static final int	W_RESIZE_CURSOR	 		= 10;

    /**
     * The east-resize cursor type.
     */
    public static final int	E_RESIZE_CURSOR			= 11;

    /**
     * The hand cursor type.
     */
    public static final int	HAND_CURSOR			= 12;

    /**
     * The move cursor type.
     */
    public static final int	MOVE_CURSOR			= 13;

    protected static Cursor predefined[] = new Cursor[14];

    /* Localization names and default values */
    static final String[][] cursorProperties = {
        { "AWT.DefaultCursor", "Default Cursor" },
        { "AWT.CrosshairCursor", "Crosshair Cursor" },
        { "AWT.TextCursor", "Text Cursor" },
        { "AWT.WaitCursor", "Wait Cursor" },
        { "AWT.SWResizeCursor", "Southwest Resize Cursor" },
        { "AWT.SEResizeCursor", "Southeast Resize Cursor" },
        { "AWT.NWResizeCursor", "Northwest Resize Cursor" },
        { "AWT.NEResizeCursor", "Northeast Resize Cursor" },
        { "AWT.NResizeCursor", "North Resize Cursor" },
        { "AWT.SResizeCursor", "South Resize Cursor" },
        { "AWT.WResizeCursor", "West Resize Cursor" },
        { "AWT.EResizeCursor", "East Resize Cursor" },
        { "AWT.HandCursor", "Hand Cursor" },
        { "AWT.MoveCursor", "Move Cursor" },
    };

    /**
     * The chosen cursor type initially set to
     * the <code>DEFAULT_CURSOR</code>.
     *
     * @serial
     * @see getType()
     */
    int type = DEFAULT_CURSOR;

    /**
     * The type associated with all custom cursors.
     */
    public static final int	CUSTOM_CURSOR			= -1;

    /*
     * hashtable, filesystem dir prefix, filename, and properties for custom cursors support
     */

    private static final Hashtable  systemCustomCursors         = new Hashtable(1);
    private static final String systemCustomCursorDirPrefix = initCursorDir();

    private static String initCursorDir() {
	String jhome =	(String) java.security.AccessController.doPrivileged(
               new sun.security.action.GetPropertyAction("java.home"));
	return jhome +
	    File.separator + "lib" + File.separator + "images" +
	    File.separator + "cursors" + File.separator;
    }

    private static final String     systemCustomCursorPropertiesFile = systemCustomCursorDirPrefix + "cursors.properties";

    private static       Properties systemCustomCursorProperties = null;

    private static final String CursorDotPrefix  = "Cursor.";
    private static final String DotFileSuffix    = ".File";
    private static final String DotHotspotSuffix = ".HotSpot";
    private static final String DotNameSuffix    = ".Name";

     /*
      * JDK 1.1 serialVersionUID
      */
    private static final long serialVersionUID = 8028237497568985504L;

    static {
        /* ensure that the necessary native libraries are loaded */
	Toolkit.loadLibraries();
	initIDs();
    }

    /**
     * Initialize JNI field and method IDs for fields that may be
       accessed from C.
     */
    private static native void initIDs();

    /**
     * The user-visible name of the cursor.
     *
     * @serial
     * @see getName()
     */
    protected String name;

    /**
     * Returns a cursor object with the specified predefined type.
     * @param type the type of predefined cursor
     */
    static public Cursor getPredefinedCursor(int type) {
	if (type < Cursor.DEFAULT_CURSOR || type > Cursor.MOVE_CURSOR) {
	    throw new IllegalArgumentException("illegal cursor type");
	}
	if (predefined[type] == null) {
	    predefined[type] = new Cursor(type);
	}
	return predefined[type];
    }

    /**
     * @return the system specific custom Cursor named
     *
     * Cursor names are, for example: "Invalid.16x16"
     */

    static public Cursor getSystemCustomCursor(final String name)
	throws AWTException {
	Cursor cursor = (Cursor)systemCustomCursors.get(name);

	if (cursor == null) {
	    synchronized(systemCustomCursors) {
		if (systemCustomCursorProperties == null)
		    loadSystemCustomCursorProperties();
	    }

	    String prefix = CursorDotPrefix + name;
	    String key    = prefix + DotFileSuffix;

	    if (!systemCustomCursorProperties.containsKey(key)) {
		System.err.println("Cursor.getSystemCustomCursor(" + name + ") returned null");
		return null;
	    }

	    final String fileName =
		systemCustomCursorProperties.getProperty(key);

	    String localized = (String)systemCustomCursorProperties.getProperty(prefix + DotNameSuffix);

	    if (localized == null) localized = name;

	    String hotspot = (String)systemCustomCursorProperties.getProperty(prefix + DotHotspotSuffix);

	    if (hotspot == null)
	    	throw new AWTException("no hotspot property defined for cursor: " + name);

	    StringTokenizer st = new StringTokenizer(hotspot, ",");

	    if (st.countTokens() != 2)
	    	throw new AWTException("failed to parse hotspot property for cursor: " + name);

	    int x = 0;
	    int y = 0;

	    try {
		x = Integer.parseInt(st.nextToken());
		y = Integer.parseInt(st.nextToken());
	    } catch (NumberFormatException nfe) {
	    	throw new AWTException("failed to parse hotspot property for cursor: " + name);
	    }

	    try {
		final int fx = x;
		final int fy = y;
		final String flocalized = localized;

                cursor = (Cursor) java.security.AccessController.doPrivileged(
		    new java.security.PrivilegedExceptionAction() {
		    public Object run() throws Exception {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image image = toolkit.getImage(
			   systemCustomCursorDirPrefix + fileName);
			return toolkit.createCustomCursor(
				    image, new Point(fx,fy), flocalized);
		    }
		});
	    } catch (Exception e) {
		throw new AWTException(
                    "Exception: " + e.getClass() + " " + e.getMessage() +
                    " occurred while creating cursor " + name);
	    }

	    if (cursor == null) {
		System.err.println("Cursor.getSystemCustomCursor(" + name + ") returned null");
	    } else {
	        systemCustomCursors.put(name, cursor);
	    }
	}

	return cursor;
    }

    /**
     * Return the system default cursor.
     */
    static public Cursor getDefaultCursor() {
        return getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    }

    /**
     * Creates a new cursor object with the specified type.
     * @param type the type of cursor
     */
    public Cursor(int type) {
	if (type < Cursor.DEFAULT_CURSOR || type > Cursor.MOVE_CURSOR) {
	    throw new IllegalArgumentException("illegal cursor type");
	}
	this.type = type;

        // Lookup localized name.
        name = Toolkit.getProperty(cursorProperties[type][0],
                                   cursorProperties[type][1]);
    }

    /**
     * Creates a new custom cursor object with the specified name.<p>
     * Note:  this constructor should only be used by AWT implementations
     * as part of their support for custom cursors.  Applications should
     * use Toolkit.createCustomCursor().
     * @param name the user-visible name of the cursor.
     * @see java.awt.Toolkit#createCustomCursor
     */
    protected Cursor(String name) {
        this.type = Cursor.CUSTOM_CURSOR;
        this.name = name;
    }

    /**
     * Returns the type for this cursor.
     */
    public int getType() {
	return type;
    }

    /**
     * Returns the name of this cursor.
     * @return    a localized description of this cursor.
     * @since     JDK1.2
     */
    public String getName() {
	return name;
    }

    /**
     * Returns a string representation of this cursor.
     * @return    a string representation of this cursor.
     * @since     JDK1.2
     */
    public String toString() {
	return getClass().getName() + "[" + getName() + "]";
    }

    /*
     * load the cursor.properties file
     */

    private static void loadSystemCustomCursorProperties() throws AWTException {
	synchronized(systemCustomCursors) {
	    systemCustomCursorProperties = new Properties();

            try {
	    	AccessController.doPrivileged(
                      new java.security.PrivilegedExceptionAction() {
		    public Object run() throws Exception {
			FileInputStream fis = null;
			try {
			    fis = new FileInputStream(
					   systemCustomCursorPropertiesFile);
			    systemCustomCursorProperties.load(fis);
			} finally {
			    if (fis != null)
				fis.close();
			}
			return null;
		    }
		});
	    } catch (Exception e) {
		systemCustomCursorProperties = null;
		 throw new AWTException("Exception: " + e.getClass() + " " +
		   e.getMessage() + " occurred while loading: " +
					systemCustomCursorPropertiesFile);
	    }
    	}
    }
}
