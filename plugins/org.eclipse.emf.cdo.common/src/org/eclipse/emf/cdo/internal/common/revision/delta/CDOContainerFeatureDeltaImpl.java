/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;

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
    newContainerFeatureID = in.readInt();
    newContainerID = in.readCDOID();
    newResourceID = in.readCDOID();
  }

  public Type getType()
  {
    return Type.CONTAINER;
  }

  public CDOFeatureDelta copy()
  {
    return new CDOContainerFeatureDeltaImpl(newResourceID, newContainerID, newContainerFeatureID);
  }

  public CDOID getResourceID()
  {
    return newResourceID;
  }

  public Object getContainerID()
  {
    return newContainerID;
  }

  public int getContainerFeatureID()
  {
    return newContainerFeatureID;
  }

  public void apply(CDORevision revision)
  {
    ((InternalCDORevision)revision).setResourceID(newResourceID);
    ((InternalCDORevision)revision).setContainerID(newContainerID);
    ((InternalCDORevision)revision).setContainingFeatureID(newContainerFeatureID);
  }

  @Override
  public void adjustReferences(CDOReferenceAdjuster referenceAdjuster)
  {
    newResourceID = (CDOID)referenceAdjuster.adjustReference(newResourceID);
    newContainerID = referenceAdjuster.adjustReference(newContainerID);
  }

  @Override
  public void write(CDODataOutput out, EClass eClass) throws IOException
  {
    out.writeInt(getType().ordinal());
    out.writeInt(newContainerFeatureID);
    out.writeCDOID(out.getIDProvider().provideCDOID(newContainerID));
    out.writeCDOID(newResourceID);
  }

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
    return ObjectUtil.equals(newResourceID, that.getResourceID())
        && ObjectUtil.equals(newContainerID, that.getContainerID())
        && newContainerFeatureID == that.getContainerFeatureID();
  }

  @Override
  protected String toStringAdditional()
  {
    return MessageFormat.format("resource={0}, container={1}, feature={2}", newResourceID, newContainerID,
        newContainerFeatureID);
  }

  /**
   * @author Simon McDuff
   */
  public static final class ContainerFeature extends EStructuralFeatureImpl
  {
    public ContainerFeature()
    {
    }

    @Override
    public String toString()
    {
      return "CONTAINER_FEATURE"; //$NON-NLS-1$
    }
  }
}
