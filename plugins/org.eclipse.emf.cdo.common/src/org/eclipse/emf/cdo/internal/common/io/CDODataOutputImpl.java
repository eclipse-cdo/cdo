/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.io;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOID.Type;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.id.CDOIDAndVersionImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOID;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class CDODataOutputImpl extends ExtendedDataOutput.Delegating implements CDODataOutput
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CDODataOutputImpl.class);

  public CDODataOutputImpl(ExtendedDataOutput delegate)
  {
    super(delegate);
  }

  public void writeCDOPackageUnit(CDOPackageUnit packageUnit, boolean withPackages) throws IOException
  {
    ((InternalCDOPackageUnit)packageUnit).write(this, withPackages);
  }

  public void writeCDOPackageUnits(CDOPackageUnit... packageUnits) throws IOException
  {
    int size = packageUnits.length;
    writeInt(size);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} package units", size);
    }

    for (CDOPackageUnit packageUnit : packageUnits)
    {
      writeCDOPackageUnit(packageUnit, false);
    }
  }

  public void writeCDOPackageUnitType(CDOPackageUnit.Type type) throws IOException
  {
    writeByte(type.ordinal());
  }

  public void writeCDOPackageInfo(CDOPackageInfo packageInfo) throws IOException
  {
    ((InternalCDOPackageInfo)packageInfo).write(this);
  }

  public void writeCDOClassifierRef(CDOClassifierRef eClassifierRef) throws IOException
  {
    eClassifierRef.write(this);
  }

  public void writeCDOClassifierRef(EClassifier eClassifier) throws IOException
  {
    writeCDOClassifierRef(new CDOClassifierRef(eClassifier));
  }

  public void writeCDOPackageURI(String uri) throws IOException
  {
    getPackageURICompressor().write(this, uri);
  }

  public void writeCDOType(CDOType cdoType) throws IOException
  {
    ((CDOTypeImpl)cdoType).write(this);
  }

  public void writeCDOID(CDOID id) throws IOException
  {
    if (id == null)
    {
      id = CDOID.NULL;
    }

    Type type = id.getType();
    int ordinal = type.ordinal();
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing CDOID of type {0} ({1})", ordinal, type);
    }

    writeByte(ordinal);
    ((AbstractCDOID)id).write(this);
  }

  public void writeCDOIDAndVersion(CDOIDAndVersion idAndVersion) throws IOException
  {
    ((CDOIDAndVersionImpl)idAndVersion).write(this);
  }

  public void writeCDOIDMetaRange(CDOIDMetaRange metaRange) throws IOException
  {
    if (metaRange == null)
    {
      writeBoolean(false);
    }
    else
    {
      writeBoolean(true);
      writeCDOID(metaRange.getLowerBound());
      writeInt(metaRange.size());
    }
  }

  public void writeCDORevision(CDORevision revision, int referenceChunk) throws IOException
  {
    if (revision != null)
    {
      writeBoolean(true);
      ((InternalCDORevision)revision).write(this, referenceChunk);
    }
    else
    {
      writeBoolean(false);
    }
  }

  public void writeCDOList(CDOList list, EStructuralFeature feature, int referenceChunk) throws IOException
  {
    // TODO Simon: Could most of this stuff be moved into the list?
    // (only if protected methods of this class don't need to become public)
    int size = list == null ? 0 : list.size();
    if (size > 0)
    {
      // Need to adjust the referenceChunk in case where we do not have enough value in the list.
      // Even if the referenceChunk is specified, a provider of data could have override that value.
      int sizeToLook = referenceChunk == CDORevision.UNCHUNKED ? size : Math.min(referenceChunk, size);
      for (int i = 0; i < sizeToLook; i++)
      {
        Object element = list.get(i, false);
        if (element == CDORevisionUtil.UNINITIALIZED)
        {
          referenceChunk = i;
          break;
        }
      }
    }

    if (referenceChunk != CDORevision.UNCHUNKED && referenceChunk < size)
    {
      // This happens only on server-side
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing feature {0}: size={1}, referenceChunk={2}", feature.getName(), size, referenceChunk);
      }

      writeInt(-size);
      writeInt(referenceChunk);
      size = referenceChunk;
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing feature {0}: size={1}", feature.getName(), size);
      }

      writeInt(size);
    }

    for (int j = 0; j < size; j++)
    {
      Object value = list.get(j, false);
      if (value != null && feature instanceof EReference)
      {
        value = getIDProvider().provideCDOID(value);
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("    " + value);
      }

      writeCDOFeatureValue(value, feature);
    }
  }

  public void writeCDOFeatureValue(Object value, EStructuralFeature feature) throws IOException
  {
    // TODO We could certainly optimized this: When a feature is a reference, NIL is only possible in the case where
    // unsettable == true. (TO be verified)

    CDOType type = CDOModelUtil.getType(feature.getEType());
    if (type.canBeNull())
    {
      if (!feature.isMany())
      {
        if (value == InternalCDORevision.NIL)
        {
          writeBoolean(true);
          return;
        }
        else
        {
          writeBoolean(false);
        }
      }
    }
    else
    {
      if (value == null)
      {
        value = feature.getDefaultValue();
      }
    }

    type.writeValue(this, value);
  }

  public void writeCDORevisionDelta(CDORevisionDelta revisionDelta) throws IOException
  {
    ((CDORevisionDeltaImpl)revisionDelta).write(this);
  }

  public void writeCDOFeatureDelta(CDOFeatureDelta featureDelta, EClass eClass) throws IOException
  {
    ((CDOFeatureDeltaImpl)featureDelta).write(this, eClass);
  }

  public void writeCDORevisionOrPrimitive(Object value) throws IOException
  {
    if (value == null)
    {
      value = CDOID.NULL;
    }
    else if (value instanceof CDORevision)
    {
      value = ((CDORevision)value).getID();
    }

    CDOType type = null;
    if (value instanceof CDOID)
    {
      CDOID id = (CDOID)value;
      if (id.isTemporary())
      {
        throw new IllegalArgumentException("Temporary ID not supported: " + value);
      }

      type = CDOType.OBJECT;
    }
    else
    {
      type = CDOModelUtil.getPrimitiveType(value.getClass());
      if (type == null)
      {
        throw new IllegalArgumentException("No type for object of class " + value.getClass());
      }
    }

    writeCDOType(type);
    type.writeValue(this, value);
  }

  public void writeCDORevisionOrPrimitiveOrClassifier(Object value) throws IOException
  {
    if (value instanceof EClassifier)
    {
      writeBoolean(true);
      writeCDOClassifierRef((EClass)value);
    }
    else
    {
      writeBoolean(false);
      writeCDORevisionOrPrimitive(value);
    }
  }

  public void writeCDOLockType(RWLockManager.LockType lockType) throws IOException
  {
    writeBoolean(lockType == RWLockManager.LockType.WRITE ? true : false);
  }

  protected StringIO getPackageURICompressor()
  {
    return StringIO.DIRECT;
  }
}
