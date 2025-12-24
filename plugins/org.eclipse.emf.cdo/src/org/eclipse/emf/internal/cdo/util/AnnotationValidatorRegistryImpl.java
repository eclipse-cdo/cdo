/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.etypes.AnnotationValidator;

import org.eclipse.net4j.util.container.IPluginContainer;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class AnnotationValidatorRegistryImpl implements AnnotationValidator.Registry
{
  public AnnotationValidatorRegistryImpl()
  {
  }

  @Override
  public Set<String> getAnnotationSources()
  {
    return IPluginContainer.INSTANCE.getFactoryTypes(PRODUCT_GROUP);
  }

  @Override
  public AnnotationValidator getAnnotationValidator(String annotationSource)
  {
    return IPluginContainer.INSTANCE.getElementOrNull(PRODUCT_GROUP, annotationSource);
  }
}
