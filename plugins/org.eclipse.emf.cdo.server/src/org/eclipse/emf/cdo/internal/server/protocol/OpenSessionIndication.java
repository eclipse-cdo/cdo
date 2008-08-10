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

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.internal.server.PackageManager;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.RepositoryNotFoundException;
import org.eclipse.emf.cdo.server.SessionCreationException;

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class OpenSessionIndication extends IndicationWithResponse
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, OpenSessionIndication.class);

  private String repositoryName;

  private boolean legacySupportEnabled;

  private boolean passiveUpdateEnabled;

  public OpenSessionIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_OPEN_SESSION;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    repositoryName = in.readString();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read repositoryName: {0}", repositoryName);
    }

    legacySupportEnabled = in.readBoolean();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read legacySupportEnabled: {0}", legacySupportEnabled);
    }

    passiveUpdateEnabled = in.readBoolean();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read passiveUpdateEnabled: {0}", passiveUpdateEnabled);
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    try
    {
      Repository repository = getRepository();
      SessionManager sessionManager = repository.getSessionManager();

      CDOServerProtocol serverProtocol = (CDOServerProtocol)getProtocol();
      Session session = sessionManager.openSession(serverProtocol, legacySupportEnabled);
      session.setPassiveUpdateEnabled(passiveUpdateEnabled);

      serverProtocol.setInfraStructure(session);

      writeSessionID(out, session);
      writeRepositoryUUID(out, repository);
      repository.getStore().getCDOIDLibraryDescriptor().write(out);
      writePackages(out, repository.getPackageManager());
    }
    catch (RepositoryNotFoundException ex)
    {
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Repository {0} not found", repositoryName);
      }

      out.writeInt(CDOProtocolConstants.ERROR_REPOSITORY_NOT_FOUND);
    }
    catch (SessionCreationException ex)
    {
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Failed to open session for repository {0}", repositoryName);
      }

      out.writeInt(CDOProtocolConstants.ERROR_NO_SESSION);
      return;
    }
  }

  private Repository getRepository()
  {
    try
    {
      CDOServerProtocol protocol = (CDOServerProtocol)getProtocol();
      IRepositoryProvider repositoryProvider = (IRepositoryProvider)protocol.getInfraStructure();
      return (Repository)repositoryProvider.getRepository(repositoryName);
    }
    catch (RuntimeException ex)
    {
      throw new RepositoryNotFoundException(repositoryName);
    }
  }

  private void writeSessionID(ExtendedDataOutputStream out, ISession session) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing sessionID: {0}", session.getSessionID());
    }

    out.writeInt(session.getSessionID());
  }

  private void writeRepositoryUUID(ExtendedDataOutputStream out, IRepository repository) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing repositoryUUID: {0}", repository.getUUID());
    }

    out.writeString(repository.getUUID());
  }

  private void writePackages(ExtendedDataOutputStream out, PackageManager packageManager) throws IOException
  {
    CDOPackage[] packages = packageManager.getPackages();
    for (CDOPackage p : packages)
    {
      if (!p.isSystem())
      {
        if (PROTOCOL.isEnabled())
        {
          PROTOCOL.format("Writing package info: uri={0}, dynamic={1}, metaIDRange={2}, parentURI={3}", p
              .getPackageURI(), p.isDynamic(), p.getMetaIDRange(), p.getParentURI());
        }

        out.writeString(p.getPackageURI());
        out.writeBoolean(p.isDynamic());
        CDOIDUtil.writeMetaRange(out, p.getMetaIDRange());
        out.writeString(p.getParentURI());
      }
    }

    out.writeString(null);
  }
}
