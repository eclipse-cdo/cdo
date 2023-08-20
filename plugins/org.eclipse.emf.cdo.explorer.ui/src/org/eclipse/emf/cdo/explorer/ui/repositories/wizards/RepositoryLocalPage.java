/*
 * Copyright (c) 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.repositories.wizards;

import org.eclipse.emf.cdo.common.util.Support;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.IDGeneration;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.VersioningMode;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;
import org.eclipse.emf.cdo.explorer.ui.checkouts.wizards.CheckoutWizardPage.ValidationProblem;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryImpl;
import org.eclipse.emf.cdo.internal.explorer.repositories.LocalCDORepository;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;
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
  private static final boolean SERVER_SECURITY_AVAILABLE = Support.SERVER_SECURITY.isAvailable();

  private static final boolean SERVER_LM_AVAILABLE = Support.SERVER_LM.isAvailable();

  private static final String DEFAULT_SECURITY_DESCRIPTION = OMPlatform.INSTANCE.getProperty( //
      "org.eclipse.emf.cdo.explorer.ui.repositories.wizards.RepositoryLocalPage.DEFAULT_SECURITY_DESCRIPTION", //
      "security:annotation:home(/home)");

  private static final String DEFAULT_LM_DESCRIPTION = OMPlatform.INSTANCE.getProperty( //
      "org.eclipse.emf.cdo.explorer.ui.repositories.wizards.RepositoryLocalPage.DEFAULT_LM_DESCRIPTION", //
      "system:module.md:dropTypes(Tag,Milestone):releaseTypes(Release)");

  private final Set<Integer> configuredPorts = new HashSet<>();

  private Text nameText;

  private Button normalButton;

  private Button auditingButton;

  private Button branchingButton;

  private Button counterButton;

  private Button uuidButton;

  private TextAndDisable securityText;

  private TextAndDisable lmText;

  private TextAndDisable portText;

  public RepositoryLocalPage()
  {
    super("local", "Local Repository");
    setTitle("New Local Repository");
    setMessage("Enter label and database parameters of the new local repository.");

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
    nameText.setText(getUniqueRepositoryName());
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
    modeGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
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
    idGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
    idGroup.setText("ID Generation");

    counterButton = new Button(idGroup, SWT.RADIO);
    counterButton.setText("Counter (efficient)");
    counterButton.addSelectionListener(this);

    uuidButton = new Button(idGroup, SWT.RADIO);
    uuidButton.setText("UUID (replicable)");
    uuidButton.setSelection(true);
    uuidButton.addSelectionListener(this);

    if (SERVER_SECURITY_AVAILABLE)
    {
      createLabel(container, "Security:");
      securityText = new TextAndDisable(container, SWT.BORDER, null)
      {
        @Override
        protected GridData createTextLayoutData()
        {
          return new GridData(SWT.FILL, SWT.CENTER, true, false);
        }
      };

      securityText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      securityText.setValue(DEFAULT_SECURITY_DESCRIPTION);
      securityText.setDisabled(true);
      securityText.addModifyListener(this);
      securityText.addSelectionListener(this);
    }

    if (SERVER_LM_AVAILABLE)
    {
      createLabel(container, "Lifecycle Management:");
      lmText = new TextAndDisable(container, SWT.BORDER, null)
      {
        @Override
        protected GridData createTextLayoutData()
        {
          return new GridData(SWT.FILL, SWT.CENTER, true, false);
        }
      };

      lmText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      lmText.setValue(DEFAULT_LM_DESCRIPTION);
      lmText.setDisabled(true);
      lmText.addModifyListener(this);
      lmText.addSelectionListener(this);
    }

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
      throw new ValidationProblem("Repository name is empty.");
    }

    for (CDORepository repository : CDOExplorerUtil.getRepositoryManager().getRepositories())
    {
      if (CDORepository.TYPE_LOCAL.equals(repository.getType()))
      {
        if (name.equals(repository.getName()))
        {
          throw new ValidationProblem("Repository name is not unique.");
        }
      }
    }

    properties.setProperty(LocalCDORepository.PROP_NAME, name);

    if (normalButton.getSelection())
    {
      properties.setProperty(CDORepositoryImpl.PROP_VERSIONING_MODE, VersioningMode.Normal.toString());
    }
    else if (auditingButton.getSelection())
    {
      properties.setProperty(CDORepositoryImpl.PROP_VERSIONING_MODE, VersioningMode.Auditing.toString());
    }
    else if (branchingButton.getSelection())
    {
      properties.setProperty(CDORepositoryImpl.PROP_VERSIONING_MODE, VersioningMode.Branching.toString());
    }

    if (counterButton.getSelection())
    {
      properties.setProperty(CDORepositoryImpl.PROP_ID_GENERATION, IDGeneration.Counter.toString());
    }
    else if (uuidButton.getSelection())
    {
      properties.setProperty(CDORepositoryImpl.PROP_ID_GENERATION, IDGeneration.UUID.toString());
    }

    if (SERVER_SECURITY_AVAILABLE)
    {
      boolean securityDisabled = securityText.isDisabled();
      properties.setProperty(LocalCDORepository.PROP_SECURITY_DISABLED, Boolean.toString(securityDisabled));

      if (!securityDisabled)
      {
        String securityConfig = securityText.getText().getText().trim();
        if (securityConfig.isBlank())
        {
          throw new ValidationProblem("Security configuration must start with the realm path.");
        }

        properties.setProperty(LocalCDORepository.PROP_SECURITY_CONFIG, securityConfig);
      }
    }

    if (SERVER_LM_AVAILABLE)
    {
      boolean branching = branchingButton.getSelection();
      lmText.getButton().setEnabled(branching);

      boolean lmEnabled = !lmText.isDisabled();
      if (lmEnabled && !branching)
      {
        lmEnabled = false;
        lmText.setDisabled(true);
      }

      properties.setProperty(LocalCDORepository.PROP_LM_ENABLED, Boolean.toString(lmEnabled));

      if (lmEnabled)
      {
        properties.setProperty(LocalCDORepository.PROP_LM_CONFIG, lmText.getText().getText().trim());
      }
    }

    boolean tcpPortDisabled = portText.isDisabled();
    properties.setProperty(LocalCDORepository.PROP_TCP_DISABLED, Boolean.toString(tcpPortDisabled));

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
        throw new ValidationProblem("Invalid TCP port.");
      }

      if (!isFreePort(port))
      {
        throw new ValidationProblem("TCP port " + port + " is not available.");
      }

      properties.setProperty(LocalCDORepository.PROP_TCP_PORT, Integer.toString(port));
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

  public static String getUniqueRepositoryName()
  {
    Set<String> names = new HashSet<>();

    CDORepositoryManager repositoryManager = CDOExplorerUtil.getRepositoryManager();
    for (CDORepository repository : repositoryManager.getRepositories())
    {
      if (CDORepository.TYPE_LOCAL.equals(repository.getType()))
      {
        names.add(repository.getName());
      }
    }

    String defaultName = "repo";
    for (int i = 1; i < Integer.MAX_VALUE; i++)
    {
      String name = i == 1 ? defaultName : defaultName + i;
      if (!names.contains(name))
      {
        return name;
      }
    }

    throw new IllegalStateException("Too many repositories");
  }
}
