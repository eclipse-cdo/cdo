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
package org.eclipse.emf.cdo.explorer.ui;

import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class DeleteDialog extends TitleAreaDialog
{
  private final AbstractElement[] elements;

  private Text text;

  private boolean deleteContents = true;

  public DeleteDialog(Shell parentShell, AbstractElement... elements)
  {
    super(parentShell);
    this.elements = elements;

    setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
  }

  public final boolean isDeleteContents()
  {
    return deleteContents;
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite area = (Composite)super.createDialogArea(parent);

    int size = elements.length;
    if (size != 0)
    {
      List<String> contents = new ArrayList<String>();
      for (AbstractElement element : elements)
      {
        collectContents(contents, element.getFolder());
      }

      Collections.sort(contents);
      StringBuilder builder = new StringBuilder();

      for (String path : contents)
      {
        if (builder.length() != 0)
        {
          builder.append('\n');
        }

        builder.append(path);
      }

      String type = elements[0] instanceof CDORepository ? "Repository" : "Checkout";
      String types = elements[0] instanceof CDORepository ? "Repositories" : "Checkouts";

      String title = "Delete " + (size == 1 ? type : types);
      getShell().setText(title);
      setTitle(title);
      setTitleImage(OM.getImage("icons/wiz/delete.gif"));
      setMessage("Are you sure you want to delete " + (size == 1 ? "this" : "these") + " " + size + " "
          + (size == 1 ? type : types) + "?");

      Composite container = new Composite(area, SWT.NONE);
      container.setLayoutData(new GridData(GridData.FILL_BOTH));
      GridLayout containerGridLayout = new GridLayout();
      containerGridLayout.marginWidth = 10;
      containerGridLayout.marginHeight = 10;
      container.setLayout(containerGridLayout);

      final Button deleteContentsButton = new Button(container, SWT.CHECK);
      deleteContentsButton.setText("&Delete " + type.toLowerCase() + " contents on disk");
      deleteContentsButton.setSelection(true);
      deleteContentsButton.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          deleteContents = deleteContentsButton.getSelection();
        }
      });

      new Label(container, SWT.NONE);
      new Label(container, SWT.NONE).setText(type + " &contents:");

      text = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI);
      text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      text.setText(builder.toString());

      deleteContentsButton.setFocus();
    }

    return area;
  }

  private void collectContents(List<String> contents, File folder)
  {
    if (folder.isDirectory())
    {
      File[] children = folder.listFiles();
      if (children != null)
      {
        for (File child : children)
        {
          if (child.isDirectory())
          {
            collectContents(contents, child);
          }
          else
          {
            contents.add(child.getAbsolutePath());
          }
        }
      }
    }
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(700, 400);
  }
}
