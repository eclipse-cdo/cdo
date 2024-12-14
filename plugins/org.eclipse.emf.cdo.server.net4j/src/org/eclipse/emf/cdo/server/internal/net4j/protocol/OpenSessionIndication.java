/*
 * Copyright (c) 2009-2013, 2015-2017, 2019-2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.server.RepositoryNotFoundException;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import org.eclipse.net4j.util.collection.Entity;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.security.NotAuthenticatedException;
import org.eclipse.net4j.util.security.operations.AuthorizableOperation;

import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class OpenSessionIndication extends CDOServerIndicationWithMonitoring
{
  private String repositoryName;

  private int sessionID;

  private String userID;

  private byte[] oneTimeLoginToken;

  private boolean loginPeek;

  private boolean passiveUpdateEnabled;

  private PassiveUpdateMode passiveUpdateMode;

  private LockNotificationMode lockNotificationMode;

  private boolean subscribed;

  private AuthorizableOperation[] operations;

  private InternalRepository repository;

  private InternalSession session;

  public OpenSessionIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OPEN_SESSION);
  }

  @Override
  protected boolean closeChannelAfterException()
  {
    return true;
  }

  @Override
  protected InternalRepository getRepository()
  {
    return repository;
  }

  @Override
  protected InternalSession getSession()
  {
    return session;
  }

  @Override
  protected int getIndicatingWorkPercent()
  {
    return 10;
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    repositoryName = in.readString();
    sessionID = in.readXInt();
    userID = in.readString();
    oneTimeLoginToken = in.readByteArray();
    loginPeek = in.readBoolean();
    passiveUpdateEnabled = in.readBoolean();
    passiveUpdateMode = in.readEnum(PassiveUpdateMode.class);
    lockNotificationMode = in.readEnum(LockNotificationMode.class);
    subscribed = in.readBoolean();

    int size = in.readXInt();
    operations = new AuthorizableOperation[size];

    for (int i = 0; i < operations.length; i++)
    {
      operations[i] = AuthorizableOperation.read(in);
    }
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws Exception
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      CDOServerProtocol protocol = getProtocol();
      IRepositoryProvider repositoryProvider = protocol.getRepositoryProvider();

      try
      {
        repository = (InternalRepository)repositoryProvider.getRepository(repositoryName);
      }
      catch (Exception ex)
      {
        //$FALL-THROUGH$
      }

      if (repository == null)
      {
        throw new RepositoryNotFoundException(repositoryName);
      }

      boolean supportingLoginPeeks = repository.isSupportingLoginPeeks();
      if (loginPeek && !supportingLoginPeeks)
      {
        out.writeBoolean(true); // Login peek failure.
        return;
      }

      out.writeBoolean(false); // No login peek failure.

      try
      {
        InternalSessionManager sessionManager = repository.getSessionManager();
        session = sessionManager.openSession(protocol, sessionID, s -> {
          if (s.getUserID() == null && userID != null)
          {
            s.setUserID(userID);
          }

          s.setPassiveUpdateEnabled(passiveUpdateEnabled);
          s.setPassiveUpdateMode(passiveUpdateMode);
          s.setLockNotificationMode(lockNotificationMode);
          s.setSubscribed(subscribed);
        }, oneTimeLoginToken);
      }
      catch (NotAuthenticatedException ex)
      {
        // Skip response because the user has canceled the authentication
        out.writeXInt(0);
        flush();

        protocol.getExecutorService().submit(new Runnable()
        {
          @Override
          public void run()
          {
            ConcurrencyUtil.sleep(500);
            protocol.getChannel().close();
          }
        });

        return;
      }

      protocol.setInfraStructure(session);

      out.writeXInt(session.getSessionID());
      out.writeString(session.getUserID());
      out.writeString(repository.getUUID());
      out.writeString(repository.getName());
      out.writeEnum(repository.getType());
      out.writeEnum(repository.getState());
      out.writeString(repository.getStoreType());

      Set<CDOID.ObjectType> objectIDTypes = repository.getObjectIDTypes();
      int types = objectIDTypes.size();
      out.writeXInt(types);
      for (CDOID.ObjectType objectIDType : objectIDTypes)
      {
        out.writeEnum(objectIDType);
      }

      out.writeXLong(repository.getCreationTime());
      out.writeXLong(session.getFirstUpdateTime());
      out.writeXLong(session.getOpeningTime());
      out.writeXInt(repository.getBranchManager().getTagModCount());
      out.writeCDOID(repository.getRootResourceID());
      out.writeBoolean(repository.isAuthenticating());
      out.writeBoolean(supportingLoginPeeks);
      out.writeBoolean(repository.isSupportingAudits());
      out.writeBoolean(repository.isSupportingBranches());
      out.writeBoolean(repository.isSupportingUnits());
      out.writeBoolean(repository.isSerializingCommits());
      out.writeBoolean(repository.isEnsuringReferentialIntegrity());
      out.writeBoolean(repository.isAuthorizingOperations());
      out.writeEnum(repository.getIDGenerationLocation());
      out.writeEnum(repository.getCommitInfoStorage());

      String[] authorizations = session.authorizeOperations(operations);
      int length = authorizations == null ? 0 : authorizations.length;
      out.writeXInt(length);

      for (int i = 0; i < length; i++)
      {
        out.writeString(authorizations[i]);
      }

      CDOPackageUnit[] packageUnits = repository.getPackageRegistry(false).getPackageUnits();
      out.writeCDOPackageUnits(packageUnits);

      Map<String, Entity> clientEntities = repository.getClientEntities();
      out.writeVarInt(clientEntities.size());

      for (Entity entity : clientEntities.values())
      {
        entity.write(out);
      }
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  @Override
  protected void handleRunException(Throwable ex) throws Throwable
  {
    if (loginPeek)
    {
      if (ex instanceof RepositoryNotFoundException || ex instanceof SecurityException)
      {
        return;
      }
    }

    super.handleRunException(ex);
  }
}
