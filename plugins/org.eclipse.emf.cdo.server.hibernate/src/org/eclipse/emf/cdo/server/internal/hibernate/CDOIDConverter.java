/**
 * Copyright (c) 2009 Martin Taal and others. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDMeta;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.server.hibernate.id.CDOIDHibernate;
import org.eclipse.emf.cdo.server.hibernate.internal.id.CDOIDHibernateFactoryImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.WrappedException;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * The CDOIDConverter is responsible for converting CDOID's to and from Strings. In addition it helps to translate
 * containing feature Id's to and from String representations.
 * <p/>
 * The String representations are used in any-type columns, resource.contents and to store references to resources and
 * to containers.
 * 
 * @see CDOID
 * @see CDOIDMeta
 * @see CDOIDExternal
 * @see CDOIDHibernate
 * @see InternalCDORevision#getContainerID()
 * @see InternalCDORevision#getContainingFeatureID()
 * @see InternalCDORevision#getResourceID()
 * @author Martin Taal
 */
public class CDOIDConverter
{
  private static final String EXT_CODE = "EXT:";

  private static final String META_CODE = "META:";

  private static String SEPARATOR = "_:_";

  private final HashMap<String, Constructor<?>> constructors = new HashMap<String, Constructor<?>>();

  private static CDOIDConverter instance = new CDOIDConverter();

  /**
   * @return the instance
   */
  public static CDOIDConverter getInstance()
  {
    return instance;
  }

  /**
   * @param instance
   *          the instance to set
   */
  public static void setInstance(CDOIDConverter instance)
  {
    CDOIDConverter.instance = instance;
  }

  /**
   * Converts a CDOID to an unique String representations. Null, {@link CDOIDTemp} and {@link CDOIDNull} are returned as
   * null value. Supports {@link CDOIDHibernate}, {@link CDOIDMeta} and {@link CDOIDExternal}.
   * 
   * @param cdoID
   *          the cdoID to convert
   * @return a unique String
   */
  public String convertCDOIDToString(CDOID cdoID)
  {
    if (cdoID == null || cdoID.isNull() || cdoID.isTemporary())
    {
      return null;
    }

    if (cdoID instanceof CDOIDMeta)
    {
      return META_CODE + ((CDOIDMeta)cdoID).getLongValue();
    }
    if (cdoID instanceof CDOIDExternal)
    {
      return EXT_CODE + ((CDOIDExternal)cdoID).getURI();
    }

    if (cdoID instanceof CDOIDHibernate)
    {
      final CDOIDHibernate cdoIDHibernate = (CDOIDHibernate)cdoID;
      return cdoIDHibernate.getEntityName() + SEPARATOR + cdoIDHibernate.getId() + SEPARATOR
          + cdoIDHibernate.getId().getClass().getName();
    }

    throw new IllegalArgumentException("CDOID type " + cdoID.getClass().getName() + " is not supported, cdoID: "
        + cdoID);
  }

  /**
   * Converts a String back to its CDOID representation. The same types as in the {@link #convertCDOIDToString(CDOID)}
   * method are supported.
   * 
   * @param strID
   *          the String representation of the CDOID
   * @return a valid CDOID, can be null
   */
  public CDOID convertStringToCDOID(String strID)
  {
    if (strID == null)
    {
      return null;
    }
    if (strID.startsWith(META_CODE))
    {
      final String idPart = strID.substring(META_CODE.length());
      return CDOIDUtil.createMeta(Long.parseLong(idPart));
    }
    if (strID.startsWith(EXT_CODE))
    {
      final String idPart = strID.substring(EXT_CODE.length());
      return CDOIDUtil.createExternal(idPart);
    }
    final String[] idParts = strID.split(SEPARATOR);
    if (idParts.length != 3)
    {
      throw new IllegalArgumentException("CDOID as string " + strID
          + " can not be converted to a valid CDOIDHibernate.");
    }
    final String entityName = idParts[0];
    final Serializable id = getId(idParts[1], idParts[2]);
    return CDOIDHibernateFactoryImpl.getInstance().createCDOID(id, entityName);
  }

  /** Creates an id object of the correct type */
  private Serializable getId(String idStr, String idType)
  {
    try
    {
      Constructor<?> constructor = constructors.get(idType);
      if (constructor == null)
      {
        Class<?> idClass = this.getClass().getClassLoader().loadClass(idType);
        constructor = idClass.getConstructor(new Class[] { String.class });
        constructors.put(idType, constructor);
      }

      return (Serializable)constructor.newInstance(new Object[] { idStr });
    }
    catch (Exception e)
    {
      throw WrappedException.wrap(e);
    }
  }

}
