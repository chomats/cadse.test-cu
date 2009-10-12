package test.fede.workspace.domain.internal;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;


@RunWith(Suite.class)
@SuiteClasses(value={
		SamCoreTest_cadseg1.class
})

public class SamTest1Suite {
	public static Test suite() {
		return (Test) new JUnit4TestAdapter(SamTest1Suite.class);
	}
	
}
