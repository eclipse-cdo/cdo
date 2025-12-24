/*
 * Copyright (c) 2008, 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config;

import org.eclipse.emf.cdo.tests.mango.MangoFactory;
import org.eclipse.emf.cdo.tests.mango.MangoPackage;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model2.Model2Factory;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model3.Model3Factory;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model3.subpackage.SubpackageFactory;
import org.eclipse.emf.cdo.tests.model3.subpackage.SubpackagePackage;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage;
import org.eclipse.emf.cdo.tests.model5.Model5Factory;
import org.eclipse.emf.cdo.tests.model5.Model5Package;
import org.eclipse.emf.cdo.tests.model6.Model6Factory;
import org.eclipse.emf.cdo.tests.model6.Model6Package;

/**
 * @author Eike Stepper
 */
public interface IModelConfig extends IConfig
{
  public static final String CAPABILITY_NATIVE = "model.native";

  public static final String CAPABILITY_LEGACY = "model.legacy";

  public MangoFactory getMangoFactory();

  public MangoPackage getMangoPackage();

  public Model1Factory getModel1Factory();

  public Model1Package getModel1Package();

  public Model2Factory getModel2Factory();

  public Model2Package getModel2Package();

  public Model3Factory getModel3Factory();

  public Model3Package getModel3Package();

  public SubpackageFactory getModel3SubPackageFactory();

  public SubpackagePackage getModel3SubPackagePackage();

  public model4Factory getModel4Factory();

  public model4Package getModel4Package();

  public model4interfacesPackage getModel4InterfacesPackage();

  public Model5Factory getModel5Factory();

  public Model5Package getModel5Package();

  public Model6Factory getModel6Factory();

  public Model6Package getModel6Package();
}
