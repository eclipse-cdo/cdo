/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/230832
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOIDLibraryDescriptor;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageURICompressor;
import org.eclipse.emf.cdo.util.ServerException;

import org.eclipse.emf.internal.cdo.CDORevisionManagerImpl;
import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.CDOSessionPackageManagerImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class OpenSessionRequest extends CDOClientRequest<OpenSessionResult>
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, OpenSessionRequest.class);

  private String repositoryName;

  private boolean passiveUpdateEnabled;

  private OpenSessionResult result;

  public OpenSessionRequest(IChannel channel, String repositoryName, boolean passiveUpdateEnabled)
  {
    super(channel);
    this.repositoryName = repositoryName;
    this.passiveUpdateEnabled = passiveUpdateEnabled;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_OPEN_SESSION;
  }

  @Override
  protected CDOSessionImpl getSession()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected CDORevisionManagerImpl getRevisionManager()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected CDOSessionPackageManagerImpl getPackageManager()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected CDOPackageURICompressor getPackageURICompressor()
  {
    if (result == null)
    {
      throw new IllegalStateException("result == null");
    }

    return result;
  }

  @Override
  protected CDOIDObjectFactory getIDFactory()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Writing repositoryName: {0}", repositoryName);
    }

    out.writeString(repositoryName);

    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Writing passiveUpdateEnabled: {0}", passiveUpdateEnabled);
    }

    out.writeBoolean(passiveUpdateEnabled);
  }

  @Override
  protected OpenSessionResult confirming(CDODataInput in) throws IOException
  {
    int sessionID = in.readInt();
    if (sessionID == CDOProtocolConstants.ERROR_REPOSITORY_NOT_FOUND)
    {
      String msg = MessageFormat.format("Repository {0} not found", repositoryName);
      throw new ServerException(msg);
    }

    if (sessionID == CDOProtocolConstants.ERROR_NO_SESSION)
    {
      String msg = MessageFormat.format("Failed to open session for repository {0}", repositoryName);
      throw new ServerException(msg);
    }

    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Read sessionID: {0}", sessionID);
    }

    String repositoryUUID = in.readString();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Read repositoryUUID: {0}", repositoryUUID);
    }

    CDOIDLibraryDescriptor libraryDescriptor = CDOIDUtil.readLibraryDescriptor(in);
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Read libraryDescriptor: {0}", libraryDescriptor);
    }

    result = new OpenSessionResult(sessionID, repositoryUUID, libraryDescriptor);
    for (;;)
    {
      boolean readInfo = in.readBoolean();
      if (!readInfo)
      {
        break;
      }

      String packageURI = in.readCDOPackageURI();
      boolean dynamic = in.readBoolean();
      CDOIDMetaRange metaIDRange = in.readCDOIDMetaRange();
      String parentURI = in.readCDOPackageURI();
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Read package info: uri={0}, dynamic={1}, metaIDRange={2}, parentURI={3}", packageURI,
            dynamic, metaIDRange, parentURI);
      }

      result.addPackageInfo(packageURI, dynamic, metaIDRange, parentURI);
    }

    return result;
  }
}
