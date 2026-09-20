/*
 * Copyright (c) 2009-2012, 2017, 2018, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config.LookupMode;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.collection.Pair;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class LoadChunkIndication extends CDOServerReadIndication
{
  private static final Config REVISION_LOADING_CONFIG = new Config(LookupMode.CACHE_THEN_LOADER, CDORevision.DEPTH_NONE, false, 0);

  private CDOID id;

  private CDOBranchVersion branchVersion;

  private EStructuralFeature feature;

  private List<Pair<Integer, Integer>> ranges;

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

    int rangeCount = in.readXInt();
    ranges = new ArrayList<>(rangeCount);

    for (int i = 0; i < rangeCount; i++)
    {
      ranges.add(Pair.create(in.readXInt(), in.readXInt() + 1));
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalRepository repository = getRepository();
    InternalCDORevisionManager revisionManager = repository.getRevisionManager();

    InternalCDORevision revision = revisionManager.getRevisionByVersion(id, branchVersion, REVISION_LOADING_CONFIG);
    repository.ensureChunks(revision, feature, ranges);

    CDOType type = CDOModelUtil.getType(feature);

    MoveableList<Object> list = revision.getListOrNull(feature);
    if (list != null)
    {
      for (Pair<Integer, Integer> range : ranges)
      {
        for (int i = range.getElement1(); i < range.getElement2(); i++)
        {
          type.writeValue(out, list.get(i));
        }
      }
    }
  }
}
