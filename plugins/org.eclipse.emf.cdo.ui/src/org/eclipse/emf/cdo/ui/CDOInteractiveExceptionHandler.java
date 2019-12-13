/*
 * Copyright (c) 2009-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.text.MessageFormat;

/**
 * A <code>CDOSession</code> {@link org.eclipse.emf.cdo.session.CDOSession.ExceptionHandler ExceptionHandler}
 * implementation that shows exception information on a UI {@link ExceptionDialog dialog}
 *
 * @author Eike Stepper
 * @since 2.0
 * @see org.eclipse.emf.cdo.session.CDOSession.ExceptionHandler
 */
public class CDOInteractiveExceptionHandler implements CDOSession.ExceptionHandler, IElementProcessor
{
  public CDOInteractiveExceptionHandler()
  {
  }

  /**
   * @since 4.0
   */
  @Override
  public void handleException(final CDOSession session, final int attempt, Exception exception) throws Exception
  {
    final Exception[] result = { exception };
    Runnable runnable = new Runnable()
    {
      @Override
      public void run()
      {
        Dialog dialog = createDialog(session, attempt, result[0]);
        boolean retry = dialog.open() == Dialog.OK;
        if (retry)
        {
          result[0] = null;
        }
      }
    };

    Display display = UIUtil.getDisplay();
    if (display != null && !display.isDisposed())
    {
      if (display.getThread() == Thread.currentThread())
      {
        runnable.run();
      }
      else
      {
        display.syncExec(runnable);
      }
    }

    if (result[0] != null)
    {
      throw result[0];
    }
  }

  @Override
  public Object process(IManagedContainer container, String productGroup, String factoryType, String description, Object element)
  {
    if (element instanceof InternalCDOSession)
    {
      InternalCDOSession session = (InternalCDOSession)element;
      if (!session.isActive())
      {
        element = processSession(container, productGroup, factoryType, description, session);
      }
    }

    return element;
  }

  protected Object processSession(IManagedContainer container, String productGroup, String factoryType, String description, InternalCDOSession session)
  {
    if (session.getExceptionHandler() == null)
    {
      session.setExceptionHandler(this);
    }

    return session;
  }

  protected Shell getParentShell()
  {
    return UIUtil.getShell();
  }

  protected Dialog createDialog(CDOSession session, int attempt, Exception exception)
  {
    return new ExceptionDialog(getParentShell(), session, attempt, exception);
  }

  /**
   * A dialog that shows CDO related exceptions in a convenient manner.
   *
   * @author Eike Stepper
   * @see org.eclipse.emf.cdo.ui.CDOInteractiveExceptionHandler
   */
  public static class ExceptionDialog extends TitleAreaDialog
  {
    public static final String TITLE = Messages.getString("CDOInteractiveExceptionHandler.0"); //$NON-NLS-1$

    private CDOSession session;

    private int attempt;

    private Exception exception;

    public ExceptionDialog(Shell parentShell, CDOSession session, int attempt, Exception exception)
    {
      super(parentShell);
      setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
      this.session = session;
      this.attempt = attempt;
      this.exception = exception;
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
      getShell().setText(TITLE);

      String attemptsStr = attempt == 1 ? Messages.getString("CDOInteractiveExceptionHandler.1") //$NON-NLS-1$
          : Messages.getString("CDOInteractiveExceptionHandler.2"); //$NON-NLS-1$
      setTitle(MessageFormat.format(Messages.getString("CDOInteractiveExceptionHandler.3"), session, attempt, attemptsStr));//$NON-NLS-1$
      setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_PROTOCOL_PROBLEM));

      GridLayout layout = UIUtil.createGridLayout(1);
      layout.marginWidth = 10;
      layout.marginHeight = 10;

      Composite composite = new Composite((Composite)super.createDialogArea(parent), SWT.NONE);
      composite.setLayoutData(UIUtil.createGridData());
      composite.setLayout(layout);

      String str = IOUtil.toString(exception);
      str = str.replaceAll("\r", ""); //$NON-NLS-1$ //$NON-NLS-2$

      Text text = new Text(composite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
      text.setLayoutData(UIUtil.createGridData());
      text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_WHITE));
      text.setEditable(false);
      text.setText(str);

      return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
      createButton(parent, IDialogConstants.OK_ID, Messages.getString("CDOInteractiveExceptionHandler.4"), true); //$NON-NLS-1$
      createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("CDOInteractiveExceptionHandler.5"), false); //$NON-NLS-1$
    }
  }
}
