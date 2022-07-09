/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOIntrospectionProvider extends EMFIntrospectionProvider
{
  protected static final int PRIORITY = EMFIntrospectionProvider.PRIORITY - 20;

  protected static final int CATEGORY = EMFIntrospectionProvider.CATEGORY + 10;

  private static final String CDO_ID = "cdoID";

  private static final String CDO_LOCK_STATE = "cdoLockState";

  private static final String CDO_PERMISSION = "cdoPermission";

  private static final String CDO_REVISION = "cdoRevision";

  private static final String CDO_CLASS_INFO = "cdoClassInfo";

  private static final String CDO_STATE = "cdoState";

  private static final String CDO_VIEW = "cdoView";

  private final Color foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_CYAN);

  public CDOIntrospectionProvider()
  {
    super(CDOObject.class.getName(), "CDOObject");
  }

  @Override
  public int getPriority()
  {
    return PRIORITY;
  }

  @Override
  public boolean canHandle(Object object)
  {
    return getCDOObject(object) != null;
  }

  @Override
  protected void fillRows(Object parent, List<Row> rows) throws Exception
  {
    super.fillRows(parent, rows);

    CDOObject cdoObject = getCDOObject(parent);
    if (cdoObject != null)
    {
      rows.add(createCDOIDRow(cdoObject));
      rows.add(createCDOLockStateRow(cdoObject));
      rows.add(createCDOPermissionRow(cdoObject));
      rows.add(createCDORevisionRow(cdoObject));
      rows.add(createCDOClassInfoRow(cdoObject));
      rows.add(createCDOStateRow(cdoObject));
      rows.add(createCDOViewRow(cdoObject));
    }
  }

  @Override
  public Row getElementByName(Object parent, String name) throws Exception
  {
    Row result = super.getElementByName(parent, name);
    if (result == null)
    {
      CDOObject cdoObject = getCDOObject(parent);
      if (cdoObject != null)
      {
        if (CDO_ID.equals(name))
        {
          return createCDOIDRow(cdoObject);
        }

        if (CDO_LOCK_STATE.equals(name))
        {
          return createCDOLockStateRow(cdoObject);
        }

        if (CDO_PERMISSION.equals(name))
        {
          return createCDOPermissionRow(cdoObject);
        }

        if (CDO_REVISION.equals(name))
        {
          return createCDORevisionRow(cdoObject);
        }

        if (CDO_CLASS_INFO.equals(name))
        {
          return createCDOClassInfoRow(cdoObject);
        }

        if (CDO_STATE.equals(name))
        {
          return createCDOStateRow(cdoObject);
        }

        if (CDO_VIEW.equals(name))
        {
          return createCDOViewRow(cdoObject);
        }
      }
    }

    return result;
  }

  private Row createCDOViewRow(CDOObject cdoObject)
  {
    return createRow(CDO_VIEW, cdoObject.cdoView(), CDOView.class.getName(), CATEGORY, foreground);
  }

  private Row createCDOStateRow(CDOObject cdoObject)
  {
    return createRow(CDO_STATE, cdoObject.cdoState(), CDOState.class.getName(), CATEGORY, foreground);
  }

  private Row createCDORevisionRow(CDOObject cdoObject)
  {
    return createRow(CDO_REVISION, cdoObject.cdoRevision(), CDORevision.class.getName(), CATEGORY, foreground);
  }

  private Row createCDOClassInfoRow(CDOObject cdoObject)
  {
    CDORevision revision = cdoObject.cdoRevision();
    return createRow(CDO_CLASS_INFO, revision == null ? null : revision.getClassInfo(), CDOClassInfo.class.getName(), CATEGORY, foreground);
  }

  private Row createCDOPermissionRow(CDOObject cdoObject)
  {
    return createRow(CDO_PERMISSION, cdoObject.cdoPermission(), CDOPermission.class.getName(), CATEGORY, foreground);
  }

  private Row createCDOLockStateRow(CDOObject cdoObject)
  {
    return createRow(CDO_LOCK_STATE, cdoObject.cdoLockState(), CDOLockState.class.getName(), CATEGORY, foreground);
  }

  private Row createCDOIDRow(CDOObject cdoObject)
  {
    return createRow(CDO_ID, cdoObject.cdoID(), CDOID.class.getName(), CATEGORY, foreground);
  }

  private static CDOObject getCDOObject(Object object)
  {
    if (object instanceof EObject)
    {
      return CDOUtil.getCDOObject((EObject)object);
    }

    return null;
  }
}
