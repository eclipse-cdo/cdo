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
import org.eclipse.emf.cdo.net4j.CDOSessionFailoverEvent;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.heartbeat.HeartBeatProtocol;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class FailoverCDOSessionImpl extends CDONet4jSessionImpl
{
  private IManagedContainer container;

  private String monitorConnectorDescription;

  private String repositoryGroup;

  private String repositoryConnectorDescription;

  public FailoverCDOSessionImpl()
  {
  }

  public void setContainer(IManagedContainer container)
  {
    this.container = container;
  }

  public void setMonitorConnectionDescription(String monitorConnectorDescription)
  {
    this.monitorConnectorDescription = monitorConnectorDescription;
  }

  public void setRepositoryGroup(String repositoryGroup)
  {
    this.repositoryGroup = repositoryGroup;
  }

  @Override
  protected void sessionProtocolDeactivated()
  {
    fireFailoverEvent(CDOSessionFailoverEvent.Type.STARTED);

    unhookSessionProtocol();
    List<AfterFailoverRunnable> runnables = failover();
    CDOSessionProtocol sessionProtocol = hookSessionProtocol();

    for (AfterFailoverRunnable runnable : runnables)
    {
      runnable.run(sessionProtocol);
    }

    fireFailoverEvent(CDOSessionFailoverEvent.Type.FINISHED);
  }

  private void fireFailoverEvent(final CDOSessionFailoverEvent.Type type)
  {
    fireEvent(new CDOSessionFailoverEvent()
    {
      public CDOSession getSource()
      {
        return FailoverCDOSessionImpl.this;
      }

      public Type getType()
      {
        return type;
      }
    });
  }

  public List<AfterFailoverRunnable> failover()
  {
    try
    {
      List<AfterFailoverRunnable> runnables = new ArrayList<AfterFailoverRunnable>();
      for (InternalCDOView view : getViews())
      {
        runnables.add(new OpenViewRunnable(view));
      }

      updateConnectorAndRepositoryName();
      initProtocol();

      // The revisionManager, branchManager, and commitInfoManager, hold their own
      // references to the sessionProtocol. We need to update those:

      InternalCDORevisionManager revisionManager = getRevisionManager();
      revisionManager.deactivate();
      revisionManager.setRevisionLoader(getSessionProtocol());
      revisionManager.activate();

      InternalCDOBranchManager branchManager = getBranchManager();
      branchManager.deactivate();
      branchManager.setBranchLoader(getSessionProtocol());
      branchManager.activate();

      InternalCDOCommitInfoManager commitInfoManager = getCommitInfoManager();
      commitInfoManager.deactivate();
      commitInfoManager.setCommitInfoLoader(getSessionProtocol());
      commitInfoManager.activate();

      return runnables;
    }
    catch (RuntimeException ex)
    {
      deactivate();
      throw ex;
    }
    catch (Error ex)
    {
      deactivate();
      throw ex;
    }
  }

  @Override
  protected void activateSession() throws Exception
  {
    updateConnectorAndRepositoryName();
    super.activateSession();
  }

  private void updateConnectorAndRepositoryName()
  {
    // System.out.println("Querying fail-over monitor...");
    queryRepositoryInfoFromMonitor();

    // System.out.println("Connecting to " + repositoryConnectorDescription + "/" + repositoryName + "...");
    IConnector connector = getConnector(repositoryConnectorDescription);
    new HeartBeatProtocol(connector, container).start(1000L, 5000L);

    setConnector(connector);
    setRepositoryName(repositoryName);
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
    return Net4jUtil.getConnector(container, "tcp", description);
  }

  /**
   * @author Eike Stepper
   */
  public static interface AfterFailoverRunnable
  {
    public void run(CDOSessionProtocol sessionProtocol);
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
