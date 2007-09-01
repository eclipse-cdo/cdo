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
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.emf.internal.cdo.bundle.OM;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadChunkRequest extends CDOClientRequest<CDOID[]>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, LoadChunkRequest.class);

  private CDOID id;

  private CDOFeature feature;

  private int fromIndex;

  private int toIndex;

  public LoadChunkRequest(IChannel channel, CDOID id, CDOFeature feature, int fromIndex, int toIndex)
  {
    super(channel, CDOProtocolConstants.SIGNAL_LOAD_CHUNK);
    this.id = id;
    this.feature = feature;
    this.fromIndex = fromIndex;
    this.toIndex = toIndex;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Writing ID: {0}", id);
    CDOIDImpl.write(out, id);

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
  protected CDOID[] confirming(ExtendedDataInputStream in) throws IOException
  {
    int size = toIndex - fromIndex + 1;
    CDOID[] ids = new CDOID[size];
    for (int i = 0; i < ids.length; i++)
    {
      ids[i] = CDOIDImpl.read(in);
    }

    return ids;
  }
}
