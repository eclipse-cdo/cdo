/*
 * Copyright (c) 2010, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 * 
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.navigator;

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.ACoreRootEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreDiagramEditor;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreDiagramEditorPlugin;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.Messages;

import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.eclipse.ui.part.FileEditorInput;

import java.util.Iterator;

/**
 * @generated
 */
public class AcoreNavigatorActionProvider extends CommonActionProvider
{

  /**
   * @generated
   */
  private boolean myContribute;

  /**
   * @generated
   */
  private OpenDiagramAction myOpenDiagramAction;

  /**
   * @generated
   */
  public void init(ICommonActionExtensionSite aSite)
  {
    super.init(aSite);
    if (aSite.getViewSite() instanceof ICommonViewerWorkbenchSite)
    {
      myContribute = true;
      makeActions((ICommonViewerWorkbenchSite)aSite.getViewSite());
    }
    else
    {
      myContribute = false;
    }
  }

  /**
   * @generated
   */
  private void makeActions(ICommonViewerWorkbenchSite viewerSite)
  {
    myOpenDiagramAction = new OpenDiagramAction(viewerSite);
  }

  /**
   * @generated
   */
  public void fillActionBars(IActionBars actionBars)
  {
    if (!myContribute)
    {
      return;
    }
    IStructuredSelection selection = (IStructuredSelection)getContext().getSelection();
    myOpenDiagramAction.selectionChanged(selection);
    if (myOpenDiagramAction.isEnabled())
    {
      actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, myOpenDiagramAction);
    }
  }

  /**
   * @generated
   */
  public void fillContextMenu(IMenuManager menu)
  {
  }

  /**
   * @generated
   */
  private class OpenDiagramAction extends Action
  {

    /**
     * @generated
     */
    private Diagram myDiagram;

    /**
     * @generated
     */
    private ICommonViewerWorkbenchSite myViewerSite;

    /**
     * @generated
     */
    public OpenDiagramAction(ICommonViewerWorkbenchSite viewerSite)
    {
      super(Messages.NavigatorActionProvider_OpenDiagramActionName);
      myViewerSite = viewerSite;
    }

    /**
     * @generated
     */
    public final void selectionChanged(IStructuredSelection selection)
    {
      myDiagram = null;
      if (selection.size() == 1)
      {
        Object selectedElement = selection.getFirstElement();
        if (selectedElement instanceof AcoreNavigatorItem)
        {
          selectedElement = ((AcoreNavigatorItem)selectedElement).getView();
        }
        else if (selectedElement instanceof IAdaptable)
        {
          selectedElement = ((IAdaptable)selectedElement).getAdapter(View.class);
        }
        if (selectedElement instanceof Diagram)
        {
          Diagram diagram = (Diagram)selectedElement;
          if (ACoreRootEditPart.MODEL_ID.equals(AcoreVisualIDRegistry.getModelID(diagram)))
          {
            myDiagram = diagram;
          }
        }
      }
      setEnabled(myDiagram != null);
    }

    /**
     * @generated
     */
    public void run()
    {
      if (myDiagram == null || myDiagram.eResource() == null)
      {
        return;
      }

      IEditorInput editorInput = getEditorInput();
      IWorkbenchPage page = myViewerSite.getPage();
      try
      {
        page.openEditor(editorInput, AcoreDiagramEditor.ID);
      }
      catch (PartInitException e)
      {
        AcoreDiagramEditorPlugin.getInstance().logError("Exception while openning diagram", e); //$NON-NLS-1$
      }
    }

    /**
     * @generated
     */
    private IEditorInput getEditorInput()
    {
      for (Iterator it = myDiagram.eResource().getContents().iterator(); it.hasNext();)
      {
        EObject nextEObject = (EObject)it.next();
        if (nextEObject == myDiagram)
        {
          return new FileEditorInput(WorkspaceSynchronizer.getFile(myDiagram.eResource()));
        }
        if (nextEObject instanceof Diagram)
        {
          break;
        }
      }
      URI uri = EcoreUtil.getURI(myDiagram);
      String editorName = uri.lastSegment() + "#" + myDiagram.eResource().getContents().indexOf(myDiagram); //$NON-NLS-1$
      IEditorInput editorInput = new URIEditorInput(uri, editorName);
      return editorInput;
    }

  }

}
