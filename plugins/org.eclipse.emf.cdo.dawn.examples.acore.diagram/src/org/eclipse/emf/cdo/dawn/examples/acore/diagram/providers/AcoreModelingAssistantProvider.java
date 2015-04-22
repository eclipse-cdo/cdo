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
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.providers;

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.ACoreRootEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreDiagramEditorPlugin;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.Messages;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.emf.type.core.ElementTypeRegistry;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.ui.services.modelingassistant.ModelingAssistantProvider;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @generated
 */
public class AcoreModelingAssistantProvider extends ModelingAssistantProvider
{

  /**
   * @generated
   */
  @Override
  public List getTypesForPopupBar(IAdaptable host)
  {
    IGraphicalEditPart editPart = host.getAdapter(IGraphicalEditPart.class);
    if (editPart instanceof ACoreRootEditPart)
    {
      ArrayList<IElementType> types = new ArrayList<IElementType>(2);
      types.add(AcoreElementTypes.AInterface_2001);
      types.add(AcoreElementTypes.AClass_2002);
      return types;
    }
    if (editPart instanceof AInterfaceEditPart)
    {
      ArrayList<IElementType> types = new ArrayList<IElementType>(2);
      types.add(AcoreElementTypes.AAttribute_3001);
      types.add(AcoreElementTypes.AOperation_3002);
      return types;
    }
    if (editPart instanceof AClassEditPart)
    {
      ArrayList<IElementType> types = new ArrayList<IElementType>(2);
      types.add(AcoreElementTypes.AAttribute_3003);
      types.add(AcoreElementTypes.AOperation_3004);
      return types;
    }
    return Collections.EMPTY_LIST;
  }

  /**
   * @generated
   */
  @Override
  public List getRelTypesOnSource(IAdaptable source)
  {
    IGraphicalEditPart sourceEditPart = source.getAdapter(IGraphicalEditPart.class);
    if (sourceEditPart instanceof AClassEditPart)
    {
      return ((AClassEditPart)sourceEditPart).getMARelTypesOnSource();
    }
    return Collections.EMPTY_LIST;
  }

  /**
   * @generated
   */
  @Override
  public List getRelTypesOnTarget(IAdaptable target)
  {
    IGraphicalEditPart targetEditPart = target.getAdapter(IGraphicalEditPart.class);
    if (targetEditPart instanceof AInterfaceEditPart)
    {
      return ((AInterfaceEditPart)targetEditPart).getMARelTypesOnTarget();
    }
    if (targetEditPart instanceof AClassEditPart)
    {
      return ((AClassEditPart)targetEditPart).getMARelTypesOnTarget();
    }
    return Collections.EMPTY_LIST;
  }

  /**
   * @generated
   */
  @Override
  public List getRelTypesOnSourceAndTarget(IAdaptable source, IAdaptable target)
  {
    IGraphicalEditPart sourceEditPart = source.getAdapter(IGraphicalEditPart.class);
    IGraphicalEditPart targetEditPart = target.getAdapter(IGraphicalEditPart.class);
    if (sourceEditPart instanceof AClassEditPart)
    {
      return ((AClassEditPart)sourceEditPart).getMARelTypesOnSourceAndTarget(targetEditPart);
    }
    return Collections.EMPTY_LIST;
  }

  /**
   * @generated
   */
  @Override
  public List getTypesForSource(IAdaptable target, IElementType relationshipType)
  {
    IGraphicalEditPart targetEditPart = target.getAdapter(IGraphicalEditPart.class);
    if (targetEditPart instanceof AInterfaceEditPart)
    {
      return ((AInterfaceEditPart)targetEditPart).getMATypesForSource(relationshipType);
    }
    if (targetEditPart instanceof AClassEditPart)
    {
      return ((AClassEditPart)targetEditPart).getMATypesForSource(relationshipType);
    }
    return Collections.EMPTY_LIST;
  }

  /**
   * @generated
   */
  @Override
  public List getTypesForTarget(IAdaptable source, IElementType relationshipType)
  {
    IGraphicalEditPart sourceEditPart = source.getAdapter(IGraphicalEditPart.class);
    if (sourceEditPart instanceof AClassEditPart)
    {
      return ((AClassEditPart)sourceEditPart).getMATypesForTarget(relationshipType);
    }
    return Collections.EMPTY_LIST;
  }

  /**
   * @generated
   */
  @Override
  public EObject selectExistingElementForSource(IAdaptable target, IElementType relationshipType)
  {
    return selectExistingElement(target, getTypesForSource(target, relationshipType));
  }

  /**
   * @generated
   */
  @Override
  public EObject selectExistingElementForTarget(IAdaptable source, IElementType relationshipType)
  {
    return selectExistingElement(source, getTypesForTarget(source, relationshipType));
  }

  /**
   * @generated
   */
  protected EObject selectExistingElement(IAdaptable host, Collection types)
  {
    if (types.isEmpty())
    {
      return null;
    }
    IGraphicalEditPart editPart = host.getAdapter(IGraphicalEditPart.class);
    if (editPart == null)
    {
      return null;
    }
    Diagram diagram = (Diagram)editPart.getRoot().getContents().getModel();
    HashSet<EObject> elements = new HashSet<EObject>();
    for (Iterator<EObject> it = diagram.getElement().eAllContents(); it.hasNext();)
    {
      EObject element = it.next();
      if (isApplicableElement(element, types))
      {
        elements.add(element);
      }
    }
    if (elements.isEmpty())
    {
      return null;
    }
    return selectElement(elements.toArray(new EObject[elements.size()]));
  }

  /**
   * @generated
   */
  protected boolean isApplicableElement(EObject element, Collection types)
  {
    IElementType type = ElementTypeRegistry.getInstance().getElementType(element);
    return types.contains(type);
  }

  /**
   * @generated
   */
  protected EObject selectElement(EObject[] elements)
  {
    Shell shell = Display.getCurrent().getActiveShell();
    ILabelProvider labelProvider = new AdapterFactoryLabelProvider(
        AcoreDiagramEditorPlugin.getInstance().getItemProvidersAdapterFactory());
    ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, labelProvider);
    dialog.setMessage(Messages.AcoreModelingAssistantProviderMessage);
    dialog.setTitle(Messages.AcoreModelingAssistantProviderTitle);
    dialog.setMultipleSelection(false);
    dialog.setElements(elements);
    EObject selected = null;
    if (dialog.open() == Window.OK)
    {
      selected = (EObject)dialog.getFirstResult();
    }
    return selected;
  }
}
