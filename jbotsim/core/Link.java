/*******************************************************************************
 * This file is part of JBotSim.
 * 
 *     JBotSim is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JBotSim is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with JBotSim.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *     contributors:
 *     Arnaud Casteigts
 *******************************************************************************/
package jbotsim;

import java.util.HashMap;
import java.util.Vector;

import jbotsim.event.LinkListener;

public class Link{
	/**
	 * Enumerates the two possible types of a link: <tt>Type.DIRECTED</tt> and
	 * <tt>Type.UNDIRECTED</tt>.
	 */
    public static enum Type{DIRECTED, UNDIRECTED};
	/**
	 * Enumerates the two possible modes of a link: <tt>Mode.WIRED</tt> and
	 * <tt>Mode.WIRELESS</tt>.
	 */
    public static enum Mode{WIRED, WIRELESS};
    private HashMap<String,Object> properties=new HashMap<String,Object>();
    private Vector<LinkListener> listeners=new Vector<LinkListener>();
    /**
     * The source node of this link (if directed), 
     * the first endpoint otherwise.
     */
    public final Node source; 
    /**
     * The destination node of this link (if directed),
     * the second endpoint otherwise.
     */
    public final Node destination;
    /**
     * The <tt>Type</tt> of this link (directed/undirected)
     */
    public final Type type;
    /**
     * The <tt>Mode</tt> of this link (wired/wireless)
     */
    public final Mode mode;
    /**
     * Creates an undirected wired link between the two specified nodes.
     * @param n1 The source node.
     * @param n2 The destination node.
     */
    public Link(Node n1, Node n2) {
        this(n1, n2, Type.UNDIRECTED, Mode.WIRED);
    }
    /**
     * Creates a wired link of the specified <tt>type</tt> between the nodes 
     * <tt>from</tt> and <tt>to</tt>. The respective order of <tt>from</tt>
     * and <tt>to</tt> does not matter if the specified type is undirected.
     * @param from The source node.
     * @param to The destination node.
     * @param type The type of the link (<tt>Type.DIRECTED</tt> or 
     * <tt>Type.UNDIRECTED</tt>).
     */
    public Link(Node from, Node to, Type type) {
        this(from, to, type, Mode.WIRED);
    }
    /**
     * Creates a link with the specified <tt>mode</tt> between the nodes 
     * <tt>from</tt> and <tt>to</tt>. The created link is undirected by
     * default. The respective order of <tt>from</tt> and <tt>to</tt> does not
     * matter.
     * @param from The source node.
     * @param to The destination node.
     * @param mode The mode of the link (<tt>Mode.WIRED</tt> or
     * <tt>Mode.WIRELESS</tt>).
     */
    public Link(Node from, Node to, Mode mode) {
        this(from, to, Type.UNDIRECTED, mode);
    }
    /**
     * Creates a link of the specified <tt>type</tt> with the specified
     * <tt>mode</tt> between the nodes from and to. The created link is wired
     * by default. The respective order of <tt>from</tt> and <tt>to</tt> does
     * not matter if the type is undirected. 
     * @param from The source node.
     * @param to The destination node.
     * @param type The type of the link (<tt>Type.DIRECTED</tt> or 
     * <tt>Type.UNDIRECTED</tt>).
     * @param mode The mode of the link (<tt>Mode.WIRED</tt> or
     * <tt>Mode.WIRELESS</tt>).
     */
    public Link(Node from, Node to, Type type, Mode mode) {
        this.source = from;
        this.destination = to;
        this.type = type;
        this.mode = mode;
    }
    /**
     * Registers the specified listener for this link.
     * @param listener The link listener. 
     */
    public void addLinkListener(LinkListener listener){
    	listeners.add(listener);
    }
    /**
     * Unregisters the specified listener for this link.
     * @param listener The link listener. 
     */
    public void removeLinkListener(LinkListener listener){
    	listeners.remove(listener);
    }
    /**
     * Returns the node located at the opposite of the specified node 
     * (reference node) on the underlying link.
     * @param n The reference node.
     * @return The opposite node.
     */
    public Node getOtherEndpoint(Node n){
        return (n==source)?destination:source;
    }
    /**
     * Returns <tt>true</tt> if the link <tt>mode</tt> is wireless, 
     * <tt>false</tt> otherwise.
     */
    public boolean isWireless() {
    	return mode==Mode.WIRELESS;
    }
    /**
     * Returns <tt>true</tt> if the link <tt>type</tt> is directed, 
     * <tt>false</tt> otherwise.
     */
    public boolean isDirected() {
        return type==Type.DIRECTED;
    }
    /**
     * Returns the property stored under the specified name.
     * @param key The property name.
     */
    public Object getProperty(String key){
    	return properties.get(key);
    }
    /**
     * Stores the specified property (<tt>value</tt>) under the specified name
     * (<tt>key</tt>). 
     * @param key The property name.
     * @param value The property value.
     */
    public void setProperty(String key, Object value){
    	properties.put(key, value);
    	notifyLinkChanged(key);
    }
    protected void notifyLinkChanged(String key){
        for (LinkListener ll : listeners)
            ll.propertyChanged(this, key);
    }
    /**
     * Compares the specified object with this link for equality. Returns
     * <tt>true</tt> if both links have the same <tt>type</tt>
     * (directed/undirected) and the same endpoints (interchangeably if 
     * undirected). The <tt>mode</tt> is not considered by the equality test.
     */
    public boolean equals(Object o){
        Link l=(Link)o;
        return ((this.type == l.type) &&
                (l.source==this.source && l.destination==this.destination) ||
                (this.type==Type.UNDIRECTED && (l.source==this.destination && l.destination==this.source)));
    }
    /**
     * Returns a string representation of this link.
     */
    public String toString(){
        if (this.type==Type.DIRECTED)
            return (source+" --> "+destination);
        else
            return (source+" <--> "+destination);
    }
}
