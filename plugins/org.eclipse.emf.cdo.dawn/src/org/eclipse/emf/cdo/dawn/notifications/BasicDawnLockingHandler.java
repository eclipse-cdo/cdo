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
package org.eclipse.emf.cdo.dawn.notifications;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditorSupport;
import org.eclipse.emf.cdo.dawn.spi.DawnState;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Fluegge
 * @since 2.0
 */
public class BasicDawnLockingHandler extends BasicDawnListener
{
  public BasicDawnLockingHandler(IDawnEditor editor)
  {
    super(editor);
  }

  @Override
  public void handleLocksChangedEvent(CDOViewLocksChangedEvent event)
  {
    CDOViewLocksChangedEvent lockEvent = event;

    Collection<CDOLockState> lockStates = lockEvent.getNewLockStates();

    Map<Object, DawnState> changedObjects = new HashMap<>();

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
        if (object.cdoWriteLock().isLockedByOthers())
        {
          changedObjects.put(object, DawnState.LOCKED_REMOTELY);
        }
        else
        {
          changedObjects.put(object, DawnState.CLEAN);
        }
      }
    }

    IDawnEditorSupport dawnEditorSupport = editor.getDawnEditorSupport();
    dawnEditorSupport.handleRemoteLockChanges(changedObjects);
  }
}
