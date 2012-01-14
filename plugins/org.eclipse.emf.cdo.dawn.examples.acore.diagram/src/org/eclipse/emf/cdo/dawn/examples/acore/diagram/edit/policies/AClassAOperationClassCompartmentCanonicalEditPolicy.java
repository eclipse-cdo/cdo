/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AOperation2EditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreDiagramUpdater;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreNodeDescriptor;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.gmf.runtime.diagram.ui.editpolicies.CanonicalEditPolicy;
import org.eclipse.gmf.runtime.notation.View;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @generated
 */
public class AClassAOperationClassCompartmentCanonicalEditPolicy extends CanonicalEditPolicy
{

  /**
   * @generated
   */
  protected List getSemanticChildrenList()
  {
    View viewObject = (View)getHost().getModel();
    LinkedList<EObject> result = new LinkedList<EObject>();
    List<AcoreNodeDescriptor> childDescriptors = AcoreDiagramUpdater
        .getAClassAOperationClassCompartment_7004SemanticChildren(viewObject);
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
  protected boolean isOrphaned(Collection semanticChildren, final View view)
  {
    int visualID = AcoreVisualIDRegistry.getVisualID(view);
    switch (visualID)
    {
    case AOperation2EditPart.VISUAL_ID:
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
  protected EStructuralFeature getFeatureToSynchronize()
  {
    return AcorePackage.eINSTANCE.getABasicClass_Operations();
  }

}
