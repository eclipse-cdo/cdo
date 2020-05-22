/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package Testmodel562011.util;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.util.XMLProcessor;

import java.util.Map;

import Testmodel562011.Testmodel562011Package;

/**
 * This class contains helper methods to serialize and deserialize XML documents
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class Testmodel562011XMLProcessor extends XMLProcessor
{

  /**
   * Public constructor to instantiate the helper.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Testmodel562011XMLProcessor()
  {
    super(new EPackageRegistryImpl(EPackage.Registry.INSTANCE));
    extendedMetaData.putPackage(null, Testmodel562011Package.eINSTANCE);
  }

  /**
   * Register for "*" and "xml" file extensions the Testmodel562011ResourceFactoryImpl factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected Map<String, Resource.Factory> getRegistrations()
  {
    if (registrations == null)
    {
      super.getRegistrations();
      registrations.put(XML_EXTENSION, new Testmodel562011ResourceFactoryImpl());
      registrations.put(STAR_EXTENSION, new Testmodel562011ResourceFactoryImpl());
    }
    return registrations;
  }

} // Testmodel562011XMLProcessor
