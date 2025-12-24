/*
 * Copyright (c) 2009-2012, 2017, 2018, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 210868
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.collection.MoveableList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadChunkIndication extends CDOServerReadIndication
{
  private CDOID id;

  private CDOBranchVersion branchVersion;

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
    branchVersion = in.readCDOBranchVersion();

    EClass eClass = (EClass)in.readCDOClassifierRefAndResolve();
    int featureID = in.readXInt();
    feature = eClass.getEStructuralFeature(featureID);

    fromIndex = in.readXInt();
    toIndex = in.readXInt();
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalRepository repository = getRepository();
    InternalCDORevisionManager revisionManager = repository.getRevisionManager();

    InternalCDORevision revision = revisionManager.getRevisionByVersion(id, branchVersion, 0, true);
    repository.ensureChunk(revision, feature, fromIndex, toIndex + 1);

    CDOType type = CDOModelUtil.getType(feature);
    MoveableList<Object> list = revision.getListOrNull(feature);
    if (list != null)
    {
      for (int i = fromIndex; i <= toIndex; i++)
      {
        Object value = list.get(i);
        type.writeValue(out, value);
      }
    }
  }
}
