/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.ui;

import org.eclipse.net4j.buddies.internal.ui.views.CollaborationsPane;
import org.eclipse.net4j.buddies.internal.ui.views.FacilityPane;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Eike Stepper
 */
public interface IFacilityPaneCreator
{
  public String getType();

  public ImageDescriptor getImageDescriptor();

  public FacilityPane createPane(CollaborationsPane collaborationsPane, int style);
}
