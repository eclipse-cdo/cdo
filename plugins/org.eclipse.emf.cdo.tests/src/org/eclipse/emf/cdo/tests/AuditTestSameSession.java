package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.session.CDOSession;

/**
 * @author Eike Stepper
 */
public class AuditTestSameSession extends AuditTest
{
  public void testRepositoryCreationTime() throws Exception
  {
    CDOSession session = openSession();
    long repositoryCreationTime = session.getRepositoryInfo().getCreationTime();
    assertEquals(getRepository().getCreationTime(), repositoryCreationTime);
    assertEquals(getRepository().getStore().getCreationTime(), repositoryCreationTime);
  }

  public void testRepositoryTime() throws Exception
  {
    CDOSession session = openSession();
    long repositoryTime = session.getRepositoryInfo().getTimeStamp();
    assertEquals(true, Math.abs(System.currentTimeMillis() - repositoryTime) < 500);
  }

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