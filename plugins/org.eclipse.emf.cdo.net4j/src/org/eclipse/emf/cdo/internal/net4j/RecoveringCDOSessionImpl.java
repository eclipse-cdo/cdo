/*
 * Copyright (c) 2010-2012, 2014, 2015, 2018, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.net4j.CDOSessionRecoveryEvent;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionEvent;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.heartbeat.HeartBeatProtocol;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Caspar De Groot
 */
public abstract class RecoveringCDOSessionImpl extends CDONet4jSessionImpl
{
  private IManagedContainer container;

  private String repositoryConnectorDescription;

  private boolean useHeartBeat;

  private long heartBeatPeriod = 1000L;

  private long heartBeatTimeout = 5000L;

  private long connectorTimeout = 10000L;

  public RecoveringCDOSessionImpl()
  {
    setExceptionHandler(new RecoveringExceptionHandler());
  }

  public long getConnectorTimeout()
  {
    return connectorTimeout;
  }

  public void setConnectorTimeout(long connectorTimeout)
  {
    this.connectorTimeout = connectorTimeout;
  }

  public void setContainer(IManagedContainer container)
  {
    this.container = container;
  }

  @Override
  public IManagedContainer getContainer()
  {
    return container;
  }

  public void setUseHeartBeat(boolean useHeartBeat)
  {
    this.useHeartBeat = useHeartBeat;
  }

  public boolean getUseHeartBeat()
  {
    return useHeartBeat;
  }

  public void setHeartBeatTimeout(long timeout)
  {
    heartBeatTimeout = timeout;
  }

  public long getHeartBeatTimeout()
  {
    return heartBeatTimeout;
  }

  public void setHeartBeatPeriod(long period)
  {
    heartBeatPeriod = period;
  }

  public long getHeartBeatPeriod()
  {
    return heartBeatPeriod;
  }

  @Override
  protected void sessionProtocolDeactivated()
  {
    recover(1, null);
  }

  protected void recover(int attempt, TransportException exception)
  {
    OM.LOG.info("Transport exception (" + attempt + ". attempt to recover)", exception);

    fireEvent(createRecoveryStartedEvent(attempt, exception));
    recover();
    fireEvent(createRecoveryFinishedEvent(attempt, exception));
  }

  protected void recover()
  {
    CDOSessionProtocol oldSessionProtocol = getSessionProtocol();
    unhookSessionProtocol();
    List<AfterRecoveryRunnable> runnables = recoverSession();

    // Check if the sessionProtocol was replaced. (This may not be the case
    // if the protocol is wrapped inside a DelegatingSessionProtocol.)
    CDOSessionProtocol newSessionProtocol = getSessionProtocol();
    if (newSessionProtocol != oldSessionProtocol)
    {
      handleProtocolChange(oldSessionProtocol, newSessionProtocol);
    }

    for (AfterRecoveryRunnable runnable : runnables)
    {
      runnable.run(newSessionProtocol);
    }

    boolean passiveUpdateEnabled = options().isPassiveUpdateEnabled();
    refresh(passiveUpdateEnabled);
  }

  protected void handleProtocolChange(CDOSessionProtocol oldProtocol, CDOSessionProtocol newProtocol)
  {
    // The revisionManager, branchManager, and commitInfoManager, hold their own
    // references to the sessionProtocol. We need to update those:

    InternalCDORevisionManager revisionManager = getRevisionManager();
    revisionManager.deactivate();
    revisionManager.setRevisionLoader(newProtocol);
    revisionManager.activate();

    InternalCDOBranchManager branchManager = getBranchManager();
    branchManager.deactivate();
    branchManager.setBranchLoader(newProtocol);
    branchManager.activate();

    InternalCDOCommitInfoManager commitInfoManager = getCommitInfoManager();
    commitInfoManager.deactivate();
    commitInfoManager.setCommitInfoLoader(newProtocol);
    commitInfoManager.activate();
  }

  /**
   * @deprecated As of 4.16 use {@link #createRecoveryStartedEvent(int, TransportException)}.
   */
  @Deprecated
  protected CDOSessionEvent createRecoveryStartedEvent()
  {
    return createRecoveryStartedEvent(0, null);
  }

  protected CDOSessionEvent createRecoveryStartedEvent(int attempt, TransportException exception)
  {
    return new CDOSessionRecoveryEventImpl(this, CDOSessionRecoveryEvent.Type.STARTED, attempt, exception);
  }

