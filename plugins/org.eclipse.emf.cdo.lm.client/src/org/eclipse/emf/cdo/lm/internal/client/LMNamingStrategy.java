/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.internal.client;

/**
 * TODO Make pluggable / customizable.
 *
 * @author Eike Stepper
 */
public final class LMNamingStrategy
{
  private LMNamingStrategy()
  {
  }

  public static String getChangeBranchName(String changeLabel)
  {
    return "change-" + sanitizeBranchName(changeLabel);
  }

  public static String sanitizeBranchName(String branchName)
  {
    return branchName.trim().replace('/', '-').replace(' ', '-');
  }
}
