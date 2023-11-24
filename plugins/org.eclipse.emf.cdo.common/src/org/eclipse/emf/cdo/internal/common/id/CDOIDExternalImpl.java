/*
 * Copyright (c) 2008-2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.id;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDString;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOID;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.ref.Interner;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public final class CDOIDExternalImpl extends AbstractCDOID implements CDOIDExternal, CDOIDString
{
  private static final long serialVersionUID = 1L;

  private static final boolean checkFragment = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.common.id.CDOIDExternal.checkFragment");

  private static final StringInterner INTERNER = new StringInterner();

  private final String uri;

  private CDOIDExternalImpl(String uri)
  {
    checkURI(uri);
    this.uri = uri;
  }

  /**
   * A private default constructor for technologies (such as Hessian) that initialize the instance fields via reflection.
   * Note that {@link #readResolve()} must be called in any case to ensure value uniqueness.
   */
  private CDOIDExternalImpl()
  {
    uri = null;
  }

  @Override
  public void write(CDODataOutput out) throws IOException
  {
    out.writeString(uri);
  }

  @Override
  public String toURIFragment()
  {
    return uri;
  }

  @Override
  public String getURI()
  {
    return uri;
  }

  @Override
  public String getStringValue()
  {
    return uri;
  }

  @Override
  public Type getType()
  {
    return Type.EXTERNAL_OBJECT;
  }

  @Override
  public boolean isExternal()
  {
    return true;
  }

  @Override
  public boolean isObject()
  {
    return true;
  }

  @Override
  public boolean isTemporary()
  {
    return false;
  }

  @Override
  public int hashCode()
  {
    return uri.hashCode();
  }

  @Override
  public String toString()
  {
    return "oid:" + uri; //$NON-NLS-1$
  }

  @Override
  protected int doCompareTo(CDOID o) throws ClassCastException
  {
    return uri.compareTo(((CDOIDExternalImpl)o).uri);
  }

  private Object readResolve() throws ObjectStreamException
  {
    return create(uri);
  }

  private static int getHashCode(String uri)
  {
    return uri.hashCode();
  }

  static void checkURI(String uri)
  {
    CheckUtil.checkArg(uri, "Null not allowed"); //$NON-NLS-1$

    if (checkFragment)
    {
      CheckUtil.checkArg(!uri.endsWith("#NNULL"), "Fragment 'NNULL' not allowed"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static CDOIDExternalImpl create(String uri)
  {
    return INTERNER.intern(uri);
  }

  public static CDOIDExternalImpl create(CDODataInput in) throws IOException
  {
    String uri = in.readString();
    return create(uri);
  }

  /**
   * @author Eike Stepper
   */
  private static final class StringInterner extends Interner<CDOIDExternalImpl>
  {
    public synchronized CDOIDExternalImpl intern(String uri)
    {
      int hashCode = getHashCode(uri);
      for (Entry<CDOIDExternalImpl> entry = getEntry(hashCode); entry != null; entry = entry.getNextEntry())
      {
        CDOIDExternalImpl id = entry.get();
        if (id != null && id.uri.equals(uri))
        {
          return id;
        }
      }

      CDOIDExternalImpl id = new CDOIDExternalImpl(uri);
      addEntry(createEntry(id, hashCode));
      return id;
    }

    @Override
    protected int hashCode(CDOIDExternalImpl id)
    {
      return getHashCode(id.uri);
    }
  }
}
