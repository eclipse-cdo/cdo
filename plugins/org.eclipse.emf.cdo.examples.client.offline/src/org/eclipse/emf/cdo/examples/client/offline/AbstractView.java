/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.client.offline;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ExampleSWTResourceManager;

/**
 * @author Eike Stepper
 */
public abstract class AbstractView<T extends IContainer<?>> extends ViewPart
{
  private final Class<T> objectType;

  private T object;

  private Text events;

  public AbstractView(Class<T> objectType)
  {
    this.objectType = objectType;
    object = Application.NODE.getObject(objectType);
  }

  public Class<T> getObjectType()
  {
    return objectType;
  }

  public T getObject()
  {
    return object;
  }

  @Override
  public void createPartControl(Composite parent)
  {
    Composite container = new Composite(parent, SWT.NONE);
    container.setLayout(new FillLayout(SWT.HORIZONTAL));

    SashForm sash = new SashForm(container, SWT.SMOOTH | SWT.VERTICAL);

    createPane(sash, object);

    events = new Text(sash, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
    events.setBackground(ExampleSWTResourceManager.getColor(SWT.COLOR_WHITE));

    sash.setWeights(new int[] { 2, 1 });

    createActions();
    initializeToolBar(getViewSite().getActionBars().getToolBarManager());
    initializeMenu(getViewSite().getActionBars().getMenuManager());
  }

  protected void addEvent(final IEvent event)
  {
    getSite().getShell().getDisplay().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        String text = events.getText();
        events.setText(event + StringUtil.NL + text);
      }
    });
  }

  protected void createActions()
  {
    // Create the actions
  }

  protected void initializeToolBar(IToolBarManager toolbarManager)
  {
  }

  protected void initializeMenu(IMenuManager menuManager)
  {
  }

  protected abstract void createPane(Composite parent, T object);
}
