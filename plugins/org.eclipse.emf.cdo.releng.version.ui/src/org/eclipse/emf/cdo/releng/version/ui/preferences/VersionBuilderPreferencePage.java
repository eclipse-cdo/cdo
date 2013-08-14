/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version.ui.preferences;

import org.eclipse.emf.cdo.releng.internal.version.Activator;
import org.eclipse.emf.cdo.releng.version.VersionUtil;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Eike Stepper
 */
public class VersionBuilderPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
  public VersionBuilderPreferencePage()
  {
    super("<taken from plugin.xml>");
    setDescription("Manage ignored releases:");
  }

  public void init(IWorkbench workbench)
  {
    // Do nothing
  }

  @Override
  protected Control createContents(Composite parent)
  {
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    layout.numColumns = 2;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(layout);

    final List list = new List(composite, SWT.BORDER | SWT.SINGLE);
    list.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

    java.util.List<String> ignoredReleases = new ArrayList<String>(Activator.getIgnoredReleases());
    Collections.sort(ignoredReleases);
    for (String releasePath : ignoredReleases)
    {
      list.add(releasePath);
    }

    final Button button = new Button(composite, SWT.PUSH);
    button.setText("Remove");
    button.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    button.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        String releasePath = getSelectedReleasePath(list);
        if (releasePath != null)
        {
          list.remove(releasePath);
          Activator.getIgnoredReleases().remove(releasePath);
          VersionUtil.cleanReleaseProjects(releasePath);
          adjustEnablement(list, button);
        }
      }
    });

    adjustEnablement(list, button);
    list.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        adjustEnablement(list, button);
      }
    });

    return composite;
  }

  private String getSelectedReleasePath(List list)
  {
    String[] selection = list.getSelection();
    if (selection != null && selection.length != 0)
    {
      return selection[0];
    }

    return null;
  }

  private void adjustEnablement(final List list, final Button button)
  {
    button.setEnabled(getSelectedReleasePath(list) != null);
  }
}
