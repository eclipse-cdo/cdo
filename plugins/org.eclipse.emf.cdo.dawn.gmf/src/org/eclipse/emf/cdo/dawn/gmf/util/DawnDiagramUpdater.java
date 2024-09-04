/*
 * Copyright (c) 2011-2013, 2015, 2016, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *     Christian W. Damus (CEA) - bug 399733: tolerate UML CacheAdapter
 */
package org.eclipse.emf.cdo.dawn.gmf.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.dawn.internal.util.bundle.OM;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.CanonicalEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.diagram.ui.services.editpart.EditPartService;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author Martin Fluegge
 */
public class DawnDiagramUpdater
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnDiagramUpdater.class);

  public static void refresh(final IGraphicalEditPart editPart)
  {
    if (editPart != null)
    {
      TransactionalEditingDomain editingDomain = editPart.getEditingDomain();
      editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain)
      {
        @Override
        public void doExecute()
        {
          editPart.refresh();

          if (editPart instanceof ConnectionEditPart)
          {
            ConnectionEditPart connectionEditPart = (ConnectionEditPart)editPart;

            DawnDiagramUpdater.refresh((IGraphicalEditPart)connectionEditPart.getTarget());
            DawnDiagramUpdater.refresh((IGraphicalEditPart)connectionEditPart.getSource());
          }
        }
      });
    }
  }

  public static void refreshEditPart(EditPart editPart)
  {
    refeshEditpartInternal(editPart);
  }

  public static void refreshEditPart(final EditPart editPart, DiagramDocumentEditor editor)
  {
    if (editPart != null)
    {
      try
      {
        editor.getEditingDomain().runExclusive(new Runnable()
        {
          @Override
          public void run()
          {
            DawnDiagramUpdater.refreshEditPart(editPart);
          }
        });
      }
      catch (InterruptedException ex)
      {
        throw new RuntimeException(ex);
      }
    }
  }

  public static void refreshEditCurrentSelected(TransactionalEditingDomain editingDomain)
  {
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain)
    {
      @Override
      protected void doExecute()
      {
        // ((ExamplediagramDocumentProvider)getDocumentProvider()).changed(getEditorInput());
        ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
        if (selection instanceof IStructuredSelection)
        {
          IStructuredSelection structuredSelection = (IStructuredSelection)selection;
          if (structuredSelection.size() != 1)
          {
            return;
          }

          if (structuredSelection.getFirstElement() instanceof EditPart)
          {
            Object model = ((EditPart)structuredSelection.getFirstElement()).getModel();
            if (model instanceof View)
            {
              EObject modelElement = ((View)model).getElement();
              List<?> editPolicies = CanonicalEditPolicy.getRegisteredEditPolicies(modelElement);
              for (Iterator<?> it = editPolicies.iterator(); it.hasNext();)
              {
                CanonicalEditPolicy nextEditPolicy = (CanonicalEditPolicy)it.next();
                nextEditPolicy.refresh();
              }
            }
          }
        }
      }
    });
  }

  private static void refeshEditpartInternal(EditPart editPart)
  {
    if (editPart != null)
    {
      try
      {
        // EObject modelElement = ((View)(editPart).getModel()).getElement();
        // List editPolicies = CanonicalEditPolicy.getRegisteredEditPolicies(modelElement);
        // for (Iterator it = editPolicies.iterator(); it.hasNext();)
        // {
        // CanonicalEditPolicy nextEditPolicy = (CanonicalEditPolicy)it.next();
        // nextEditPolicy.refresh();
        // }

        if (hasValidModel(editPart))
        {
          editPart.refresh();
        }
      }
      catch (Exception e)
      {
        //$FALL-THROUGH$
      }

      if (editPart instanceof DiagramEditPart)
      {
        for (Object connectionEditPart : ((DiagramEditPart)editPart).getConnections())
        {
          if (connectionEditPart instanceof EditPart)
          {
            Object model = ((EditPart)connectionEditPart).getModel();
            if (model instanceof Edge)
            {
              if (((Edge)model).getBendpoints() != null)
              {
                refeshEditpartInternal((EditPart)connectionEditPart);
              }
            }
          }
        }
      }

      for (Object childEditPart : editPart.getChildren())
      {
        if (childEditPart instanceof EditPart)
        {
          refeshEditpartInternal((EditPart)childEditPart);
        }
      }
    }
  }

  private static boolean hasValidModel(EditPart editPart)
  {
    Object model = editPart.getModel();
    if (model instanceof EObject)
    {
      CDOObject cdoObject = CDOUtil.getCDOObject((EObject)model, false);
      if (cdoObject.cdoInvalid())
      {
        return false;
      }
    }

    return true;
  }

  public static View findViewByContainer(EObject element)
  {
    if (element instanceof View)
    {
      return (View)element;
    }

    EObject eContainer = element.eContainer();
    if (eContainer == null)
    {
      return null;
    }

    if (eContainer instanceof View)
    {
      return (View)eContainer;
    }

    return findViewByContainer(eContainer);
  }

  public static View findViewForModel(EObject object, DiagramDocumentEditor editor)
  {
    if (object == null || editor == null)
    {
      return null;
    }

    for (EObject e : editor.getDiagram().eContents())
    {
      if (e instanceof View)
      {
        View view = (View)e;
        if (object.equals(view.getElement()))
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("FOUND View: {0} for view obj: {1} ", e, object); //$NON-NLS-1$
          }
        }

        return (View)e;
      }
    }

    return null;
  }

  public static View getViewFromObject(EObject element, DiagramDocumentEditor editor)
  {
    View view;

    if (element instanceof Diagram)
    {
      view = ViewUtil.getViewContainer(element);
      return view;
    }

    if (element instanceof View)
    {
      view = (View)element;
    }
    else
    {
      view = DawnDiagramUpdater.findViewByContainer(element); // something which is not view (Edge or Node)
      if (view == null)
      {
        view = DawnDiagramUpdater.findViewForModel(element, editor);
      }
    }

    return view;
  }

  public static EditPart createOrFindEditPartIfViewExists(View view, DiagramDocumentEditor editor)
  {
    EditPart editPart = editor == null ? null : findEditPart(view, editor.getDiagramEditPart());

    if (view != null)
    {
      if (editPart == null) // does not exist?
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("EditPart does not exist for view  {0} ", view); //$NON-NLS-1$
        }

        editPart = EditPartService.getInstance().createGraphicEditPart(view);
        setParentEditPart(editor, view, editPart);
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Found EditPart:  {0} ", editPart); //$NON-NLS-1$
    }

    return editPart;
  }

  public static void setParentEditPart(final DiagramDocumentEditor editor, View view, EditPart editPart)
  {
    View viewParent = (View)view.eContainer();
    EditPart parentEditPart = findEditPart(viewParent, editor);
    if (parentEditPart == null)
    {
      parentEditPart = editor.getDiagramEditPart();
    }

    editPart.setParent(parentEditPart);
  }

  public static EditPart findEditPart(View view, DiagramDocumentEditor dawnDiagramEditor)
  {
    if (dawnDiagramEditor == null)
    {
      return null;
    }

    DiagramEditPart diagramEditPart = dawnDiagramEditor.getDiagramEditPart();

    for (Object e : diagramEditPart.getChildren())
    {
      EditPart ep = (EditPart)e;
      if (Objects.equals(ep.getModel(), view))
      {
        return ep;
      }
    }

    for (Object e : diagramEditPart.getConnections())
    {
      EditPart ep = (EditPart)e;
      if (Objects.equals(ep.getModel(), view))
      {
        return ep;
      }
    }

    return null;
  }

  public static EditPart findEditPart(View view, EditPart editPart)
  {
    if (editPart == null)
    {
      return null;
    }

    if (Objects.equals(editPart.getModel(), view))
    {
      return editPart;
    }

    for (Object o : editPart.getChildren())
    {
      EditPart child = (EditPart)o;

      EditPart ret = findEditPart(view, child);
      if (ret != null)
      {
        return ret;
      }
    }

    if (editPart instanceof DiagramEditPart)
    {
      for (Object o : ((DiagramEditPart)editPart).getConnections())
      {
        EditPart child = (EditPart)o;

        EditPart ret = findEditPart(view, child);
        if (ret != null)
        {
          return ret;
        }
      }
    }

    return null;
  }

  public static EditPart findEditPart(View view, EditPartViewer viewer)
  {
    return viewer.getEditPartRegistry().get(view);
  }

  /**
   * In a normal GMF environment the diagram is loaded from a xml resource. In this scenario the XMLHelper sets "null"
   * to the element of edge. Thus the value is 'set' with a null value. We do not have this in our case because the
   * element is carefully loaded from the CDO repository. But if the value is not set a getElement() call fills the
   * element with the eContainer. See <b>org.eclipse.gmf.runtime.notation.impl.ViewImpl.isSetElement()</b>. This happens
   * when the ConnectionEditPart is activated and the model registered. See
   * <b>org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart.registerModel()</b>
   * <p>
   * In our scenario the Edges will be wrongly connected to the diagram itself, which makes the CanonicalEditingPolicy
   * to remove and restore the edge. Long, short story, we must 'touch' the elements here to have the element set.
   */
  public static void initializeElement(Diagram diagram)
  {
    for (Object obj : diagram.getEdges())
    {
      Edge edge = (Edge)obj;
      if (!edge.isSetElement())
      {
        boolean eDeliver = edge.eDeliver();
        edge.eSetDeliver(false);
        edge.setElement(null);
        edge.eSetDeliver(eDeliver);
      }
    }
  }

  public static View findViewFromCrossReferences(EObject element)
  {
    ECrossReferenceAdapter crossreferenceAdapter = ECrossReferenceAdapter.getCrossReferenceAdapter(element);
    if (crossreferenceAdapter != null)
    {
      Collection<EStructuralFeature.Setting> iinverseReferences = crossreferenceAdapter.getNonNavigableInverseReferences(element);

      for (EStructuralFeature.Setting f : iinverseReferences)
      {
        if (f.getEStructuralFeature() == NotationPackage.Literals.VIEW__ELEMENT)
        {
          return (View)f.getEObject();
        }
      }
    }

    return null;
  }

  public static View findView(EObject element)
  {
    View view = DawnDiagramUpdater.findViewByContainer(element);
    if (view == null)
    {
      view = DawnDiagramUpdater.findViewFromCrossReferences(element);
    }

    return view;
  }
}
