/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;

import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.InternalCDOObject;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ReloadObjectsAction extends EditingDomainAction
{
  public static final String ID = "reload-objects";

  private static final String TITLE = "Reload";

  private List<InternalCDOObject> objects = new ArrayList<InternalCDOObject>();

  public ReloadObjectsAction()
  {
    super(TITLE);
    setId(ID);
  }

  public void selectionChanged(IStructuredSelection selection)
  {
    objects.clear();
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

    update();
  }

  @Override
  public void update()
  {
    setEnabled(!objects.isEmpty());
  }

  @Override
  protected void doRun() throws Exception
  {
    if (!objects.isEmpty())
    {
      InternalCDOObject[] array = objects.toArray(new InternalCDOObject[objects.size()]);

      CDOStateMachine.INSTANCE.reload(array);

      IWorkbenchPage page = getPage();
      if (page != null)
      {
        CDOView view = array[0].cdoView();
        CDOEditor.refresh(page, view);
      }
    }
  }
}
