/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.eresource.CDOResourceNode;

import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public abstract class ResourceNodeAction extends LongRunningAction
{
  private CDOResourceNode resourceNode;

  public ResourceNodeAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image, CDOResourceNode resourceNode)
  {
    super(page, text, toolTipText, image);
    this.resourceNode = resourceNode;
  }

  public CDOResourceNode getResourceNode()
  {
    return resourceNode;
  }
}
