/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.editors;

import org.eclipse.emf.cdo.dawn.spi.IDawnUIElement;

import org.eclipse.ui.IEditorPart;

/**
 * @author Martin Fluegge
 */
public interface IDawnEditor extends IDawnUIElement, IEditorPart
{
  public String getContributorID();
}
