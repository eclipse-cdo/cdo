/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Simon McDuff - http://bugs.eclipse.org/204890 
 *    Simon McDuff - http://bugs.eclipse.org/213402
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.revision.delta;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassProxy;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import java.io.IOException;

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

  public CDOContainerFeatureDeltaImpl(CDODataInput in, CDOClass cdoClass) throws IOException
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
  public void write(CDODataOutput out, CDOClass cdoClass) throws IOException
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

  /**
   * @author Simon McDuff
   */
  public static final class ContainerFeature implements CDOFeature
  {
    public CDOClass getContainingClass()
    {
      return null;
    }

    public void setContainingClass(CDOClass cdoClass)
    {
    }

    public CDOPackage getContainingPackage()
    {
      return null;
    }

    public int getFeatureID()
    {
      return 0;
    }

    public int getFeatureIndex()
    {
      return 0;
    }

    public CDOClass getReferenceType()
    {
      return null;
    }

    public CDOClassProxy getReferenceTypeProxy()
    {
      return null;
    }

    public CDOType getType()
    {
      return null;
    }

    public boolean isContainment()
    {
      return false;
    }

    public boolean isMany()
    {
      return false;
    }

    public boolean isReference()
    {
      return false;
    }

    public Object getClientInfo()
    {
      return null;
    }

    public String getName()
    {
      return null;
    }

    public String getQualifiedName()
    {
      return null;
    }

    public CDOPackageManager getPackageManager()
    {
      return null;
    }

    public Object getServerInfo()
    {
      return null;
    }

    public void setClientInfo(Object clientInfo)
    {
    }

    public void setServerInfo(Object serverInfo)
    {
    }

    @Override
    public String toString()
    {
      return "CONTAINER_FEATURE";
    }

    public Object getDefaultValue()
    {
      return null;
    }
  }
}
