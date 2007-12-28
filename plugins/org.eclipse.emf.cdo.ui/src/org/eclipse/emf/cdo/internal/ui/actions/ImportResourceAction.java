/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;

import org.eclipse.emf.common.ui.dialogs.ResourceDialog;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.IWorkbenchPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ImportResourceAction extends ViewAction
{
  public static final String ID = "import-resource";

  private static final String TITLE = "Import Resource";

  private URI sourceURI;

  private String targetPath;

  public ImportResourceAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE + INTERACTIVE, "Import a CDO resource", null, view);
    setId(ID);
  }

  @Override
  protected void preRun() throws Exception
  {
    ImportResourceDialog dialog = new ImportResourceDialog(getShell(), TITLE, SWT.OPEN);
    if (dialog.open() == ImportResourceDialog.OK)
    {
      List<URI> uris = dialog.getURIs();
      if (uris.size() == 1)
      {
        sourceURI = uris.get(0);
        targetPath = dialog.getTargetPath();
      }
      else
      {
        MessageDialog.openError(getShell(), TITLE, "A single URI must be entered!");
        cancel();
      }
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun() throws Exception
  {
    CDOTransaction transaction = getTransaction();

    // Source ResourceSet
    ResourceSet sourceSet = new ResourceSetImpl();
    Map<String, Object> map = sourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    map.put("*", new XMIResourceFactoryImpl());
    sourceSet.setPackageRegistry(transaction.getSession().getPackageRegistry());

    // Source Resource
    Resource source = sourceSet.getResource(sourceURI, true);
    List<EObject> sourceContents = new ArrayList<EObject>(source.getContents());

    // Target Resource
    Resource target = transaction.createResource(targetPath);
    EList<EObject> targetContents = target.getContents();

    // Move contents over
    for (EObject root : sourceContents)
    {
      targetContents.add(root);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ImportResourceDialog extends ResourceDialog
  {
    private String targetPath = "/";

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
      label.setText("Target path:");
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

      return composite;
    }
  }
}
