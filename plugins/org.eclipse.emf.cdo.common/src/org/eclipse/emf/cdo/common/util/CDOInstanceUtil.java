/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOInstanceUtil
{
  private static Map<Class<?>, CDOType> idTypes = new HashMap<Class<?>, CDOType>();

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

  private CDOInstanceUtil()
  {

  }

  /**
   * Write into a {@link ExtendedDataOutput} CDOClass, CDORevision or primitive.
   */
  static public void writeObjectOrClass(ExtendedDataOutput out, Object id) throws IOException
  {
    if (id instanceof CDOClass)
    {
      out.writeBoolean(true);
      CDOModelUtil.writeClassRef(out, ((CDOClass)id).createClassRef(), null);
      return;
    }

    out.writeBoolean(false);

    writeObject(out, id);

  }

  /**
   * Write into a {@link ExtendedDataOutput} CDORevision or primitive.
   */
  static public void writeObject(ExtendedDataOutput out, Object id) throws IOException
  {
    if (id == null)
    {
      id = CDOID.NULL;
    }
    else if (id instanceof CDORevision)
    {
      id = ((CDORevision)id).getID();
    }

    CDOType type = null;

    if (id instanceof CDOID)
    {
      if (((CDOID)id).isTemporary())
      {
        throw new IllegalArgumentException("Do not support temporary for " + id);
      }

      type = CDOType.OBJECT;
    }
    else
    {
      type = idTypes.get(id.getClass());

      if (type == null)
      {
        throw new IllegalStateException("No type for object " + id.getClass());
      }
    }

    CDOModelUtil.writeType(out, type);
    type.writeValue(out, id);
  }

  /**
   * Read from an {@link ExtendedDataInput} CDORevision or primitive.
   */
  static public Object readObject(ExtendedDataInput in, CDOIDObjectFactory idObjectFactory) throws IOException
  {
    CDOType type = CDOModelUtil.readType(in);
    return type.readValue(in, idObjectFactory);
  }

  /**
   * Read from an {@link ExtendedDataInput} CDOClass, CDORevision or primitive.
   */
  static public Object readObjectOrClass(ExtendedDataInput in, CDOIDObjectFactory idObjectFactory,
      CDOPackageManager packageManager) throws IOException
  {
    boolean isClass = in.readBoolean();
    if (isClass)
    {
      return CDOModelUtil.readClassRef(in).resolve(packageManager);
    }
    return readObject(in, idObjectFactory);
  }
}
