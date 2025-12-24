/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

import org.eclipse.net4j.util.AbstractSupport.ClassAvailability;

/**
 * @author Eike Stepper
 * @since 4.21
 */
public final class Support
{
  public static final ClassAvailability UI_PROPERTIES = new ClassAvailability("org.eclipse.ui.views", //
      "org.eclipse.ui.views.properties.PropertySheet");

  public static final ClassAvailability UI_HISTORY = new ClassAvailability("org.eclipse.emf.cdo.ui.team", //
      "org.eclipse.emf.cdo.ui.internal.team.history.CDOHistoryPage");

  public static final ClassAvailability UI_COMPARE = new ClassAvailability("org.eclipse.emf.cdo.ui.compare",
      "org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil");

  public static final ClassAvailability UI_FORMS = new ClassAvailability("org.eclipse.emf.ecp.ui.view.swt",
      "org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer");

  public static final ClassAvailability SERVER = new ClassAvailability("org.eclipse.emf.cdo.server", //
      "org.eclipse.emf.cdo.server.CDOServerUtil");

  public static final ClassAvailability SERVER_DB = new ClassAvailability("org.eclipse.emf.cdo.server.db", //
      "org.eclipse.emf.cdo.server.db.CDODBUtil");

  public static final ClassAvailability SERVER_SECURITY = new ClassAvailability("org.eclipse.emf.cdo.server.security", //
      "org.eclipse.emf.cdo.server.security.SecurityManagerUtil");

  public static final ClassAvailability SERVER_LM = new ClassAvailability("org.eclipse.emf.cdo.lm.server", //
      "org.eclipse.emf.cdo.lm.server.AbstractLifecycleManager");

  public static final ClassAvailability WORKSPACE = new ClassAvailability("org.eclipse.emf.cdo.workspace", //
      "org.eclipse.emf.cdo.workspace.CDOWorkspaceUtil");

  private Support()
  {
  }
}
