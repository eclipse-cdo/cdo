/*
 * Copyright (c) 2012, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.jface.resource.DeviceResourceDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.LabelProvider;

/**
 * @author Eike Stepper
 * @since 3.3
 */
public class ManagedLabelProvider extends LabelProvider
{
  private ResourceManager resourceManager;

  public ManagedLabelProvider()
  {
  }

  @Override
  public void dispose()
  {
    if (resourceManager != null)
    {
      resourceManager.dispose();
      resourceManager = null;
    }

    super.dispose();
  }

  public <R> R getResource(DeviceResourceDescriptor<R> descriptor)
  {
    ResourceManager resourceManager = getResourceManager();
    return resourceManager.get(descriptor);
  }

  public final ResourceManager getResourceManager()
  {
    if (resourceManager == null)
    {
      resourceManager = createResourceManager();
    }

    return resourceManager;
  }

  protected LocalResourceManager createResourceManager()
  {
    return new LocalResourceManager(JFaceResources.getResources());
  }
}
