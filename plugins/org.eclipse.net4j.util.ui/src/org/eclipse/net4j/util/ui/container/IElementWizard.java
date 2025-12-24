/*
 * Copyright (c) 2010-2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.container;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.ui.ValidationContext;

import org.eclipse.swt.widgets.Composite;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public interface IElementWizard
{
  public void create(Composite parent, IManagedContainer container, String productGroup, String factoryType, String defaultDescription,
      ValidationContext context);

  public String getResultDescription();

  public Object getResultElement();
}
