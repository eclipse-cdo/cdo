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
  public static Map<Class<?>, CDOType> idTypes = new HashMap<Class<?>, CDOType>();
  
  static
  {
    idTypes.put(String.class, CDOType.STRING);
    idTypes.put(Boolean.class, CDOType.BOOLEAN);
    idTypes.put(Date.class, CDOType.DATE);
    idTypes.put(Long.class, CDOType.LONG);
    idTypes.put(Integer.class, CDOType.INT);
    idTypes.put(Double.class, CDOType.DOUBLE);
    idTypes.put(Byte.class, CDOType.BYTE);
  }

  private CDOInstanceUtil()
  {

  }

  /**
   * @param out
   * @param id
   * @throws IOException
   */
  static public void writeObject(ExtendedDataOutput out, Object id) throws IOException
  {
    if (id instanceof CDOClass)
    {
      out.writeByte(0);
      CDOModelUtil.writeClassRef(out, ((CDOClass)id).createClassRef(), null);
      return;
    }
    
    out.writeByte(1);
    
    writeInstance(out, id);

  }
  /**
   * @param out
   * @param id
   * @throws IOException
   */
  static public void writeInstance(ExtendedDataOutput out, Object id) throws IOException
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
      type = CDOType.OBJECT;
    }
    else
    {
       type = idTypes.get(id.getClass());
    }
    
    if (type == null) throw new IllegalStateException("No type for object " + id.getClass());

    CDOModelUtil.writeType(out, type);
    type.writeValue(out, id);
  }
  /**
   * @param out
   * @param id
   * @throws IOException
   */
  static public Object readInstance(ExtendedDataInput in, CDOIDObjectFactory objectFactory) throws IOException
  {
      CDOType type = CDOModelUtil.readType(in);
      return type.readValue(in, objectFactory);
  }
  
  static public Object readObject(ExtendedDataInput in, CDOIDObjectFactory objectFactory, CDOPackageManager packageManager) throws IOException
  {
    byte value = in.readByte();
    
    if (value == 0)
    {
      return CDOModelUtil.readClassRef(in).resolve(packageManager);
    }
    else if (value == 1)
    {
      readInstance(in, objectFactory);
     }
    throw new IllegalStateException();
  }  
}
