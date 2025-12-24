/*
 * Copyright (c) 2007, 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import java.util.Iterator;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CrossReferencesPackageClosure extends PackageClosure
{
  public CrossReferencesPackageClosure()
  {
  }

  @Override
  protected void handleEPackage(EPackage ePackage, Set<EPackage> visited)
  {
    visited.add(ePackage);
    for (Iterator<EObject> it = ePackage.eAllContents(); it.hasNext();)
    {
      EObject content = it.next();
      collectCrossReferences(content, visited);
    }
  }

  protected void collectCrossReferences(EObject content, Set<EPackage> visited)
  {
    EList<EObject> crossReferences = content.eCrossReferences();
    for (EObject crossReference : crossReferences)
    {
      EPackage crossReferencePackage = crossReference.eClass().getEPackage();
      if (!visited.contains(crossReferencePackage))
      {
        handleEPackage(crossReferencePackage, visited);
      }
    }
  }
}
