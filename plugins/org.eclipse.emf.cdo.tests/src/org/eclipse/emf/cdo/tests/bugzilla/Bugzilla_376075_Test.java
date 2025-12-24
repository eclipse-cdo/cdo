/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model6.PropertiesMap;
import org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Eike Stepper
 */
public class Bugzilla_376075_Test extends AbstractCDOTest
{
  public void testTransientPut() throws Throwable
  {
    PropertiesMap properties = getModel6Factory().createPropertiesMap();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    resource.getContents().add(properties);
    transaction.commit();

    PropertiesMapEntryValue value = getModel6Factory().createPropertiesMapEntryValue();
    value.setLabel("value");

    CDOView view = session.openView();
    properties = view.getObject(properties);
    properties.getTransientMap().put("key", value);
  }

  public void testTransientRemove() throws Throwable
  {
    PropertiesMapEntryValue value = getModel6Factory().createPropertiesMapEntryValue();
    value.setLabel("value");

    PropertiesMap properties = getModel6Factory().createPropertiesMap();
    properties.getTransientMap().put("key", value);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    resource.getContents().add(properties);
    transaction.commit();

    CDOView view = session.openView();
    properties = view.getObject(properties);
    properties.getTransientMap().remove("key");
  }
}
