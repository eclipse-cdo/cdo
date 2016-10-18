/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
