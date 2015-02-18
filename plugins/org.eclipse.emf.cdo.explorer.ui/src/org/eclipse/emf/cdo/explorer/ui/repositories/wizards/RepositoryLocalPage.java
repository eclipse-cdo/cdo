/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.repositories.wizards;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;
import org.eclipse.emf.cdo.internal.explorer.repositories.LocalCDORepository;
import org.eclipse.emf.cdo.internal.explorer.repositories.LocalCDORepository.IDGeneration;
import org.eclipse.emf.cdo.internal.explorer.repositories.LocalCDORepository.VersioningMode;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.ui.widgets.TextAndDisable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class RepositoryLocalPage extends AbstractRepositoryPage
{
  private final Set<Integer> configuredPorts = new HashSet<Integer>();

  private Text nameText;

  private Button normalButton;

  private Button auditingButton;

  private Button branchingButton;

  private Button counterButton;

  private Button uuidButton;

  private TextAndDisable portText;

  public RepositoryLocalPage()
  {
    super("local", "Local Repository 1");
    setTitle("New Local Repository");
    setMessage("Enter the label and the connection parameters of the new remote location.");

    CDORepositoryManager repositoryManager = CDOExplorerUtil.getRepositoryManager();
    for (CDORepository repository : repositoryManager.getRepositories())
    {
      try
      {
        URI uri = new URI(repository.getURI());
        if ("tcp".equals(uri.getScheme()))
        {
          String host = uri.getHost();
          if ("localhost".equals(host) || "127.0.0.1".equals(host))
          {
            configuredPorts.add(uri.getPort());
          }
        }
      }
      catch (URISyntaxException ex)
      {
        //$FALL-THROUGH$
      }
    }

  }

  @Override
  protected void fillPage(Composite container)
  {
    createLabel(container, "Repository name:");
    nameText = new Text(container, SWT.BORDER);
    nameText.setText("repo2");
    nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    nameText.addModifyListener(this);

    new Label(container, SWT.NONE);

    GridLayout layout = new GridLayout(2, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;

    Composite composite = new Composite(container, SWT.NONE);
    composite.setLayout(layout);

    Group modeGroup = new Group(composite, SWT.NONE);
    modeGroup.setLayout(new GridLayout(1, false));
    modeGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
    modeGroup.setText("Versioning Mode");

    normalButton = new Button(modeGroup, SWT.RADIO);
    normalButton.setText("Normal (no history)");
    normalButton.addSelectionListener(this);

    auditingButton = new Button(modeGroup, SWT.RADIO);
    auditingButton.setText("Auditing (linear history)");
    auditingButton.addSelectionListener(this);

    branchingButton = new Button(modeGroup, SWT.RADIO);
    branchingButton.setText("Branching (history tree)");
    branchingButton.setSelection(true);
    branchingButton.addSelectionListener(this);

    Group idGroup = new Group(composite, SWT.NONE);
    idGroup.setLayout(new GridLayout(1, false));
    idGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
    idGroup.setText("ID Generation");

    counterButton = new Button(idGroup, SWT.RADIO);
    counterButton.setText("Counter (efficient)");
    counterButton.addSelectionListener(this);

    uuidButton = new Button(idGroup, SWT.RADIO);
    uuidButton.setText("UUID (replicable)");
    uuidButton.setSelection(true);
    uuidButton.addSelectionListener(this);

    createLabel(container, "TCP port:");
    portText = new TextAndDisable(container, SWT.BORDER, null);
    portText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    portText.setValue(Integer.toString(getDefaultPort()));
    portText.setDisabled(true);
    portText.addModifyListener(this);
    portText.addSelectionListener(this);
  }

  @Override
  protected void doValidate(Properties properties) throws Exception
  {
    super.doValidate(properties);

    String name = nameText.getText();
    if (StringUtil.isEmpty(name))
    {
      throw new Exception("Name is empty.");
    }

    properties.put(LocalCDORepository.PROP_NAME, name);

    if (normalButton.getSelection())
    {
      properties.put(LocalCDORepository.PROP_VERSIONING_MODE, VersioningMode.Normal.toString());
    }
    else if (auditingButton.getSelection())
    {
      properties.put(LocalCDORepository.PROP_VERSIONING_MODE, VersioningMode.Auditing.toString());
    }
    else if (branchingButton.getSelection())
    {
      properties.put(LocalCDORepository.PROP_VERSIONING_MODE, VersioningMode.Branching.toString());
    }

    if (counterButton.getSelection())
    {
      properties.put(LocalCDORepository.PROP_ID_GENERATION, IDGeneration.Counter.toString());
    }
    else if (uuidButton.getSelection())
    {
      properties.put(LocalCDORepository.PROP_ID_GENERATION, IDGeneration.UUID.toString());
    }

    boolean tcpPortDisabled = portText.isDisabled();
    properties.put(LocalCDORepository.PROP_TCP_DISABLED, Boolean.toString(tcpPortDisabled));

    if (!tcpPortDisabled)
    {
      int port;

      try
      {
        port = Integer.parseInt(portText.getValue());
        if (port < 0 || port > 0xffff)
        {
          throw new Exception();
        }
      }
      catch (Exception ex)
      {
        throw new Exception("Invalid TCP port.");
      }

      if (!isFreePort(port))
      {
        throw new Exception("TCP port " + port + " is not available.");
      }

      properties.put(LocalCDORepository.PROP_TCP_PORT, port);
    }
  }

  private int getDefaultPort()
  {
    for (int port = 2036; port < 0xffff; port++)
    {
      if (isFreePort(port))
      {
        return port;
      }
    }

    return 2036;
  }

  private boolean isFreePort(int port)
  {
    if (configuredPorts.contains(port))
    {
      return false;
    }

    return IOUtil.isFreePort(port);
  }
}
