package org.eclipse.emf.cdo.tests.testbed;


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

  /**
   * TODO Remove me
   * 
   * @author Eike Stepper
   */
  public static class DummyTest1 extends ConfigTest
  {
    public void test11() throws Exception
    {
    }

    public void test12() throws Exception
    {
    }

    public void test13() throws Exception
    {
      skipConfig(ModelConfig.Legacy.INSTANCE);
    }
  }

  /**
   * TODO Remove me
   * 
   * @author Eike Stepper
   */
  public static class DummyTest2 extends ConfigTest
  {
    public void test21() throws Exception
    {
    }

    public void test22() throws Exception
    {
    }

    public void test23() throws Exception
    {
    }
  }

  /**
   * TODO Remove me
   * 
   * @author Eike Stepper
   */
  public static class DummyTest3 extends ConfigTest
  {
    public void test31() throws Exception
    {
    }

    public void test32() throws Exception
    {
    }

    public void test33() throws Exception
    {
    }
  }
}
