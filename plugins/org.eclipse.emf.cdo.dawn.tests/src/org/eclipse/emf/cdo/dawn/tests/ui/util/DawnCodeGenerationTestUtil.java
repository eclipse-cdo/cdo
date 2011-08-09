/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.ui.util;

import org.eclipse.core.resources.IProject;

/**
 * @author Martin Fluegge
 */
public class DawnCodeGenerationTestUtil
{
  public static boolean exists(IProject project, String path)
  {
    return project.getFile(path).exists();
  }
}
