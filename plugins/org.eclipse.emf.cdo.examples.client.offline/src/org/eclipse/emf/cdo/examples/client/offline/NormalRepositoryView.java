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
public class NormalRepositoryView extends AbstractView
{
  public static final String ID = "org.eclipse.emf.cdo.examples.client.offline.MasterView"; //$NON-NLS-1$

  private TreeViewer treeViewer;

  private ScrolledComposite details;

  public NormalRepositoryView()
  {
  }

  @Override
  protected void createPane(Composite parent)
  {
    ItemProvider<IRepository> itemProvider = new ContainerItemProvider<IRepository>()
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
    treeViewer.setInput(Application.NODE.getObjects().get(IRepository.class));

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
}
