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
import org.eclipse.emf.cdo.tests.XATransactionTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;

/**
 * Override {@link XATransactionTest} to do mapping of external references.
 *
 * @author Martin Taal
 */
public class HibernateXATransactionTest extends XATransactionTest
{
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
