/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/230832
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOIDLibraryDescriptor;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.util.ServerException;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.OpenSessionResult;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class OpenSessionRequest extends CDOTimeRequest<OpenSessionResult>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, OpenSessionRequest.class);

  private String repositoryName;

  private boolean passiveUpdateEnabled;

  private OpenSessionResult result;

  public OpenSessionRequest(CDOClientProtocol protocol, String repositoryName, boolean passiveUpdateEnabled)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OPEN_SESSION);
    this.repositoryName = repositoryName;
    this.passiveUpdateEnabled = passiveUpdateEnabled;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    super.requesting(out);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing repositoryName: {0}", repositoryName);
    }

    out.writeString(repositoryName);

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing passiveUpdateEnabled: {0}", passiveUpdateEnabled);
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

    if (TRACER.isEnabled())
    {
      TRACER.format("Read sessionID: {0}", sessionID);
    }

    String repositoryUUID = in.readString();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read repositoryUUID: {0}", repositoryUUID);
    }

    long repositoryCreationTime = in.readLong();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read repositoryCreationTime: {0,date} {0,time}", repositoryCreationTime);
    }

    boolean repositorySupportingAudits = in.readBoolean();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read repositorySupportingAudits: {0}", repositorySupportingAudits);
    }

    CDOIDLibraryDescriptor libraryDescriptor = CDOIDUtil.readLibraryDescriptor(in);
    if (TRACER.isEnabled())
    {
      TRACER.format("Read libraryDescriptor: {0}", libraryDescriptor);
    }

    result = new OpenSessionResult(sessionID, repositoryUUID, repositoryCreationTime, repositorySupportingAudits,
        libraryDescriptor);

    CDOPackageUnit[] packageUnits = in.readCDOPackageUnits(null);
    for (int i = 0; i < packageUnits.length; i++)
    {
      result.getPackageUnits().add((InternalCDOPackageUnit)packageUnits[i]);
    }

    super.confirming(in);
    result.setRepositoryTimeResult(getRepositoryTimeResult());
    return result;
  }
}
