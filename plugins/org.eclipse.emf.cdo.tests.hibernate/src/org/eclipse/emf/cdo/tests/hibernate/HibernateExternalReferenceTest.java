/*
 * Copyright (c) 2009, 2011-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;
import org.eclipse.emf.cdo.tests.ExternalReferenceTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;

/**
 * Test {@link ExternalReferenceTest}, disables some testcases which will never work anyway.
 * 
 * @author Martin Taal
 */
public class HibernateExternalReferenceTest extends ExternalReferenceTest
{

  @Override
  public void testXRefExternalObject() throws Exception
  {
    // xreffing an external object is not possible as the
    // external reference does not hold type information
  }

  @Override
  public void testManyViewsOnOneResourceSet() throws Exception
  {
    // this testcase does not work because it there are external temporary references between two
    // objects and the objects are stored at the same time. The temporary references are then
    // stored in the database (as external), when retrieving the objects the temporary references
    // can not be resolved to real ones.
    // one note in the second part of the test the supplier is read. The supplier is not read
    // from the database but is cached server side
    // super.testManyViewsOnOneResourceSet();
  }

  @Override
  public void testUsingObjectsBetweenSameTransaction() throws Exception
  {
    // note this testcase requires that no id's are mapped externally
    // this testcase does not work for hibernate because 2 objects reference eachother and
    // are added in different transactions, hibernate/mysql will throw a fk-constraint
    // exception. This is correct behavior.
    // super.testUsingObjectsBetweenSameTransaction();
  }

  @Override
  public void testOneXMIResourceManyViewsOnOneResourceSet()
  {

  }

  @Override
  protected void doSetUp() throws Exception
  {
    final IRepositoryConfig repConfig = getRepositoryConfig();
    final HibernateConfig hbConfig = (HibernateConfig)repConfig;
    final String persistenceXML = "org/eclipse/emf/cdo/tests/hibernate/cdo_hibernate.persistence.xml";
    hbConfig.getAdditionalProperties().put(HibernateStore.PERSISTENCE_XML, persistenceXML);

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
