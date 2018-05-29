/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity.utils;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;

import com.objy.as.app.Class_Object;
import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;

/**
 * Originally EOOUtil TBD: verify if we really need this class...
 *
 * @author ibrahim
 */
public class TypeConvert
{

  static public ooId toOoId(Object target)
  {
    if (target == null)
    {
      return null;
    }
    if (target instanceof CDOID)
    {
      if ((CDOID)target == CDOID.NULL)
      {
        return null;
      }

      return OBJYCDOIDUtil.getooId((CDOID)target);
    }
    if (target instanceof ooId)
    {
      return (ooId)target;
    }
    else if (target instanceof ObjyObject)
    {
      return ((ObjyObject)target).ooId();
    }
    else if (target instanceof ooObj)
    {
      return ((ooObj)target).getOid();
    }

    throw new IllegalArgumentException(target.toString());
  }

  static public ooObj toOoObj(Object target)
  {
    if (target instanceof ObjyObject)
    {
      target = ((ObjyObject)target).ooId();
    }
    if (target instanceof ooId)
    {
      return ooObj.create_ooObj((ooId)target);
    }
    throw new IllegalArgumentException(target.toString());
  }

  static public Class_Object toClassObject(Object target)
  {
    if (target == null)
    {
      return null;
    }
    if (target instanceof Class_Object)
    {
      return (Class_Object)target;
    }
    else if (target instanceof ooObj)
    {
      return new Class_Object(target);
    }
    else if (target instanceof ooId)
    {
      return Class_Object.class_object_from_oid((ooId)target);
    }

    throw new IllegalArgumentException("Not supported " + target);

  }

  static public ObjyObject toObjyObject(Object target)
  {
    if (target == null)
    {
      return null;
    }

    if (target instanceof ObjyObject)
    {
      return (ObjyObject)target;
    }
    else if (target instanceof Class_Object)
    {
      throw new IllegalArgumentException("Not supported " + target);
    }
    else if (target instanceof ooId)
    {
      throw new IllegalArgumentException("Not supported " + target);
    }
    return null;
  }

}
