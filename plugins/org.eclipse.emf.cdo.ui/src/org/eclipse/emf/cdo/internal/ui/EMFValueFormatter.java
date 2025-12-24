/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.net4j.util.ui.views.ValueFormatter;

import org.eclipse.emf.ecore.ENamedElement;

/**
 * @author Eike Stepper
 */
public class EMFValueFormatter extends ValueFormatter
{
  public EMFValueFormatter()
  {
    super(ENamedElement.class.getName(), "ENamedElement");
  }

  @Override
  public boolean canHandle(Object value)
  {
    return value instanceof ENamedElement;
  }

  @Override
  public String formatValue(Object value) throws Exception
  {
    return ((ENamedElement)value).getName();
  }
}
