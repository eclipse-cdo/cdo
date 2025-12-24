/*
 * Copyright (c) 2008-2012, 2014, 2016, 2017, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Simon McDuff - bug 204890
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.internal.common.revision.delta;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EReferenceImpl;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Simon McDuff
 */
public class CDOContainerFeatureDeltaImpl extends CDOFeatureDeltaImpl implements CDOContainerFeatureDelta
{
  private CDOID newResourceID;

  private Object newContainerID;

  private int newContainerFeatureID;

  public CDOContainerFeatureDeltaImpl(CDOID newResourceID, Object newContainerID, int newContainerFeatureID)
  {
    super(CONTAINER_FEATURE);
    this.newResourceID = newResourceID;
    this.newContainerID = newContainerID;
    this.newContainerFeatureID = newContainerFeatureID;
  }

  public CDOContainerFeatureDeltaImpl(CDODataInput in, EClass eClass) throws IOException
  {
    super(CONTAINER_FEATURE);
    newContainerFeatureID = in.readXInt();
    newContainerID = in.readCDOID();
    newResourceID = in.readCDOID();
  }

  @Override
  public Type getType()
  {
    return Type.CONTAINER;
  }

  @Override
  public CDOFeatureDelta copy()
  {
    return new CDOContainerFeatureDeltaImpl(newResourceID, newContainerID, newContainerFeatureID);
  }

  @Override
  public CDOID getResourceID()
  {
    return newResourceID;
  }

  @Override
  public Object getContainerID()
  {
    return newContainerID;
  }

  @Override
  public int getContainerFeatureID()
  {
    return newContainerFeatureID;
  }

  @Override
  public Object applyTo(CDORevision revision)
  {
    InternalCDORevision internalRevision = (InternalCDORevision)revision;
    internalRevision.setResourceID(newResourceID);
    internalRevision.setContainerID(newContainerID);
    internalRevision.setContainerFeatureID(newContainerFeatureID);
    return null;
  }

  @Override
  public boolean adjustReferences(CDOReferenceAdjuster referenceAdjuster)
  {
    boolean changed = false;

    CDOID id1 = (CDOID)referenceAdjuster.adjustReference(newResourceID, CONTAINER_FEATURE, NO_INDEX);
    if (id1 != newResourceID)
    {
      newResourceID = id1;
      changed = true;
    }

    Object id2 = referenceAdjuster.adjustReference(newContainerID, CONTAINER_FEATURE, NO_INDEX);
    if (id2 != newContainerID)
    {
      newContainerID = id2;
      changed = true;
    }

    return changed;
  }

  @Override
  public void write(CDODataOutput out, EClass eClass) throws IOException
  {
    out.writeXInt(getType().ordinal());
    out.writeXInt(newContainerFeatureID);
    out.writeCDOID(out.getIDProvider().provideCDOID(newContainerID));
    out.writeCDOID(newResourceID);
  }

  @Override
  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  @Override
  public boolean isStructurallyEqual(Object obj)
  {
    if (!super.isStructurallyEqual(obj))
    {
      return false;
    }

    CDOContainerFeatureDelta that = (CDOContainerFeatureDelta)obj;
    return ObjectUtil.equals(newResourceID, that.getResourceID()) && ObjectUtil.equals(newContainerID, that.getContainerID())
        && newContainerFeatureID == that.getContainerFeatureID();
  }

  @Override
  protected String toStringAdditional()
  {
    return MessageFormat.format("resource={0}, container={1}, feature={2}", newResourceID, newContainerID, newContainerFeatureID);
  }

  /**
   * @author Simon McDuff
   */
  public static final class ContainerFeature extends EReferenceImpl
  {
    public static final String NAME = "eContainer";

    public ContainerFeature()
    {
    }

    @Override
    public String getName()
    {
      return null;
    }

    @Override
    public String toString()
    {
      return NAME;
    }
  }
}
