/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.gmf.notifications.impl;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.gmf.appearance.DawnAppearancer;
import org.eclipse.emf.cdo.dawn.gmf.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.dawn.notifications.BasicDawnListener;
import org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.net4j.util.event.IEvent;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @author Martin Fluegge
 */
public class DawnGMFLockingHandler extends BasicDawnListener
{
  public DawnGMFLockingHandler(IDawnEditor editor)
  {
    super(editor);
  }

  public void handleViewInvalidationEvent(CDOViewInvalidationEvent event)
  {
    System.out.println("DawnGMFLockingHandler.handleViewInvalidationEvent()");
  }

  public void handleTransactionConflictEvent(CDOTransactionConflictEvent event)
  {
    System.out.println("DawnGMFLockingHandler.handleTransactionConflictEvent()");
  }

  public void handleEvent(IEvent event)
  {
    if (event instanceof CDOViewLocksChangedEvent)
    {
      CDOViewLocksChangedEvent lockEvent = (CDOViewLocksChangedEvent)event;

      CDOLockState[] lockStates = lockEvent.getLockStates();
      for (CDOLockState state : lockStates)
      {
        Object lockedObject = state.getLockedObject();

        CDOView view = editor.getDawnEditorSupport().getView();
        CDOID id;
        if (lockedObject instanceof CDOID)
        {
          id = (CDOID)lockedObject;
        }
        else if (lockedObject instanceof CDOIDAndBranch)
        {
          id = ((CDOIDAndBranch)lockedObject).getID();
        }
        else
        {
          throw new RuntimeException("Unexpected object type: " + lockedObject);
        }

        if (id != null)
        {
          CDOObject object = view.getObject(id);
          handleLock(object, view);
          System.out.println(object);
        }
      }

      System.out.println("DawnGMFLockingHandler.handleEvent()");
      editor.getDawnEditorSupport().refresh();
    }
  }

  private void handleLock(CDOObject object, CDOView cdoView)
  {
    EObject element = CDOUtil.getEObject(object); // either semantic object or notational
    View view = DawnDiagramUpdater.findView(element);
    if (view != null)
    {
      // if there is no view, the semantic object is not displayed.
      EditPart editPart = DawnDiagramUpdater.createOrFindEditPartIfViewExists(view, (DiagramDocumentEditor)editor);

      if (object.cdoWriteLock().isLocked())
      {
        DawnAppearancer.setEditPartLocked(editPart, DawnAppearancer.TYPE_LOCKED_LOCALLY);
      }
      else if (object.cdoWriteLock().isLockedByOthers())
      {
        DawnAppearancer.setEditPartLocked(editPart, DawnAppearancer.TYPE_LOCKED_GLOBALLY);
      }
      else
      {
        DawnAppearancer.setEditPartDefault(editPart);
      }
    }
  }
}
