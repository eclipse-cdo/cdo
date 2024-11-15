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

import org.eclipse.net4j.util.container.ContainerElementList;

/**
 * @author Eike Stepper
 * @since 1.2
 */
public final class ModelReferenceExtractorRegistry extends ContainerElementList<Extractor> implements Registry
{
  public static final ModelReferenceExtractorRegistry INSTANCE = new ModelReferenceExtractorRegistry();

  private ModelReferenceExtractorRegistry()
  {
    super(Extractor.class);
    initContainerElements(ModelReference.Extractor.PRODUCT_GROUP);
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
    Extractor[] extractors = getExtractors();
    for (Extractor extractor : extractors)
    {
      ModelReference modelReference = extractor.extractModelReference(object);
      if (modelReference != null)
      {
        return modelReference;
      }
    }

    return null;
  }
}
