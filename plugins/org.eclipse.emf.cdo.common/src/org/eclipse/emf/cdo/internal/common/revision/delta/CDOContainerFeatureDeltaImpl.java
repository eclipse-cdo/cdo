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
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.revision.delta;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassProxy;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public class CDOContainerFeatureDeltaImpl extends CDOFeatureDeltaImpl implements CDOContainerFeatureDelta
{
  /**
   * TODO Move to CDOObjectContainerFeature
   */
  private static CDOFeature CONTAINER_FEATURE = new ContainerFeature();

  private CDOID newContainerID;

  private int newContainerFeatureID;

  public CDOContainerFeatureDeltaImpl(CDOID newContainerID, int newContainerFeatureID)
  {
    super(CONTAINER_FEATURE);
    this.newContainerID = newContainerID;
    this.newContainerFeatureID = newContainerFeatureID;
  }

  public CDOContainerFeatureDeltaImpl(ExtendedDataInput in, CDOClass cdoClass) throws IOException
  {
    super(CONTAINER_FEATURE);
    newContainerFeatureID = in.readInt();
    newContainerID = CDOIDUtil.read(in, cdoClass.getPackageManager().getCDOIDObjectFactory());
  }

  public int getContainerFeatureID()
  {
    return newContainerFeatureID;
  }

  public CDOID getContainerID()
  {
    return newContainerID;
  }

  public Type getType()
  {
    return Type.CONTAINER;
  }

  public void apply(CDORevision revision)
  {
    ((InternalCDORevision)revision).setContainerID(newContainerID);
    ((InternalCDORevision)revision).setContainingFeatureID(newContainerFeatureID);
  }

  @Override
  public void adjustReferences(Map<CDOIDTemp, CDOID> idMappings)
  {
    newContainerID = (CDOID)CDORevisionUtil.remapID(newContainerID, idMappings);
  }

  @Override
  public void write(ExtendedDataOutput out, CDOClass cdoClass, CDOIDProvider idProvider) throws IOException
  {
    out.writeInt(getType().ordinal());
    out.writeInt(newContainerFeatureID);
    CDOIDUtil.write(out, newContainerID);
  }

  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  /**
   * @author Simon McDuff
   */
  private static final class ContainerFeature implements CDOFeature
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
  }
}
