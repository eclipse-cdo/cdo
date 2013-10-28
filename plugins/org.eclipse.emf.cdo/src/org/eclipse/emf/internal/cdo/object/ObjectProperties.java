/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 420528
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;
import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Eike Stepper
 */
public class ObjectProperties extends Properties<EObject>
{
  public static final IProperties<EObject> INSTANCE = new ObjectProperties();

  private static final String CATEGORY_CDO = "CDO"; //$NON-NLS-1$

  private ObjectProperties()
  {
    super(EObject.class);

    add(new Property<EObject>("id", //$NON-NLS-1$
        "ID", "The technical CDOID of this object.", CATEGORY_CDO)
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject(object);
        if (cdoObject == null)
        {
          return null;
        }

        return cdoObject.cdoID();
      }
    });

    add(new Property<EObject>("state", //$NON-NLS-1$
        "State", "The current state of this object.", CATEGORY_CDO)
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject(object);
        if (cdoObject == null)
        {
          return null;
        }

        return cdoObject.cdoState();
      }
    });

    add(new Property<EObject>("transactional") //$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject(object);
        if (cdoObject == null)
        {
          return false;
        }

        CDOView view = cdoObject.cdoView();
        if (view == null)
        {
          return false;
        }

        return !view.isReadOnly();
      }
    });

    add(new Property<EObject>("readable") //$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject(object);
        if (cdoObject == null)
        {
          return true;
        }

        return cdoObject.cdoPermission() != CDOPermission.NONE;
      }
    });

    add(new Property<EObject>("writable") //$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject(object);
        if (cdoObject == null)
        {
          return true;
        }

        CDOView view = cdoObject.cdoView();
        if (view == null)
        {
          return true;
        }

        return !view.isReadOnly() && cdoObject.cdoPermission() == CDOPermission.WRITE;
      }
    });

    add(new Property<EObject>("writableContainer") //$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = null;

        EObject eContainer = object.eContainer();
        if (eContainer != null)
        {
          cdoObject = CDOUtil.getCDOObject(eContainer);
        }

        if (cdoObject == null)
        {
          Resource resource = object.eResource();
          if (resource instanceof CDOObject)
          {
            cdoObject = (CDOObject)resource;
          }
        }

        if (cdoObject == null)
        {
          return true;
        }

        CDOView view = cdoObject.cdoView();
        if (view == null)
        {
          return true;
        }

        return !view.isReadOnly() && cdoObject.cdoPermission() == CDOPermission.WRITE;
      }
    });

    add(new Property<EObject>("permission", //$NON-NLS-1$
        "Permission", "The permission the current user has for this object.", CATEGORY_CDO)
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject(object);
        if (cdoObject == null)
        {
          return null;
        }

        return cdoObject.cdoPermission();
      }
    });

    add(new Property<EObject>("permissionContainer") //$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = null;

        EObject eContainer = object.eContainer();
        if (eContainer != null)
        {
          cdoObject = CDOUtil.getCDOObject(eContainer);
        }

        if (cdoObject == null)
        {
          Resource resource = object.eResource();
          if (resource instanceof CDOObject)
          {
            cdoObject = (CDOObject)resource;
          }
        }

        if (cdoObject == null)
        {
          return null;
        }

        return cdoObject.cdoPermission();
      }
    });

    add(new Property<EObject>("readLocks", //$NON-NLS-1$
        "Read Locks", "The owner of read locks on this object.", CATEGORY_CDO)
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject(object);
        if (cdoObject == null)
        {
          return null;
        }

        CDOLockState lockState = cdoObject.cdoLockState();
        if (lockState == null)
        {
          return null;
        }

        return lockState.getReadLockOwners();
      }
    });

    add(new Property<EObject>("writeLock", //$NON-NLS-1$
        "Write Lock", "The owner of a write lock on this object.", CATEGORY_CDO)
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject(object);
        if (cdoObject == null)
        {
          return null;
        }

        CDOLockState lockState = cdoObject.cdoLockState();
        if (lockState == null)
        {
          return null;
        }

        return lockState.getWriteLockOwner();
      }
    });

    add(new Property<EObject>("writeOption", //$NON-NLS-1$
        "Write Option", "The owner of a write option on this object.", CATEGORY_CDO)
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject(object);
        if (cdoObject == null)
        {
          return null;
        }

        CDOLockState lockState = cdoObject.cdoLockState();
        if (lockState == null)
        {
          return null;
        }

        return lockState.getWriteOptionOwner();
      }
    });

    add(new Property<EObject>("viewHistorical") //$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject(object);
        if (cdoObject == null)
        {
          return false;
        }

        CDOView view = cdoObject.cdoView();
        if (view == null)
        {
          return false;
        }

        return view.getTimeStamp() != CDOBranchPoint.UNSPECIFIED_DATE;
      }
    });

  }

  public static void main(String[] args)
  {
    new Tester().dumpContributionMarkup();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Tester extends DefaultPropertyTester<EObject>
  {
    public static final String NAMESPACE = "org.eclipse.emf.cdo.object";

    public Tester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }
}
