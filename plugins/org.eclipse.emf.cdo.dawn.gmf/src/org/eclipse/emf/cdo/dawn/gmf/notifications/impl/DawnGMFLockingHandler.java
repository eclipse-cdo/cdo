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

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.notifications.BasicDawnLockingHandler;

/**
 * @author Martin Fluegge
 */
public class DawnGMFLockingHandler extends BasicDawnLockingHandler
{
  public DawnGMFLockingHandler(IDawnEditor editor)
  {
    super(editor);
  }

  // @Override
  // public void handleLocksChangedEvent(CDOViewLocksChangedEvent event)
  // {
  // CDOViewLocksChangedEvent lockEvent = event;
  //
  // CDOLockState[] lockStates = lockEvent.getLockStates();
  //
  // List<Object> remotelyLockedObjects = new ArrayList<Object>();
  // List<Object> remotelyUnlockedObjects = new ArrayList<Object>();
  //
  // for (CDOLockState state : lockStates)
  // {
  // Object lockedObject = state.getLockedObject();
  //
  // CDOView view = editor.getDawnEditorSupport().getView();
  // CDOID id;
  // if (lockedObject instanceof CDOID)
  // {
  // id = (CDOID)lockedObject;
  // }
  // else if (lockedObject instanceof CDOIDAndBranch)
  // {
  // id = ((CDOIDAndBranch)lockedObject).getID();
  // }
  // else
  // {
  // throw new RuntimeException("Unexpected object type: " + lockedObject);
  // }
  //
  // if (id != null)
  // {
  // CDOObject object = view.getObject(id);
  // if (object.cdoWriteLock().isLocked())
  // {
  // throw new RuntimeException("Shoudl not occur");
  // }
  // else if (object.cdoWriteLock().isLockedByOthers())
  // {
  // remotelyLockedObjects.add(object);
  // }
  // else
  // {
  // remotelyUnlockedObjects.add(object);
  // }
  // }
  // }
  //
  // IDawnEditorSupport dawnEditorSupport = editor.getDawnEditorSupport();
  //
  // dawnEditorSupport.handleRemotelyLockedObjects(remotelyLockedObjects);
  // dawnEditorSupport.handleRemotelyUnlockedObjects(remotelyUnlockedObjects);
  // // dawnEditorSupport.refresh();
  // }
}
