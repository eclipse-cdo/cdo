/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.tests.config.IModelConfig;
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

import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class ModelConfig extends Config implements IModelConfig
{
  private static final long serialVersionUID = 1L;

  public ModelConfig(String name)
  {
    super(name);
  }

  /**
   * @author Eike Stepper
   */
  public static final class Native extends ModelConfig
  {
    public static final String NAME = "Native";

    public static final Native INSTANCE = new Native();

    private static final long serialVersionUID = 1L;

    public Native()
    {
      super(NAME);
    }

    public void initCapabilities(Set<String> capabilities)
    {
      capabilities.add(CAPABILITY_NATIVE);
    }

    public MangoFactory getMangoFactory()
    {
      return org.eclipse.emf.cdo.tests.mango.MangoFactory.eINSTANCE;
    }

    public MangoPackage getMangoPackage()
    {
      return org.eclipse.emf.cdo.tests.mango.MangoPackage.eINSTANCE;
    }

    public Model1Factory getModel1Factory()
    {
      return org.eclipse.emf.cdo.tests.model1.Model1Factory.eINSTANCE;
    }

    public Model1Package getModel1Package()
    {
      return org.eclipse.emf.cdo.tests.model1.Model1Package.eINSTANCE;
    }

    public Model2Factory getModel2Factory()
    {
      return org.eclipse.emf.cdo.tests.model2.Model2Factory.eINSTANCE;
    }

    public Model2Package getModel2Package()
    {
      return org.eclipse.emf.cdo.tests.model2.Model2Package.eINSTANCE;
    }

    public Model3Factory getModel3Factory()
    {
      return org.eclipse.emf.cdo.tests.model3.Model3Factory.eINSTANCE;
    }

    public Model3Package getModel3Package()
    {
      return org.eclipse.emf.cdo.tests.model3.Model3Package.eINSTANCE;
    }

    public SubpackageFactory getModel3SubPackageFactory()
    {
      return org.eclipse.emf.cdo.tests.model3.subpackage.SubpackageFactory.eINSTANCE;
    }

    public SubpackagePackage getModel3SubPackagePackage()
    {
      return org.eclipse.emf.cdo.tests.model3.subpackage.SubpackagePackage.eINSTANCE;
    }

    public model4Factory getModel4Factory()
    {
      return org.eclipse.emf.cdo.tests.model4.model4Factory.eINSTANCE;
    }

    public model4Package getModel4Package()
    {
      return org.eclipse.emf.cdo.tests.model4.model4Package.eINSTANCE;
    }

    public model4interfacesPackage getModel4InterfacesPackage()
    {
      return org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage.eINSTANCE;
    }

    public Model5Factory getModel5Factory()
    {
      return org.eclipse.emf.cdo.tests.model5.Model5Factory.eINSTANCE;
    }

    public Model5Package getModel5Package()
    {
      return org.eclipse.emf.cdo.tests.model5.Model5Package.eINSTANCE;
    }

    public Model6Factory getModel6Factory()
    {
      return org.eclipse.emf.cdo.tests.model6.Model6Factory.eINSTANCE;
    }

    public Model6Package getModel6Package()
    {
      return org.eclipse.emf.cdo.tests.model6.Model6Package.eINSTANCE;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Legacy extends ModelConfig
  {
    private static final long serialVersionUID = 1L;

    public static final String NAME = "Legacy";

    public static final Legacy INSTANCE = new Legacy();

    public Legacy()
    {
      super(NAME);
    }

    public void initCapabilities(Set<String> capabilities)
    {
      capabilities.add(CAPABILITY_LEGACY);
    }

    public MangoFactory getMangoFactory()
    {
      return org.eclipse.emf.cdo.tests.legacy.mango.impl.MangoFactoryImpl.eINSTANCE;
    }

    public MangoPackage getMangoPackage()
    {
      return org.eclipse.emf.cdo.tests.legacy.mango.impl.MangoPackageImpl.eINSTANCE;
    }

    public Model1Factory getModel1Factory()
    {
      return org.eclipse.emf.cdo.tests.legacy.model1.Model1Factory.eINSTANCE;
    }

    public Model1Package getModel1Package()
    {
      return org.eclipse.emf.cdo.tests.legacy.model1.Model1Package.eINSTANCE;
    }

    public Model2Factory getModel2Factory()
    {
      return org.eclipse.emf.cdo.tests.legacy.model2.Model2Factory.eINSTANCE;
    }

    public Model2Package getModel2Package()
    {
      return org.eclipse.emf.cdo.tests.legacy.model2.Model2Package.eINSTANCE;
    }

    public Model3Factory getModel3Factory()
    {
      return org.eclipse.emf.cdo.tests.legacy.model3.Model3Factory.eINSTANCE;
    }

    public Model3Package getModel3Package()
    {
      return org.eclipse.emf.cdo.tests.legacy.model3.Model3Package.eINSTANCE;
    }

    public SubpackageFactory getModel3SubPackageFactory()
    {
      return org.eclipse.emf.cdo.tests.legacy.model3.subpackage.SubpackageFactory.eINSTANCE;
    }

    public SubpackagePackage getModel3SubPackagePackage()
    {
      return org.eclipse.emf.cdo.tests.legacy.model3.subpackage.SubpackagePackage.eINSTANCE;
    }

    public model4Factory getModel4Factory()
    {
      return org.eclipse.emf.cdo.tests.legacy.model4.model4Factory.eINSTANCE;
    }

    public model4Package getModel4Package()
    {
      return org.eclipse.emf.cdo.tests.legacy.model4.model4Package.eINSTANCE;
    }

    public model4interfacesPackage getModel4InterfacesPackage()
    {
      return org.eclipse.emf.cdo.tests.legacy.model4interfaces.model4interfacesPackage.eINSTANCE;
    }

    public Model5Factory getModel5Factory()
    {
      return org.eclipse.emf.cdo.tests.legacy.model5.Model5Factory.eINSTANCE;
    }

    public Model5Package getModel5Package()
    {
      return org.eclipse.emf.cdo.tests.legacy.model5.Model5Package.eINSTANCE;
    }

    public Model6Factory getModel6Factory()
    {
      return org.eclipse.emf.cdo.tests.legacy.model6.Model6Factory.eINSTANCE;
    }

    public Model6Package getModel6Package()
    {
      return org.eclipse.emf.cdo.tests.legacy.model6.Model6Package.eINSTANCE;
    }
  }
}
