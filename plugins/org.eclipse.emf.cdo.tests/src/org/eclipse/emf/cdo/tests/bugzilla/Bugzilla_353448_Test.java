/*
 * Copyright (c) 2012, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class Bugzilla_353448_Test extends AbstractCDOTest
{
  private static final long TIMEOUT = 5 * 1000;

  private static final String BASE_PREFIX = "baseFolder";

  private static final String MODEL1 = "company1Models";

  private static final String MODEL2 = "company2Models";

  private static final String MODEL1_PREFIX = BASE_PREFIX + "/" + MODEL1;

  private static int TOTAL_JOBS_NUMBER = 5;

  private CDOSession session;

  @Requires(IRepositoryConfig.CAPABILITY_UNAVAILABLE)
  public void testDuplicateEntry() throws Exception
  {
    bootStrapPaths();

    final Exception[] exception = { null };
    final CountDownLatch latch = new CountDownLatch(TOTAL_JOBS_NUMBER);
    for (int i = 0; i < TOTAL_JOBS_NUMBER; i++)
    {
      final IPath p = new Path("/Project1/SubProject1/" + i % 4 + "/" + i);
      new Thread("Create Resource Job")
      {
        @Override
        public void run()
        {
          msg("Commiting " + MODEL1_PREFIX + p);
          TransactionDelegateMock delegate = null;

          try
          {
            delegate = new TransactionDelegateMock(session.openTransaction(), new Path("/" + MODEL1_PREFIX + p));
            CommitException commitEx = null;

            for (int j = 0; j < 10; j++)
            {
              try
              {
                delegate.resource.getContents().add(getModel1Factory().createOrderDetail());
                delegate.transaction.commit();
                msg("END OF COMMIT " + MODEL1_PREFIX + p);
                return;
              }
              catch (CommitException ex)
              {
                delegate.transaction.rollback();
                commitEx = ex;

                // Try again
                sleep(200L);
              }
            }

            throw commitEx;
          }
          catch (Exception ex)
          {
            IOUtil.print(ex);
            if (exception[0] == null)
            {
              exception[0] = ex;
            }
          }
          finally
          {
            latch.countDown();
            if (delegate != null)
            {
              delegate.transaction.close();
            }
          }
        }
      }.start();
    }

    await(latch);

    if (exception[0] != null)
    {
      throw exception[0];
    }
  }

  private void bootStrapPaths() throws CommitException
  {
    session = openSession();
    CDOTransaction transaction = session.openTransaction();

    try
    {
      transaction.getResourceNode(getResourcePath(BASE_PREFIX));
    }
    catch (Exception e)
    {
      CDOResourceFolder base = transaction.createResourceFolder(getResourcePath(BASE_PREFIX));
      base.addResourceFolder(MODEL1);
      base.addResourceFolder(MODEL2);

      try
      {
        transaction.commit();
      }
      finally
      {
        transaction.close();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private class TransactionDelegateMock
  {
    private final CDOTransaction transaction;

    private final CDOResource resource;

    public TransactionDelegateMock(CDOTransaction transaction, IPath path) throws Exception
    {
      this.transaction = transaction;
      resource = getResource(path, transaction);
    }

    private synchronized CDOResource getResource(IPath path2, CDOTransaction transaction2) throws Exception
    {
      if (!transaction2.hasResource(getResourcePath(path2.toString())))
      {
        // only if resource does not exists
        String[] segments = path2.segments();
        CDOResourceNode resourceNode = null;
        for (int i = 0; i < segments.length; i++)
        {
          String curPath = path2.uptoSegment(i).toString();
          if (!existsResourceNode(transaction2, curPath))
          {
            assert resourceNode != null : "The base resource nodes should have been bootstrapped";
            CDOLock lock = resourceNode.cdoWriteLock();

            if (lock.tryLock(TIMEOUT, TimeUnit.MILLISECONDS))
            {
              if (!existsResourceNode(transaction2, curPath))
              {
                msg("Adding resource folder " + curPath + "  curThread " + Thread.currentThread());
                resourceNode = transaction2.createResourceFolder(getResourcePath(curPath));
                transaction2.commit();
              }
              else
              {
                resourceNode = transaction2.getResourceNode(getResourcePath(curPath));
              }
            }
          }
          else
          {
            resourceNode = transaction2.getResourceNode(getResourcePath(curPath));
            msg("Path Exists " + curPath + " OID " + resourceNode.cdoID());
          }
        }

        resourceNode = transaction2.getResourceNode(getResourcePath(path2.removeLastSegments(1).toString()));
        if (resourceNode.cdoWriteLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS))
        {
          CDOResource rez = transaction2.createResource(getResourcePath(path2.toString()));
          transaction2.commit();
          return rez;
        }
      }

      return transaction2.getResource(getResourcePath(path2.toString()));
    }

    private boolean existsResourceNode(CDOTransaction transaction, String path)
    {
      try
      {
        return transaction.getResourceNode(getResourcePath(path)) != null;
      }
      catch (Exception e)
      {
        return false;
      }
    }
  }
}
