/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl.MoveableList;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadChunkRequest extends CDOClientRequest<CDOID>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, LoadChunkRequest.class);

  private CDORevisionImpl revision;

  private CDOFeature feature;

  private int accessIndex;

  private int fromIndex;

  private int toIndex;

  public LoadChunkRequest(IChannel channel, CDORevisionImpl revision, CDOFeature feature, int accessIndex,
      int fromIndex, int toIndex)
  {
    super(channel);
    this.revision = revision;
    this.feature = feature;
    this.accessIndex = accessIndex;
    this.fromIndex = fromIndex;
    this.toIndex = toIndex;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_LOAD_CHUNK;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    CDOID id = revision.getID();
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Writing revision ID: {0}", id);
    CDOIDImpl.write(out, id);

    int version = revision.getVersion();
    if (revision.isTransactional())
    {
      --version;
    }

    if (PROTOCOL.isEnabled()) PROTOCOL.format("Writing revision version: {0}", version);
    out.writeInt(version);

    if (PROTOCOL.isEnabled()) PROTOCOL.format("Writing feature: {0}", feature);
    CDOClassRefImpl classRef = (CDOClassRefImpl)feature.getContainingClass().createClassRef();
    classRef.write(out, null);
    out.writeInt(feature.getFeatureID());

    if (PROTOCOL.isEnabled()) PROTOCOL.format("Writing fromIndex: {0}", fromIndex);
    out.writeInt(fromIndex);

    if (PROTOCOL.isEnabled()) PROTOCOL.format("Writing toIndex: {0}", toIndex);
    out.writeInt(toIndex);
  }

  @Override
  protected CDOID confirming(ExtendedDataInputStream in) throws IOException
  {
    CDOID accessID = null;
    MoveableList list = revision.getList(feature);
    for (int i = fromIndex; i <= toIndex; i++)
    {
      CDOID id = CDOIDImpl.read(in);
      list.set(i, id);
      if (i == accessIndex)
      {
        accessID = id;
      }
    }

    return accessID;
  }
}
