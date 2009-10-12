package test.fede.workspace.domain.internal;
import java.net.URL;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


public class TestActivator extends Plugin {

	
	static private TestActivator SINGLETON;



	public TestActivator() {
	}
	
	
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		SINGLETON = this;
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		SINGLETON = null;
	}
	
	
	public static URL findResource(String name) {
		return SINGLETON.getBundle().getEntry("resources/"+name);
	}

}
