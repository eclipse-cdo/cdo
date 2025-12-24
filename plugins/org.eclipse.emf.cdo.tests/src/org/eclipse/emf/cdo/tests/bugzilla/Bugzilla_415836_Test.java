/*
 * Copyright (c) 2014-2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.emf.cdo.internal.net4j.protocol.CDOClientProtocol;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.tests.util.TestRevisionManager;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.TCPUtil;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * Bug 415836 - Test followings operations :
 *
 * <ol>
 *  <li>{@link CDOTransaction#getOrCreateResource(String)}</li>
 *  <li>{@link CDOTransaction#getOrCreateResourceFolder(String)}</li>
 *  <li>{@link CDOTransaction#getOrCreateBinaryResource(String)}</li>
 *  <li>{@link CDOTransaction#getOrCreateTextResource(String)}</li>
 * </ol>
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_415836_Test extends AbstractCDOTest
{
  private CDOSession session;

  private CDOTransaction transaction;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    session = openSession();
    transaction = session.openTransaction();
  }

  public void testCDOTransaction_GetOrCreateResourceWithNullResourcePath() throws Exception
  {
    try
    {
      transaction.getOrCreateResource(null);
      fail("IllegalArgumentException must be thrown for a null path");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
    }
  }

  public void testCDOTransaction_GetOrCreateResourceWithEmptyResourcePath() throws Exception
  {
    try
    {
      transaction.getOrCreateResource("");
      fail("IllegalArgumentException must be thrown for a empty path");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
    }
  }

  public void testCDOTransaction_GetOrCreateResourceWithExistingResourceAndTimeoutException() throws Exception
  {
    String resourcePath = getResourcePath("test1");
    transaction.getOrCreateResource(resourcePath);
    transaction.commit();
    transaction.close();
    session.close();
    session = openSession();
    transaction = session.openTransaction();

    try
    {
      enableTimeoutException(true);
      transaction.getOrCreateResource(resourcePath);
      fail("TransportException must be thrown for a timeout");
    }
    catch (TransportException expected)
    {
      // SUCCESS
    }
  }

  public void testCDOTransaction_GetOrCreateResourceFolderWithNullResourcePath() throws Exception
  {
    try
    {
      transaction.getOrCreateResourceFolder(null);
      fail("IllegalArgumentException must be thrown for a null path");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
    }
  }

  public void testCDOTransaction_GetOrCreateResourceFolderWithEmptyResourcePath() throws Exception
  {
    try
    {
      transaction.getOrCreateResourceFolder("");
      fail("IllegalArgumentException must be thrown for a empty path");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
    }
  }

  public void testCDOTransaction_GetOrCreateResourceFolderWithExistingResourceAndTimeoutException() throws Exception
  {
    String resourcePath = getResourcePath("test1");
    transaction.getOrCreateResourceFolder(resourcePath);
    transaction.commit();
    transaction.close();
    session.close();
    session = openSession();
    transaction = session.openTransaction();

    try
    {
      enableTimeoutException(true);
      transaction.getOrCreateResourceFolder(resourcePath);
      fail("TransportException must be thrown for a timeout");
    }
    catch (TransportException expected)
    {
      // SUCCESS
    }
  }

  public void testCDOTransaction_GetOrCreateTextResourceWithNullResourcePath() throws Exception
  {
    try
    {
      transaction.getOrCreateTextResource(null);
      fail("IllegalArgumentException must be thrown for a null path");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
    }
  }

  public void testCDOTransaction_GetOrCreateTextResourceWithEmptyResourcePath() throws Exception
  {
    try
    {
      transaction.getOrCreateTextResource("");
      fail("IllegalArgumentException must be thrown for a empty path");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
    }
  }

  public void testCDOTransaction_GetOrCreateTextResourceWithExistingResourceAndTimeoutException() throws Exception
  {
    String resourcePath = getResourcePath("test1");
    transaction.getOrCreateTextResource(resourcePath);
    transaction.commit();
    transaction.close();
    session.close();
    session = openSession();
    transaction = session.openTransaction();

    try
    {
      enableTimeoutException(true);
      transaction.getOrCreateTextResource(resourcePath);
      fail("TransportException must be thrown for a timeout");
    }
    catch (TransportException expected)
    {
      // SUCCESS
    }
  }

  public void testCDOTransaction_GetOrCreateBinaryResourceWithNullResourcePath() throws Exception
  {
    try
    {
      transaction.getOrCreateBinaryResource(null);
      fail("IllegalArgumentException must be thrown for a null path");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
    }
  }

  public void testCDOTransaction_GetOrCreateBinaryResourceWithEmptyResourcePath() throws Exception
  {
    try
    {
      transaction.getOrCreateBinaryResource("");
      fail("IllegalArgumentException must be thrown for a empty path");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
    }
  }

  public void testCDOTransaction_GetOrCreateBinaryResourceWithExistingResourceAndTimeoutException() throws Exception
  {
    String resourcePath = getResourcePath("test1");

    transaction.getOrCreateBinaryResource(resourcePath);
    transaction.commit();
    transaction.close();
    session.close();

    session = openSession();
    transaction = session.openTransaction();

    try
    {
      enableTimeoutException(true);
      transaction.getOrCreateBinaryResource(resourcePath);
      fail("TransportException must be thrown for a timeout");
    }
    catch (TransportException expected)
    {
      // SUCCESS
    }
  }

  public void testEnableDisableTimeoutException()
  {
    enableTimeoutException(true);
    enableTimeoutException(false);
  }

  @Requires(ISessionConfig.CAPABILITY_NET4J_TCP)
  public void testSetSignalTimeoutBeforeSessionOpening()
  {
    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
    ITCPConnector connector = TCPUtil.getConnector(getClientContainer(), SessionConfig.Net4j.TCP.CONNECTOR_HOST);
    configuration.setConnector(connector);
    configuration.setRepositoryName(RepositoryConfig.REPOSITORY_NAME);
    configuration.setRevisionManager(new TestRevisionManager());

    long signalTimeout = configuration.getSignalTimeout();
    long newSignalTimeout = 2 * signalTimeout + 1;
    configuration.setSignalTimeout(newSignalTimeout);
    InternalCDOSession cdoNet4jSession = (InternalCDOSession)configuration.openNet4jSession();
    CDOClientProtocol cdoClientProtocol = (CDOClientProtocol)cdoNet4jSession.getSessionProtocol();
    long timeout = cdoClientProtocol.getTimeout();
    assertEquals(newSignalTimeout, timeout);
  }

  private void enableTimeoutException(boolean enable)
  {
    if (session instanceof InternalCDOSession)
    {
      InternalCDOSession internalCDOSession = (InternalCDOSession)session;
      CDOSessionProtocol sessionProtocol = internalCDOSession.getSessionProtocol();
      if (sessionProtocol instanceof CDOClientProtocol)
      {
        CDOClientProtocol cdoClientProtocol = (CDOClientProtocol)sessionProtocol;
        cdoClientProtocol.setTimeout(enable ? 0L : -1L, enable);
      }
    }
  }
}
