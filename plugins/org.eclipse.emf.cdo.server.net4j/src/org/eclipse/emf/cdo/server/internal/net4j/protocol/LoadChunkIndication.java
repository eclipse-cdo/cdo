/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/210868
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRevisionManager;

import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadChunkIndication extends CDOReadIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, LoadChunkIndication.class);

  private CDOID id;

  private int version;

  private EStructuralFeature feature;

  private int fromIndex;

  private int toIndex;

  public LoadChunkIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_CHUNK);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    id = in.readCDOID();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read revision ID: {0}", id); //$NON-NLS-1$
    }

    version = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read revision version: {0}", version); //$NON-NLS-1$
    }

    EClass eClass = (EClass)in.readCDOClassifierRefAndResolve();
    int featureID = in.readInt();
    feature = eClass.getEStructuralFeature(featureID);
    if (TRACER.isEnabled())
    {
      TRACER.format("Read feature: {0}", feature); //$NON-NLS-1$
    }

    fromIndex = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read fromIndex: {0}", fromIndex); //$NON-NLS-1$
    }

    toIndex = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read toIndex: {0}", toIndex); //$NON-NLS-1$
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalRevisionManager revisionManager = getRepository().getRevisionManager();
    InternalCDORevision revision = (InternalCDORevision)revisionManager.getRevisionByVersion(id, 0, version);
    revisionManager.ensureChunk(revision, feature, fromIndex, toIndex + 1);

    CDOType type = CDOModelUtil.getType(feature);
    MoveableList<Object> list = revision.getList(feature);
    for (int i = fromIndex; i <= toIndex; i++)
    {
      type.writeValue(out, list.get(i));
    }
  }
}
