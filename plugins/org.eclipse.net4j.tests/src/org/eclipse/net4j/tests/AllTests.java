package org.eclipse.net4j.tests;


import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.net4j.tests");
//       TODO: add real JUnit tests here
		suite.addTestSuite(SampleTest.class);
		return suite;
	}

}
