/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.graphiti.util;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

/**
 * @author Martin Fluegge
 */
public class AcoreImageProvider extends AbstractImageProvider
{
  protected static final String PREFIX = "org.eclipse.graphiti.examples.tutorial."; //$NON-NLS-1$

  public static final String IMG_EREFERENCE = PREFIX + "ereference"; //$NON-NLS-1$

  @Override
  protected void addAvailableImages()
  {
    addImageFilePath(IMG_EREFERENCE, "icons/ereference.gif"); //$NON-NLS-1$
  }
}
