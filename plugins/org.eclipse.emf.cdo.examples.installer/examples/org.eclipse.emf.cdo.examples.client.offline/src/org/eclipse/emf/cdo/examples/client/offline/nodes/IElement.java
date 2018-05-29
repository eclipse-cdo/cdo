/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.client.offline.nodes;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public interface IElement
{
  public Properties getSettings();

  public void showSettings();

  public Image getImage();

  public Composite getDetailsControl();

}
