/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.server.offline;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.examples.company.CompanyFactory;
import org.eclipse.emf.cdo.examples.company.Customer;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSession;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Following console parameters are allowed: <br>
 * -automerge provides an automatic merging of the offline changes to the master repository
 * 
 * @author Eike Stepper
 * @author Martin Fluegge
 */
public class OfflineExampleClient
{
  public static final int PORT = 2037;

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

  private static void createSessionListener(final CDOSession session, final boolean autoMerging)
  {
    session.addListener(new IListener()
    {
      private boolean wasOffline = false;

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

      private void merge(final CDOSession session, State newState)
      {
        if (newState == State.ONLINE && wasOffline)
        {
          try
          {
            CDOTransaction newTransaction = session.openTransaction(session.getBranchManager().getMainBranch());

            // CDOBranch mainBranch = session.getBranchManager().getMainBranch();
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
    IConnector connector = Net4jUtil.getConnector(container, AbstractOfflineExampleServer.TRANSPORT_TYPE, "localhost:"
        + PORT);

    CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName(OfflineExampleClone.NAME);

    CDOSession session = configuration.openSession();
    CDORepositoryInfo repositoryInfo = session.getRepositoryInfo();
    System.out.println("Connected to " + repositoryInfo.getName());

    tx = session.openTransaction();
    createSessionListener(session, autoMerging);

    for (;;)
    {
      new BufferedReader(new InputStreamReader(System.in)).readLine();
      addObject(tx);
    }
  }
}
