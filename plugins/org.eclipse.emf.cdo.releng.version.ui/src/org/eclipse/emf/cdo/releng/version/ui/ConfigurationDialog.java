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
package org.eclipse.emf.cdo.releng.version.ui;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 */
public class ConfigurationDialog extends TitleAreaDialog
{
  private static final String BUILDER_CONFIGURATION = "Version Builder Configuration";

  private Text releasePathText;

  private String releasePath;

  private Button ignoreMissingDependencyRangesButton;

  private boolean ignoreMissingDependencyRanges;

  private Button ignoreMissingExportVersionsButton;

  private boolean ignoreMissingExportVersions;

  private Button ignoreFeatureContentChangesButton;

  private boolean ignoreFeatureContentChanges;

  private Button ignoreFeatureContentRedundancyButton;

  private boolean ignoreFeatureContentRedundancy;

  public ConfigurationDialog(Shell shell)
  {
    super(shell);
    setHelpAvailable(false);
  }

  public String getReleasePath()
  {
    return releasePath;
  }

  public boolean isIgnoreMissingDependencyRanges()
  {
    return ignoreMissingDependencyRanges;
  }

  public boolean isIgnoreMissingExportVersions()
  {
    return ignoreMissingExportVersions;
  }

  public boolean isIgnoreFeatureContentRedundancy()
  {
    return ignoreFeatureContentRedundancy;
  }

  public boolean isIgnoreFeatureContentChanges()
  {
    return ignoreFeatureContentChanges;
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    newShell.setText(BUILDER_CONFIGURATION);
    super.configureShell(newShell);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    setTitle(BUILDER_CONFIGURATION);
    setMessage("Select a release specification file and check additional settings.");

    Composite dialogArea = (Composite)super.createDialogArea(parent);

    Composite composite = new Composite(dialogArea, SWT.NONE);
    composite.setLayout(new GridLayout());
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));

    new Label(composite, SWT.NONE).setText("Path to release specification file: ");
    releasePathText = new Text(composite, SWT.BORDER);
    releasePathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    ignoreMissingDependencyRangesButton = new Button(composite, SWT.CHECK);
    ignoreMissingDependencyRangesButton.setText("Ignore missing dependency version ranges");

    ignoreMissingExportVersionsButton = new Button(composite, SWT.CHECK);
    ignoreMissingExportVersionsButton.setText("Ignore missing package export versions");

    ignoreFeatureContentRedundancyButton = new Button(composite, SWT.CHECK);
    ignoreFeatureContentRedundancyButton.setText("Ignore feature content redundancy");

    ignoreFeatureContentChangesButton = new Button(composite, SWT.CHECK);
    ignoreFeatureContentChangesButton.setText("Ignore feature content changes");

    return dialogArea;
  }

  @Override
  protected void okPressed()
  {
    releasePath = releasePathText.getText();
    ignoreMissingDependencyRanges = ignoreMissingDependencyRangesButton.getSelection();
    ignoreMissingExportVersions = ignoreMissingExportVersionsButton.getSelection();
    ignoreFeatureContentRedundancy = ignoreFeatureContentRedundancyButton.getSelection();
    ignoreFeatureContentChanges = ignoreFeatureContentChangesButton.getSelection();
    super.okPressed();
  }
}
