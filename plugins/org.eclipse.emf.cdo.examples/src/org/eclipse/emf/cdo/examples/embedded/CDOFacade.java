/*
 * Copyright (c) 2017, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.embedded;

import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.io.File;

/**
 * @author Eike Stepper
 */
public final class CDOFacade extends EmbeddedH2Repository
{
  public static final CDOFacade INSTANCE = new CDOFacade();

  private static final String NAME = "repo";

  private static final boolean AUDITING = false;

  private static final boolean BRANCHING = false;

  private static final File DB_FOLDER = new File("./database");

  private CDOServerBrowser serverBrowser;

  private CDONet4jSession session;

  private CDOTransaction transaction;

  private ResourceSet resourceSet;

  private CDOFacade()
  {
    super(NAME, AUDITING, BRANCHING, DB_FOLDER);
  }

  public synchronized CDONet4jSession getSession(boolean openOnDemand)
  {
    checkActive();

    if (session == null && openOnDemand)
    {
      session = openClientSession();
      session.addListener(new LifecycleEventAdapter()
      {
        @Override
        protected void onDeactivated(ILifecycle lifecycle)
        {
          if (lifecycle == session)
          {
            session = null;
          }
        }
      });
    }

    return session;
  }

  public synchronized CDOTransaction getTransaction()
  {
    checkActive();

    if (transaction == null)
    {
      resourceSet = new ResourceSetImpl();

      CDONet4jSession session = getSession(true);
      transaction = session.openTransaction(resourceSet);

      transaction.addListener(new LifecycleEventAdapter()
      {
        @Override
        protected void onDeactivated(ILifecycle lifecycle)
        {
          if (lifecycle == transaction)
          {
            transaction = null;
            resourceSet = null;
          }
        }
      });
    }

    return transaction;
  }

  public ResourceSet getResourceSet()
  {
    return resourceSet;
  }

  @Override
  public boolean isInitialPackage(IRepository repository, String nsURI)
  {
    return nsURI.equals("http://www.eclipse.org/emf/CDO/examples/company/1.0.0");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    serverBrowser = new CDOServerBrowser.ContainerBased(getContainer());
    LifecycleUtil.activate(serverBrowser);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(serverBrowser);
    serverBrowser = null;

    super.doDeactivate();
  }
}
