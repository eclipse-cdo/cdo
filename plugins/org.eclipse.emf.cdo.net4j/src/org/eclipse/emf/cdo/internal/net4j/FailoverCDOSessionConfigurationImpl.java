/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j;

import org.eclipse.emf.cdo.net4j.FailoverCDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSession.ExceptionHandler;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class FailoverCDOSessionConfigurationImpl extends CDONet4jSessionConfigurationImpl implements
    FailoverCDOSessionConfiguration
{
  private String monitorConnectorDescription;

  private String repositoryGroup;

  public FailoverCDOSessionConfigurationImpl(String monitorConnectorDescription, String repositoryGroup)
  {
    this.monitorConnectorDescription = monitorConnectorDescription;
    this.repositoryGroup = repositoryGroup;
  }

  public String getMonitorConnectorDescription()
  {
    return monitorConnectorDescription;
  }

  public String getRepositoryGroup()
  {
    return repositoryGroup;
  }

  @Override
  public void setRepositoryName(String repositoryName)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setConnector(IConnector connector)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setFailOverStrategy(IFailOverStrategy failOverStrategy)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setExceptionHandler(ExceptionHandler exceptionHandler)
  {
    throw new UnsupportedOperationException();
  }

  public void superSetRepositoryName(String repositoryName)
  {
    super.setRepositoryName(repositoryName);
  }

  public void superSetConnector(IConnector connector)
  {
    super.setConnector(connector);
  }

  public void superSetFailOverStrategy(IFailOverStrategy failOverStrategy)
  {
    super.setFailOverStrategy(failOverStrategy);
  }

  public void superSetExceptionHandler(ExceptionHandler exceptionHandler)
  {
    super.setExceptionHandler(exceptionHandler);
  }

  @Override
  public InternalCDOSession createSession()
  {
    return new FailoverCDOSessionImpl(this);
  }

  public void sessionProtocolDeactivated(FailoverCDOSessionImpl session)
  {
    Pair<String, String> info = queryRepositoryInfoFromMonitor();
    IConnector connector = getConnector(info.getElement1());
    String repositoryName = null;

    superSetConnector(connector);
    superSetRepositoryName(repositoryName);
    initProtocol(session);

    // TODO Re-register all remote sessions
    // TODO Re-register all views
  }

  protected Pair<String, String> queryRepositoryInfoFromMonitor()
  {
    SignalProtocol<Object> protocol = new SignalProtocol<Object>("failover-client");

    try
    {
      protocol.open(getConnector(monitorConnectorDescription));
      return new RequestWithConfirmation<Pair<String, String>>(protocol, (short)1)
      {
        @Override
        protected void requesting(ExtendedDataOutputStream out) throws Exception
        {
          out.writeString(repositoryGroup);
        }

        @Override
        protected Pair<String, String> confirming(ExtendedDataInputStream in) throws Exception
        {
          String connectorDescription = in.readString();
          String repositoryName = in.readString();
          return new Pair<String, String>(connectorDescription, repositoryName);
        }
      }.send();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      protocol.close();
    }
  }

  protected IConnector getConnector(String description)
  {
    return (IConnector)getContainer().getElement("org.eclipse.net4j.connectors", "tcp", description);
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
