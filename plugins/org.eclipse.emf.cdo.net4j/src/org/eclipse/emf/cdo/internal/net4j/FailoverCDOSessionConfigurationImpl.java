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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.net4j.FailoverCDOSessionImpl.AfterFailoverRunnable;
import org.eclipse.emf.cdo.net4j.FailoverCDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSession.ExceptionHandler;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.heartbeat.HeartBeatProtocol;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class FailoverCDOSessionConfigurationImpl extends CDONet4jSessionConfigurationImpl implements
    FailoverCDOSessionConfiguration
{
  private String monitorConnectorDescription;

  // private IConnector monitorConnector;
  //
  // private SignalProtocol<Object> monitorProtocol;

  private String repositoryGroup;

  private String repositoryConnectorDescription;

  private String repositoryName;

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
  public void setExceptionHandler(ExceptionHandler exceptionHandler)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public InternalCDOSession createSession()
  {
    updateConnectorAndRepositoryName();
    return new FailoverCDOSessionImpl(this);
  }

  public List<AfterFailoverRunnable> failover(FailoverCDOSessionImpl session)
  {
    try
    {
      List<AfterFailoverRunnable> runnables = new ArrayList<AfterFailoverRunnable>();
      for (InternalCDOView view : session.getViews())
      {
        runnables.add(new OpenViewRunnable(view));
      }

      uncheckedSetPassiveUpdateEnabled(session.options().isPassiveUpdateEnabled());
      uncheckedSetPassiveUpdateMode(session.options().getPassiveUpdateMode());

      updateConnectorAndRepositoryName();
      initProtocol(session);
      return runnables;
    }
    catch (RuntimeException ex)
    {
      session.deactivate();
      throw ex;
    }
    catch (Error ex)
    {
      session.deactivate();
      throw ex;
    }
  }

  private void updateConnectorAndRepositoryName()
  {
    System.out.println("Querying fail-over monitor...");
    queryRepositoryInfoFromMonitor();

    System.out.println("Connecting to " + repositoryConnectorDescription + "/" + repositoryName + "...");
    IConnector connector = getConnector(repositoryConnectorDescription);
    new HeartBeatProtocol(connector, getContainer()).start(1000L, 5000L);

    uncheckedSetConnector(connector);
    uncheckedSetRepositoryName(repositoryName);
  }

  protected void queryRepositoryInfoFromMonitor()
  {
    IConnector connector = getConnector(monitorConnectorDescription);
    SignalProtocol<Object> protocol = new SignalProtocol<Object>("failover-client");
    protocol.open(connector);

    try
    {
      String oldRepositoryConnectorDescription = repositoryConnectorDescription;
      String oldRepositoryName = repositoryName;

      while (ObjectUtil.equals(repositoryConnectorDescription, oldRepositoryConnectorDescription)
          && ObjectUtil.equals(repositoryName, oldRepositoryName))
      {
        new RequestWithConfirmation<Boolean>(protocol, (short)1, "QueryRepositoryInfo")
        {
          @Override
          protected void requesting(ExtendedDataOutputStream out) throws Exception
          {
            out.writeString(repositoryGroup);
          }

          @Override
          protected Boolean confirming(ExtendedDataInputStream in) throws Exception
          {
            repositoryConnectorDescription = in.readString();
            repositoryName = in.readString();
            return true;
          }
        }.send();
      }
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      protocol.close();
      if (connector.getChannels().isEmpty())
      {
        connector.close();
      }
    }
  }

  protected IConnector getConnector(String description)
  {
    IManagedContainer container = getContainer();
    // container.removeElement("org.eclipse.net4j.connectors", "tcp", description);
    return (IConnector)container.getElement("org.eclipse.net4j.connectors", "tcp", description);
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  /**
   * @author Eike Stepper
   */
  private final class OpenViewRunnable implements AfterFailoverRunnable
  {
    private int viewID;

    private CDOBranchPoint branchPoint;

    private boolean transaction;

    public OpenViewRunnable(InternalCDOView view)
    {
      viewID = view.getViewID();
      branchPoint = CDOBranchUtil.copyBranchPoint(view);
      transaction = view instanceof CDOTransaction;
    }

    public void run(CDOSessionProtocol sessionProtocol)
    {
      sessionProtocol.openView(viewID, branchPoint, !transaction);
    }
  }
}
