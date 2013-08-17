/*
 * @(#)MouseEvent.java	1.22 99/04/22
 *
 * Copyright 1996-1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt.event;

import java.awt.Component;
import java.awt.Event;
import java.awt.Point;

/**
/**
 * An event which indicates that a mouse action occurred in a component.
 * This event is used both for mouse events (click, enter, exit) and mouse 
 * motion events (moves and drags). 
 * <P>
 * This low-level event is generated by a component object for:
 * <ul>
 * <li>Mouse Events
 *     <ul>
 *     <li>a mouse button is pressed
 *     <li>a mouse button is released
 *     <li>a mouse button is clicked (pressed and released)
 *     <li>the mouse cursor enters a component
 *     <li>the mouse cursor exits a component
 *     </ul>
 * <li> Mouse Motion Events
 *     <ul>
 *     <li>the mouse is moved
 *     <li>the mouse is dragged
 *     </ul>
 * </ul>
 * <P>
 * A MouseEvent object is passed to every <code>MouseListener</code>
 * or <code>MouseAdapter</code> object which registered to receive 
 * the "interesting" mouse events using the component's 
 * <code>addMouseListener</code> method.
 * (<code>MouseAdapter</code> objects implement the 
 * <code>MouseListener</code> interface.) Each such listener object 
 * gets a <code>MouseEvent</code> containing the mouse event.
 * <P>
 * A MouseEvent object is also passed to every <code>MouseMotionListener</code>
 * or <code>MouseMotionAdapter</code> object which registered to receive 
 * mouse motion events using the component's <code>addMouseMotionListener</code>
 * method. (<code>MouseMotionAdapter</code> objects implement the 
 * <code>MouseMotionListener</code> interface.) Each such listener object 
 * gets a <code>MouseEvent</code> containing the mouse motion event.
 * <P>
 * When a mouse button is clicked, events are generated and sent to the
 * registered MouseListeners, with the button mask set in the modifier field.
 * For example, if the first mouse button is pressed, events are sent in the
 * following order:
 * <PRE>
 *    MOUSE_PRESSED:  BUTTON1_MASK
 *    MOUSE_RELEASED: BUTTON1_MASK
 *    MOUSE_CLICKED:  BUTTON1_MASK
 * </PRE>
 * When multiple mouse buttons are pressed, each press, release, and click
 * results in a separate event. The button mask in the modifier field reflects
 * only the button that changed state, not the current state of all buttons.
 * <P> 
 * For example, if the user presses button 1 followed by button 2 and
 * releases them in the same order, the following sequence of events is
 * generated:
 * <PRE>
 *    MOUSE_PRESSED:  BUTTON1_MASK
 *    MOUSE_PRESSED:  BUTTON2_MASK
 *    MOUSE_RELEASED: BUTTON1_MASK
 *    MOUSE_CLICKED:  BUTTON1_MASK
 *    MOUSE_RELEASED: BUTTON2_MASK
 *    MOUSE_CLICKED:  BUTTON2_MASK
 * </PRE>
 * If button2 is released first, the MOUSE_RELEASED/MOUSE_CLICKED pair
 * for BUTTON2_MASK arrives first, followed by the pair for BUTTON1_MASK.
 *   
 * @see MouseAdapter
 * @see MouseListener
 * @see MouseMotionAdapter
 * @see MouseMotionListner
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/mouselistener.html">Tutorial: Writing a Mouse Listener</a>
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/mousemotionlistener.html">Tutorial: Writing a Mouse Motion Listener</a>
 * @see <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">Reference: The Java Class Libraries (update file)</a>
 *
 * @version 1.22 04/22/99
 * @author Carl Quinn
 */
public class MouseEvent extends InputEvent {

    /**
     * The first number in the range of ids used for mouse events.
     */
    public static final int MOUSE_FIRST 	= 500;

    /**
     * The last number in the range of ids used for mouse events.
     */
    public static final int MOUSE_LAST          = 506;

    /**
     * The "mouse clicked" event. This MouseEvent occurs when a mouse
     * button is pressed and released.
     */
    public static final int MOUSE_CLICKED = MOUSE_FIRST;

    /**
     * The "mouse pressed" event. This MouseEvent occurs when a mouse
     * button is pushed down.
     */
    public static final int MOUSE_PRESSED = 1 + MOUSE_FIRST; //Event.MOUSE_DOWN

    /**
     * The "mouse released" event. This MouseEvent occurs when a mouse
     * button is let up.
     */
    public static final int MOUSE_RELEASED = 2 + MOUSE_FIRST; //Event.MOUSE_UP

    /**
     * The "mouse moved" event. This MouseMotionEvent occurs when the mouse
     * position changes.
     */
    public static final int MOUSE_MOVED = 3 + MOUSE_FIRST; //Event.MOUSE_MOVE

    /**
     * The "mouse entered" event. This MouseEvent occurs when the mouse
     * cursor enters a component's area.
     */
    public static final int MOUSE_ENTERED = 4 + MOUSE_FIRST; //Event.MOUSE_ENTER

    /**
     * The "mouse exited" event. This MouseEvent occurs when the mouse
     * cursor leaves a component's area.
     */
    public static final int MOUSE_EXITED = 5 + MOUSE_FIRST; //Event.MOUSE_EXIT

