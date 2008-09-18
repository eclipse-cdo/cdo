/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.testbed;

import org.eclipse.emf.cdo.tests.mango.MangoFactory;
import org.eclipse.emf.cdo.tests.mango.MangoPackage;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model2.Model2Factory;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model3.Model3Factory;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage;

/**
 * @author Eike Stepper
 */
public abstract class ModelConfig extends Config implements ModelProvider
{
  public static final String DIMENSION = "model";

  public static final ModelConfig[] CONFIGS = { Native.INSTANCE, Legacy.INSTANCE };

  public ModelConfig(String name)
  {
    super(DIMENSION, name);
  }

  /**
   * @author Eike Stepper
   */
  public static final class Native extends ModelConfig
  {
    public static final String NAME = "Native";

    public static final Native INSTANCE = new Native();

    public Native()
    {
      super(NAME);
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
  }

  /**
   * @author Eike Stepper
   */
  public static final class Legacy extends ModelConfig
  {
    public static final String NAME = "Legacy";

    public static final Legacy INSTANCE = new Legacy();

    public Legacy()
    {
      super(NAME);
    }

    public MangoFactory getMangoFactory()
    {
      return org.eclipse.emf.cdo.tests.legacy.mango.MangoFactory.eINSTANCE;
    }

    public MangoPackage getMangoPackage()
    {
      return org.eclipse.emf.cdo.tests.legacy.mango.MangoPackage.eINSTANCE;
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
  }
}
