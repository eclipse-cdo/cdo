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
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.emf.cdo.releng.internal.setup.ui.BundlePoolAnalyzer.Artifact;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import java.util.List;

/**
 * @author Eike Stepper
 */
public final class AdditionalURIPrompterDialog extends AbstractSetupDialog
{
  private List<Artifact> artifacts;

  private Table artifactTable;

  private Table repositoryTable;

  private Composite artifactComposite;

  private Composite repositoryComposite;

  public AdditionalURIPrompterDialog(Shell parentShell, List<Artifact> artifacts)
  {
    super(parentShell, "Bundle Pool Management", 400, 500);
    setShellStyle(SWT.TITLE | SWT.MAX | SWT.RESIZE | SWT.BORDER | SWT.APPLICATION_MODAL);
    this.artifacts = artifacts;
  }

  @Override
  protected String getDefaultMessage()
  {
    return "Some artifacts could not be downloaded from the repositories listed in the profiles.\n"
        + "Select additional repositories and try again.";
  }

  @Override
  protected void createUI(Composite parent)
  {
    GridLayout gridLayout = (GridLayout)parent.getLayout();
    gridLayout.marginWidth = 10;
    gridLayout.marginHeight = 10;

    SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
    sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    artifactComposite = new Composite(sashForm, SWT.NONE);
    GridLayout gl_artifactComposite = new GridLayout(1, false);
    gl_artifactComposite.marginWidth = 0;
    gl_artifactComposite.marginHeight = 0;
    artifactComposite.setLayout(gl_artifactComposite);

    Label artifactLabel = new Label(artifactComposite, SWT.NONE);
    artifactLabel.setBounds(0, 0, 55, 15);
    artifactLabel.setText("Remaining Artifacts:");

    TableViewer artifactViewer = new TableViewer(artifactComposite, SWT.BORDER | SWT.FULL_SELECTION);
    artifactTable = artifactViewer.getTable();
    artifactTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    artifactViewer.setLabelProvider(new BundlePoolComposite.TableLabelProvider(parent.getDisplay()));
    artifactViewer.setContentProvider(new ArrayContentProvider());
    artifactViewer.setInput(artifacts);

    repositoryComposite = new Composite(sashForm, SWT.NONE);
    GridLayout gl_repositoryComposite = new GridLayout(1, false);
    gl_repositoryComposite.marginWidth = 0;
    gl_repositoryComposite.marginHeight = 0;
    repositoryComposite.setLayout(gl_repositoryComposite);

    Label repositoryLabel = new Label(repositoryComposite, SWT.NONE);
    repositoryLabel.setBounds(0, 0, 55, 15);
    repositoryLabel.setText("Additional Repositories:");

    CheckboxTableViewer repositoryViewer = CheckboxTableViewer.newCheckList(repositoryComposite, SWT.BORDER
        | SWT.FULL_SELECTION);
    repositoryTable = repositoryViewer.getTable();
    repositoryTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    repositoryViewer.setLabelProvider(new BundlePoolComposite.TableLabelProvider(parent.getDisplay()));
    repositoryViewer.setContentProvider(new ArrayContentProvider());
    repositoryViewer.setInput(artifacts.get(0).getBundlePool().getRepositoryURIs());

    sashForm.setWeights(new int[] { 1, 2 });
  }
}
