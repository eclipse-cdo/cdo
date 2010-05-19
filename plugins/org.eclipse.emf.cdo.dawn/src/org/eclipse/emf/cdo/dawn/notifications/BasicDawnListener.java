/*******************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.notifications;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.dawn.synchronize.DawnConflictHelper;
import org.eclipse.emf.cdo.dawn.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.InvalidObjectException;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.widgets.Display;

/**
 * @author Martin Fluegge
 */
public class BasicDawnListener implements IListener
{
  protected DiagramDocumentEditor editor;

  public void setEditor(DiagramDocumentEditor editor)
  {
    this.editor = editor;
  }

  public BasicDawnListener()
  {
  }

  public BasicDawnListener(DiagramDocumentEditor editor)
  {
    this.editor = editor;
  }

  public void notifyEvent(IEvent event)
  {
    if (event instanceof CDOViewInvalidationEvent)
    {
      CDOViewInvalidationEvent e = (CDOViewInvalidationEvent)event;

      for (CDOObject object : e.getDirtyObjects())
      {
        System.out.println("Dirty: " + object);
      }
      for (CDOObject object : e.getDetachedObjects())
      {
        System.out.println("Dirty: " + object);
      }
    }
    else
    {
      System.out.println("Unhandeled Event: " + event);
    }
  }

  /**
   * Edges must be adjusted because of the transience of the Node source/targetEdges CDO cannot see this because
   * removing an edges just removes the edge from the diagram. CDO just notices the change in the diagram but not in the
   * (detached) edge. The other site (node) is transient and will not be part of the notification. So I must adjust this
   * later. CDOLEgacy Wrapper breakes because it only adjusts the changes in the diagram and not the removed edge. So I
   * cannot adjust this in the Wrapper. Maybe there is another more generic way.
   */
  public static void adjustDeletedEdges(final CDOViewInvalidationEvent e)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        for (CDOObject obj : e.getDetachedObjects())
        {
          EObject view = CDOUtil.getEObject(obj);
          if (view instanceof Edge)
          {
            try
            {
              ((Edge)view).setTarget(null);
            }
            catch (InvalidObjectException ignore)
            {
            }

            try
            {
              ((Edge)view).setSource(null);
            }
            catch (InvalidObjectException ignore)
            {
            }
          }
        }
      }
    });
  }

  protected void handleConflicts(CDOViewInvalidationEvent e)
  {
    for (CDOObject obj : e.getDetachedObjects())
    {
      EObject element = CDOUtil.getEObject(obj);
      View view = DawnDiagramUpdater.findViewByContainer(element);
      DawnConflictHelper.handleConflictedView(CDOUtil.getCDOObject(element), view, editor);
    }
  }
}
