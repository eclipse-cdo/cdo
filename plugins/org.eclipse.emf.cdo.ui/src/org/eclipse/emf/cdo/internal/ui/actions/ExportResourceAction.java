/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.actions;

/**
 * @author Eike Stepper
 */
@Deprecated
public class ExportResourceAction extends EditingDomainAction
{
  public static final String ID = "export-resource";

  private static final String TITLE = "Export Resource";

  public ExportResourceAction()
  {
    super(TITLE + INTERACTIVE);
    setId(ID);
  }

  @Override
  protected void doRun() throws Exception
  {
  }
}
