package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.session.CDOSession;

/**
 * @author Eike Stepper
 */
public class BranchingTestSameSession extends BranchingTest
{
  @Override
  protected void closeSession1()
  {
    // Do nothing
  }

  @Override
  protected CDOSession openSession2()
  {
    return session1;
  }
}