/*
 * Copyright (c) 2011-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.winexplorer;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class PromptAction extends AbstractLocationAction
{
  public PromptAction()
  {
  }

  @Override
  protected void run(File location) throws Exception
  {
    Runtime.getRuntime().exec("cmd /c cd \"" + location + "\" && start cmd.exe");
  }
}
