/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.server.offline;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.examples.company.CompanyFactory;
import org.eclipse.emf.cdo.examples.company.Customer;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Following console parameters are allowed: <br>
 * -automerge provides an automatic merging of the offline changes to the master repository
 *
 * @author Eike Stepper
 * @author Martin Fluegge
 * @since 4.0
 */
public class OfflineExampleClient
{
  public static final int PORT = OfflineExampleUtil.CLONE_PORT;

  private static CDOTransaction tx;

  private static void addObject(CDOTransaction tx)
  {
    try
    {
      Customer customer = CompanyFactory.eINSTANCE.createCustomer();
      tx.getOrCreateResource("/r1").getContents().add(customer);

      System.out.println("Committing an object to " + tx.getBranch().getPathName());
      CDOCommitInfo commitInfo = tx.commit();
      CDOBranch branch = commitInfo.getBranch();
      System.out.println("Committed an object to  " + branch.getPathName());
      tx.setBranch(branch);
    }
    catch (CommitException x)
    {
      throw new RuntimeException(x);
    }
  }

  private static void lockObject(CDOTransaction tx)
  {
    EList<EObject> contents = tx.getOrCreateResource("/r1").getContents();
    int size = contents.size();
    if (size < 1)
    {
      System.out.println("There are no objects; can't lock anything.");
    }

    System.out.println("Locking last object");
    CDOObject firstObject = CDOUtil.getCDOObject(contents.get(size - 1));
    firstObject.cdoWriteLock().lock();
    System.out.println("Locked last object");
  }

  private static void unlockObject(CDOTransaction tx)
  {
    EList<EObject> contents = tx.getOrCreateResource("/r1").getContents();
    int size = contents.size();
    if (size < 1)
    {
      System.out.println("There are no objects; can't unlock anything.");
    }

    System.out.println("Unlocking last object");
    CDOObject firstObject = CDOUtil.getCDOObject(contents.get(size - 1));
    firstObject.cdoWriteLock().unlock();
    System.out.println("Unlocked last object");
  }

  private static void createBranch(CDOTransaction tx)
  {
    CDOBranch subBranch = tx.getBranch().createBranch("sub.1");
    tx.setBranch(subBranch);
  }

  private static boolean isAutoMerge(String[] args)
  {
    for (int i = 0; i < args.length; i++)
    {
      if (args[i].equals("-automerge"))
      {
        return true;
      }
    }

    return false;
  }

  private static void createSessionListener(final CDONet4jSession session, final boolean autoMerging)
  {
    session.addListener(new IListener()
    {
      private boolean wasOffline;

      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOCommonRepository.StateChangedEvent)
        {
          CDOCommonRepository.StateChangedEvent e = (CDOCommonRepository.StateChangedEvent)event;
          State newState = e.getNewState();
          System.out.println("State changed to " + newState);
          if (autoMerging)
          {
            merge(session, newState);
          }
        }
      }

      private void merge(final CDONet4jSession session, State newState)
      {
        if (newState == State.ONLINE && wasOffline)
        {
          try
          {
            CDOTransaction newTransaction = session.openTransaction(session.getBranchManager().getMainBranch());
            newTransaction.merge(tx.getBranch().getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
            newTransaction.commit();

            tx.close();
            tx = newTransaction;
          }
          catch (CommitException ex)
          {
            ex.printStackTrace();
          }
          finally
          {
            wasOffline = false;
          }
        }
        else if (newState == State.OFFLINE)
        {
          wasOffline = true;
        }
      }
    });
  }

  public static void main(String[] args) throws Exception
  {
    boolean autoMerging = isAutoMerge(args);

    System.out.println("Client starting...");
    IManagedContainer container = OfflineExampleUtil.createContainer();
    IConnector connector = Net4jUtil.getConnector(container, AbstractOfflineExampleServer.TRANSPORT_TYPE, "localhost:" + PORT);

    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName(OfflineExampleUtil.CLONE_NAME);

    CDONet4jSession session = configuration.openNet4jSession();
    CDORepositoryInfo repositoryInfo = session.getRepositoryInfo();
    System.out.println("Connected to " + repositoryInfo.getName());

    tx = session.openTransaction();
    tx.enableDurableLocking();
    createSessionListener(session, autoMerging);

    for (;;)
    {
      System.out.println();
      System.out.println("Enter a command:");
      System.out.println("0 - exit");
      System.out.println("1 - add an object to the repository");
      System.out.println("2 - lock the last object in the repository");
      System.out.println("3 - unlock the last object in the repository");
      System.out.println("4 - create a branch");

      String command = new BufferedReader(new InputStreamReader(System.in)).readLine();
      if ("0".equals(command))
      {
        break;
      }

      if ("1".equals(command))
      {
        addObject(tx);
      }
      else if ("2".equals(command))
      {
        lockObject(tx);
      }
      else if ("3".equals(command))
      {
        unlockObject(tx);
      }
      else if ("4".equals(command))
      {
        createBranch(tx);
      }
    }

    session.close();
    LifecycleUtil.deactivate(container);
  }
}