    /**
     * The "mouse dragged" event. This MouseMotionEvent occurs when the mouse
     * position changes while the "drag" modifier is active (for example, the
     * shift key).
     */
    public static final int MOUSE_DRAGGED = 6 + MOUSE_FIRST; //Event.MOUSE_DRAG

    /**
     * The mouse events x coordinate.
     * The x value is relative to the component
     * that fired the event.
     *
     * @serial
     * @see getX()
     */
    int x;
    /**
     * The mouse events y coordinate.
     * The y value is relative to the component
     * that fired the event.
     *
     * @serial
     * @see getY()
     */
    int y;
    /**
     * Indicates the number of quick consecutive clicks of
     * a mouse button.
     * clickCount will be valid for only three mouse events :<BR>
     * <code>MOUSE_CLICKED</code>,
     * <code>MOUSE_PRESSED</code> and
     * <code>MOUSE_RELEASED</code>.
     * For the above, the clickCount will be at least 1.  For all
     * other events the count will be 0.
     *
     * @serial
     * @see getClickCount().
     */
    int clickCount;
    /**
     * A property used to indicate whether a Popup Menu
     * should appear  with a certain gestures.
     * If <code>popupTrigger</code> = <code>false</code> no popup menu
     * should appear.  If it is <code>true</code> then a popup menu should appear
.
     *
     * @serial
     * @see java.awt.PopupMenu
     * @see isPopupTrigger()
     */
    boolean popupTrigger = false;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -991214153494842848L;

    static {
        /* ensure that the necessary native libraries are loaded */
	NativeLibLoader.loadLibraries();
	initIDs();
    }

    /**
     * Initialize JNI field and method IDs for fields that may be
       accessed from C.
     */
    private static native void initIDs();

    /**
     * Constructs a MouseEvent object with the specified source component,
     * type, modifiers, coordinates, and click count.
     *
     * @param source       the Component that originated the event
     * @param id           the integer that identifies the event
     * @param when         a long int that gives the time the event occurred
     * @param modifiers    the modifier keys down during event
     *                     (shift, ctrl, alt, meta)
     * @param x            the horizontal x coordinate for the mouse location
     * @param y            the vertical y coordinate for the mouse location
     * @param clickCount   the number of mouse clicks associated with event
     * @param popupTrigger a boolean, true if this event is a trigger for a
     *                     popup-menu 
     */
    public MouseEvent(Component source, int id, long when, int modifiers,
                      int x, int y, int clickCount, boolean popupTrigger) {
        super(source, id, when, modifiers);
        this.x = x;
        this.y = y;
        this.clickCount = clickCount;
        this.popupTrigger = popupTrigger;
    }

    /**
     * Returns the horizontal x position of the event relative to the 
     * source component.
     *
     * @return x  an integer indicating horizontal position relative to
     *            the component
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the vertical y position of the event relative to the
     * source component.
     *
     * @return y  an integer indicating vertical position relative to
     *            the component
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the x,y position of the event relative to the source component.
     *
     * @return a Point object containing the x and y coordinates 
     *         relative to the source component 
     *
     */
    public Point getPoint() {
	int x;
	int y;
	synchronized (this) {
	    x = this.x;
	    y = this.y;
	}
        return new Point(x, y);
    }

    /**
     * Translates the event's coordinates to a new position
     * by adding specified x (horizontal) and y (veritcal) offsets.
     *
     * @param x the horizontal x value to add to the current x coordinate position
     * @param y the vertical y value to add to the current y coordinate position
     */
    public synchronized void translatePoint(int x, int y) {
        this.x += x;
        this.y += y;
    }

    /**
     * Return the number of mouse clicks associated with this event.
     *
     * @return integer value for the number of clicks
     */
    public int getClickCount() {
        return clickCount;
    }

    /**
     * Returns whether or not this mouse event is the popup-menu
     * trigger event for the platform.
     *
     * @return boolean, true if this event is the popup-menu trigger
     *         for this platform
     */
    public boolean isPopupTrigger() {
        return popupTrigger;
    }

    /**
     * Returns a parameter string identifying this event.
     * This method is useful for event-logging and for debugging.
     *
     * @return a string identifying the event and its attributes
     */
    public String paramString() {
        String typeStr;
        switch(id) {
          case MOUSE_PRESSED:
              typeStr = "MOUSE_PRESSED";
              break;
          case MOUSE_RELEASED:
              typeStr = "MOUSE_RELEASED";
              break;
          case MOUSE_CLICKED:
              typeStr = "MOUSE_CLICKED";
              break;
          case MOUSE_ENTERED:
              typeStr = "MOUSE_ENTERED";
              break;
          case MOUSE_EXITED:
              typeStr = "MOUSE_EXITED";
              break;
          case MOUSE_MOVED:
              typeStr = "MOUSE_MOVED";
              break;
          case MOUSE_DRAGGED:
              typeStr = "MOUSE_DRAGGED";
              break;
          default:
              typeStr = "unknown type";
        }
        return typeStr + ",("+x+","+y+")"+ ",mods="+getModifiers()+ 
               ",clickCount="+clickCount;
    }

}
