/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.graphiti.notifications;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.gmf.appearance.DawnAppearancer;
import org.eclipse.emf.cdo.dawn.gmf.synchronize.DawnConflictHelper;
import org.eclipse.emf.cdo.dawn.gmf.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.dawn.graphiti.util.DawnGraphitiUtil;
import org.eclipse.emf.cdo.dawn.helper.DawnEditorHelper;
import org.eclipse.emf.cdo.dawn.notifications.BasicDawnTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.InvalidObjectException;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.draw2d.graph.Edge;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.widgets.Display;

import java.util.List;

/**
 * @author Martin Fluegge
 */
public class DawnGraphitiHandler extends BasicDawnTransactionHandler
{
  public DawnGraphitiHandler(IDawnEditor editor)
  {
    super(editor);
  }

  /**
   * @since 2.0
   */
  @Override
  public void handleViewInvalidationEvent(CDOViewInvalidationEvent event)
  {
    editor.getDawnEditorSupport().refresh();
  }

  /**
   * @since 2.0
   */
  @Override
  public void handleTransactionConflictEvent(@SuppressWarnings("deprecation") org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent event)
  {
    CDOObject cdoObject = event.getConflictingObject();

    EObject element = CDOUtil.getEObject(cdoObject); // either semantic object or notational

    List<PictogramElement> pictgramElements = DawnGraphitiUtil.getPictgramElements(((DiagramEditor)editor).getDiagramTypeProvider().getDiagram(), element);

    GraphicalViewer graphicalViewer = ((DiagramEditor)editor).getGraphicalViewer();

    for (PictogramElement pictgramElement : pictgramElements)
    {
      final EditPart editpart = DawnGraphitiUtil.getEditpart(pictgramElement, graphicalViewer.getRootEditPart());

      if (DawnConflictHelper.isConflicted(cdoObject))
      {
        DawnEditorHelper.getDisplay().syncExec(new Runnable()
        {
          @Override
          public void run()
          {
            int typeConflictLocallyDeleted = DawnAppearancer.TYPE_CONFLICT_REMOTELY_DELETED;
            DawnAppearancer.setEditPartConflicted(editpart, typeConflictLocallyDeleted);
          }
        });
      }
    }
  }

  @Override
  public void modifyingObject(CDOTransaction transaction, final CDOObject object, CDOFeatureDelta featureDelta)
  {
    super.modifyingObject(transaction, object, featureDelta);
    editor.getDawnEditorSupport().refresh();
  }

  /**
   * @since 2.0
   */
  @Override
  public void attachingObject(CDOTransaction transaction, CDOObject object)
  {
    super.attachingObject(transaction, object);
    editor.getDawnEditorSupport().refresh();
  }

  /**
   * Edges must be adjusted because of the transience of the Node source/targetEdges CDO cannot see this because
   * removing an edges just removes the edge from the diagram. CDO just notices the change in the diagram but not in the
   * (detached) edge. The other site (node) is transient and will not be part of the notification. So I must adjust this
   * later. CDOLEgacy Wrapper breakes because it only adjusts the changes in the diagram and not the removed edge. So I
   * cannot adjust this in the Wrapper. Maybe there is another more generic way.
   */
  public void adjustDeletedEdges(final CDOViewInvalidationEvent e)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        for (CDOObject obj : e.getDetachedObjects())
        {
          final EObject view = CDOUtil.getEObject(obj);
          if (view instanceof Edge)
          {
            EditingDomain editingDomain = ((IEditingDomainProvider)view.eResource().getResourceSet()).getEditingDomain();
            editingDomain.getCommandStack().execute(new RecordingCommand((TransactionalEditingDomain)editingDomain)
            {
              @Override
              protected void doExecute()
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
            });
          }
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void handleObject(CDOObject dirtyObject)
  {
    if (dirtyObject.cdoInvalid())
    {
      return;
    }
    EObject element = CDOUtil.getEObject(dirtyObject); // either sementic object or notational

    // EditingDomain editingDomain = ((DiagramEditor)editor).getEditingDomain();
    // editingDomain.getCommandStack().execute(new RecordingCommand((TransactionalEditingDomain)editingDomain)
    // {
    // @Override
    // protected void doExecute()
    // {
    // RootEditPart rootEditPart = ((DiagramEditor)editor).getGraphicalViewer().getRootEditPart();
    // DawnDiagramUpdater.refreshEditPart(rootEditPart);
    // }
    // });

    Display.getDefault().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        RootEditPart rootEditPart = ((DiagramEditor)editor).getGraphicalViewer().getRootEditPart();
        DawnDiagramUpdater.refreshEditPart(rootEditPart);
      }
    });

    // View view = DawnDiagramUpdater.findView(element);

    if (DawnConflictHelper.isConflicted(dirtyObject))
    {
      // DawnConflictHelper.handleConflictedView(dirtyObject, view, editor);
      return;
    }

    // EditPart relatedEditPart = DawnDiagramUpdater.findEditPart(view, ((DiagramEditor)editor).getGraphicalViewer()
    // .getRootEditPart().getViewer());
    // if (relatedEditPart != null)
    // {
    // DawnDiagramUpdater.refreshEditPart(relatedEditPart.getParent());
    // }
    // else
    // {
    // if (TRACER.isEnabled())
    // {
    // TRACER.format("Updating DiagramEditPart {0} ", ((DiagramEditor)editor).getDiagramEditPart()); //$NON-NLS-1$
    // }
    // DawnDiagramUpdater.refreshEditPart(((DiagramEditor)editor).getDiagramEditPart(), (DiagramDocumentEditor)editor);
    // }
  }

  protected void refresh(CDOObject object)
  {
    DawnDiagramUpdater.refreshEditPart(((DiagramEditor)editor).getGraphicalViewer().getRootEditPart());
  }
}
