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
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl.MoveableList;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadChunkIndication extends CDOReadIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, LoadChunkIndication.class);

  private CDOID id;

  private int version;

  private CDOFeature feature;

  private int fromIndex;

  private int toIndex;

  public LoadChunkIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_LOAD_CHUNK;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    id = CDOIDImpl.read(in);
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Read revision ID: {0}", id);

    version = in.readInt();
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Read revision version: {0}", version);

    CDOClassRef classRef = new CDOClassRefImpl(in, null);
    int featureID = in.readInt();
    CDOClass cdoClass = classRef.resolve(getPackageManager());
    feature = cdoClass.lookupFeature(featureID);
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Read feature: {0}", feature);

    fromIndex = in.readInt();
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Read fromIndex: {0}", fromIndex);

    toIndex = in.readInt();
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Read toIndex: {0}", toIndex);
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    CDORevisionImpl revision = getRevisionManager().getRevisionByVersion(id, 0, version);
    MoveableList list = revision.getList(feature);
    for (int i = fromIndex; i <= toIndex; i++)
    {
      CDOIDImpl.write(out, (CDOID)list.get(i));
    }
  }
}
