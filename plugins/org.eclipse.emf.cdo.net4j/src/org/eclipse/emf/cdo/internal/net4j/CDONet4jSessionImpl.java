/*
 * Copyright (c) 2009-2016, 2019-2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 226778
 *    Simon McDuff - bug 230832
 *    Simon McDuff - bug 233490
 *    Simon McDuff - bug 213402
 *    Victor Roldan Betancort - maintenance
 *    Andre Dietisheim - bug 256649
 *    Christian W. Damus (CEA LIST) - bug 399306
 */
package org.eclipse.emf.cdo.internal.net4j;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.util.NotAuthenticatedException;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionConfigurationImpl.RepositoryInfo;
import org.eclipse.emf.cdo.internal.net4j.protocol.CDOClientProtocol;
import org.eclipse.emf.cdo.internal.net4j.protocol.CommitTransactionRequest;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOUserInfoManager;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDOCommitInfoUtil;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import org.eclipse.emf.internal.cdo.session.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.session.DelegatingSessionProtocol;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.collection.Entity;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IStreamWrapper;
import org.eclipse.net4j.util.security.operations.AuthorizableOperation;
import org.eclipse.net4j.util.security.operations.AuthorizableOperationFactory;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.OpenSessionResult;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("deprecation")
public class CDONet4jSessionImpl extends CDOSessionImpl implements org.eclipse.emf.cdo.net4j.CDOSession
{
  private IStreamWrapper streamWrapper;

  private IConnector connector;

  private String repositoryName;

  private long signalTimeout = SignalProtocol.DEFAULT_TIMEOUT;

  private AuthorizationCache authorizationCache;

  public CDONet4jSessionImpl()
  {
  }

  @Override
  public IManagedContainer getContainer()
  {
    IManagedContainer container = ContainerUtil.getContainer(connector);
    if (container != null)
    {
      return container;
    }

    return super.getContainer();
  }

  public IStreamWrapper getStreamWrapper()
  {
    return streamWrapper;
  }

  public void setStreamWrapper(IStreamWrapper streamWrapper)
  {
    this.streamWrapper = streamWrapper;
  }

  public IConnector getConnector()
  {
    return connector;
  }