  /**
   * @deprecated As of 4.16 use {@link #createRecoveryFinishedEvent(int, TransportException)}.
   */
  @Deprecated
  protected CDOSessionEvent createRecoveryFinishedEvent()
  {
    return createRecoveryFinishedEvent(0, null);
  }

  protected CDOSessionEvent createRecoveryFinishedEvent(int attempt, TransportException exception)
  {
    return new CDOSessionRecoveryEventImpl(this, CDOSessionRecoveryEvent.Type.FINISHED, attempt, exception);
  }

  protected IConnector createTCPConnector(boolean heartBeat)
  {
    IConnector connector = getTCPConnector(repositoryConnectorDescription);
    if (heartBeat)
    {
      new HeartBeatProtocol(connector, container).start(heartBeatPeriod, heartBeatTimeout);
    }

    connector.addListener(new AutoCloser());
    return connector;
  }

  protected IConnector getTCPConnector(String description)
  {
    IManagedContainer container = getContainer();
    return Net4jUtil.getConnector(container, "tcp", description, connectorTimeout);
  }

  protected List<AfterRecoveryRunnable> recoverSession()
  {
    try
    {
      List<AfterRecoveryRunnable> runnables = new ArrayList<>();
      for (InternalCDOView view : getViews())
      {
        runnables.add(new OpenViewRunnable(view));
      }

      updateConnectorAndRepositoryName();
      openSession();

      CDOSessionProtocol sessionProtocol = getSessionProtocol();
      sessionProtocol.openedSession();

      return runnables;
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Error ex)
    {
      throw ex;
    }
  }

  @Override
  public void setSessionProtocol(CDOSessionProtocol sessionProtocol)
  {
    super.setSessionProtocol(sessionProtocol);

    // Bug 534014: The DelegatingSessionProtocol of this session is deactivated by
    // CDOSessionImpl.sessionProtocolListener when the delegate protocol becomes inactive.
    // The super.setSessionProtocol() method just replaces the delegate protocol but
    // doesn't reactivate the DelegatingSessionProtocol. Reactivate it now.
    LifecycleUtil.activate(getSessionProtocol());
  }

  protected IConnector removeTCPConnector()
  {
    return (IConnector)container.removeElement("org.eclipse.net4j.connectors", "tcp", repositoryConnectorDescription);
  }

  protected void setRepositoryConnectorDescription(String description)
  {
    repositoryConnectorDescription = description;
  }

  protected String getRepositoryConnectorDescription()
  {
    return repositoryConnectorDescription;
  }

  @Override
  protected void doActivate() throws Exception
  {
    updateConnectorAndRepositoryName();
    super.doActivate();
  }

  protected abstract void updateConnectorAndRepositoryName();

  /**
   * @author Eike Stepper
   */
  public static interface AfterRecoveryRunnable
  {
    public void run(CDOSessionProtocol sessionProtocol);
  }

  /**
   * @author Eike Stepper
   */
  private class RecoveringExceptionHandler implements ExceptionHandler
  {
    @Override
    public void handleException(CDOSession session, int attempt, Exception exception) throws Exception
    {
      if (exception instanceof TransportException)
      {
        recover(attempt, (TransportException)exception);
      }
      else
      {
        throw exception;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class OpenViewRunnable implements AfterRecoveryRunnable
  {
    private final int viewID;

    private final boolean transaction;

    private final String durableLockingID;

    private final CDOBranchPoint branchPoint;

    private final CDOViewImpl.OptionsImpl options;

    public OpenViewRunnable(InternalCDOView view)
    {
      viewID = view.getViewID();
      transaction = view instanceof CDOTransaction;

      durableLockingID = view.getDurableLockingID();
      if (durableLockingID == null)
      {
        branchPoint = CDOBranchUtil.copyBranchPoint(view);
      }
      else
      {
        branchPoint = null;
      }

      options = (CDOViewImpl.OptionsImpl)view.options();
    }

    @Override
    public void run(CDOSessionProtocol sessionProtocol)
    {
      if (durableLockingID != null)
      {
        sessionProtocol.openView(viewID, !transaction, durableLockingID);
      }
      else
      {
        sessionProtocol.openView(viewID, !transaction, branchPoint);
      }

      options.recoverView();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class AutoCloser implements IListener
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof IContainerEvent<?>)
      {
        IContainerEvent<?> containerEvent = (IContainerEvent<?>)event;
        if (containerEvent.getDelta().getKind() == IContainerDelta.Kind.REMOVED)
        {
          IConnector connector = (IConnector)event.getSource();
          if (connector.getChannels().size() == 0)
          {
            LifecycleUtil.deactivate(connector);
          }
        }
      }
    }
  }
}
