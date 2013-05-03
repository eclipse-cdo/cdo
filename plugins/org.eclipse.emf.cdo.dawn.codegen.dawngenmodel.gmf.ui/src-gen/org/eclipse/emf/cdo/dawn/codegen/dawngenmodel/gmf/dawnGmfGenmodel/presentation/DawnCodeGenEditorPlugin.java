/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.presentation;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.provider.DawnCodeGenEditPlugin;

import org.eclipse.emf.codegen.ecore.genmodel.provider.GenModelEditPlugin;
import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.ui.EclipseUIPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.provider.EcoreEditPlugin;

import org.eclipse.gmf.codegen.gmfgen.presentation.EditorPlugin;

/**
 * This is the central singleton for the DawnCodeGen editor plugin. <!-- begin-user-doc --> <!-- end-user-doc -->
 *
 * @author Martin Fluegge
 * @generated
 */
public final class DawnCodeGenEditorPlugin extends EMFPlugin
{
  /**
   * Keep track of the singleton. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public static final DawnCodeGenEditorPlugin INSTANCE = new DawnCodeGenEditorPlugin();

  /**
   * Keep track of the singleton. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private static Implementation plugin;

  /**
   * Create the instance. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public DawnCodeGenEditorPlugin()
  {
    super(new ResourceLocator[] { DawnCodeGenEditPlugin.INSTANCE, EcoreEditPlugin.INSTANCE,
        GenModelEditPlugin.INSTANCE, EditorPlugin.INSTANCE, });
  }

  /**
   * Returns the singleton instance of the Eclipse plugin. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the singleton instance.
   * @generated
   */
  @Override
  public ResourceLocator getPluginResourceLocator()
  {
    return plugin;
  }

  /**
   * Returns the singleton instance of the Eclipse plugin. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the singleton instance.
   * @generated
   */
  public static Implementation getPlugin()
  {
    return plugin;
  }

  /**
   * The actual implementation of the Eclipse <b>Plugin</b>. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public static class Implementation extends EclipseUIPlugin
  {
    /**
     * Creates an instance. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public Implementation()
    {
      super();

      // Remember the static instance.
      //
      plugin = this;
    }
  }

}
