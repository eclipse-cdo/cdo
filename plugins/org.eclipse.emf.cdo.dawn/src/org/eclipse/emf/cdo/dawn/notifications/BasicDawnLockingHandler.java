/*
 * Copyright (c) 2011, 2012, 2015, 2019, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.notifications;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditorSupport;
import org.eclipse.emf.cdo.dawn.spi.DawnState;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

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
    Map<Object, DawnState> changedObjects = new HashMap<>();
    CDOView view = editor.getDawnEditorSupport().getView();

    for (CDOLockDelta lockDelta : event.getLockDeltas())
    {
      CDOID id = lockDelta.getID();
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

    if (!changedObjects.isEmpty())
    {
      IDawnEditorSupport dawnEditorSupport = editor.getDawnEditorSupport();
      dawnEditorSupport.handleRemoteLockChanges(changedObjects);
    }
  }
}
