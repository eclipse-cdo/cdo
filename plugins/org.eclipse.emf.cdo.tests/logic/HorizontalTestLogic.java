/*
 * Copyright (c) 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.store.logic;

import org.eclipse.emf.cdo.internal.server.Transaction;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.HorizontalMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.MappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.ToMany;
import org.eclipse.emf.cdo.server.internal.db.ToOne;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class HorizontalTestLogic extends DBStoreTestLogic
{
  public HorizontalTestLogic()
  {
  }

  @Override
  protected IMappingStrategy createMappingStrategy()
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(MappingStrategy.PROP_TO_MANY_REFERENCE_MAPPING, ToMany.PER_CLASS.toString());
    props.put(MappingStrategy.PROP_TO_ONE_REFERENCE_MAPPING, ToOne.LIKE_ATTRIBUTES.toString());

    HorizontalMappingStrategy mappingStrategy = new HorizontalMappingStrategy();
    mappingStrategy.setProperties(props);
    return mappingStrategy;
  }

  @Override
  protected void verifyCreateModel1(Transaction transaction) throws Exception
  {
    defineOrCompare("defs/horizontal/verifyCreateModel1");
    // assertRowCount(1, "cdo_repository");
    // assertRowCount(1, "cdo_packages");
    // assertRowCount(11, "cdo_classes");
    // assertRowCount(8, "cdo_supertypes");
    // assertRowCount(26, "cdo_features");
  }

  @Override
  protected void verifyCreateModel2(Transaction transaction) throws Exception
  {
    defineOrCompare("defs/horizontal/verifyCreateModel2");
    // assertRowCount(1, "cdo_repository");
    // assertRowCount(2, "cdo_packages");
    // assertRowCount(12, "cdo_classes");
    // assertRowCount(9, "cdo_supertypes");
    // assertRowCount(28, "cdo_features");
  }

  @Override
  protected void verifyCreateModel3(Transaction transaction) throws Exception
  {
    defineOrCompare("defs/horizontal/verifyCreateModel3");
    // assertRowCount(1, "cdo_repository");
    // assertRowCount(1, "cdo_packages");
    // assertRowCount(1, "cdo_classes");
    // assertRowCount(0, "cdo_supertypes");
    // assertRowCount(1, "cdo_features");
  }

  @Override
  protected void verifyCreateMango(Transaction transaction) throws Exception
  {
    defineOrCompare("defs/horizontal/verifyCreateMango");
    // assertRowCount(1, "cdo_repository");
    // assertRowCount(1, "cdo_packages");
    // assertRowCount(2, "cdo_classes");
    // assertRowCount(0, "cdo_supertypes");
    // assertRowCount(3, "cdo_features");
  }

  @Override
  protected void verifyCommitCompany(Transaction transaction) throws Exception
  {
    defineOrCompare("defs/horizontal/verifyCommitCompany");
    // assertRowCount(1, "CDOResource");
    // assertFieldValue("/res1", "select path_0 from CDOResource where cdo_id=1 and cdo_version=1");
    //
    // assertRowCount(1, "Company");
    // assertFieldValue("Sympedia", "select name from Company where cdo_id=1 and cdo_version=1");
    // assertFieldValue("Homestr. 17", "select street from Company where cdo_id=1 and cdo_version=1");
    // assertFieldValue("Berlin", "select city from Company where cdo_id=1 and cdo_version=1");
  }
}
