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
package org.eclipse.emf.cdo.internal.migrator.dbstore;

import org.eclipse.emf.cdo.internal.migrator.MigratorPlugin;

import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.BasicEAnnotationValidator;

import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class DBStoreAnnotationValidator extends BasicEAnnotationValidator
{
  public static final DBStoreAnnotationValidator INSTANCE = new DBStoreAnnotationValidator();

  public static final String ANNOTATION_URI = "http://www.eclipse.org/CDO/DBStore";

  public static final String DIAGNOSTIC_SOURCE = "org.eclipse.emf.cdo.server.db";

  private static final List<EClass> FEATURE;

  private static final List<EClass> FEATURE_MANY;

  private static final List<EClass> CLASS;

  private static final List<EClass> PACKAGE;

  static
  {
    EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(ANNOTATION_URI);
    if (ePackage == null)
    {
      // If the package isn't registered, as well be the case in a stand alone application, try to load it dynamically.
      // This will ensure that the package is registered as well.
      ePackage = loadEPackage(MigratorPlugin.INSTANCE.getBaseURL().toString() + "model/DBStore.ecore");
    }

    FEATURE = Collections.singletonList((EClass)ePackage.getEClassifier("Feature"));
    FEATURE_MANY = Collections.singletonList((EClass)ePackage.getEClassifier("FeatureMany"));
    CLASS = Collections.singletonList((EClass)ePackage.getEClassifier("Class"));
    PACKAGE = Collections.singletonList((EClass)ePackage.getEClassifier("Package"));
  }

  public DBStoreAnnotationValidator()
  {
    super(ANNOTATION_URI, "DBStore", DIAGNOSTIC_SOURCE);
  }

  @Override
  protected ResourceLocator getResourceLocator()
  {
    return MigratorPlugin.INSTANCE;
  }

  @Override
  protected boolean isValidLocation(EAnnotation annotation, EModelElement modelElement)
  {
    return modelElement instanceof EStructuralFeature || modelElement instanceof EClass || modelElement instanceof EPackage;
  }

  @Override
  protected List<EClass> getPropertyClasses(EModelElement modelElement)
  {
    if (modelElement instanceof EStructuralFeature)
    {
      return ((EStructuralFeature)modelElement).isMany() ? FEATURE_MANY : FEATURE;
    }

    if (modelElement instanceof EClass)
    {
      return CLASS;
    }

    if (modelElement instanceof EPackage)
    {
      return PACKAGE;
    }

    return Collections.emptyList();
  }
}
