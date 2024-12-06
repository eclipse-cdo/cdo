/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.internal.ui.dialogs.DeleteBranchDialog;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.Authorizer.AuthorizerContext;

import org.eclipse.net4j.util.ui.widgets.AbstractDialog;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * An abstract base class for {@link AbstractDialog dialogs} that asynchronously update their UI with
 * {@link Authorizer#getAuthorization(Object) authorization} information.
 *
 * @author Eike Stepper
 * @since 4.11
 * @see DeleteBranchDialog
 */
public abstract class AbstractAuthorizingDialog<E> extends AbstractDialog implements AuthorizerContext<E>
{
  protected final Authorizer<E> authorizer;

  public AbstractAuthorizingDialog(Shell parentShell, CDOSession session)
  {
    super(parentShell);
    authorizer = new Authorizer<>(this, session);
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(450, 300);
  }

  @Override
  protected final void createUI(Composite container)
  {
    doCreateUI(container);
    authorizer.authorize();
  }

  protected abstract void doCreateUI(Composite container);

  @Override
  protected Control createButtonBar(Composite parent)
  {
    Control buttonBar = super.createButtonBar(parent);
    enableOKButton(!authorizer.isAuthorizing());
    return buttonBar;
  }

  @Override
  protected void doValidate() throws Exception
  {
    authorizer.validate();
    super.doValidate();
  }

  @Override
  public String getAuthorizationDeniedMessage()
  {
    return "Authorization denied.";
  }
}
