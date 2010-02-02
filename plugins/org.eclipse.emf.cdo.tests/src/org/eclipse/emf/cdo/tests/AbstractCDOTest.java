/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.model.CDOPackageTypeRegistry;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.CDOLegacyWrapper;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 */
public abstract class AbstractCDOTest extends ConfigTest
{
  @SuppressWarnings("restriction")
  @Override
  protected void doSetUp() throws Exception
  {
    try
    {
      super.doSetUp();
    }
    finally
    {
      org.eclipse.emf.cdo.internal.net4j.bundle.OM.PREF_COMMIT_MONITOR_PROGRESS_SECONDS.setValue(60);
      org.eclipse.emf.cdo.internal.net4j.bundle.OM.PREF_COMMIT_MONITOR_TIMEOUT_SECONDS.setValue(60 * 60);
      org.eclipse.internal.net4j.bundle.OM.DEBUG.setEnabled(true);
      org.eclipse.net4j.internal.tcp.bundle.OM.DEBUG.setEnabled(true);
      CDOPackageTypeRegistry.INSTANCE.reset();
      startTransport();
    }
  }

  protected static void assertTransient(EObject eObject)
  {
    CDOObject object = CDOUtil.getCDOObject(eObject);
    if (object != null)
    {
      assertEquals(true, FSMUtil.isTransient(object));
      assertNull(object.cdoID());
      assertNull(object.cdoRevision());
      assertNull(object.cdoView());
    }
  }

  protected static void assertInvalid(EObject eObject)
  {
    CDOObject object = CDOUtil.getCDOObject(eObject);
    if (object != null)
    {
      assertEquals(true, FSMUtil.isInvalid(object));
    }
  }

  protected static void assertNotTransient(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertEquals(false, FSMUtil.isTransient(object));
    assertNotNull(object.cdoID());
    assertNotNull(object.cdoRevision());
    assertNotNull(object.cdoView());
    assertNotNull(object.eResource());
    assertEquals(view, object.cdoView());
    assertEquals(object, view.getObject(object.cdoID(), false));
  }

  protected static void assertNew(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertNotTransient(object, view);
    assertEquals(CDOState.NEW, object.cdoState());
  }

  protected static void assertDirty(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertNotTransient(object, view);
    assertEquals(CDOState.DIRTY, object.cdoState());
  }

  protected static void assertClean(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertNotTransient(object, view);
    assertEquals(CDOState.CLEAN, object.cdoState());
  }

  protected static void assertProxy(EObject eObject)
  {
    CDOObject object = CDOUtil.getCDOObject(eObject);
    if (object != null)
    {
      assertEquals(false, FSMUtil.isTransient(object));
      assertNotNull(object.cdoID());
      assertNotNull(object.cdoView());
      assertEquals(CDOState.PROXY, object.cdoState());
    }
  }

  protected static void assertContent(EObject eContainer, EObject eContained)
  {
    CDOObject container = CDOUtil.getCDOObject(eContainer);
    CDOObject contained = CDOUtil.getCDOObject(eContained);
    if (container != null && contained != null)
    {
      assertEquals(true, container.eContents().contains(contained));
      if (container instanceof CDOResource)
      {
        assertEquals(container, contained.eResource());
        assertEquals(null, contained.eContainer());
        assertEquals(true, ((CDOResource)container).getContents().contains(contained));
      }
      else
      {
        assertEquals(container.eResource(), contained.eResource());
        assertEquals(container, contained.eContainer());
      }
    }
  }

  protected static void assertNotProxy(Object object)
  {
    assertEquals(false, CDOLegacyWrapper.isLegacyProxy(object));
  }

  protected static void assertCreatedTime(EObject eObject, long timeStamp)
  {
    CDOObject object = CDOUtil.getCDOObject(eObject);
    if (object != null)
    {
      assertEquals(timeStamp, object.cdoRevision().getTimeStamp());
    }
  }
}
