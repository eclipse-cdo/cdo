/*
 * Copyright (c) 2014, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.mango.MangoParameter;
import org.eclipse.emf.cdo.tests.mango.ParameterPassing;
import org.eclipse.emf.cdo.tests.mango.legacy.MangoPackage;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EcoreFactory;

import org.hibernate.cache.ehcache.EhCacheRegionFactory;
import org.hibernate.cfg.Environment;

/**
 *
 * @author Martin Taal
 */
public class HibernateBugzilla_417797_Test extends AbstractCDOTest
{

  @Override
  protected void doSetUp() throws Exception
  {
    final IRepositoryConfig repConfig = getRepositoryConfig();
    final HibernateConfig hbConfig = (HibernateConfig)repConfig;

    final EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
    eAnnotation.setSource("teneo.jpa");
    eAnnotation.getDetails().put("value", "@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)");
    MangoPackage.eINSTANCE.getMangoParameter().getEAnnotations().add(eAnnotation);

    hbConfig.getAdditionalProperties().put(Environment.USE_SECOND_LEVEL_CACHE, "true");
    hbConfig.getAdditionalProperties().put(Environment.CACHE_REGION_FACTORY, EhCacheRegionFactory.class.getName());
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

  @CleanRepositoriesBefore(reason = "Start with a fresh repo")
  public void testBugzilla() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      MangoParameter createMangoParameter = getMangoFactory().createMangoParameter();
      createMangoParameter.setName("Mango1");
      createMangoParameter.setPassing(ParameterPassing.BY_REFERENCE);

      resource.getContents().add(createMangoParameter);

      transaction.commit();
      session.close();
    }
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));
      System.err.println(resource.getContents().get(0));
      transaction.commit();
      session.close();
    }
  }
}
