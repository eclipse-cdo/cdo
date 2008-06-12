/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassProxy;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.InternalCDOClass;
import org.eclipse.emf.cdo.spi.common.InternalCDOFeature;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class CDOFeatureImpl extends CDOModelElementImpl implements InternalCDOFeature
{
  private static final int UNKNOWN_FEATURE_INDEX = Integer.MIN_VALUE;

  private static final ContextTracer MODEL = new ContextTracer(OM.DEBUG_MODEL, CDOFeatureImpl.class);

  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, CDOFeatureImpl.class);

  private CDOClass containingClass;

  private int featureID;

  private int featureIndex = UNKNOWN_FEATURE_INDEX;

  private CDOType type;

  private boolean many;

  private boolean containment;

  private CDOClassProxy referenceTypeProxy;

  public CDOFeatureImpl()
  {
  }

  public CDOFeatureImpl(CDOClass containingClass, int featureID, String name, CDOType type, boolean many)
  {
    super(name);
    if (type == CDOType.OBJECT)
    {
      throw new IllegalArgumentException("type == OBJECT");
    }

    this.containingClass = containingClass;
    this.featureID = featureID;
    this.type = type;
    this.many = many;
    if (MODEL.isEnabled())
    {
      MODEL.format("Created {0}", this);
    }
  }

  public CDOFeatureImpl(CDOClass containingClass, int featureID, String name, CDOClassProxy referenceTypeProxy,
      boolean many, boolean containment)
  {
    super(name);
    if (referenceTypeProxy == null)
    {
      throw new IllegalArgumentException("referenceTypeProxy == null");
    }

    this.containingClass = containingClass;
    this.featureID = featureID;
    type = CDOType.OBJECT;
    this.many = many;
    this.containment = containment;
    this.referenceTypeProxy = referenceTypeProxy;
    if (MODEL.isEnabled())
    {
      MODEL.format("Created {0}", this);
    }
  }

  public CDOFeatureImpl(CDOClass containingClass, ExtendedDataInput in) throws IOException
  {
    this.containingClass = containingClass;
    read(in);
  }

  @Override
  public void read(ExtendedDataInput in) throws IOException
  {
    super.read(in);
    featureID = in.readInt();
    type = CDOModelUtil.readType(in);
    many = in.readBoolean();
    containment = in.readBoolean();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read feature: ID={0}, name={1}, type={2}, many={3}, containment={4}", featureID, getName(),
          type, many, containment);
    }

    if (isReference())
    {
      String defaultURI = containingClass.getContainingPackage().getPackageURI();
      CDOClassRef classRef = CDOModelUtil.readClassRef(in, defaultURI);
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Read reference type: classRef={0}", classRef);
      }

      referenceTypeProxy = new CDOClassProxy(classRef, containingClass.getContainingPackage().getPackageManager());
    }
  }

  @Override
  public void write(ExtendedDataOutput out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing feature: ID={0}, name={1}, type={2}, many={3}, containment={4}", featureID, getName(),
          type, many, containment);
    }

    super.write(out);
    out.writeInt(featureID);
    CDOModelUtil.writeType(out, type);
    out.writeBoolean(many);
    out.writeBoolean(containment);

    if (isReference())
    {
      CDOClassRef classRef = referenceTypeProxy.getClassRef();
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Writing reference type: classRef={0}", classRef);
      }

      CDOModelUtil.writeClassRef(out, classRef, getContainingPackage().getPackageURI());
    }
  }

  public CDOPackageManager getPackageManager()
  {
    return getContainingPackage().getPackageManager();
  }

  public CDOPackage getContainingPackage()
  {
    return containingClass.getContainingPackage();
  }

  public CDOClass getContainingClass()
  {
    return containingClass;
  }

  public void setContainingClass(CDOClass containingClass)
  {
    this.containingClass = containingClass;
  }

  public int getFeatureID()
  {
    return featureID;
  }

  public void setFeatureID(int featureID)
  {
    this.featureID = featureID;
  }

  public int getFeatureIndex()
  {
    if (featureIndex == UNKNOWN_FEATURE_INDEX)
    {
      featureIndex = ((InternalCDOClass)containingClass).getFeatureIndex(featureID);
    }

    return featureIndex;
  }

  public void setFeatureIndex(int featureIndex)
  {
    this.featureIndex = featureIndex;
  }

  public CDOType getType()
  {
    return type;
  }

  public void setType(CDOType type)
  {
    this.type = type;
  }

  public boolean isMany()
  {
    return many;
  }

  public void setMany(boolean many)
  {
    this.many = many;
  }

  public boolean isReference()
  {
    return type == CDOType.OBJECT;
  }

  public boolean isContainment()
  {
    return containment;
  }

  public void setContainment(boolean containment)
  {
    this.containment = containment;
  }

  public CDOClass getReferenceType()
  {
    if (referenceTypeProxy == null)
    {
      return null;
    }

    return referenceTypeProxy.getCdoClass();
  }

  public void setReferenceType(CDOClassRef cdoClassRef)
  {
    referenceTypeProxy = new CDOClassProxy(cdoClassRef, getPackageManager());
  }

  public void setReferenceType(CDOClass cdoClass)
  {
    referenceTypeProxy = new CDOClassProxy(cdoClass);
  }

  public CDOClassProxy getReferenceTypeProxy()
  {
    return referenceTypeProxy;
  }

  @Override
  public String toString()
  {
    if (type == CDOType.OBJECT)
    {
      return MessageFormat.format("CDOFeature(ID={0}, name={1}, type={2})", featureID, getName(), referenceTypeProxy);
    }
    else
    {
      return MessageFormat.format("CDOFeature(ID={0}, name={1}, type={2})", featureID, getName(), type);
    }
  }

  @Override
  protected void onInitialize()
  {
  }
}
