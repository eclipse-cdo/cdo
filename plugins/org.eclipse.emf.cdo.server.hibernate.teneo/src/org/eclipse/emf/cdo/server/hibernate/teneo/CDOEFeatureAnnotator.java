/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.hibernate.teneo;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.teneo.annotations.mapper.EFeatureAnnotator;
import org.eclipse.emf.teneo.annotations.pamodel.PAnnotatedEReference;
import org.eclipse.emf.teneo.annotations.pamodel.PAnnotatedEStructuralFeature;
import org.eclipse.emf.teneo.annotations.pannotation.PannotationFactory;

/**
 * Extends the Teneo EFeatureAnnotator to add an external annotation to each EReference to an EModelElement.
 * 
 * @author Martin Taal
 * @since 3.0
 */
public class CDOEFeatureAnnotator extends EFeatureAnnotator
{
  public CDOEFeatureAnnotator()
  {
  }

  @Override
  public void annotate(PAnnotatedEStructuralFeature aStructuralFeature)
  {
    super.annotate(aStructuralFeature);

    // now determine if it needs to be annotated with External
    if (aStructuralFeature instanceof PAnnotatedEReference)
    {
      final PAnnotatedEReference paReference = (PAnnotatedEReference)aStructuralFeature;
      final boolean refersToEcoreModelElement = paReference.getModelEReference().getEReferenceType().getEPackage() == EcorePackage.eINSTANCE;

      // these are done with a <any ..> mapping
      final boolean refersToEObject = paReference.getModelEReference().getEReferenceType() == EcorePackage.eINSTANCE
          .getEObject();
      final boolean isPartOfEcoreModel = paReference.getModelEReference().getEContainingClass().getEPackage() == EcorePackage.eINSTANCE;
      if (refersToEcoreModelElement && !isPartOfEcoreModel && !refersToEObject)
      {
        paReference.setExternal(PannotationFactory.eINSTANCE.createExternal());
      }
    }
  }
}
