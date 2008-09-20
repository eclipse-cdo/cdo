package org.eclipse.emf.cdo.tests.config.dummy;

import org.eclipse.emf.cdo.tests.config.ConfigTest;
import org.eclipse.emf.cdo.tests.config.ConfigTestSuite;

import java.util.List;

import junit.framework.Test;

/**
 * TODO Remove me
 * 
 * @author Eike Stepper
 */
public class AllTests extends ConfigTestSuite
{
  public static Test suite()
  {
    return new AllTests().getTestSuite("Dummy Tests");
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    testClasses.add(DummyTest1.class);
    testClasses.add(DummyTest2.class);
    testClasses.add(DummyTest3.class);
  }
}
