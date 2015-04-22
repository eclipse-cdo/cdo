/*
 * Copyright (c) 2010, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.preferences;

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreDiagramEditorPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @generated
 */
public class DiagramPreferenceInitializer extends AbstractPreferenceInitializer
{

  /**
   * @generated
   */
  @Override
  public void initializeDefaultPreferences()
  {
    IPreferenceStore store = getPreferenceStore();
    DiagramGeneralPreferencePage.initDefaults(store);
    DiagramAppearancePreferencePage.initDefaults(store);
    DiagramConnectionsPreferencePage.initDefaults(store);
    DiagramPrintingPreferencePage.initDefaults(store);
    DiagramRulersAndGridPreferencePage.initDefaults(store);

  }

  /**
   * @generated
   */
  protected IPreferenceStore getPreferenceStore()
  {
    return AcoreDiagramEditorPlugin.getInstance().getPreferenceStore();
  }
}
