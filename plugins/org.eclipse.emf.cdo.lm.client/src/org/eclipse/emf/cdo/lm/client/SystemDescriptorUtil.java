/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.client;

/**
 * @author Eike Stepper
 */
public class SystemDescriptorUtil
{
  public static String getChangeBranchName(String label)
  {
    int NamingStrategy;
    return "change-" + label.replace(' ', '-');
  }
}
