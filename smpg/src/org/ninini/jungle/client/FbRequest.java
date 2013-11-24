package org.ninini.jungle.client;

import com.google.gwt.core.client.JavaScriptObject;

public class FbRequest extends JavaScriptObject {
    
	protected FbRequest(){};
	
	public final native String setMethod(String method)/*-{ 
            this.method = method; }-*/;
    
	public final native String getMethod() /*-{  
            return this.method;  }-*/;
    
	public final native String setFilters(String filters)/*-{ this.filters = filters; }-*/;
    
	public final native String setMessage(String message)/*-{ 
            this.message = message; }-*/;
	
	public final native String setTo(String to)/*-{this.to = to}-*/;
	
	public final native String setTitle(String title)/*-{this.title = title}-*/;

}
