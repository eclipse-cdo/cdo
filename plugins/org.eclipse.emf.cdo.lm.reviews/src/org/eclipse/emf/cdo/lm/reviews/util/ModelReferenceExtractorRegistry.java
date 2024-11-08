/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.util;

import org.eclipse.emf.cdo.lm.reviews.ModelReference;
import org.eclipse.emf.cdo.lm.reviews.ModelReference.Extractor;
import org.eclipse.emf.cdo.lm.reviews.ModelReference.Extractor.Registry;

import org.eclipse.net4j.util.container.PrioritizingElementList;

/**
 * @author Eike Stepper
 * @since 1.2
 */
public class ModelReferenceExtractorRegistry extends PrioritizingElementList<Extractor> implements Registry
{
  public static final ModelReferenceExtractorRegistry INSTANCE = new ModelReferenceExtractorRegistry();

  private ModelReferenceExtractorRegistry()
  {
    super(PRODUCT_GROUP, Extractor.class);
    activate();
  }

  @Override
  public int getPriority()
  {
    return Integer.MAX_VALUE;
  }

  @Override
  public Extractor[] getExtractors()
  {
    return getElements();
  }

  @Override
  public ModelReference extractModelReference(Object object)
  {
    for (Extractor extractor : getExtractors())
    {
      ModelReference modelReference = extractor.extractModelReference(object);
      if (modelReference != null)
      {
        return modelReference;
      }
    }

    return null;
  }

  @Override
  protected Extractor[] createElementArray(int length)
  {
    return new Extractor[length];
  }
}
