/*
 * Copyright (c) 2013, 2015, 2016, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.common.security.NoPermissionException;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
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

  public static final String NAMESPACE = "org.eclipse.emf.cdo.object";

  private static final String CATEGORY_CDO = "CDO"; //$NON-NLS-1$

  private ObjectProperties()
  {
    super(EObject.class);

    add(new Property<EObject>("isCDO") //$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        return getCDOObject(object) != null;
      }
    });

    add(new Property<EObject>("id", //$NON-NLS-1$
        "ID", "The technical CDOID of this object.", CATEGORY_CDO)
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
        if (cdoObject == null)
        {
          return null;
        }

        return cdoObject.cdoID();
      }
    });

    add(new Property<EObject>("version", //$NON-NLS-1$
        "Version", "The version of this object.", CATEGORY_CDO)
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
        if (cdoObject == null)
        {
          return 0;
        }

        CDORevision revision = cdoObject.cdoRevision();
        if (revision == null)
        {
          return 0;
        }

        return revision.getVersion();
      }
    });

    add(new Property<EObject>("branch", //$NON-NLS-1$
        "Branch", "The branch of this object.", CATEGORY_CDO)
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
        if (cdoObject == null)
        {
          return null;
        }

        CDORevision revision = cdoObject.cdoRevision();
        if (revision == null)
        {
          return null;
        }

        return revision.getBranch().getPathName();
      }
    });

    add(new Property<EObject>("timeStamp") //$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
        if (cdoObject == null)
        {
          return CDOBranchPoint.UNSPECIFIED_DATE;
        }

        CDORevision revision = cdoObject.cdoRevision();
        if (revision == null)
        {
          return CDOBranchPoint.UNSPECIFIED_DATE;
        }

        return revision.getTimeStamp();
      }
    });

    add(new Property<EObject>("modificationTime", //$NON-NLS-1$
        "Modification Time", "The modification time of this object.", CATEGORY_CDO)
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
        if (cdoObject == null)
        {
          return null;
        }

        CDORevision revision = cdoObject.cdoRevision();
        if (revision == null)
        {
          return null;
        }

        return CDOCommonUtil.formatTimeStamp(revision.getTimeStamp());
      }
    });

    add(new Property<EObject>("state", //$NON-NLS-1$
        "State", "The current state of this object.", CATEGORY_CDO)
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
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
        CDOObject cdoObject = getCDOObject(object);
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
        CDOObject cdoObject = getCDOObject(object);
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
        CDOObject cdoObject = getCDOObject(object);
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
        EObject container = CDOElement.getParentOf(object);
        if (container == null)
        {
          return true;
        }

        CDOObject cdoContainer = getCDOObject(container);
        if (cdoContainer == null)
        {
          return true;
        }

        CDOView view = cdoContainer.cdoView();
        if (view == null)
        {
          return true;
        }

        return !view.isReadOnly() && cdoContainer.cdoPermission() == CDOPermission.WRITE;
      }
    });

    add(new Property<EObject>("container") //$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        EObject container = CDOElement.getParentOf(object);
        if (container == null)
        {
          return false;
        }

        CDOObject cdoContainer = getCDOObject(container);
        if (cdoContainer == null)
        {
          return false;
        }

        return true;
      }
    });

    add(new Property<EObject>("children") //$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        try
        {
          return !object.eContents().isEmpty();
        }
        catch (NoPermissionException ex)
        {
          return false;
        }
      }
    });

    add(new Property<EObject>("permission", //$NON-NLS-1$
        "Permission", "The permission the current user has for this object.", CATEGORY_CDO)
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
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
          cdoObject = getCDOObject(eContainer);
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
        CDOObject cdoObject = getCDOObject(object);
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

    add(new Property<EObject>("readLocked")//$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
        if (cdoObject == null)
        {
          return false;
        }

        CDOView view = cdoObject.cdoView();
        if (view == null)
        {
          return false;
        }

        CDOLock lock = cdoObject.cdoReadLock();
        if (lock == null)
        {
          return false;
        }

        return lock.isLocked();
      }
    });

    add(new Property<EObject>("readLockedByOthers")//$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
        if (cdoObject == null)
        {
          return false;
        }

        CDOView view = cdoObject.cdoView();
        if (view == null)
        {
          return false;
        }

        CDOLock lock = cdoObject.cdoReadLock();
        if (lock == null)
        {
          return false;
        }

        return lock.isLockedByOthers();
      }
    });

    add(new Property<EObject>("writeLock", //$NON-NLS-1$
        "Write Lock", "The owner of a write lock on this object.", CATEGORY_CDO)
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
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

    add(new Property<EObject>("writeLocked")//$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
        if (cdoObject == null)
        {
          return false;
        }

        CDOView view = cdoObject.cdoView();
        if (view == null)
        {
          return false;
        }

        CDOLock lock = cdoObject.cdoWriteLock();
        if (lock == null)
        {
          return false;
        }

        return lock.isLocked();
      }
    });

    add(new Property<EObject>("writeLockedByOthers")//$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
        if (cdoObject == null)
        {
          return false;
        }

        CDOView view = cdoObject.cdoView();
        if (view == null)
        {
          return false;
        }

        CDOLock lock = cdoObject.cdoWriteLock();
        if (lock == null)
        {
          return false;
        }

        return lock.isLockedByOthers();
      }
    });

    add(new Property<EObject>("writeOption", //$NON-NLS-1$
        "Write Option", "The owner of a write option on this object.", CATEGORY_CDO)
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
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

    add(new Property<EObject>("writeOptioned")//$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
        if (cdoObject == null)
        {
          return false;
        }

        CDOView view = cdoObject.cdoView();
        if (view == null)
        {
          return false;
        }

        CDOLock lock = cdoObject.cdoWriteOption();
        if (lock == null)
        {
          return false;
        }

        return lock.isLocked();
      }
    });

    add(new Property<EObject>("writeOptionedByOthers")//$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
        if (cdoObject == null)
        {
          return false;
        }

        CDOView view = cdoObject.cdoView();
        if (view == null)
        {
          return false;
        }

        CDOLock lock = cdoObject.cdoWriteOption();
        if (lock == null)
        {
          return false;
        }

        return lock.isLockedByOthers();
      }
    });

    add(new Property<EObject>("autoReleaseLocksExemption")//$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
        if (cdoObject == null)
        {
          return false;
        }

        CDOView view = cdoObject.cdoView();
        if (view instanceof CDOTransaction)
        {
          CDOTransaction transaction = (CDOTransaction)view;
          return transaction.options().isAutoReleaseLocksExemption(cdoObject);
        }

        return false;
      }
    });

    add(new Property<EObject>("viewHistorical") //$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
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

    add(new Property<EObject>("uri", "URI", "The URI of this object.", CATEGORY_CDO) //$NON-NLS-1$
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOObject cdoObject = getCDOObject(object);
        if (cdoObject == null)
        {
          return null;
        }

        Resource resource = cdoObject.eResource();
        if (resource == null)
        {
          return null;
        }

        CDOID id = cdoObject.cdoID();
        String fragment = id != null ? id.toURIFragment() : resource.getURIFragment(cdoObject);
        return resource.getURI().appendFragment(fragment).toString();
      }
    });
  }

  private static CDOObject getCDOObject(EObject object)
  {
    return CDOUtil.getCDOObject(object, false);
  }

  public static void main(String[] args)
  {
    new Tester().dumpContributionMarkup();
    new ElementTester().dumpContributionMarkup();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Tester extends DefaultPropertyTester<EObject>
  {
    public Tester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ElementTester extends CDOElementTester
  {
    public ElementTester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }
}
