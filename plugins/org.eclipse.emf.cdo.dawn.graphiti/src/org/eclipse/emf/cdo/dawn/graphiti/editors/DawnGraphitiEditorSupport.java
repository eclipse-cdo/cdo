/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.graphiti.editors;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.editors.impl.DawnAbstractEditorSupport;
import org.eclipse.emf.cdo.dawn.gmf.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.dawn.graphiti.notifications.DawnGraphitiHandler;
import org.eclipse.emf.cdo.dawn.graphiti.notifications.DawnGraphitiLockingHandler;
import org.eclipse.emf.cdo.dawn.helper.DawnEditorHelper;
import org.eclipse.emf.cdo.dawn.notifications.BasicDawnListener;
import org.eclipse.emf.cdo.dawn.spi.DawnState;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandlerBase;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.gef.RootEditPart;
import org.eclipse.graphiti.ui.editor.DiagramEditor;

import java.util.Map;

/**
 * @author Martin Fluegge
 */
/*
 * TODO remove this suppress warning as soon as I have found a way to workaround the problem that the Graphiti editor
 * which is extended is internal
 */
@SuppressWarnings("restriction")
public class DawnGraphitiEditorSupport extends DawnAbstractEditorSupport
{
  private DawnGraphitiHandler dawnGraphitiHandler;

  public DawnGraphitiEditorSupport(IDawnEditor editor)
  {
    super(editor);
    dawnGraphitiHandler = new DawnGraphitiHandler(getEditor());
  }

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
    return dawnGraphitiHandler;
  }

  @Override
  protected BasicDawnListener getLockingHandler()
  {
    return new DawnGraphitiLockingHandler(getEditor());
  }

  @Override
  protected CDOTransactionHandlerBase getTransactionHandler()
  {
    return dawnGraphitiHandler;
  }

  @Override
  public void rollback()
  {
    super.rollback();
    refresh();
  }

  public void refresh()
  {
    // final DiagramEditor editor = (DiagramEditor)getEditor();
    // TransactionalEditingDomain editingDomain = editor.getEditingDomain();
    // editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain)
    // {
    // @Override
    // public void doExecute()
    // {
    // RootEditPart rootEditPart = editor.getGraphicalViewer().getRootEditPart();
    // DawnAppearancer.setEditPartDefaultAllChildren(rootEditPart);
    // DawnDiagramUpdater.refreshEditPart(rootEditPart);
    // }
    // });

    DawnEditorHelper.getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        RootEditPart rootEditPart = ((DiagramEditor)getEditor()).getGraphicalViewer().getRootEditPart();
        DawnDiagramUpdater.refreshEditPart(rootEditPart);
      }
    });
  }

  public void lockObject(Object objectToBeLocked)
  {
    if (objectToBeLocked instanceof EObject)
    {
      CDOUtil.getCDOObject((EObject)objectToBeLocked).cdoWriteLock().lock();
    }
    refresh();
  }

  public void unlockObject(Object objectToBeUnlocked)
  {
    CDOUtil.getCDOObject((EObject)objectToBeUnlocked).cdoWriteLock().unlock();
    refresh();
  }

  public void handleRemoteLockChanges(Map<Object, DawnState> changedObjects)
  {
    getEditor().getSite().getShell().getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        refresh();
      }
    });
  }
}
