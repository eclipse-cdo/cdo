/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.db;

import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import com.objy.as.app.Class_Object;
import com.objy.db.ObjyRuntimeException;
import com.objy.db.app.Session;
import com.objy.db.app.ooContObj;
import com.objy.db.app.ooDBObj;
import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;

/**
 * @author Ibrahim Sallam
 */
public class ObjyScope
{
  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjyScope.class);

  private ooContObj contObj = null;

  private ooDBObj dbObj = null;

  private String dbName;

  private String contName;

  /**
   * Static function used for initialisation of the store.
   */
  public static void insureScopeExist(ObjySession objySession, String dbName, String contName)
  {
    ooDBObj db;
    ooContObj cont;
    try
    {
      if (!objySession.getFD().hasDB(dbName))
      {
        db = Session.getCurrent().getFD().newDB(dbName);
      }
      else
      {
        db = Session.getCurrent().getFD().lookupDB(dbName);
      }

      if (db.hasContainer(contName))
      {
        cont = db.lookupContainer(contName);
      }
      else
      {
        cont = new ooContObj();
        db.addContainer(cont, 0, contName, 0, 0);
      }

    }
    catch (ObjyRuntimeException ex)
    {
      ex.printStackTrace();
    }
  }

  public ObjyScope(String dbName, String contName)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("ObjyScope - DB: " + dbName + " - CT: " + contName);
    }
    this.dbName = dbName;
    this.contName = contName;
    init();
  }

  // optimized version that takes an ooDBObj.
  public ObjyScope(ooDBObj dbObj, String contName)
  {
    dbName = dbObj.getName();
    this.contName = contName;
    this.dbObj = dbObj;

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("ObjyScope - DB: " + dbObj.getName() + " - CT: " + contName);
    }
    init();
  }

  public ooId getScopeContOid()
  {
    return getContainerObj().getOid();
  }

  public ooId getScopeDbOid()
  {
    return getDatabaseObj().getOid();
  }

  private void init()
  {
    getContainerObj();
  }

  // /**
  // * TODO - verify need.
  // */
  // private ooContObj getContainer(ooId id)
  // {
  // String contID = "#" + id.getDB() + "-" + id.getOC() + "-1-1";
  //
  // return (ooContObj)Session.getCurrent().getFD().objectFrom(contID);
  // }

  public ooContObj getContainerObj()
  {

    if (contObj == null)
    {
      ooDBObj db = getDatabaseObj();

      if (db.hasContainer(contName))
      {

        contObj = db.lookupContainer(contName);

      }
      else
      {
        contObj = createNewContainer(contName);
      }
    }
    return contObj;
  }

  /**
   * Need this when scanning a scope.
   */
  public ooDBObj getDatabaseObj()
  {
    if (dbObj == null)
    {
      if (!Session.getCurrent().getFD().hasDB(dbName))
      {
        dbObj = Session.getCurrent().getFD().newDB(dbName);
        // System.out.println("OBJY: Creating new DB ID: " + dbObj.getOid().getStoreString() +
        // " - name:" + dbObj.getName());
      }
      else
      {
        dbObj = Session.getCurrent().getFD().lookupDB(dbName);
      }
      // System.out.println("OBJY: Working with DB ID: " + dbObj.getOid().getStoreString() +
      // " - name:" + dbObj.getName());
    }

    return dbObj;
  }

  // /**
  // * TODO - verify need.
  // */
  // private ooContObj createNewContainer()
  // {
  // return createNewContainer(null);
  // }

  /**
   * TODO - verify need.
   * 
   * @return
   */
  private ooContObj createNewContainer(String name)
  {
    ooDBObj db = getDatabaseObj();
    ooContObj cont = new ooContObj();
    db.addContainer(cont, 0, name, 0, 0);
    return cont;
  }

  public String getDbName()
  {
    return dbName;
  }

  /**
   * This function will throw an exception if the lookupObj() fails to find the named object.
   */
  public ObjyObject lookupObjyObject(String nameObject)
  {
    ObjyObject objyObject = null;
    ooId oid = lookupObjectOid(nameObject);
    objyObject = new ObjyObject(Class_Object.class_object_from_oid(oid));
    return objyObject;
  }

  public ooId lookupObjectOid(String nameObject)
  {
    ooObj anObj = null;
    anObj = (ooObj)getContainerObj().lookupObj(nameObject);
    return anObj.getOid();
  }

  public ooObj lookupObject(String nameObject)
  {
    ooObj anObj = null;
    anObj = (ooObj)getContainerObj().lookupObj(nameObject);
    return anObj;
  }

  public void nameObj(String objName, ObjyObject objyObject)
  {
    ooId oid = objyObject.ooId();
    nameObj(objName, oid);
  }

  public void nameObj(String objName, ooId oid)
  {
    ooObj object = ooObj.create_ooObj(oid);
    nameObj(objName, object);
  }

  public void nameObj(String objName, ooObj obj)
  {
    // if (!obj.isPersistent())
    // {
    // getContainerObj().cluster(obj);
    // }
    getContainerObj().nameObj(obj, objName);
  }

}