  public void setConnector(IConnector connector)
  {
    this.connector = connector;
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public void setRepositoryName(String repositoryName)
  {
    this.repositoryName = repositoryName;
  }

  public long getSignalTimeout()
  {
    return signalTimeout;
  }

  public void setSignalTimeout(long signalTimeout)
  {
    this.signalTimeout = signalTimeout;

    // Deal with the possibility that the sessionProtocol has already been created.
    CDOClientProtocol clientProtocol = getClientProtocol();
    if (clientProtocol != null)
    {
      clientProtocol.setTimeout(this.signalTimeout);
    }
  }

  @Override
  @Deprecated
  public void changeCredentials()
  {
    changeServerPassword();
    // Send a request to the server to initiate (from the server) the password change protocol
    CDOSessionProtocol sessionProtocol = getSessionProtocol();
    sessionProtocol.requestChangeCredentials();
  }

  @Override
  public char[] changeServerPassword()
  {
    AtomicReference<char[]> result = new AtomicReference<>();

    // Send a request to the server to initiate (from the server) the password change protocol
    CDOSessionProtocol sessionProtocol = getSessionProtocol();
    sessionProtocol.requestChangeServerPassword(result);

    return result.getAndSet(null);
  }

  @Override
  public void resetCredentials(String userID)
  {
    // Send a request to the server to initiate (from the server) the password reset protocol
    CDOSessionProtocol sessionProtocol = getSessionProtocol();
    sessionProtocol.requestResetCredentials(userID);
  }

  @Override
  public OptionsImpl options()
  {
    return (OptionsImpl)super.options();
  }

  @Override
  protected OptionsImpl createOptions()
  {
    return new OptionsImpl();
  }

  protected InternalCDOBranchManager createBranchManager()
  {
    return CDOBranchUtil.createBranchManager();
  }

  @Override
  protected void authorizeOperationsRemote(AuthorizableOperation[] operations, String[] vetoes)
  {
    if (authorizationCache != null)
    {
      authorizationCache.authorizeOperations(operations, vetoes);
    }
  }

  protected OpenSessionResult openSession()
  {
    CDOClientProtocol protocol = createProtocol();
    setSessionProtocol(protocol);
    hookSessionProtocol();

    try
    {
      String userID = getUserID();
      boolean loginPeek = isLoginPeek();
      boolean passiveUpdateEnabled = options().isPassiveUpdateEnabled();
      PassiveUpdateMode passiveUpdateMode = options().getPassiveUpdateMode();
      LockNotificationMode lockNotificationMode = options().getLockNotificationMode();

      InternalCDORemoteSessionManager remoteSessionManager = getRemoteSessionManager();
      boolean subscribed = remoteSessionManager != null ? remoteSessionManager.isSubscribed() : false;
      AuthorizableOperation[] operations = AuthorizableOperationFactory.getAuthorizableOperations(getContainer());

      // TODO (CD) The next call is on the CDOClientProtocol; shouldn't it be on the DelegatingSessionProtocol instead?
      OpenSessionResult result = protocol.openSession(repositoryName, getSessionID(), userID, getOneTimeLoginToken(), loginPeek, passiveUpdateEnabled,
          passiveUpdateMode, lockNotificationMode, subscribed, operations);

      if (result == null)
      {
        // Skip to response because the user has canceled the authentication
        return null;
      }

      setSessionID(result.getSessionID());
      setUserID(result.getUserID());
      setLastUpdateTime(result.getLastUpdateTime());
      setOpeningTime(result.getOpeningTime());
      setRepositoryInfo(new RepositoryInfo(this, result));
      setClientEntities(result.getClientEntities());

      if (result.isAuthorizingOperations())
      {
        authorizationCache = new AuthorizationCache(getSessionProtocol(), operations, result.getAuthorizationResults());
      }

      return result;
    }
    catch (RemoteException ex)
    {
      if (ex.getCause() instanceof SecurityException)
      {
        throw (SecurityException)ex.getCause();
      }

      throw ex;
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    // Package registry must be available when CDOPackageUnits are received in the open session response!
    InternalCDOPackageRegistry packageRegistry = getPackageRegistry();
    if (packageRegistry == null)
    {
      packageRegistry = (InternalCDOPackageRegistry)CDOModelUtil.createPackageRegistry();
      setPackageRegistry(packageRegistry);
    }

    packageRegistry.setPackageProcessor(this);
    packageRegistry.setPackageLoader(this);
    packageRegistry.activate();

    OpenSessionResult result = openSession();
    if (result == null)
    {
      // Existing clients may expect this deprecated exception type.
      throw new NotAuthenticatedException();
    }

    super.doActivate();

    CDORepositoryInfo repository = getRepositoryInfo();
    CDOSessionProtocol sessionProtocol = getSessionProtocol();

    InternalCDORevisionManager revisionManager = getRevisionManager();
    if (revisionManager == null)
    {
      revisionManager = (InternalCDORevisionManager)CDORevisionUtil.createRevisionManager();
      setRevisionManager(revisionManager);
    }

    if (!revisionManager.isActive())
    {
      revisionManager.setSupportingAudits(repository.isSupportingAudits());
      revisionManager.setSupportingBranches(repository.isSupportingBranches());
      revisionManager.setRevisionLoader(sessionProtocol);
      revisionManager.setRevisionLocker(this);
      revisionManager.activate();
    }

    InternalCDOBranchManager branchManager = getBranchManager();
    if (branchManager == null)
    {
      branchManager = createBranchManager();
      setBranchManager(branchManager);
    }

    if (!branchManager.isActive())
    {
      branchManager.setRepository(repository);
      branchManager.setBranchLoader(sessionProtocol);
      branchManager.initMainBranch(isMainBranchLocal(), repository.getCreationTime());
      branchManager.setTagModCount(result.getTagModCount());
      branchManager.activate();
    }

    doActivateAfterBranchManager();

    InternalCDOCommitInfoManager commitInfoManager = getCommitInfoManager();
    if (commitInfoManager == null)
    {
      commitInfoManager = CDOCommitInfoUtil.createCommitInfoManager(true);
      setCommitInfoManager(commitInfoManager);
    }

    if (!commitInfoManager.isActive())
    {
      commitInfoManager.setRepository(repository);
      commitInfoManager.setBranchManager(branchManager);
      commitInfoManager.setCommitInfoLoader(sessionProtocol);
      commitInfoManager.setLastCommitOfBranch(null, getLastUpdateTime());
      commitInfoManager.activate();
    }

    CDOUserInfoManager userInfoManager = getUserInfoManager();
    if (userInfoManager == null)
    {
      userInfoManager = new UserInfoManager();
      setUserInfoManager(userInfoManager);
    }

    for (InternalCDOPackageUnit packageUnit : result.getPackageUnits())
    {
      getPackageRegistry().putPackageUnit(packageUnit);
    }

    repository.getTimeStamp(true);
    sessionProtocol.openedSession();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    CDOSessionProtocol sessionProtocol = getSessionProtocol();
    super.doDeactivate();

    InternalCDOCommitInfoManager commitInfoManager = getCommitInfoManager();
    if (commitInfoManager.getCommitInfoLoader() == sessionProtocol)
    {
      commitInfoManager.deactivate();
    }

    InternalCDORevisionManager revisionManager = getRevisionManager();
    if (revisionManager.getRevisionLoader() == sessionProtocol)
    {
      revisionManager.deactivate();
    }

    InternalCDOBranchManager branchManager = getBranchManager();
    if (branchManager.getBranchLoader() == sessionProtocol)
    {
      branchManager.deactivate();
    }

    getPackageRegistry().deactivate();
  }

  private CDOClientProtocol createProtocol()
  {
    CDOClientProtocol protocol = new CDOClientProtocol();
    protocol.setInfraStructure(this);
    if (streamWrapper != null)
    {
      protocol.setStreamWrapper(streamWrapper);
    }

    protocol.open(connector);
    protocol.setTimeout(signalTimeout);
    return protocol;
  }

  /**
   * Gets the CDOClientProtocol instance, which may be wrapped inside a DelegatingSessionProtocol
   */
  private CDOClientProtocol getClientProtocol()
  {
    CDOSessionProtocol sessionProtocol = getSessionProtocol();
    CDOClientProtocol clientProtocol;
    if (sessionProtocol instanceof DelegatingSessionProtocol)
    {
      clientProtocol = (CDOClientProtocol)((DelegatingSessionProtocol)sessionProtocol).getDelegate();
    }
    else
    {
      clientProtocol = (CDOClientProtocol)sessionProtocol;
    }

    return clientProtocol;
  }

  /**
   * @author Eike Stepper
   */
  private final class UserInfoManager implements CDOUserInfoManager
  {
    private final Map<String, Entity> userInfos = new ConcurrentHashMap<>();

    public UserInfoManager()
    {
    }

    @Override
    public CDOSession getSession()
    {
      return CDONet4jSessionImpl.this;
    }

    @Override
    public Map<String, Entity> getUserInfos(Iterable<String> userIDs)
    {
      Map<String, Entity> result = new HashMap<>();
      List<String> userIDsToRequest = null;

      for (String userID : userIDs)
      {
        Entity userInfo = userInfos.get(userID);
        if (userInfo != null)
        {
          result.put(userID, userInfo);
        }
        else
        {
          if (userIDsToRequest == null)
          {
            userIDsToRequest = new ArrayList<>();
          }

          userIDsToRequest.add(userID);
        }
      }

      if (userIDsToRequest != null)
      {
        String[] names = userIDsToRequest.toArray(new String[userIDsToRequest.size()]);
        Map<String, Entity> entities = getClientProtocol().requestEntities(CDOProtocolConstants.USER_INFO_NAMESPACE, names);
        userInfos.putAll(entities);
        result.putAll(entities);
      }

      return result;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class AuthorizationCache
  {
    private static final String GRANTED = new String("");

    private final Map<String, String> authorizations = new HashMap<>();

    private final CDOSessionProtocol sessionProtocol;

    public AuthorizationCache(CDOSessionProtocol sessionProtocol, AuthorizableOperation[] operations, String[] authorizations)
    {
      this.sessionProtocol = sessionProtocol;

      // Prime the cache.
      for (int i = 0; i < operations.length; i++)
      {
        this.authorizations.put(operations[i].getID(), authorizations[i]);
      }
    }

    public void authorizeOperations(AuthorizableOperation[] operations, String[] vetoes)
    {
      int count = operations.length;
      Map<AuthorizableOperation, Integer> operationsToLoad = null;

      synchronized (authorizations)
      {
        for (int i = 0; i < count; i++)
        {
          AuthorizableOperation operation = operations[i];
          if (operation != null && !operation.hasParameters())
          {
            String operationID = operation.getID();

            String authorization = authorizations.get(operationID);
            if (authorization != null)
            {
              vetoes[i] = authorization == GRANTED ? null : authorization;
            }
            else
            {
              if (operationsToLoad == null)
              {
                operationsToLoad = new LinkedHashMap<>();
              }

              operationsToLoad.put(operation, i);
            }
          }
        }
      }

      int size = operationsToLoad == null ? 0 : operationsToLoad.size();
      if (size != 0)
      {
        AuthorizableOperation[] requestOperations = operationsToLoad.keySet().toArray(new AuthorizableOperation[size]);
        String[] responseVetoes = sessionProtocol.authorizeOperations(requestOperations);

        synchronized (authorizations)
        {
          for (int i = 0; i < responseVetoes.length; i++)
          {
            AuthorizableOperation operation = requestOperations[i];
            String veto = responseVetoes[i];

            authorizations.put(operation.getID(), veto == null ? GRANTED : veto);

            int index = operationsToLoad.get(operation);
            vetoes[index] = veto;
          }
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  protected class OptionsImpl extends org.eclipse.emf.internal.cdo.session.CDOSessionImpl.OptionsImpl implements org.eclipse.emf.cdo.net4j.CDOSession.Options
  {
    private int commitTimeout = CommitTransactionRequest.DEFAULT_MONITOR_TIMEOUT_SECONDS;

    private int progressInterval = CommitTransactionRequest.DEFAULT_MONITOR_PROGRESS_SECONDS;

    public OptionsImpl()
    {
    }

    @Override
    public CDONet4jSession getContainer()
    {
      return (CDONet4jSession)super.getContainer();
    }

    @Override
    public ISignalProtocol<org.eclipse.emf.cdo.net4j.CDONet4jSession> getNet4jProtocol()
    {
      CDOSessionProtocol protocol = getSessionProtocol();
      if (protocol instanceof DelegatingSessionProtocol)
      {
        protocol = ((DelegatingSessionProtocol)protocol).getDelegate();
      }

      @SuppressWarnings("unchecked")
      ISignalProtocol<CDONet4jSession> signalProtocol = (ISignalProtocol<CDONet4jSession>)protocol;
      return signalProtocol;
    }

    @Override
    public ISignalProtocol<org.eclipse.emf.cdo.net4j.CDOSession> getProtocol()
    {
      @SuppressWarnings("unchecked")
      ISignalProtocol<org.eclipse.emf.cdo.net4j.CDOSession> net4jProtocol = (ISignalProtocol<org.eclipse.emf.cdo.net4j.CDOSession>)(ISignalProtocol<?>)getNet4jProtocol();
      return net4jProtocol;
    }

    @Override
    public long getSignalTimeout()
    {
      return CDONet4jSessionImpl.this.getSignalTimeout();
    }

    @Override
    public void setSignalTimeout(long signalTimeout)
    {
      CDONet4jSessionImpl.this.setSignalTimeout(signalTimeout);
    }

    @Override
    public int getCommitTimeout()
    {
      return commitTimeout;
    }

    @Override
    public synchronized void setCommitTimeout(int commitTimeout)
    {
      this.commitTimeout = commitTimeout;
    }

    @Override
    public int getProgressInterval()
    {
      return progressInterval;
    }

    @Override
    public synchronized void setProgressInterval(int progressInterval)
    {
      this.progressInterval = progressInterval;
    }
  }
}
