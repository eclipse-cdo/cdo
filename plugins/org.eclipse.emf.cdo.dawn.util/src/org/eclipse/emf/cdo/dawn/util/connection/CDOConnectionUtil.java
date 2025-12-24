/*
 * Copyright (c) 2010-2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.util.connection;

import org.eclipse.emf.cdo.dawn.internal.util.bundle.OM;
import org.eclipse.emf.cdo.dawn.util.exceptions.DawnInvalidIdException;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Martin Fluegge
 */
public class CDOConnectionUtil
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOConnectionUtil.class);

  public static CDOConnectionUtil instance = new CDOConnectionUtil();

  private CDOSession currentSession;

  private String repositoryName;

  private String protocol;

  private String host;

  private Map<String, CDOTransaction> transactions;

  private IConnector connector;

  static
  {
    if (!OMPlatform.INSTANCE.isOSGiRunning())
    {
      Net4jUtil.prepareContainer(IPluginContainer.INSTANCE);
      TCPUtil.prepareContainer(IPluginContainer.INSTANCE);
      CDONet4jUtil.prepareContainer(IPluginContainer.INSTANCE);
    }
  }

  public CDOConnectionUtil()
  {
  }

  public void init(String repositoryName, String protocol, String host)
  {
    this.repositoryName = repositoryName;
    this.protocol = protocol;
    this.host = host;
    setConnector(Net4jUtil.getConnector(IPluginContainer.INSTANCE, protocol, host));
  }

  public void registerPackages(List<EPackage> packages)
  {
    if (packages == null)
    {
      return;
    }

    for (EPackage pack : packages)
    {
      pack.eClass();
    }
  }

  /**
   * opens the session if it is not opened.
   */
  public CDOSession openSession()
  {
    currentSession = (CDOSession)IPluginContainer.INSTANCE.getElement("org.eclipse.emf.cdo.sessions", "cdo",
        protocol + "://" + host + "?repositoryName=" + repositoryName);

    return currentSession;
  }

  public void closeCurrentSession()
  {
    getCurrentSession().close();
  }

  /**
   * opens a transaction on the given resourceSet
   */
  public CDOTransaction openCurrentTransaction(ResourceSet resourceSet, String id)
  {
    if (id == null)
    {
      throw new DawnInvalidIdException("The identifier '" + id + "' is invalid for openeing a transaction");
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Opening transaction for {0} on {1}", id, resourceSet); //$NON-NLS-1$
    }

    id = convert(id);
    CDOTransaction transaction = getCurrentSession().openTransaction(resourceSet);
    getTransactions().put(id, transaction);
    return transaction;
  }

  public void setChangeSubscribtionPolicyForCurrentTransaction(CDOAdapterPolicy policy, String id)
  {
    id = convert(id);
    getTransactions().get(id).options().addChangeSubscriptionPolicy(policy);
  }

  public CDOTransaction getCurrentTransaction(String id)
  {
    id = convert(id);
    return getTransactions().get(id);
  }

  // TODO find a better way to solve this problem
  private String convert(String id)
  {
    return id.replace("dawn", "cdo");
  }

  public CDOSession getCurrentSession()
  {
    if (currentSession == null)
    {
      currentSession = openSession();
    }

    return currentSession;
  }

  public Map<String, CDOTransaction> getTransactions()
  {
    if (transactions == null)
    {
      transactions = new HashMap<>();
    }

    return transactions;
  }

  public CDOView openView(CDOSession session)
  {
    return session.openView();
  }

  public CDOTransaction openTransaction(CDOSession session)
  {
    return session.openTransaction();
  }

  @Deprecated
  public static void closeSession(CDOSession session)
  {
    session.close();
  }

  @SuppressWarnings("deprecation")
  public CDOTransaction getOrOpenCurrentTransaction(String id, ResourceSet resourceSet, String repositoryName)
  {
    CDOTransaction transaction = getCurrentTransaction(id);
    CDOViewSet viewSet = CDOUtil.getViewSet(resourceSet);
    if (viewSet != null)
    {
      return ((InternalCDOView)viewSet.resolveView(repositoryName)).toTransaction();
    }

    if (transaction == null)
    {
      transaction = openCurrentTransaction(resourceSet, id);
    }

    return transaction;
  }

  public void setConnector(IConnector connector)
  {
    this.connector = connector;
  }

  public IConnector getConnector()
  {
    return connector;
  }
}
