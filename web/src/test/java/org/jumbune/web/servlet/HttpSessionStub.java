package org.jumbune.web.servlet;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * A stub implementation of the HttpSession class for use in Servlet/JSP 
 * test cases
 */
public class HttpSessionStub  implements HttpSession {

    
    private Object arg0;
	private HashMap<String,Object> hm=new HashMap<String, Object>();

	/* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getAttribute(java.lang.String)
     */
    public Object getAttribute(String key) {
        return hm.get(key);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getAttributeNames()
     */
    public Enumeration getAttributeNames() {
        
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getCreationTime()
     */
    public long getCreationTime() {
        
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getRef()
     */
    public String getId() {
        
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getLastAccessedTime()
     */
    public long getLastAccessedTime() {
        
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
     */
    public int getMaxInactiveInterval() {
        
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getSessionContext()
     */
    public HttpSessionContext getSessionContext() {
        
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getValue(java.lang.String)
     */
    public Object getValue(String arg0) {
        
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getValueNames()
     */
    public String[] getValueNames() {
        
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#invalidate()
     */
    public void invalidate() {
        hm.clear();
        
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#isNew()
     */
    public boolean isNew() {
        
        return false;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#putValue(java.lang.String, java.lang.Object)
     */
    public void putValue(String arg0, Object arg1) {
        
        
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String arg0) {
        
        
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
     */
    public void removeValue(String arg0) {
        
        
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String arg0, Object arg1) {
        
       
        hm.put(arg0,arg1);
        
    }
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#setMaxInactiveInterval(int)
     */
    public void setMaxInactiveInterval(int arg0) {
        
        
    }
    
    public ServletContext getServletContext() {
                
        return null;
    }
    

}
