/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.server.RepositoryNotFoundException;
import org.eclipse.emf.cdo.server.SessionCreationException;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class OpenSessionIndication extends CDOServerIndication
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, OpenSessionIndication.class);

  private String repositoryName;

  private boolean passiveUpdateEnabled;

  private Repository repository;

  private Session session;

  public OpenSessionIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_OPEN_SESSION;
  }

  @Override
  protected Repository getRepository()
  {
    return repository;
  }

  @Override
  protected Session getSession()
  {
    return session;
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    repositoryName = in.readString();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Read repositoryName: {0}", repositoryName);
    }

    passiveUpdateEnabled = in.readBoolean();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Read passiveUpdateEnabled: {0}", passiveUpdateEnabled);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    try
    {
      CDOServerProtocol protocol = getProtocol();
      IRepositoryProvider repositoryProvider = (IRepositoryProvider)protocol.getInfraStructure();
      repository = (Repository)repositoryProvider.getRepository(repositoryName);
      if (repository == null)
      {
        throw new RepositoryNotFoundException(repositoryName);
      }

      SessionManager sessionManager = repository.getSessionManager();

      session = sessionManager.openSession(protocol);
      session.setPassiveUpdateEnabled(passiveUpdateEnabled);

      // Adjust the infra structure (was IRepositoryProvider)
      protocol.setInfraStructure(session);
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Writing sessionID: {0}", session.getSessionID());
      }

      out.writeInt(session.getSessionID());
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Writing repositoryUUID: {0}", repository.getUUID());
      }

      out.writeString(repository.getUUID());
      repository.getStore().getCDOIDLibraryDescriptor().write(out);
      writePackageInfos(out);
    }
    catch (RepositoryNotFoundException ex)
    {
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Repository {0} not found", repositoryName);
      }

      out.writeInt(CDOProtocolConstants.ERROR_REPOSITORY_NOT_FOUND);
    }
    catch (SessionCreationException ex)
    {
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Failed to open session for repository {0}", repositoryName);
      }

      out.writeInt(CDOProtocolConstants.ERROR_NO_SESSION);
      return;
    }
  }

  private void writePackageInfos(CDODataOutput out) throws IOException
  {
    CDOPackage[] packages = getPackageManager().getPackages();
    for (CDOPackage p : packages)
    {
      if (!p.isSystem())
      {
        if (PROTOCOL_TRACER.isEnabled())
        {
          PROTOCOL_TRACER.format("Writing package info: uri={0}, dynamic={1}, metaIDRange={2}, parentURI={3}", p
              .getPackageURI(), p.isDynamic(), p.getMetaIDRange(), p.getParentURI());
        }

        out.writeBoolean(true);
        out.writeCDOPackageURI(p.getPackageURI());
        out.writeBoolean(p.isDynamic());
        out.writeCDOIDMetaRange(p.getMetaIDRange());
        out.writeCDOPackageURI(p.getParentURI());
      }
    }

    out.writeBoolean(false);
  }
}
