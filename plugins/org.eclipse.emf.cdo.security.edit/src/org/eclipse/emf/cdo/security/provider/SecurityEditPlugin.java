/*
 * Copyright (c) 2012, 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.provider;

import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.edit.CDOEditPlugin;
import org.eclipse.emf.cdo.expressions.provider.ExpressionsEditPlugin;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.provider.EcoreEditPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This is the central singleton for the Security edit plugin.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public final class SecurityEditPlugin extends EMFPlugin
{
  /**
   * Keep track of the singleton.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final SecurityEditPlugin INSTANCE = new SecurityEditPlugin();

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
  public SecurityEditPlugin()
  {
    super(new ResourceLocator[] { EcoreEditPlugin.INSTANCE, CDOEditPlugin.INSTANCE, ExpressionsEditPlugin.INSTANCE, });
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
   * @since 4.4
   */
  public static List<EPackage> getSortedPackages(CDOView view)
  {
    List<EPackage> result = new ArrayList<>();
    for (CDOPackageInfo packageInfo : view.getSession().getPackageRegistry().getPackageInfos())
    {
      result.add(packageInfo.getEPackage());
    }

    Collections.sort(result, new Comparator<EPackage>()
    {
      @Override
      public int compare(EPackage p1, EPackage p2)
      {
        return p1.getNsURI().compareTo(p2.getNsURI());
      }
    });

    return result;
  }

  /**
   * @since 4.4
   */
  public static List<EClass> getSortedClasses(CDOView view)
  {
    List<EClass> result = new ArrayList<>();
    for (CDOPackageInfo packageInfo : view.getSession().getPackageRegistry().getPackageInfos())
    {
      for (EClassifier classifier : packageInfo.getEPackage().getEClassifiers())
      {
        if (classifier instanceof EClass)
        {
          result.add((EClass)classifier);

        }
      }
    }

    Collections.sort(result, new Comparator<EClass>()
    {
      @Override
      public int compare(EClass c1, EClass c2)
      {
        int comparison = c1.getName().compareTo(c2.getName());
        if (comparison == 0)
        {
          comparison = c1.getEPackage().getNsURI().compareTo(c2.getEPackage().getNsURI());
        }

        return comparison;
      }
    });

    return result;
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
  }

}
