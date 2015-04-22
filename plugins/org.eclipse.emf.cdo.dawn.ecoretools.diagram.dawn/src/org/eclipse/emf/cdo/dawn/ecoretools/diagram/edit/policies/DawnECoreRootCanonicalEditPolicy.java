/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ecoretools.diagram.edit.policies;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecoretools.diagram.edit.policies.EPackageCanonicalEditPolicy;
import org.eclipse.emf.ecoretools.diagram.part.EcoreDiagramEditorPlugin;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest.ViewDescriptor;
import org.eclipse.gmf.runtime.notation.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Martin Fluegge
 */
public class DawnECoreRootCanonicalEditPolicy extends EPackageCanonicalEditPolicy
{

  public DawnECoreRootCanonicalEditPolicy()
  {
    super();
    EcoreDiagramEditorPlugin.getInstance().logInfo("Running DawnEcoreCanonicalEditPolicy instead of original one");
  }

  @Override
  protected CreateViewRequest getCreateViewRequest(List<ViewDescriptor> descriptors)
  {
    List<View> viewChildren = getViewChildren();

    List<ViewDescriptor> tbr = new ArrayList<CreateViewRequest.ViewDescriptor>();

    for (ViewDescriptor desc : descriptors)
    {
      EObject obj = (EObject)((CanonicalElementAdapter)desc.getElementAdapter()).getRealObject();

      boolean found = false;

      for (View view : viewChildren)
      {
        if (view.getElement().equals(obj))
        {
          found = true;
          break;
        }
      }
      if (!found)
      {
        tbr.add(desc);
      }
    }

    descriptors.removeAll(tbr);

    return new CreateViewRequest(descriptors);
  }

  @Override
  protected void refreshSemantic()
  {
    deleteOrphanedViews();
    List<IAdaptable> createdConnectionViews = new LinkedList<IAdaptable>();
    createdConnectionViews.addAll(refreshSemanticConnections());
    // createdConnectionViews.addAll(refreshConnections());

    makeViewsImmutable(createdConnectionViews);
  }
}
