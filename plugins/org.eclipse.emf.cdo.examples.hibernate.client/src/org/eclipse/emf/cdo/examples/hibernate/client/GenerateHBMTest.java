/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.hibernate.client;

import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.server.hibernate.teneo.CDOMappingGenerator;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.teneo.PersistenceOptions;

import java.util.Properties;

import junit.framework.TestCase;

/**
 * This class shows how the hibernate mapping can be generated programmatically. The hibernate mapping can be stored
 * locally and used in the server.
 * <p/>
 * See <a href=
 * "http://wiki.eclipse.org/CDO_Hibernate_Store_Model_Relational_Mapping#Generating_the_mapping.2C_manually_changing_it_and_then_use_the_manual_mapping"
 * >this</a> wiki page for more information.
 * <p/>
 * The generated hbm is printed to the console. For the rest see the inline comments.
 * <p/>
 * Note that this method uses CDO server side plugins and Teneo. This results in two extra dependencies in this plugin:
 * <ul>
 * <li>org.eclipse.emf.teno</li>
 * <li>org.eclipse.emf.cdo.server.hibernate.teneo</li>
 * <li>org.apache.commons.logging</li>
 * </ul>
 * The first two dependencies normally do not exist on a client side application.
 * 
 * @author Martin Taal
 */
public class GenerateHBMTest extends TestCase
{

  public void testGenerateHBM()
  {
    // create a properties
    final Properties props = new Properties();
    // this property should normally be set like this:
    props.put(PersistenceOptions.CASCADE_POLICY_ON_NON_CONTAINMENT, "PERSIST,MERGE"); //$NON-NLS-1$

    // add some demo properties
    props.put(PersistenceOptions.INHERITANCE_MAPPING, "JOINED"); //$NON-NLS-1$
    props.put(PersistenceOptions.ADD_INDEX_FOR_FOREIGN_KEY, "true"); //$NON-NLS-1$

    // get the epackages
    final EPackage[] ePackages = new EPackage[] { CompanyPackage.eINSTANCE };

    // generate the mapping
    final CDOMappingGenerator mappingGenerator = new CDOMappingGenerator();
    final String mapping = mappingGenerator.generateMapping(ePackages, props);

    // show it somewhere....
    // then store the hbm somewhere in a file and change it manually
    IOUtil.ERR().println(mapping);
  }
}
