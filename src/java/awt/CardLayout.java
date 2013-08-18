/*
 * @(#)CardLayout.java	1.37 02/10/31
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.IOException;

/**
 * A <code>CardLayout</code> object is a layout manager for a
 * container. It treats each component in the container as a card.
 * Only one card is visible at a time, and the container acts as
 * a stack of cards. The first component added to a 
 * <code>CardLayout</code> object is the visible component when the 
 * container is first displayed.
 * <p>
 * The ordering of cards is determined by the container's own internal
 * ordering of its component objects. <code>CardLayout</code>
 * defines a set of methods that allow an application to flip
 * through these cards sequentially, or to show a specified card.
 * The {@link CardLayout#addLayoutComponent}
 * method can be used to associate a string identifier with a given card
 * for fast random access.
 *
 * @version 	1.37 10/31/02
 * @author 	Arthur van Hoff
 * @see         java.awt.Container
 * @since       JDK1.0
 */

public class CardLayout implements LayoutManager2,
				   Serializable {

    private static final long serialVersionUID = -4328196481005934313L;

    /*
     * This creates a Vector to store associated
     * pairs of components and their names.
     * @see java.util.Vector
     */
    Vector vector = new Vector();

    /*
     * A pair of Component and String that represents its name.
     */
    class Card implements Serializable {
        static final long serialVersionUID = 6640330810709497518L;
        public String name;
        public Component comp;
        public Card(String cardName, Component cardComponent) {
            name = cardName;
            comp = cardComponent;
        }
    }

    /*
     * Index of Component currently displayed by CardLayout.
     */
    int currentCard = 0;


    /*
    * A cards horizontal Layout gap (inset). It specifies
    * the space between the left and right edges of a 
    * container and the current component.
    * This should be a non negative Integer.
    * @see getHgap()
    * @see setHgap()
    */
    int hgap;

    /*
    * A cards vertical Layout gap (inset). It specifies
    * the space between the top and bottom edges of a 
    * container and the current component.
    * This should be a non negative Integer.
    * @see getVgap()
    * @see setVgap()
    */
    int vgap;

    /**
     * @serialField tab	        Hashtable
     *      deprectated, for forward compatibility only
     * @serialField hgap        int
     * @serialField vgap        int
     * @serialField vector      Vector
     * @serialField currentCard int
     */
    private static final ObjectStreamField[] serialPersistentFields = { 
	new ObjectStreamField("tab", Hashtable.class), 
        new ObjectStreamField("hgap", Integer.TYPE), 
        new ObjectStreamField("vgap", Integer.TYPE), 
        new ObjectStreamField("vector", Vector.class), 
        new ObjectStreamField("currentCard", Integer.TYPE) 
    };

    /**
     * Creates a new card layout with gaps of size zero.
     */
    public CardLayout() {
	this(0, 0);
    }

    /**
     * Creates a new card layout with the specified horizontal and
     * vertical gaps. The horizontal gaps are placed at the left and
     * right edges. The vertical gaps are placed at the top and bottom
     * edges.
     * @param     hgap   the horizontal gap.
     * @param     vgap   the vertical gap.
     */
    public CardLayout(int hgap, int vgap) {
	this.hgap = hgap;
	this.vgap = vgap;
    }

    /**
     * Gets the horizontal gap between components.
     * @return    the horizontal gap between components.
     * @see       java.awt.CardLayout#setHgap(int)
     * @see       java.awt.CardLayout#getVgap()
     * @since     JDK1.1
     */
    public int getHgap() {
	return hgap;
    }

    /**
     * Sets the horizontal gap between components.
     * @param hgap the horizontal gap between components.
     * @see       java.awt.CardLayout#getHgap()
     * @see       java.awt.CardLayout#setVgap(int)
     * @since     JDK1.1
     */
    public void setHgap(int hgap) {
	this.hgap = hgap;
    }

    /**
     * Gets the vertical gap between components.
     * @return the vertical gap between components.
     * @see       java.awt.CardLayout#setVgap(int)
     * @see       java.awt.CardLayout#getHgap()
     */
    public int getVgap() {
	return vgap;
    }

    /**
     * Sets the vertical gap between components.
     * @param     vgap the vertical gap between components.
     * @see       java.awt.CardLayout#getVgap()
     * @see       java.awt.CardLayout#setHgap(int)
     * @since     JDK1.1
     */
    public void setVgap(int vgap) {
	this.vgap = vgap;
    }

    /**
     * Adds the specified component to this card layout's internal
     * table of names. The object specified by <code>constraints</code>
     * must be a string. The card layout stores this string as a key-value
     * pair that can be used for random access to a particular card.
     * By calling the <code>show</code> method, an application can
     * display the component with the specified name.
     * @param     comp          the component to be added.
     * @param     constraints   a tag that identifies a particular
     *                                        card in the layout.
     * @see       java.awt.CardLayout#show(java.awt.Container, java.lang.String)
     * @exception  IllegalArgumentException  if the constraint is not a string.
     */
    public void addLayoutComponent(Component comp, Object constraints) {
      synchronized (comp.getTreeLock()) {
	if (constraints instanceof String) {
	    addLayoutComponent((String)constraints, comp);
	} else {
	    throw new IllegalArgumentException("cannot add to layout: constraint must be a string");
	}
      }
    }

    /**
     * @deprecated   replaced by
     *      <code>addLayoutComponent(Component, Object)</code>.
     */
    public void addLayoutComponent(String name, Component comp) {
      synchronized (comp.getTreeLock()) {
	if (!vector.isEmpty()) {
	    comp.setVisible(false);
	}
        for (int i=0; i < vector.size(); i++) {
            if (((Card)vector.get(i)).name.equals(name)) {
                vector.remove(i);
                break;
            }
        }
	vector.add(new Card(name, comp));
      }
    }

    /**
     * Removes the specified component from the layout.
     * @param   comp   the component to be removed.
     * @see     java.awt.Container#remove(java.awt.Component)
     * @see     java.awt.Container#removeAll()
     */
    public void removeLayoutComponent(Component comp) {
      synchronized (comp.getTreeLock()) {
        for (int i = 0; i < vector.size(); i++) {
            if (((Card)vector.get(i)).comp == comp) {
				// component to be removed is present
				// if it is visible, show the next component
				if (comp.isVisible()) {
					Container parent = comp.getParent();
					if(parent != null) { 
						next(parent); 
					}
				}
                vector.remove(i);
				if (i < currentCard) {
					currentCard--; 
				}
                break;
            }
        }
      }
    }

    /**
     * Determines the preferred size of the container argument using
     * this card layout.
     * @param   parent the name of the parent container.
     * @return  the preferred dimensions to lay out the subcomponents
     *                of the specified container.
     * @see     java.awt.Container#getPreferredSize
     * @see     java.awt.CardLayout#minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container parent) {
      synchronized (parent.getTreeLock()) {
	Insets insets = parent.getInsets();
	int ncomponents = vector.size();
	int w = 0;
	int h = 0;

	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = ((Card)vector.get(i)).comp;
	    Dimension d = comp.getPreferredSize();
	    if (d.width > w) {
		w = d.width;
	    }
	    if (d.height > h) {
		h = d.height;
	    }
	}
	return new Dimension(insets.left + insets.right + w + hgap*2,
			     insets.top + insets.bottom + h + vgap*2);
      }
    }

    /**
     * Calculates the minimum size for the specified panel.
     * @param     parent the name of the parent container
     *                in which to do the layout.
     * @return    the minimum dimensions required to lay out the
     *                subcomponents of the specified container.
     * @see       java.awt.Container#doLayout
     * @see       java.awt.CardLayout#preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent) {
      synchronized (parent.getTreeLock()) {
	Insets insets = parent.getInsets();
	int ncomponents = vector.size();
	int w = 0;
	int h = 0;

	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = ((Card) vector.get(i)).comp;
	    Dimension d = comp.getMinimumSize();
	    if (d.width > w) {
		w = d.width;
	    }
	    if (d.height > h) {
		h = d.height;
	    }
	}
	return new Dimension(insets.left + insets.right + w + hgap*2,
			     insets.top + insets.bottom + h + vgap*2);
      }
    }

    /**
     * Returns the maximum dimensions for this layout given the components
     * in the specified target container.
     * @param target the component which needs to be laid out
     * @see Container
     * @see #minimumLayoutSize
     * @see #preferredLayoutSize
     */
    public Dimension maximumLayoutSize(Container target) {
	return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentX(Container parent) {
	return 0.5f;
    }

    /**
     * Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentY(Container parent) {
	return 0.5f;
    }

    /**
     * Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded.
     */
    public void invalidateLayout(Container target) {
    }

    /**
     * Lays out the specified container using this card layout.
     * <p>
     * Each component in the <code>parent</code> container is reshaped
     * to be the size of the container, minus space for surrounding
     * insets, horizontal gaps, and vertical gaps.
     *
     * @param     parent the name of the parent container
     *                             in which to do the layout.
     * @see       java.awt.Container#doLayout
     */
    public void layoutContainer(Container parent) {
      synchronized (parent.getTreeLock()) {
	Insets insets = parent.getInsets();

        if (!vector.isEmpty()) {
            Component comp = ((Card) vector.get(currentCard)).comp;
            comp.setBounds(hgap + insets.left, vgap + insets.top,
                           parent.width - (hgap*2 + insets.left + insets.right),
                           parent.height - (vgap*2 + insets.top + insets.bottom));
            if (!comp.isVisible())
                comp.setVisible(true);
        }
      }
    }

    /**
     * Make sure that the Container really has a CardLayout installed.
     * Otherwise havoc can ensue!
     */
    void checkLayout(Container parent) {
	if (parent.getLayout() != this) {
	    throw new IllegalArgumentException("wrong parent for CardLayout");
	}
    }

    /**
     * Flips to the first card of the container.
     * @param     parent   the name of the parent container
     *                          in which to do the layout.
     * @see       java.awt.CardLayout#last
     */
    public void first(Container parent) {
	synchronized (parent.getTreeLock()) {
	    checkLayout(parent);
            show(parent, 0);
	}
    }

    /**
     * Flips to the next card of the specified container. If the
     * currently visible card is the last one, this method flips to the
     * first card in the layout.
     * @param     parent   the name of the parent container
     *                          in which to do the layout.
     * @see       java.awt.CardLayout#previous
     */
    public void next(Container parent) {
	synchronized (parent.getTreeLock()) {
	    checkLayout(parent);
            show(parent, (currentCard + 1) % vector.size());
	}
    }

    /**
     * Flips to the previous card of the specified container. If the
     * currently visible card is the first one, this method flips to the
     * last card in the layout.
     * @param     parent   the name of the parent container
     *                          in which to do the layout.
     * @see       java.awt.CardLayout#next
     */
    public void previous(Container parent) {
	synchronized (parent.getTreeLock()) {
	    checkLayout(parent);
            int newIndex = (currentCard + vector.size() - 1) % vector.size(); 
            show(parent, newIndex);
	}
    }

    /**
     * Flips to the last card of the container.
     * @param     parent   the name of the parent container
     *                          in which to do the layout.
     * @see       java.awt.CardLayout#first
     */
    public void last(Container parent) {
	synchronized (parent.getTreeLock()) {
	    checkLayout(parent);
            show(parent, vector.size() - 1);
	}
    }

    /**
     * Flips to the component that was added to this layout with the
     * specified <code>name</code>, using <code>addLayoutComponent</code>.
     * If no such component exists, then nothing happens.
     * @param     parent   the name of the parent container
     *                     in which to do the layout.
     * @param     name     the component name.
     * @see       java.awt.CardLayout#addLayoutComponent(java.awt.Component, java.lang.Object)
     */
    public void show(Container parent, String name) {
	synchronized (parent.getTreeLock()) {
	    checkLayout(parent);

            if (((Card) vector.get(currentCard)).name.equals(name))
                return;

            int ncomponents = vector.size();
            for (int i = 0; i < ncomponents; i++) {
                Card card = (Card)vector.get(i);
                if (card.name.equals(name)) {
                    show(parent, i);
                    break;
                }
            }
	}
    }

    void show(Container parent, int newIndex) {
        if (!vector.isEmpty() && (currentCard != newIndex)) {
            ((Card) vector.get(currentCard)).comp.setVisible(false);
            currentCard = newIndex;
            ((Card) vector.get(currentCard)).comp.setVisible(true);
            parent.validate();
        }
    }

    /**
     * Returns a string representation of the state of this card layout.
     * @return    a string representation of this card layout.
     */
    public String toString() {
	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
    }

    /**
     * Reads serializable fields from stream.
     */
    private void readObject(ObjectInputStream s)
	throws ClassNotFoundException, IOException
    {
        ObjectInputStream.GetField f = s.readFields();

        hgap = f.get("hgap", 0);
        vgap = f.get("vgap", 0);

        if (f.defaulted("vector")) { 
            //  pre-1.4 stream
            Hashtable tab = (Hashtable)f.get("tab", null);
            vector = new Vector();
            if (tab != null && !tab.isEmpty()) {
                for (Enumeration e = tab.keys() ; e.hasMoreElements() ; ) {
                    String key = (String)e.nextElement();
                    Component comp = (Component)tab.get(key);
                    vector.add(new Card(key, comp));
                    if (comp.isVisible()) {
                        currentCard = vector.size() - 1;
                    }
                }
            }
        } else {
            vector = (Vector)f.get("vector", null);
            currentCard = f.get("currentCard", 0);
        }
    }

    /**
     * Writes serializable fields to stream.
     */
    private void writeObject(ObjectOutputStream s)
        throws IOException
    {
        Hashtable tab = new Hashtable();
        int ncomponents = vector.size();
        for (int i = 0; i < ncomponents; i++) {
            Card card = (Card)vector.get(i);
            tab.put(card.name, card.comp);
        }

        ObjectOutputStream.PutField f = s.putFields();
        f.put("hgap", hgap);
        f.put("vgap", vgap);
        f.put("vector", vector);
        f.put("currentCard", currentCard);
        f.put("tab", tab);
        s.writeFields();
    }
}
