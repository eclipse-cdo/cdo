/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */

package org.eclipse.emf.cdo.server.internal.objectivity.utils;

import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import com.objy.db.FetchCompletedWithErrors;
import com.objy.db.ObjyRuntimeException;
import com.objy.db.app.Session;
import com.objy.db.app.oo;
import com.objy.db.app.ooContObj;
import com.objy.db.app.ooObj;
import com.objy.db.iapp.ActivateInfo;
import com.objy.db.iapp.FetchErrorInfo;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author Simon McDuff To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SmartLock
{
  // private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, SmartLock.class);

  private static final ContextTracer TRACER_INFO = new ContextTracer(OM.INFO, SmartLock.class);

  private static final ContextTracer TRACER_ERROR = new ContextTracer(OM.ERROR, SmartLock.class);

  public static boolean lock(ObjyObject objyObject)
  {
    ooObj objectToLock = (ooObj)Session.getCurrent().getFD().objectFrom(objyObject.ooId());
    if (!objectToLock.isPersistent())
    {
      return false;
    }
    ooContObj container = null;
    if (objectToLock instanceof ooContObj)
    {
      container = (ooContObj)objectToLock;
    }
    else
    {
      container = objectToLock.getContainer();
    }
    return lock(container);
  }

  public static boolean readLock(ooContObj container)
  {
    container.fetch();
    if (container.isUpdated())
    {
      container.refresh(oo.READ);
      return true;
    }

    try
    {
      container.lock(oo.READ);
    }
    catch (Exception e)
    {
      // In MROW Mode, an Exception occur if we try to upgrade the lock from read to write when the container is
      // Locked by someone else.
      container.refresh(oo.READ);
    }

    return false;
  }

  public static boolean lock(ooContObj container)
  {
    container.fetch();
    if (container.isUpdated())
    {
      container.refresh(oo.WRITE);
      return true;
    }

    try
    {
      container.lock(oo.WRITE);
    }
    catch (Exception e)
    {
      // In MROW Mode, an Exception occur if we try to upgrade the lock from read to write when the container is
      // Locked by someone else.
      container.refresh(oo.WRITE);
    }

    return false;
  }

  public static boolean unlock(ooObj objectToLock)
  {
    // ooContObj container = objectToLock.getContainer();
    Session.getCurrent().checkpoint(oo.DOWNGRADE_ALL);
    return false;
  }

  /**
   * This should replace ooObj.activate. We do not need to call super.activate because we implemented the code in
   * ooObj.activate in here. This safeActivate will refresh container in case of errors. >> ... Msg: objref member: test
   * with oid: #26-387-1-2 not found or accessible
   *
   * @param object
   * @param fcweEx
   */
  @SuppressWarnings("unchecked")
  public static void safeActivate(ooObj object, ActivateInfo fcweEx)
  {
    if (!fcweEx.hasFetchErrors())
    {
      return;
    }
    if (TRACER_INFO.isEnabled())
    {
      TRACER_INFO.trace("   >> Object: " + object.getOid().getStoreString() + " Fetch with errors");
    }
    Vector<Object> errors = fcweEx.getFetchErrors();
    // Make sure there are fetch-error information objects
    if (errors != null)
    {
      // Get Enumeration from Vector
      Enumeration<Object> errs = errors.elements();
      FetchErrorInfo feInfo = null;
      HashSet<Object> hashSet = new HashSet<Object>();
      hashSet.add(object.getContainer());
      while (errs.hasMoreElements())
      {
        feInfo = (FetchErrorInfo)errs.nextElement();
        TRACER_INFO.trace("  >> ... fieldName: " + feInfo.getFieldName());
        TRACER_INFO.trace("  >> ... Msg: " + feInfo.getErrorMessage());
        String needFetchingOID = feInfo.getOid().getStoreString();
        TRACER_INFO.trace("  >> ... OID: " + needFetchingOID);
        try
        {
          TRACER_INFO.trace("  >> Trying to refetch the object....");
          String contID = "#" + feInfo.getOid().getDB() + "-" + feInfo.getOid().getOC() + "-" + "1-1";
          TRACER_INFO.trace("contID: " + contID);
          ooContObj tempCont = (ooContObj)Session.getCurrent().getFD().objectFrom(contID);
          if (!hashSet.contains(tempCont))
          {
            hashSet.add(tempCont);
          }
        }
        catch (ObjyRuntimeException ex)
        {
          TRACER_ERROR.trace("FATAL", ex);
        }

      }
      Iterator<Object> itrCont = hashSet.iterator();
      int numCont = 0;
      while (itrCont.hasNext())
      {
        ooContObj cont = (ooContObj)itrCont.next();
        if (cont.isUpdated())
        {
          TRACER_INFO.trace("RECOVER : REFRESH CONT " + cont.getOid().getStoreString());
          cont.refresh(oo.READ);
          numCont++;
        }
      }
      if (numCont != 0)
      {
        // End while more fetch-error information objects
        object.markFetchRequired();
        object.fetch();
      }
      else
      {
        throw new FetchCompletedWithErrors("Fetch completed but errors occurred", object, fcweEx.getFetchErrors());
      }
    }
  }

}
