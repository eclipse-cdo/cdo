/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.LegacyModeRegistry;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.ui.actions.SafeAction;

/**
 * @author Victor Roldan Betancort
 */
public class ToggleLegacyModeDefaultAction extends SafeAction
{
  private static final String TITLE = "Legacy Mode";

  private static final String TOOL_TIP = "Toggles the default for legacy mode";

  private CDOSession session;

  public ToggleLegacyModeDefaultAction(CDOSession session)
  {
    super(TITLE, AS_CHECK_BOX);
    setToolTipText(TOOL_TIP);
    this.session = session;
    setChecked(LegacyModeRegistry.isLegacyEnabled(session));
  }

  @Override
  protected void safeRun() throws Exception
  {
    LegacyModeRegistry.setLegacyEnabled(session, !LegacyModeRegistry.isLegacyEnabled(session));
  }
}
