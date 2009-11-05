/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.tests.ExternalReferenceTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;

import org.eclipse.emf.teneo.PersistenceOptions;

/**
 * Test {@link ExternalReferenceTest}, disables some testcases which will never work anyway.
 * 
 * @author Martin Taal
 */
public class HibernateExternalReferenceTest extends ExternalReferenceTest
{
  @Override
  public void testManyViewsOnOneResourceSet() throws Exception
  {
    // super.testManyViewsOnOneResourceSet();
  }

  @Override
  public void testOneXMIResourceManyViewsOnOneResourceSet() throws Exception
  {
    // super.testOneXMIResourceManyViewsOnOneResourceSet();
  }

  @Override
  public void testUsingObjectsBetweenSameTransaction() throws Exception
  {
    // super.testUsingObjectsBetweenSameTransaction();
  }

  @Override
  protected void doSetUp() throws Exception
  {
    final IRepositoryConfig repConfig = getRepositoryConfig();
    final HibernateConfig hbConfig = (HibernateConfig)repConfig;
    final String persistenceXML = "org/eclipse/emf/cdo/tests/hibernate/external_model4.persistence.xml";
    hbConfig.getAdditionalProperties().put(PersistenceOptions.PERSISTENCE_XML, persistenceXML);

    super.doSetUp();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    final IRepositoryConfig repConfig = getRepositoryConfig();
    final HibernateConfig hbConfig = (HibernateConfig)repConfig;
    hbConfig.getAdditionalProperties().clear();
    super.doTearDown();
  }
}
