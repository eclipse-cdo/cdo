/*
 * Copyright (c) 2008-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Simon McDuff
 * @deprecated As of 4.6 no longer supported.
 */
@Deprecated
public abstract class AbstractLockObjectsAction extends EditingDomainAction
{
  private List<InternalCDOObject> objects = new ArrayList<InternalCDOObject>();

  private List<InternalCDOObject> lockObjects = new ArrayList<InternalCDOObject>();

  private Boolean lock;

  public AbstractLockObjectsAction(String title)
  {
    super(title);
  }

  public void selectionChanged(IStructuredSelection selection)
  {
    objects.clear();
    lock = null;
    if (selection != null)
    {
      for (Iterator<?> it = selection.iterator(); it.hasNext();)
      {
        Object object = it.next();
        if (object instanceof InternalCDOObject)
        {
          objects.add((InternalCDOObject)object);
        }
      }
    }
  }

  @Override
  public void update()
  {
    updateLockInfo();
    setEnabled(!lockObjects.isEmpty() && lock != null);
    setChecked(lock != null && lock);
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    if (!objects.isEmpty())
    {
      InternalCDOObject[] array = objects.toArray(new InternalCDOObject[objects.size()]);
      for (InternalCDOObject object : lockObjects)
      {
        CDOLock cdoLock = getLock(object);
        if (lock)
        {
          cdoLock.unlock();
        }
        else
        {
          if (!cdoLock.tryLock())
          {
            getDisplay().syncExec(new Runnable()
            {
              @Override
              public void run()
              {
                MessageDialog.openError(getShell(), Messages.getString("AbstractLockObjectsAction.0"), //$NON-NLS-1$
                    Messages.getString("AbstractLockObjectsAction.1")); //$NON-NLS-1$
              }
            });
          }
        }
      }

      IWorkbenchPage page = getPage();
      if (page != null)
      {
        CDOView view = array[0].cdoView();
        CDOEditorUtil.refreshEditors(page, view);
      }
    }
  }

  protected abstract CDOLock getLock(InternalCDOObject object);

  private void updateLockInfo()
  {
    lock = null;
    lockObjects.clear();
    for (InternalCDOObject object : objects)
    {
      boolean isLocked = getLock(object).isLocked();
      if (lock == null || isLocked == lock)
      {
        lock = isLocked;
        lockObjects.add(object);
      }
    }
  }
}
