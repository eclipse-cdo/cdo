/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.MoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;

import java.text.ParseException;

/**
 * @author Christophe Bouhier
 */
public class Bugzilla_408534_Test extends AbstractCDOTest
{
  public void testListElementMove() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    Resource resource = transaction.getOrCreateResource(getResourcePath("test.model1"));

    // Add a company with 3 categories.
    Company company = getModel1Factory().createCompany();
    addCategory(company);
    addCategory(company);
    addCategory(company);
    resource.getContents().add(company);
    transaction.commit();

    // Bring the resourceset under an editing domain.
    ResourceSet resourceSet = transaction.getResourceSet();
    AdapterFactory adapterFactory = new ReflectiveItemProviderAdapterFactory();
    CommandStack commandStack = new SaneCommandStack();
    EditingDomain domain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, resourceSet);

    // Add a notfification adapter.
    company.eAdapters().add(new AdapterImpl()
    {
      @Override
      public void notifyChanged(Notification msg)
      {
        // We expect an object of type Category as the new value.
        assertInstanceOf(Category.class, msg.getNewValue());
      }
    });

    // Move the last element to index = 0;
    Command moveCommand = new MoveCommand(domain, company.getCategories(), company.getCategories().size() - 1, 0);
    commandStack.execute(moveCommand); // Notifier should not throw an exception.
  }

  private void addCategory(Company company) throws ParseException
  {
    Category category = getModel1Factory().createCategory();
    category.setName("Cat" + (company.getCategories().size() + 1));
    company.getCategories().add(category);
  }

  /**
   * @author Caspar De Groot
   */
  private static class SaneCommandStack extends BasicCommandStack
  {
    @Override
    protected void handleError(Exception exception)
    {
      throw new WrappedException(exception);
    }
  }
}
