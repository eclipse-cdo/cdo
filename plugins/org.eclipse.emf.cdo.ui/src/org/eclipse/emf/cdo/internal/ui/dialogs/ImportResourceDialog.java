/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.internal.ui.messages.Messages;

import org.eclipse.emf.common.ui.dialogs.ResourceDialog;
import org.eclipse.emf.common.util.URI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class ImportResourceDialog extends ResourceDialog
{
  private String targetPath = "/"; //$NON-NLS-1$

  private Text targetText;

  public ImportResourceDialog(Shell parent, String title, int style)
  {
    super(parent, title, style);
  }

  public String getTargetPath()
  {
    return targetPath;
  }

  public void setTargetPath(String targetPath)
  {
    this.targetPath = targetPath;
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite composite = (Composite)super.createDialogArea(parent);

    Label separatorLabel1 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
    {
      FormData data = new FormData();
      data.top = new FormAttachment(uriField, (int)(1.5 * CONTROL_OFFSET));
      data.left = new FormAttachment(0, -CONTROL_OFFSET);
      data.right = new FormAttachment(100, CONTROL_OFFSET);
      separatorLabel1.setLayoutData(data);
    }

    Label label = new Label(composite, SWT.NONE);
    label.setText(Messages.getString("ImportResourceDialog.1")); //$NON-NLS-1$
    {
      FormData data = new FormData();
      data.top = new FormAttachment(separatorLabel1, CONTROL_OFFSET);
      data.left = new FormAttachment(0, CONTROL_OFFSET);
      data.right = new FormAttachment(100, -CONTROL_OFFSET);
      label.setLayoutData(data);
    }

    targetText = new Text(composite, SWT.BORDER);
    {
      FormData data = new FormData();
      data.top = new FormAttachment(label, CONTROL_OFFSET);
      data.left = new FormAttachment(0, CONTROL_OFFSET);
      data.right = new FormAttachment(100, -CONTROL_OFFSET);
      targetText.setLayoutData(data);
      targetText.setText(targetPath);
      targetText.addModifyListener(new ModifyListener()
      {
        @Override
        public void modifyText(ModifyEvent e)
        {
          targetPath = targetText.getText();
        }
      });
    }

    Label separatorLabel2 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
    {
      FormData data = new FormData();
      data.top = new FormAttachment(targetText, (int)(1.5 * CONTROL_OFFSET));
      data.left = new FormAttachment(0, -CONTROL_OFFSET);
      data.right = new FormAttachment(100, CONTROL_OFFSET);
      separatorLabel2.setLayoutData(data);
    }

    uriField.addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent e)
      {
        List<URI> uris = getURIs();
        if (uris.size() == 1)
        {
          URI uri = uris.get(0);
          String name = uri.lastSegment();
          targetText.setText("/" + name);
        }
      }
    });

    return composite;
  }
}
