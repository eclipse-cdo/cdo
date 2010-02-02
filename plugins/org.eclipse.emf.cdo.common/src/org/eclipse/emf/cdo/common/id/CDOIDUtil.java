/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 226778
 *    Simon McDuff - bug 213402
 *    Martin Taal - Added subtype handling and EClass conversion, bug 283106
 */
package org.eclipse.emf.cdo.common.id;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID.Type;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.id.CDOIDAndBranchImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDAndVersionAndBranchImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDAndVersionImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDExternalImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDMetaImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDMetaRangeImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectLongImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectLongWithClassifierImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectStringImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectStringWithClassifierImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempMetaImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectExternalImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectImpl;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOID;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDLong;
import org.eclipse.emf.cdo.spi.common.id.InternalCDOIDObject;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public final class CDOIDUtil
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_MODEL, CDOIDUtil.class);

  private CDOIDUtil()
  {
  }

  /**
   * @since 2.0
   */
  public static boolean isNull(CDOID id)
  {
    return id == null || id.isNull();
  }

  public static long getLong(CDOID id)
  {
    if (id == null)
    {
      return AbstractCDOIDLong.NULL_VALUE;
    }

    switch (id.getType())
    {
    case NULL:
      return AbstractCDOIDLong.NULL_VALUE;

    case OBJECT:
      if (id instanceof AbstractCDOIDLong)
      {
        return ((AbstractCDOIDLong)id).getLongValue();
      }

      throw new IllegalArgumentException(MessageFormat.format(
          Messages.getString("CDOIDUtil.0"), id.getClass().getName())); //$NON-NLS-1$

    case META:
      return ((CDOIDMeta)id).getLongValue();

    case TEMP_META:
    case TEMP_OBJECT:
      throw new IllegalArgumentException(Messages.getString("CDOIDUtil.1")); //$NON-NLS-1$

    case EXTERNAL_OBJECT:
    case EXTERNAL_TEMP_OBJECT:
      throw new IllegalArgumentException(Messages.getString("CDOIDUtil.2")); //$NON-NLS-1$

    default:
      throw new IllegalArgumentException(MessageFormat.format(
          Messages.getString("CDOIDUtil.3"), id.getClass().getName())); //$NON-NLS-1$
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

  /**
   * @since 3.0
   */
  public static CDOIDExternal createTempObjectExternal(String uri)
  {
    return new CDOIDTempObjectExternalImpl(uri);
  }

  public static CDOID createLong(long value)
  {
    if (value == AbstractCDOIDLong.NULL_VALUE)
    {
      return CDOID.NULL;
    }

    return new CDOIDObjectLongImpl(value);
  }

  /**
   * @since 3.0
   */
  public static CDOID createStringWithClassifier(CDOClassifierRef classifierRef, String value)
  {
    return new CDOIDObjectStringWithClassifierImpl(classifierRef, value);
  }

  /**
   * @since 3.0
   */
  public static CDOID createLongWithClassifier(CDOClassifierRef classifierRef, long value)
  {
    return new CDOIDObjectLongWithClassifierImpl(classifierRef, value);
  }

  /**
   * @since 2.0
   */
  public static CDOIDExternal createExternal(String uri)
  {
    return new CDOIDExternalImpl(uri);
  }

  /**
   * Format of the URI fragment.
   * <p>
   * Non-legacy: <code>&lt;ID TYPE>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   * <p>
   * Legacy: <code>&lt;ID TYPE>/&lt;PACKAGE URI>/&lt;CLASSIFIER ID>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   * 
   * @since 3.0
   */
  public static CDOID read(String uriFragment)
  {
    // An OBJECT subtype has a negative value
    if (uriFragment.startsWith("-"))
    {
      return createCDOIDObject(uriFragment);
    }

    byte ordinal = Byte.valueOf(uriFragment.substring(0, 1));
    if (TRACER.isEnabled())
    {
      try
      {
        String type = Type.values()[ordinal].toString();
        TRACER.format("Reading CDOID of type {0} ({1})", ordinal, type); //$NON-NLS-1$
      }
      catch (RuntimeException ex)
      {
        TRACER.trace(ex);
      }
    }

    Type type = Type.values()[ordinal];
    String fragment = uriFragment.substring(2);
    switch (type)
    {
    case NULL:
      return CDOID.NULL;

    case TEMP_OBJECT:
      return new CDOIDTempObjectImpl(Integer.valueOf(fragment));

    case TEMP_META:
      return new CDOIDTempMetaImpl(Integer.valueOf(fragment));

    case META:
      return new CDOIDMetaImpl(Long.valueOf(fragment));

    case EXTERNAL_OBJECT:
      return new CDOIDExternalImpl(fragment);

    case EXTERNAL_TEMP_OBJECT:
      return new CDOIDTempObjectExternalImpl(fragment);

    case OBJECT:
    {
      // Normally this case should not occur (is an OBJECT subtype).
      throw new ImplementationError();
    }

    default:
      throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOIDUtil.5"), uriFragment)); //$NON-NLS-1$
    }
  }

  private static CDOID createCDOIDObject(String uriFragment)
  {
    byte negOrdinal = Byte.valueOf(uriFragment.substring(0, 2));
    int ordinal = -1 * negOrdinal - 1;
    if (TRACER.isEnabled())
    {
      try
      {
        String type = InternalCDOIDObject.SubType.values()[ordinal].toString();
        TRACER.format("Reading CDOID Object of subType {0} ({1})", ordinal, type); //$NON-NLS-1$
      }
      catch (RuntimeException ex)
      {
        TRACER.trace(ex);
      }
    }

    InternalCDOIDObject.SubType subType = InternalCDOIDObject.SubType.values()[ordinal];
    AbstractCDOID id = createCDOIDObject(subType);
    // note position 2 in the uriFragment is a /
    // see the write method
    String fragment = uriFragment.substring(3);
    id.read(fragment);
    return id;
  }

  /**
   * Creates the correct implementation class for the passed {@link InternalCDOIDObject.SubType}.
   * 
   * @param subType
   *          the subType for which to create an empty CDOID instance
   * @return the instance of CDOIDObject which represents the subtype.
   * @since 3.0
   */
  public static AbstractCDOID createCDOIDObject(InternalCDOIDObject.SubType subType)
  {
    if (subType == null)
    {
      throw new IllegalArgumentException("SubType may not be null");
    }

    InternalCDOIDObject id;
    switch (subType)
    {
    case LONG:
      id = new CDOIDObjectLongImpl();
      break;

    case STRING:
      id = new CDOIDObjectStringImpl();
      break;

    case LONG_WITH_CLASSIFIER:
      id = new CDOIDObjectLongWithClassifierImpl();
      break;

    case STRING_WITH_CLASSIFIER:
      id = new CDOIDObjectStringWithClassifierImpl();
      break;

    default:
      throw new IllegalArgumentException("Subtype " + subType.name() + " not supported");
    }

    if (id.getSubType() != subType)
    {
      throw new IllegalStateException("Subtype of created id " + id + " is unequal (" + id.getSubType().name()
          + ") to requested subtype " + subType.name());
    }

    return (AbstractCDOID)id;
  }

  /**
   * Format of the uri fragment.
   * <p>
   * Non-legacy: <code>&lt;ID TYPE>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   * <p>
   * Legacy: <code>&lt;ID TYPE>/&lt;PACKAGE URI>/&lt;CLASSIFIER ID>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   * 
   * @since 2.0
   */
  public static void write(StringBuilder builder, CDOID id)
  {
    if (id == null)
    {
      id = CDOID.NULL;
    }

    if (id instanceof InternalCDOIDObject)
    {
      InternalCDOIDObject internalCDOID = (InternalCDOIDObject)id;
      int subOrdinal = (internalCDOID.getSubType().ordinal() + 1) * -1;
      builder.append(subOrdinal);
    }
    else
    {
      Type type = id.getType();
      int ordinal = type.ordinal();
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing CDOID of type {0} ({1})", ordinal, type); //$NON-NLS-1$
      }

      builder.append(ordinal);
      switch (type)
      {
      case NULL:
      case TEMP_OBJECT:
      case TEMP_META:
      case META:
      case EXTERNAL_OBJECT:
      case OBJECT:
        break;

      default:
        throw new ImplementationError();
      }
    }

    builder.append("/" + id.toURIFragment()); //$NON-NLS-1$
  }

  public static CDOIDMeta createMeta(long value)
  {
    return new CDOIDMetaImpl(value);
  }

  public static CDOIDMetaRange createMetaRange(CDOID lowerBound, int count)
  {
    return new CDOIDMetaRangeImpl(lowerBound, count);
  }

  public static CDOIDAndVersion createIDAndVersion(CDOID id, int version)
  {
    return new CDOIDAndVersionImpl(id, version);
  }

  /**
   * @since 3.0
   */
  public static CDOIDAndBranch createIDAndBranch(CDOID id, CDOBranch branch)
  {
    return new CDOIDAndBranchImpl(id, branch);
  }

  /**
   * @since 3.0
   */
  public static CDOIDAndVersionAndBranch createIDAndVersionAndBranch(CDOID id, int version, int branchID)
  {
    return new CDOIDAndVersionAndBranchImpl(id, version, branchID);
  }

  /**
   * @since 2.0
   */
  public static boolean equals(CDOID id1, CDOID id2)
  {
    if (id1 == null)
    {
      id1 = CDOID.NULL;
    }

    if (id2 == null)
    {
      id2 = CDOID.NULL;
    }

    return ObjectUtil.equals(id1, id2);
  }
}
