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
      addRow(rows, "cdoID", cdoObject.cdoID(), CDOID.class.getName(), CATEGORY, foreground);
      addRow(rows, "cdoLockState", cdoObject.cdoLockState(), CDOLockState.class.getName(), CATEGORY, foreground);
      addRow(rows, "cdoPermission", cdoObject.cdoPermission(), CDOPermission.class.getName(), CATEGORY, foreground);
      addRow(rows, "cdoRevision", cdoObject.cdoRevision(), CDORevision.class.getName(), CATEGORY, foreground);
      addRow(rows, "cdoState", cdoObject.cdoState(), CDOState.class.getName(), CATEGORY, foreground);
      addRow(rows, "cdoView", cdoObject.cdoView(), CDOView.class.getName(), CATEGORY, foreground);
    }
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
