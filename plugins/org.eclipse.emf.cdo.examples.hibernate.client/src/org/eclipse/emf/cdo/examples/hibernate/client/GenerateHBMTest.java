/**
 * Copyright (c) 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.hibernate.client;

import java.util.Properties;

import junit.framework.TestCase;

import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.server.hibernate.teneo.CDOHelper;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.teneo.PersistenceOptions;

/**
 * This class shows how the hibernate mapping can be generated programmatically. The hibernate mapping can be stored
 * locally and used in the server.
 * <p/>
 * See <a href="TBD">this</a> wiki page for more information.
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
    final String mapping = CDOHelper.getInstance().generateMapping(ePackages, props);

    // show it somewhere....
    // then store the hbm somewhere in a file and change it manually
    System.err.println(mapping);
  }
}
