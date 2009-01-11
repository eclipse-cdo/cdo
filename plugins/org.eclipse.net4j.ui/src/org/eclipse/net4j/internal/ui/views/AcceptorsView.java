/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.ui.views;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;

/**
 * @author Eike Stepper
 */
public class AcceptorsView extends ContainerView
{
  private Action addAcceptorAction2036 = new SafeAction("Add Acceptor 2036", "Add an acceptor for port 2036",
      getAddImageDescriptor())
  {
    @Override
    protected void safeRun() throws Exception
    {
      Net4jUtil.getAcceptor(IPluginContainer.INSTANCE, "tcp", "0.0.0.0:2036");
    }
  };

  private Action addAcceptorAction2037 = new SafeAction("Add Acceptor 2037", "Add an acceptor for port 2037",
      getAddImageDescriptor())
  {
    @Override
    protected void safeRun() throws Exception
    {
      Net4jUtil.getAcceptor(IPluginContainer.INSTANCE, "tcp", "0.0.0.0:2037");
    }
  };

  public AcceptorsView()
  {
  }

  @Override
  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new Net4jItemProvider(new IElementFilter()
    {
      public boolean filter(Object element)
      {
        return element instanceof IAcceptor;
      }
    });
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(addAcceptorAction2036);
    manager.add(addAcceptorAction2037);
    super.fillLocalToolBar(manager);
  }
}
