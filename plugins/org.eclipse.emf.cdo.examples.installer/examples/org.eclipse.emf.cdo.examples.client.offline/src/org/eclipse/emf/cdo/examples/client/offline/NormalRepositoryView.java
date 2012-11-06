/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.client.offline;

import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ItemProvider;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Eike Stepper
 */
public class NormalRepositoryView extends AbstractView<IRepository>
{
  public static final String ID = "org.eclipse.emf.cdo.examples.client.offline.NormalRepositoryView"; //$NON-NLS-1$

  private ItemProvider<IContainer<Object>> itemProvider;

  private TreeViewer treeViewer;

  private ScrolledComposite details;

  public NormalRepositoryView()
  {
    super(IRepository.class);
  }

  @Override
  protected void createPane(Composite parent, IRepository repository)
  {
    itemProvider = new ContainerItemProvider<IContainer<Object>>()
    {
      @Override
      protected void handleElementEvent(final IEvent event)
      {
        addEvent(event);
      }
    };

    SashForm sash = new SashForm(parent, SWT.SMOOTH);

    treeViewer = new TreeViewer(sash, SWT.BORDER);
    treeViewer.setLabelProvider(itemProvider);
    treeViewer.setContentProvider(itemProvider);
    treeViewer.setInput(IPluginContainer.INSTANCE);

    details = new ScrolledComposite(sash, SWT.V_SCROLL);
    details.setExpandHorizontal(true);
    details.setExpandVertical(true);

    sash.setWeights(new int[] { 1, 1 });
  }

  @Override
  public void setFocus()
  {
    treeViewer.getTree().setFocus();
  }

  @Override
  public void dispose()
  {
    itemProvider.dispose();
    super.dispose();
  }
}
