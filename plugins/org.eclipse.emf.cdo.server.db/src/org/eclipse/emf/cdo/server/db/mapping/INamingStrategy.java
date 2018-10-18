/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.7
 */
public interface INamingStrategy
{
  public void initialize(IMappingStrategy mappingStrategy);

  /**
   * Create a suitable table name which can be used to map the given element. Should only be called by mapping classes.
   *
   * @param element
   *          the element for which the name should be created. It must hold:
   *          <code>element instanceof EClass || element instanceof EPackage</code>.
   * @return the created table name. It is guaranteed that the table name is compatible with the chosen database.
   */
  public String getTableName(ENamedElement element);

  /**
   * Create a suitable table name which can be used to map the given element. Should only be called by mapping classes.
   * Should only be called by mapping classes.
   *
   * @param containingClass
   *          the class that contains the feature.
   * @param feature
   *          the feature for which the table name should be created.
   * @return the created table name. It is guaranteed that the table name is compatible with the chosen database.
   */
  public String getTableName(EClass containingClass, EStructuralFeature feature);

  /**
   * Create a suitable column name which can be used to map the given element. Should only be called by mapping classes.
   *
   * @param feature
   *          the feature for which the column name should be created.
   * @return the created column name. It is guaranteed that the name is compatible with the chosen database.
   */
  public String getFieldName(EStructuralFeature feature);

  public String getUnsettableFieldName(EStructuralFeature feature);

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.PropertiesFactory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.db.namingStrategies";

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract INamingStrategy create(Map<String, String> properties) throws ProductCreationException;
  }
}
