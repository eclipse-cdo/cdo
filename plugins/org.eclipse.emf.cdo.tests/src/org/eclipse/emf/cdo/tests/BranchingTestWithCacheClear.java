package org.eclipse.emf.cdo.tests;


/**
 * @author Eike Stepper
 */
public class BranchingTestWithCacheClear extends BranchingTest
{
  @Override
  protected void closeSession1()
  {
    super.closeSession1();
    clearCache(getRepository().getRevisionManager());
  }
}
