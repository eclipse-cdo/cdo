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
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.policies;

import org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.ACoreRootEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreDiagramUpdater;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreLinkDescriptor;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreNodeDescriptor;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.commands.DeferredLayoutCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.commands.SetViewMutabilityCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.CanonicalEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @generated
 */
public class ACoreRootCanonicalEditPolicy extends CanonicalEditPolicy
{

  /**
   * @generated
   */
  Set<EStructuralFeature> myFeaturesToSynchronize;

  /**
   * @generated
   */
  protected List getSemanticChildrenList()
  {
    View viewObject = (View)getHost().getModel();
    LinkedList<EObject> result = new LinkedList<EObject>();
    List<AcoreNodeDescriptor> childDescriptors = AcoreDiagramUpdater.getACoreRoot_1000SemanticChildren(viewObject);
    for (Iterator<AcoreNodeDescriptor> it = childDescriptors.iterator(); it.hasNext();)
    {
      AcoreNodeDescriptor d = it.next();
      result.add(d.getModelElement());
    }
    return result;
  }

  /**
   * @generated
   */
  protected boolean shouldDeleteView(View view)
  {
    return true;
  }

  /**
   * @generated
   */
  protected boolean isOrphaned(Collection semanticChildren, final View view)
  {
    int visualID = AcoreVisualIDRegistry.getVisualID(view);
    switch (visualID)
    {
    case AInterfaceEditPart.VISUAL_ID:
    case AClassEditPart.VISUAL_ID:
      if (!semanticChildren.contains(view.getElement()))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * @generated
   */
  protected Set getFeaturesToSynchronize()
  {
    if (myFeaturesToSynchronize == null)
    {
      myFeaturesToSynchronize = new HashSet<EStructuralFeature>();
      myFeaturesToSynchronize.add(AcorePackage.eINSTANCE.getACoreRoot_Interfaces());
      myFeaturesToSynchronize.add(AcorePackage.eINSTANCE.getACoreRoot_Classes());
    }
    return myFeaturesToSynchronize;
  }

  /**
   * @generated
   */
  protected void refreshSemantic()
  {
    if (resolveSemanticElement() == null)
    {
      return;
    }
    LinkedList<IAdaptable> createdViews = new LinkedList<IAdaptable>();
    // refreshSemanticChildren() alternative
    List<AcoreNodeDescriptor> childDescriptors = AcoreDiagramUpdater.getACoreRoot_1000SemanticChildren((View)getHost()
        .getModel());
    ArrayList<EObject> semanticChildren = new ArrayList<EObject>(childDescriptors.size());
    for (Iterator<AcoreNodeDescriptor> it = childDescriptors.iterator(); it.hasNext();)
    {
      AcoreNodeDescriptor next = it.next();
      semanticChildren.add(next.getModelElement());
    }
    List<View> orphaned = cleanCanonicalSemanticChildren(getViewChildren(), semanticChildren);
    boolean changed = deleteViews(orphaned.iterator());
    // leave descriptors that reference survived semanticChildren.
    // NOTE, we may want to stop using cleanCanonicalSemanticChildren() here, replacing with own code, that respects
    // NodeDescriptors
    for (Iterator<AcoreNodeDescriptor> it = childDescriptors.iterator(); it.hasNext();)
    {
      AcoreNodeDescriptor next = it.next();
      if (!semanticChildren.contains(next.getModelElement()))
      {
        it.remove();
      }
    }
    ArrayList<CreateViewRequest.ViewDescriptor> viewDescriptors = new ArrayList<CreateViewRequest.ViewDescriptor>(
        childDescriptors.size());
    for (Iterator<AcoreNodeDescriptor> it = childDescriptors.iterator(); it.hasNext();)
    {
      AcoreNodeDescriptor next = it.next();
      String hint = AcoreVisualIDRegistry.getType(next.getVisualID());
      IAdaptable elementAdapter = new CanonicalElementAdapter(next.getModelElement(), hint);
      viewDescriptors.add(new CreateViewRequest.ViewDescriptor(elementAdapter, Node.class, hint, ViewUtil.APPEND,
          false, host().getDiagramPreferencesHint()));
    }
    //
    CreateViewRequest request = getCreateViewRequest(viewDescriptors);
    Command cmd = getCreateViewCommand(request);
    if (cmd != null && cmd.canExecute())
    {
      SetViewMutabilityCommand.makeMutable(new EObjectAdapter(host().getNotationView())).execute();
      executeCommand(cmd);
      createdViews.addAll((List<IAdaptable>)request.getNewObject());
    }
    if (changed || createdViews.size() > 0)
    {
      postProcessRefreshSemantic(createdViews);
    }
    Collection<IAdaptable> createdConnectionViews = refreshConnections();

    if (createdViews.size() > 1)
    {
      // perform a layout of the container
      DeferredLayoutCommand layoutCmd = new DeferredLayoutCommand(host().getEditingDomain(), createdViews, host());
      executeCommand(new ICommandProxy(layoutCmd));
    }

    createdViews.addAll(createdConnectionViews);
    makeViewsImmutable(createdViews);
  }

  /**
   * @generated
   */
  private Diagram getDiagram()
  {
    return ((View)getHost().getModel()).getDiagram();
  }

  /**
   * @generated
   */
  private Collection<IAdaptable> refreshConnections()
  {
    Map<EObject, View> domain2NotationMap = new HashMap<EObject, View>();
    Collection<AcoreLinkDescriptor> linkDescriptors = collectAllLinks(getDiagram(), domain2NotationMap);
    Collection existingLinks = new LinkedList(getDiagram().getEdges());
    for (Iterator linksIterator = existingLinks.iterator(); linksIterator.hasNext();)
    {
      Edge nextDiagramLink = (Edge)linksIterator.next();
      int diagramLinkVisualID = AcoreVisualIDRegistry.getVisualID(nextDiagramLink);
      if (diagramLinkVisualID == -1)
      {
        if (nextDiagramLink.getSource() != null && nextDiagramLink.getTarget() != null)
        {
          linksIterator.remove();
        }
        continue;
      }
      EObject diagramLinkObject = nextDiagramLink.getElement();
      EObject diagramLinkSrc = nextDiagramLink.getSource().getElement();
      EObject diagramLinkDst = nextDiagramLink.getTarget().getElement();
      for (Iterator<AcoreLinkDescriptor> linkDescriptorsIterator = linkDescriptors.iterator(); linkDescriptorsIterator
          .hasNext();)
      {
        AcoreLinkDescriptor nextLinkDescriptor = linkDescriptorsIterator.next();
        if (diagramLinkObject == nextLinkDescriptor.getModelElement()
            && diagramLinkSrc == nextLinkDescriptor.getSource()
            && diagramLinkDst == nextLinkDescriptor.getDestination()
            && diagramLinkVisualID == nextLinkDescriptor.getVisualID())
        {
          linksIterator.remove();
          linkDescriptorsIterator.remove();
          break;
        }
      }
    }
    deleteViews(existingLinks.iterator());
    return createConnections(linkDescriptors, domain2NotationMap);
  }

  /**
   * @generated
   */
  private Collection<AcoreLinkDescriptor> collectAllLinks(View view, Map<EObject, View> domain2NotationMap)
  {
    if (!ACoreRootEditPart.MODEL_ID.equals(AcoreVisualIDRegistry.getModelID(view)))
    {
      return Collections.emptyList();
    }
    LinkedList<AcoreLinkDescriptor> result = new LinkedList<AcoreLinkDescriptor>();
    switch (AcoreVisualIDRegistry.getVisualID(view))
    {
    case ACoreRootEditPart.VISUAL_ID:
    {
      if (!domain2NotationMap.containsKey(view.getElement()))
      {
        result.addAll(AcoreDiagramUpdater.getACoreRoot_1000ContainedLinks(view));
      }
      if (!domain2NotationMap.containsKey(view.getElement()) || view.getEAnnotation("Shortcut") == null) { //$NON-NLS-1$
        domain2NotationMap.put(view.getElement(), view);
      }
      break;
    }
    case AInterfaceEditPart.VISUAL_ID:
    {
      if (!domain2NotationMap.containsKey(view.getElement()))
      {
        result.addAll(AcoreDiagramUpdater.getAInterface_2001ContainedLinks(view));
      }
      if (!domain2NotationMap.containsKey(view.getElement()) || view.getEAnnotation("Shortcut") == null) { //$NON-NLS-1$
        domain2NotationMap.put(view.getElement(), view);
      }
      break;
    }
    case AClassEditPart.VISUAL_ID:
    {
      if (!domain2NotationMap.containsKey(view.getElement()))
      {
        result.addAll(AcoreDiagramUpdater.getAClass_2002ContainedLinks(view));
      }
      if (!domain2NotationMap.containsKey(view.getElement()) || view.getEAnnotation("Shortcut") == null) { //$NON-NLS-1$
        domain2NotationMap.put(view.getElement(), view);
      }
      break;
    }
    }
    for (Iterator children = view.getChildren().iterator(); children.hasNext();)
    {
      result.addAll(collectAllLinks((View)children.next(), domain2NotationMap));
    }
    for (Iterator edges = view.getSourceEdges().iterator(); edges.hasNext();)
    {
      result.addAll(collectAllLinks((View)edges.next(), domain2NotationMap));
    }
    return result;
  }

  /**
   * @generated
   */
  private Collection<IAdaptable> createConnections(Collection<AcoreLinkDescriptor> linkDescriptors,
      Map<EObject, View> domain2NotationMap)
  {
    LinkedList<IAdaptable> adapters = new LinkedList<IAdaptable>();
    for (Iterator<AcoreLinkDescriptor> it = linkDescriptors.iterator(); it.hasNext();)
    {
      AcoreLinkDescriptor nextLinkDescriptor = it.next();
      EditPart sourceEditPart = getEditPart(nextLinkDescriptor.getSource(), domain2NotationMap);
      EditPart targetEditPart = getEditPart(nextLinkDescriptor.getDestination(), domain2NotationMap);
      if (sourceEditPart == null || targetEditPart == null)
      {
        continue;
      }
      CreateConnectionViewRequest.ConnectionViewDescriptor descriptor = new CreateConnectionViewRequest.ConnectionViewDescriptor(
          nextLinkDescriptor.getSemanticAdapter(), String.valueOf(nextLinkDescriptor.getVisualID()), ViewUtil.APPEND,
          false, ((IGraphicalEditPart)getHost()).getDiagramPreferencesHint());
      CreateConnectionViewRequest ccr = new CreateConnectionViewRequest(descriptor);
      ccr.setType(RequestConstants.REQ_CONNECTION_START);
      ccr.setSourceEditPart(sourceEditPart);
      sourceEditPart.getCommand(ccr);
      ccr.setTargetEditPart(targetEditPart);
      ccr.setType(RequestConstants.REQ_CONNECTION_END);
      Command cmd = targetEditPart.getCommand(ccr);
      if (cmd != null && cmd.canExecute())
      {
        executeCommand(cmd);
        IAdaptable viewAdapter = (IAdaptable)ccr.getNewObject();
        if (viewAdapter != null)
        {
          adapters.add(viewAdapter);
        }
      }
    }
    return adapters;
  }

  /**
   * @generated
   */
  private EditPart getEditPart(EObject domainModelElement, Map<EObject, View> domain2NotationMap)
  {
    View view = (View)domain2NotationMap.get(domainModelElement);
    if (view != null)
    {
      return (EditPart)getHost().getViewer().getEditPartRegistry().get(view);
    }
    return null;
  }
}
