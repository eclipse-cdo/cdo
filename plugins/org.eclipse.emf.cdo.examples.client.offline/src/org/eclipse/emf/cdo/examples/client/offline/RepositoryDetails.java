/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.client.offline;

import org.eclipse.emf.cdo.examples.client.offline.nodes.NodeType;
import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 */
public class RepositoryDetails extends Composite
{
  private IRepository repository;

  private Text node;

  private Text name;

  private Text type;

  private Text state;

  private Button connected;

  public RepositoryDetails(Composite parent, IRepository repository, final Control enablementControl)
  {
    super(parent, SWT.NONE);
    this.repository = repository;

    setLayout(new GridLayout(2, false));

    Label lblNodeName = new Label(this, SWT.NONE);
    lblNodeName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblNodeName.setText("Node:");

    node = new Text(this, SWT.BORDER);
    node.setEditable(false);
    node.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    node.setText(Application.NODE.getName());

    Label lblName = new Label(this, SWT.NONE);
    lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblName.setText("Name:");

    name = new Text(this, SWT.BORDER);
    name.setEditable(false);
    name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    name.setText(repository.getName());

    Label lblType = new Label(this, SWT.NONE);
    lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblType.setText("Type:");

    type = new Text(this, SWT.BORDER);
    type.setEditable(false);
    type.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblState = new Label(this, SWT.NONE);
    lblState.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblState.setText("State:");

    state = new Text(this, SWT.BORDER);
    state.setEditable(false);
    state.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    NodeType nodeType = Application.NODE.getType();
    if (nodeType instanceof NodeType.Server && !(nodeType instanceof NodeType.FailoverMonitor))
    {
      final NodeType.Server server = (NodeType.Server)nodeType;

      new Label(this, SWT.NONE);

      connected = new Button(this, SWT.CHECK);
      connected.setText("Connected to Network");
      connected.setSelection(true);
      connected.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          boolean on = connected.getSelection();
          enablementControl.setEnabled(on);

          server.setConnectedToNetwork(Application.NODE, on);
        }
      });
    }

    updateUI();
    repository.addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (!isDisposed())
        {
          getDisplay().asyncExec(new Runnable()
          {
            @Override
            public void run()
            {
              updateUI();
            }
          });
        }
      }
    });
  }

  private void updateUI()
  {
    type.setText(repository.getType().toString());
    state.setText(repository.getState().toString());
  }
}
