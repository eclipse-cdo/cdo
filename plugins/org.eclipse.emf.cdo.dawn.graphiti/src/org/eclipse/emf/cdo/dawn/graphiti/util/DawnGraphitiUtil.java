/*
 * Copyright (c) 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.graphiti.util;

import org.eclipse.emf.cdo.dawn.preferences.PreferenceConstants;
import org.eclipse.emf.cdo.dawn.transaction.DawnTransactionalEditingDomainImpl;
import org.eclipse.emf.cdo.dawn.util.connection.CDOConnectionUtil;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;

import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

import java.util.Collections;
import java.util.List;

/**
 * @author Martin Fluegge
 */
public class DawnGraphitiUtil
{
  public static TransactionalEditingDomain createResourceSetAndEditingDomain()
  {
    // TODO check if this is still needed here
    CDOConnectionUtil.instance.init(PreferenceConstants.getRepositoryName(), PreferenceConstants.getProtocol(), PreferenceConstants.getServerName());
    CDOConnectionUtil.instance.getCurrentSession();

    final ResourceSet resourceSet = new ResourceSetImpl();

    @SuppressWarnings("restriction")
    final TransactionalCommandStack workspaceCommandStack = new org.eclipse.graphiti.ui.internal.editor.GFWorkspaceCommandStackImpl(
        new DefaultOperationHistory());

    final TransactionalEditingDomainImpl editingDomain = new DawnTransactionalEditingDomainImpl(
        new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE), workspaceCommandStack, resourceSet);
    WorkspaceEditingDomainFactory.INSTANCE.mapResourceSet(editingDomain);
    return editingDomain;
  }

  /**
   * This method tries to find an editpart for a given pictogram element. It recursivly searches all children for the
   * given editpart if the model matches to pictogramElement.
   */
  public static EditPart getEditpart(PictogramElement pictogramElement, EditPart part)
  {
    for (Object object : part.getChildren())
    {
      EditPart child = (EditPart)object;
      if (child.getModel().equals(pictogramElement))
      {
        return child;
      }

      EditPart childEditPart = getEditpart(pictogramElement, child);
      if (childEditPart != null)
      {
        return childEditPart;
      }
    }
    return null;
  }

  public static List<PictogramElement> getPictgramElements(Diagram diagram, EObject element)
  {
    PictogramElement pictgramElement = getPictgramElement(element);

    if (element instanceof PictogramElement)
    {
      return Collections.singletonList((PictogramElement)element);
    }

    if (pictgramElement != null)
    {
      return Collections.singletonList(pictgramElement);
    }

    return Graphiti.getLinkService().getPictogramElements(diagram, element);
  }

  /**
   * Tries to retriev the pictogram element from a given element. If the element itself is a PictogramElement, the
   * element will be returned. Otherwise all eContainers will be checked until a PictogramElement is found.
   */
  public static PictogramElement getPictgramElement(EObject element)
  {
    if (element == null)
    {
      return null;
    }

    if (element instanceof PictogramElement)
    {
      return (PictogramElement)element;
    }

    EObject eContainer = element.eContainer();

    if (eContainer instanceof PictogramElement)
    {
      return (PictogramElement)eContainer;
    }
    return getPictgramElement(eContainer);
  }
}
