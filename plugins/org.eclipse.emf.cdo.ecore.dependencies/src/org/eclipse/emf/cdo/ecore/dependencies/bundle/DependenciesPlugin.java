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
package org.eclipse.emf.cdo.ecore.dependencies.bundle;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.om.pref.OMPreferences;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;

import org.osgi.framework.BundleContext;

/**
 * This is the central singleton for the Dependencies edit plugin.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public final class DependenciesPlugin extends EMFPlugin
{
  /**
   * Keep track of the singleton.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final DependenciesPlugin INSTANCE = new DependenciesPlugin();

  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.ecore.dependencies"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, DependenciesPlugin.class);

  public static final OMPreferences PREFS = BUNDLE.preferences();

  public static final OMPreference<Boolean> PREF_SHOW_CALLERS = //
      PREFS.init("PREF_SHOW_CALLERS", false); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_SHOW_FLAT = //
      PREFS.init("PREF_SHOW_FLAT", false); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_SORT_BY_DEPENDENCIES = //
      PREFS.init("PREF_SORT_BY_DEPENDENCIES", false); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_SHOW_BROKEN_LINKS = //
      PREFS.init("PREF_SHOW_BROKEN_LINKS", false); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_SHOW_GENERICS = //
      PREFS.init("PREF_SHOW_GENERICS", false); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_LAYOUT_VERTICAL = //
      PREFS.init("PREF_LAYOUT_VERTICAL", true); //$NON-NLS-1$

  /**
   * JFace.
   */
  public static final Styler JFACE_ERROR_STYLER = StyledString.createColorRegistryStyler(JFacePreferences.ERROR_COLOR, null);

  /**
   * Keep track of the singleton.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static Implementation plugin;

  /**
   * Create the instance.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependenciesPlugin()
  {
    super(new ResourceLocator[] {});
  }

  /**
   * Returns the singleton instance of the Eclipse plugin.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the singleton instance.
   * @generated
   */
  @Override
  public ResourceLocator getPluginResourceLocator()
  {
    return plugin;
  }

  /**
   * Returns the singleton instance of the Eclipse plugin.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the singleton instance.
   * @generated
   */
  public static Implementation getPlugin()
  {
    return plugin;
  }

  /**
   * The actual implementation of the Eclipse <b>Plugin</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static class Implementation extends EclipsePlugin
  {
    /**
     * Creates an instance.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Implementation()
    {
      super();

      // Remember the static instance.
      //
      plugin = this;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void start(BundleContext context) throws Exception
    {
      super.start(context);
      BUNDLE.setBundleContext(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
      PREFS.save();
      super.stop(context);
    }
  }

}
