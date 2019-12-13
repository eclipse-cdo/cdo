/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.emf.editors.impl;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.editors.impl.DawnAbstractEditorSupport;
import org.eclipse.emf.cdo.dawn.emf.notifications.impl.DawnEMFHandler;
import org.eclipse.emf.cdo.dawn.emf.notifications.impl.DawnEMFLockingHandler;
import org.eclipse.emf.cdo.dawn.notifications.BasicDawnListener;
import org.eclipse.emf.cdo.dawn.spi.DawnState;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandlerBase;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.ecore.EObject;

import java.util.Map;

/**
 * @author Martin Fluegge
 * @since 1.0
 */
public class DawnEMFEditorSupport extends DawnAbstractEditorSupport
{
  public DawnEMFEditorSupport(IDawnEditor editor)
  {
    super(editor);
  }

  @Override
  public void close()
  {
    CDOView view = getView();
    if (view != null && !view.isClosed())
    {
      view.close();
    }
  }

  @Override
  protected BasicDawnListener getBasicHandler()
  {
    return new DawnEMFHandler(getEditor());
  }

  @Override
  protected BasicDawnListener getLockingHandler()
  {
    return new DawnEMFLockingHandler(getEditor());
  }

  @Override
  protected CDOTransactionHandlerBase getTransactionHandler()
  {
    return new DawnEMFHandler(getEditor());
  }

  @Override
  public void rollback()
  {
    super.rollback();
    refresh();
  }

  @Override
  public void refresh()
  {
    ((IViewerProvider)getEditor()).getViewer().refresh();
  }

  @Override
  public void lockObject(Object objectToBeLocked)
  {
    if (objectToBeLocked instanceof EObject)
    {
      CDOUtil.getCDOObject((EObject)objectToBeLocked).cdoWriteLock().lock();
    }
    refresh();
  }

  @Override
  public void unlockObject(Object objectToBeUnlocked)
  {
    CDOUtil.getCDOObject((EObject)objectToBeUnlocked).cdoWriteLock().unlock();
    refresh();
  }

  @Override
  public void handleRemoteLockChanges(Map<Object, DawnState> changedObjects)
  {
    getEditor().getSite().getShell().getDisplay().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        refresh();
      }
    });
  }
}
