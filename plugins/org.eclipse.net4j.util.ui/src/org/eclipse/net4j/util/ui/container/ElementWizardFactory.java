/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.container;

import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public abstract class ElementWizardFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.ui.elementWizards"; //$NON-NLS-1$

  public ElementWizardFactory(String elementProductGroup, String elementFactoryType)
  {
    super(PRODUCT_GROUP, elementProductGroup + ":" + elementFactoryType);
  }

  @Override
  public abstract IElementWizard create(String description) throws ProductCreationException;
}
