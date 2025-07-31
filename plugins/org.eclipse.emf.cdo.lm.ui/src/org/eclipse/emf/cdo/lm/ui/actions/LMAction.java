/*
 * Copyright (c) 2022, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.actions;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.util.LMLocalOperationAuthorizer;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.security.operations.AuthorizableOperation;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public abstract class LMAction<CONTEXT extends CDOObject> extends LongRunningAction
{
  private final String bannerMessage;

  private final String bannerImagePath;

  private Image bannerImage;

  private LMDialog dialog;

  private final CONTEXT context;

  public LMAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image, String bannerMessage, String bannerImagePath, CONTEXT context)
  {
    super(page, text, toolTipText, image);
    this.bannerImagePath = bannerImagePath;
    this.bannerMessage = bannerMessage;
    this.context = context;

    String veto = authorize(getAuthorizableOperationID(), context);
    if (veto != null)
    {
      setEnabled(false);
      setToolTipText(veto + ": " + getToolTipText());
    }
  }

  public CONTEXT getContext()
  {
    return context;
  }

  public String getAuthorizableOperationID()
  {
    return null;
  }

  @Override
  protected void preRun() throws Exception
  {
    if (isDialogNeeded())
    {
      TitleAreaDialog dialog = new LMDialog(getShell());

      if (dialog.open() == Window.CANCEL)
      {
        cancel();
      }
    }
  }

  @Override
  protected final void doRun(IProgressMonitor monitor) throws Exception
  {
    doRun(context, monitor);
  }

  protected abstract void doRun(CONTEXT context, IProgressMonitor monitor) throws Exception;

  protected Point getInitialSize(LMDialog dialog)
  {
    return dialog._getInitialSize();
  }

  protected Point getInitialLocation(LMDialog dialog, Point initialSize)
  {
    return dialog._getInitialLocation(initialSize);
  }

  protected void configureShell(LMDialog dialog, Shell newShell)
  {
    dialog._configureShell(newShell);
  }

  protected Image getBannerImage(LMDialog dialog)
  {
    if (bannerImage == null && bannerImagePath != null)
    {
      ImageDescriptor descriptor = getImageDescriptor(bannerImagePath);
      bannerImage = descriptor.createImage(getDisplay());
    }

    return bannerImage;
  }

  protected ImageDescriptor getImageDescriptor(String imagePath)
  {
    return OM.Activator.INSTANCE.loadImageDescriptor(imagePath);
  }

  protected boolean isDialogNeeded()
  {
    return true;
  }

  protected abstract void fillDialogArea(LMDialog dialog, Composite parent);

  protected final Button newCheckBox(Composite parent, String text)
  {
    new Label(parent, SWT.NONE);

    Button checkBox = new Button(parent, SWT.CHECK);
    checkBox.setText(text);
    checkBox.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).create());
    return checkBox;
  }

  protected final void openError(String message)
  {
    MessageDialog.openError(getShell(), "Lifecycle Management", message);
  }

  private final void validateDialog(boolean withMessage)
  {
    if (dialog != null)
    {
      String errorMessage = doValidate(dialog);
      if (errorMessage != null)
      {
        if (withMessage)
        {
          dialog.setErrorMessage(errorMessage);
        }

        enableOkButton(false);
      }
      else
      {
        dialog.setErrorMessage(null);
        enableOkButton(true);
      }
    }
  }

  protected final void validateDialog()
  {
    validateDialog(true);
  }

  protected String doValidate(LMDialog dialog)
  {
    return null;
  }

  protected void dialogClosed(LMDialog dialog)
  {
    if (bannerImage != null)
    {
      bannerImage.dispose();
    }
  }

  private void enableOkButton(boolean enabled)
  {
    Button okButton = dialog.getOkButton();
    if (okButton != null)
    {
      okButton.setEnabled(enabled);
    }
    else
    {
      UIUtil.asyncExec(getDisplay(), () -> {
        Button button = dialog.getOkButton();
        if (button != null && !button.isDisposed())
        {
          button.setEnabled(enabled);
        }
      });
    }
  }

  public static String authorize(String operationID, CDOObject context)
  {
    if (operationID != null)
    {
      CDOSession session = CDOUtil.getSession(context);
      if (session != null)
      {
        AuthorizableOperation operation = AuthorizableOperation.builder(operationID) //
            .parameter(LMLocalOperationAuthorizer.CONTEXT_PARAMETER, context) //
            .build();

        return session.authorizeOperations(operation)[0];
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  protected class LMDialog extends TitleAreaDialog
  {
    protected LMDialog(Shell parentShell)
    {
      super(parentShell);
      dialog = this;
    }

    @Override
    protected Point getInitialSize()
    {
      return LMAction.this.getInitialSize(this);
    }

    @Override
    protected Point getInitialLocation(Point initialSize)
    {
      return LMAction.this.getInitialLocation(this, initialSize);
    }

    @Override
    protected boolean isResizable()
    {
      return true;
    }

    @Override
    protected void configureShell(Shell newShell)
    {
      LMAction.this.configureShell(this, newShell);
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
      Shell shell = getShell();
      shell.setText("Lifecycle Management");

      String text = getText();
      if (text.endsWith(INTERACTIVE))
      {
        text = text.substring(0, text.length() - INTERACTIVE.length());
      }

      setTitle(text);

      Image image = getBannerImage(this);
      if (image != null)
      {
        setTitleImage(image);
      }

      if (bannerMessage != null)
      {
        setMessage(bannerMessage);
      }

      Composite area = (Composite)super.createDialogArea(parent);

      Composite composite = new Composite(area, SWT.NONE);
      composite.setLayoutData(new GridData(GridData.FILL_BOTH));
      composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(10, 10).create());

      fillDialogArea(this, composite);
      UIUtil.asyncExec(shell.getDisplay(), () -> validateDialog(false));

      return area;
    }

    public Button getOkButton()
    {
      return getButton(IDialogConstants.OK_ID);
    }

    @Override
    public boolean close()
    {
      dialogClosed(this);
      dialog = null;
      return super.close();
    }

    private Point _getInitialSize()
    {
      return super.getInitialSize();
    }

    private Point _getInitialLocation(Point initialSize)
    {
      return super.getInitialLocation(initialSize);
    }

    private void _configureShell(Shell newShell)
    {
      super.configureShell(newShell);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class NewElement<CONTEXT extends CDOObject> extends LMAction<CONTEXT>
  {
    private final StructuredViewer viewer;

    public NewElement(IWorkbenchPage page, StructuredViewer viewer, //
        String text, String toolTipText, ImageDescriptor image, //
        String bannerMessage, String bannerImagePath, CONTEXT context)
    {
      super(page, text, toolTipText, image, bannerMessage, bannerImagePath, context);
      this.viewer = viewer;
    }

    @Override
    protected final void doRun(CONTEXT context, IProgressMonitor monitor) throws Exception
    {
      CDOObject element = newElement(context, monitor);
      if (element != null)
      {
        UIUtil.asyncExec(() -> viewer.setSelection(new StructuredSelection(element)));
      }
    }

    protected abstract CDOObject newElement(CONTEXT context, IProgressMonitor monitor) throws Exception;
  }
}
