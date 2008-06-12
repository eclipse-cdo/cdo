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
package org.eclipse.emf.cdo.common.id;

import org.eclipse.emf.cdo.common.id.CDOID.Type;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.id.CDOIDAndVersionImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDMetaImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDMetaRangeImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempMetaImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectImpl;
import org.eclipse.emf.cdo.spi.common.AbstractCDOID;
import org.eclipse.emf.cdo.spi.common.CDOIDLibraryDescriptorImpl;
import org.eclipse.emf.cdo.spi.common.CDOIDLongImpl;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public final class CDOIDUtil
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_MODEL, CDOIDUtil.class);

  private CDOIDUtil()
  {
  }

  public static long getLong(CDOID id)
  {
    if (id == null)
    {
      return 0L;
    }

    switch (id.getType())
    {
    case NULL:
      return 0L;
    case OBJECT:
    case LEGACY_OBJECT:
      if (id instanceof CDOIDLongImpl)
      {
        return ((CDOIDLongImpl)id).getLongValue();
      }

      throw new IllegalArgumentException("Unknown CDOIDObject implementation: " + id.getClass().getName());

    case META:
      return ((CDOIDMeta)id).getLongValue();
    case TEMP_META:
    case TEMP_OBJECT:
      throw new IllegalArgumentException("id instanceof CDOIDTemp");
    default:
      throw new ImplementationError();
    }
  }

  public static CDOIDTemp createTempMeta(int value)
  {
    return new CDOIDTempMetaImpl(value);
  }

  public static CDOIDTemp createTempObject(int value)
  {
    return new CDOIDTempObjectImpl(value);
  }

  public static CDOID createLong(long value)
  {
    if (value == 0L)
    {
      return CDOID.NULL;
    }

    return new CDOIDLongImpl(value);
  }

  public static CDOID read(ExtendedDataInput in, CDOIDObjectFactory factory) throws IOException
  {
    return read(in, factory, false);
  }

  public static CDOID read(ExtendedDataInput in, CDOIDObjectFactory factory, boolean asLegacy) throws IOException
  {
    byte ordinal = in.readByte();
    if (TRACER.isEnabled())
    {
      String type;
      try
      {
        type = Type.values()[ordinal].toString();
      }
      catch (RuntimeException ex)
      {
        type = ex.getMessage();
      }

      TRACER.format("Reading CDOID of type {0} ({1})", ordinal, type);
    }

    Type type = Type.values()[ordinal];
    if (asLegacy)
    {
      switch (type)
      {
      case NULL:
      case TEMP_OBJECT:
      case TEMP_META:
      case META:
      case OBJECT:
        throw new IllegalStateException("Missing classRef");

      case LEGACY_OBJECT:
      {
        CDOIDObject id = factory.createCDOIDObject(in);
        ((AbstractCDOID)id).read(in);
        CDOClassRef classRef = CDOModelUtil.readClassRef(in);
        return id.asLegacy(classRef);
      }

      default:
        throw new ImplementationError();
      }
    }

    // Not asLegacy
    switch (type)
    {
    case NULL:
      return CDOID.NULL;

    case TEMP_OBJECT:
      return new CDOIDTempObjectImpl(in.readInt());

    case TEMP_META:
      return new CDOIDTempMetaImpl(in.readInt());

    case META:
      return new CDOIDMetaImpl(in.readLong());

    case OBJECT:
    {
      CDOIDObject id = factory.createCDOIDObject(in);
      ((AbstractCDOID)id).read(in);
      return id;
    }

    case LEGACY_OBJECT:
    {
      CDOIDObject id = factory.createCDOIDObject(in);
      ((AbstractCDOID)id).read(in);
      CDOModelUtil.readClassRef(in); // Discard classRef from stream
      return id;
    }

    default:
      throw new ImplementationError();
    }
  }

  public static void write(ExtendedDataOutput out, CDOID id) throws IOException
  {
    write(out, id, false);
  }

  public static void write(ExtendedDataOutput out, CDOID id, boolean asLegacy) throws IOException
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

    out.writeByte(ordinal);
    if (asLegacy)
    {
      switch (type)
      {
      case NULL:
      case TEMP_OBJECT:
      case TEMP_META:
      case META:
      case OBJECT:
        throw new IllegalStateException("Missing classRef");

      case LEGACY_OBJECT:
        CDOIDObject legacy = (CDOIDObject)id;
        ((AbstractCDOID)legacy).write(out);
        CDOModelUtil.writeClassRef(out, legacy.getClassRef());
        return;

      default:
        throw new ImplementationError();
      }
    }

    // Not asLegacy
    ((AbstractCDOID)id).write(out);
  }

  public static CDOIDMeta createMeta(long value)
  {
    return new CDOIDMetaImpl(value);
  }

  public static CDOIDMetaRange createMetaRange(CDOID lowerBound, int count)
  {
    return new CDOIDMetaRangeImpl(lowerBound, count);
  }

  public static CDOIDMetaRange readMetaRange(ExtendedDataInput in) throws IOException
  {
    boolean exist = in.readBoolean();
    if (exist)
    {
      return new CDOIDMetaRangeImpl(read(in, null), in.readInt());
    }

    return null;
  }

  public static void writeMetaRange(ExtendedDataOutput out, CDOIDMetaRange idRange) throws IOException
  {
    if (idRange == null)
    {
      out.writeBoolean(false);
    }
    else
    {
      out.writeBoolean(true);
      write(out, idRange.getLowerBound());
      out.writeInt(idRange.size());
    }
  }

  public static CDOIDLibraryDescriptor readLibraryDescriptor(ExtendedDataInput in) throws IOException
  {
    return new CDOIDLibraryDescriptorImpl(in);
  }

  public static CDOIDAndVersion createIDAndVersion(CDOID id, int version)
  {
    return new CDOIDAndVersionImpl(id, version);
  }

  public static CDOIDAndVersion readIDAndVersion(ExtendedDataInput in, CDOIDObjectFactory factory) throws IOException
  {
    return readIDAndVersion(in, factory, false);
  }

  public static CDOIDAndVersion readIDAndVersion(ExtendedDataInput in, CDOIDObjectFactory factory, boolean asLegacy)
      throws IOException
  {
    return new CDOIDAndVersionImpl(in, factory, asLegacy);
  }
}
