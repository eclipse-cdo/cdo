package org.eclipse.emf.cdo.tests.config.dummy;

import org.eclipse.emf.cdo.tests.config.ConfigTest;
import org.eclipse.emf.cdo.tests.config.SessionConfig;

/**
 * TODO Remove me
 * 
 * @author Eike Stepper
 */
public class DummyTest1 extends ConfigTest
{
  public void test11() throws Exception
  {
  }

  public void test12() throws Exception
  {
  }

  public void test13() throws Exception
  {
    skipConfig(SessionConfig.JVM.INSTANCE);
  }
}
