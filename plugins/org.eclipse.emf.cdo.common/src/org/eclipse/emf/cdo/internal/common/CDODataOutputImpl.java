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
package org.eclipse.emf.cdo.internal.common;

import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOID.Type;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageURICompressor;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.id.CDOIDAndVersionImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.AbstractCDOID;
import org.eclipse.emf.cdo.spi.common.InternalCDOClass;
import org.eclipse.emf.cdo.spi.common.InternalCDOFeature;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;

import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class CDODataOutputImpl implements CDODataOutput
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CDODataOutputImpl.class);

  private static final Map<Class<?>, CDOType> idTypes = new HashMap<Class<?>, CDOType>();

  private ExtendedDataOutput out;

  static
  {
    idTypes.put(String.class, CDOType.STRING);
    idTypes.put(Boolean.class, CDOType.BOOLEAN);
    idTypes.put(Date.class, CDOType.DATE);
    idTypes.put(Long.class, CDOType.LONG);
    idTypes.put(Integer.class, CDOType.INT);
    idTypes.put(Double.class, CDOType.DOUBLE);
    idTypes.put(Byte.class, CDOType.BYTE);
    idTypes.put(Character.class, CDOType.CHAR);
    idTypes.put(Float.class, CDOType.FLOAT);
  }

  public CDODataOutputImpl(ExtendedDataOutput out)
  {
    this.out = out;
  }

  public ExtendedDataOutput getDelegate()
  {
    return out;
  }

  public void write(byte[] b, int off, int len) throws IOException
  {
    out.write(b, off, len);
  }

  public void write(byte[] b) throws IOException
  {
    out.write(b);
  }

  public void write(int b) throws IOException
  {
    out.write(b);
  }

  public void writeBoolean(boolean v) throws IOException
  {
    out.writeBoolean(v);
  }

  public void writeByte(int v) throws IOException
  {
    out.writeByte(v);
  }

  public void writeByteArray(byte[] b) throws IOException
  {
    out.writeByteArray(b);
  }

  public void writeBytes(String s) throws IOException
  {
    out.writeBytes(s);
  }

  public void writeChar(int v) throws IOException
  {
    out.writeChar(v);
  }

  public void writeChars(String s) throws IOException
  {
    out.writeChars(s);
  }

  public void writeDouble(double v) throws IOException
  {
    out.writeDouble(v);
  }

  public void writeFloat(float v) throws IOException
  {
    out.writeFloat(v);
  }

  public void writeInt(int v) throws IOException
  {
    out.writeInt(v);
  }

  public void writeLong(long v) throws IOException
  {
    out.writeLong(v);
  }

  public void writeObject(Object object) throws IOException
  {
    out.writeObject(object);
  }

  public void writeShort(int v) throws IOException
  {
    out.writeShort(v);
  }

  public void writeString(String str) throws IOException
  {
    out.writeString(str);
  }

  public void writeUTF(String str) throws IOException
  {
    out.writeUTF(str);
  }

  public void writeCDOType(CDOType cdoType) throws IOException
  {
    ((CDOTypeImpl)cdoType).write(this);
  }

  public void writeCDOPackageURI(String uri) throws IOException
  {
    getPackageURICompressor().writePackageURI(this, uri);
  }

  public void writeCDOClassRef(CDOClassRef cdoClassRef) throws IOException
  {
    ((CDOClassRefImpl)cdoClassRef).write(this);
  }

  public void writeCDOClassRef(CDOClass cdoClass) throws IOException
  {
    writeCDOClassRef(cdoClass.createClassRef());
  }

  public void writeCDOPackage(CDOPackage cdoPackage) throws IOException
  {
    ((InternalCDOPackage)cdoPackage).write(this);
  }

  public void writeCDOClass(CDOClass cdoClass) throws IOException
  {
    ((InternalCDOClass)cdoClass).write(this);
  }

  public void writeCDOFeature(CDOFeature cdoFeature) throws IOException
  {
    ((InternalCDOFeature)cdoFeature).write(this);
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
    ((CDORevisionImpl)revision).write(this, referenceChunk);
  }

  public void writeCDORevisionDelta(CDORevisionDelta revisionDelta) throws IOException
  {
    ((CDORevisionDeltaImpl)revisionDelta).write(this);
  }

  public void writeCDOFeatureDelta(CDOFeatureDelta featureDelta, CDOClass cdoClass) throws IOException
  {
    ((CDOFeatureDeltaImpl)featureDelta).write(this, cdoClass);
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
      if (((CDOID)value).isTemporary())
      {
        throw new IllegalArgumentException("Temporary ID not supported: " + value);
      }

      type = CDOType.OBJECT;
    }
    else
    {
      type = idTypes.get(value.getClass());
      if (type == null)
      {
        throw new IllegalArgumentException("No type for object " + value.getClass());
      }
    }

    writeCDOType(type);
    type.writeValue(this, value);
  }

  public void writeCDORevisionOrPrimitiveOrClass(Object value) throws IOException
  {
    if (value instanceof CDOClass)
    {
      writeBoolean(true);
      writeCDOClassRef(((CDOClass)value).createClassRef());
    }
    else
    {
      writeBoolean(false);
      writeCDORevisionOrPrimitive(value);
    }
  }

  protected abstract CDOPackageURICompressor getPackageURICompressor();
}
