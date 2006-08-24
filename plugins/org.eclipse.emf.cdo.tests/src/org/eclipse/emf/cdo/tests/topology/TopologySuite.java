package org.eclipse.emf.cdo.tests.topology;


import junit.framework.Assert;
import junit.framework.TestResult;
import junit.framework.TestSuite;


public class TopologySuite extends TestSuite implements ITopologyConstants
{
  private String mode = DEFAULT_MODE;

  public TopologySuite(String mode)
  {
    super("Mode " + mode);
    this.mode = mode;
  }

  public String getMode()
  {
    return mode;
  }

  @Override
  public void run(TestResult result)
  {
    Assert.assertNotNull(mode);
    System.setProperty(CDO_TEST_MODE_KEY, mode);
    super.run(result);
  }
}
