/*
 * Copyright (c) 2015, 2016, 2019-2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.repositories.wizards;

import org.eclipse.emf.cdo.admin.CDOAdminClient;
import org.eclipse.emf.cdo.admin.CDOAdminClientManager;
import org.eclipse.emf.cdo.admin.CDOAdminClientRepository;
import org.eclipse.emf.cdo.admin.CDOAdminClientUtil;
import org.eclipse.emf.cdo.common.security.LoginPeekException;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.IDGeneration;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.VersioningMode;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.ViewerUtil;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.DelayingExecutor;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.NotAuthenticatedException;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;

import org.eclipse.emf.spi.cdo.InternalCDOSessionConfiguration;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class MasterRepositoryController
{
  private static final Image REPOSITORY_IMAGE = OM.getImage("icons/repository.gif");

  private static final Image EMPTY_IMAGE = OM.getImage("icons/empty.gif");

  private static final Image OK_IMAGE = OM.getImage("icons/ok.gif");

  private static final Image ERROR_IMAGE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);

  private static final int VALIDATING_WIDTH = 120;

  private static final int ADDRESS_VALIDATION_DELAY = OMPlatform.INSTANCE
      .getProperty("org.eclipse.emf.cdo.explorer.ui.repositories.wizards.MasterRepositoryController.ADDRESS_VALIDATION_DELAY", 400);

  private static final int REPOSITORY_VALIDATION_DELAY = OMPlatform.INSTANCE
      .getProperty("org.eclipse.emf.cdo.explorer.ui.repositories.wizards.MasterRepositoryController.REPOSITORY_VALIDATION_DELAY", 1000);

  private static final String CONNECTOR_TYPE = OMPlatform.INSTANCE
      .getProperty("org.eclipse.emf.cdo.explorer.ui.repositories.wizards.MasterRepositoryController.CONNECTOR_TYPE", "tcp");

  private static final String DEFAULT_HOST = OMPlatform.INSTANCE
      .getProperty("org.eclipse.emf.cdo.explorer.ui.repositories.wizards.MasterRepositoryController.DEFAULT_HOST", "localhost");

  private static final String DEFAULT_PORT = OMPlatform.INSTANCE
      .getProperty("org.eclipse.emf.cdo.explorer.ui.repositories.wizards.MasterRepositoryController.DEFAULT_PORT", "2036");

  private final DisposeListener disposeListener = new DisposeListener()
  {
    @Override
    public void widgetDisposed(DisposeEvent e)
    {
      dispose();
    }
  };

  private IListener adminListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      ViewerUtil.refresh(repositoryTableViewer, null);
    }
  };

  private IListener adminManagerListener = new ContainerEventAdapter<CDOAdminClient>()
  {
    @Override
    protected void onAdded(IContainer<CDOAdminClient> container, CDOAdminClient admin)
    {
      admin.addListener(adminListener);
    }

    @Override
    protected void onRemoved(IContainer<CDOAdminClient> container, CDOAdminClient admin)
    {
      admin.removeListener(adminListener);
    }
  };

  private CDOAdminClientManager adminManager;

  private IManagedContainer container;

  private Composite parent;

  private Text hostText;

  private ValidatingText portText;

  private TableViewer repositoryTableViewer;

  private ValidatingText repositoryNameText;

  private Label userNameLabel;

  private Text userNameText;

  private Label passwordLabel;

  private Text passwordText;

  private String connectorDescription;

  private String repositoryName;

  private String userName;

  private String password;

  private boolean authenticating;

  private VersioningMode versioningMode;

  private IDGeneration idGeneration;

  public MasterRepositoryController(Composite parent)
  {
    this.parent = parent;

    container = ContainerUtil.createPluginContainer();
    LifecycleUtil.activate(container);

    adminManager = CDOAdminClientUtil.createAdminManager(container);
    adminManager.addListener(adminManagerListener);
    LifecycleUtil.activate(adminManager);

    AbstractRepositoryPage.createLabel(parent, "Host:");
    hostText = new Text(parent, SWT.BORDER);
    hostText.setText(DEFAULT_HOST);
    hostText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    AbstractRepositoryPage.createLabel(parent, "Port:");
    portText = new AddressValidatingText(parent);
    portText.setText(DEFAULT_PORT);
    hostText.addModifyListener(portText);

    AbstractRepositoryPage.createLabel(parent, "Repositories:");
    repositoryTableViewer = new TableViewer(parent, SWT.BORDER | SWT.SINGLE);
    repositoryTableViewer.setContentProvider(new AdminContentProvider());
    repositoryTableViewer.setLabelProvider(new AdminLabelProvider());
    repositoryTableViewer.setInput(adminManager);
    repositoryTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    repositoryTableViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        CDOAdminClientRepository adminRepository = (CDOAdminClientRepository)selection.getFirstElement();
        if (adminRepository != null)
        {
          repositoryNameText.setText(adminRepository.getName());
          repositoryNameText.modifyText(false);
        }
      }
    });

    AbstractRepositoryPage.createLabel(parent, "Repository name:");
    repositoryNameText = new RepositoryValidatingText(parent);

    userNameLabel = AbstractRepositoryPage.createLabel(parent, "User name:");
    userNameText = new Text(parent, SWT.BORDER);
    userNameText.setLayoutData(createWidthGridData());
    userNameText.addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent e)
      {
        userName = userNameText.getText();
        repositoryNameText.modifyText(true);
      }
    });

    passwordLabel = AbstractRepositoryPage.createLabel(parent, "Password:");
    passwordText = new Text(parent, SWT.BORDER | SWT.PASSWORD);
    passwordText.setLayoutData(createWidthGridData());
    passwordText.addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent e)
      {
        password = passwordText.getText();
        repositoryNameText.modifyText(true);
      }
    });

    portText.modifyText(false);
    repositoryNameText.modifyText(false);

    parent.addDisposeListener(disposeListener);
  }

  public final String getConnectorURL()
  {
    return connectorDescription == null ? null : CONNECTOR_TYPE + "://" + connectorDescription;
  }

  public final String getConnectorDescription()
  {
    return connectorDescription;
  }

  public final String getRepositoryName()
  {
    return repositoryName;
  }

  public final boolean isAuthenticating()
  {
    return authenticating;
  }

  public final IPasswordCredentials getCredentials()
  {
    if (userNameText.isEnabled() && !StringUtil.isEmpty(userName))
    {
      return new PasswordCredentials(userName, password);
    }

    return null;
  }

  public final VersioningMode getVersioningMode()
  {
    return versioningMode;
  }

  public final IDGeneration getIDGeneration()
  {
    return idGeneration;
  }

  public final boolean isValid()
  {
    return portText.isValid() && repositoryNameText.isValid();
  }

  public void dispose()
  {
    parent.removeDisposeListener(disposeListener);

    if (container != null)
    {
      repositoryNameText.cancelValidation();
      portText.cancelValidation();

      LifecycleUtil.deactivate(adminManager);
      adminManager = null;

      container.deactivate();
      container = null;
    }
  }

  protected void validateController()
  {
    if (repositoryNameText != null && !repositoryNameText.isDisposed())
    {
      repositoryName = repositoryNameText.getText();
    }
  }

  protected void showCredentials(final boolean show)
  {
    parent.getDisplay().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        if (!parent.isDisposed())
        {
          userNameLabel.setEnabled(show);
          userNameText.setEnabled(show);
          passwordLabel.setEnabled(show);
          passwordText.setEnabled(show);
        }
      }
    });
  }

  private static GridData createWidthGridData()
  {
    GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
    gridData.widthHint = VALIDATING_WIDTH;
    return gridData;
  }

  /**
   * @author Eike Stepper
   */
  private abstract class ValidatingText extends Composite implements ModifyListener
  {
    private final Text text;

    private final Label imageLabel;

    private final Label statusLabel;

    private final DelayingExecutor validator;

    private boolean valid;

    public ValidatingText(Composite parent, int validationDelay)
    {
      super(parent, SWT.NONE);

      if (validationDelay >= 0)
      {
        ExecutorService threadPool = ConcurrencyUtil.getExecutorService(container);

        validator = new DelayingExecutor(validationDelay)
        {
          @Override
          protected void doExecute(Runnable runnable)
          {
            threadPool.execute(runnable);
          }
        };

        threadPool.execute(validator);
      }
      else
      {
        validator = null;
      }

      setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      GridLayout layout = new GridLayout(3, false);
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      setLayout(layout);

      text = new Text(this, SWT.BORDER);
      text.setLayoutData(createWidthGridData());
      text.addModifyListener(this);

      imageLabel = new Label(this, SWT.NONE);

      statusLabel = new Label(this, SWT.NONE);
      statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    public final String getText()
    {
      return text.getText();
    }

    public final void setText(String text)
    {
      this.text.setText(text);
    }

    public final boolean isValid()
    {
      return valid;
    }

    public void cancelValidation()
    {
      valid = false;

      if (validator != null)
      {
        validator.stop();
      }
    }

    @Override
    public void modifyText(ModifyEvent e)
    {
      modifyText(true);
    }

    public void modifyText(boolean delay)
    {
      validateController();

      if (validator != null)
      {
        imageLabel.setImage(EMPTY_IMAGE);
        statusLabel.setText("");

        String validationInfo = getValidationInfo();
        if (validationInfo != null)
        {
          validator.execute(() -> {
            updateLabels(null, false);
            String message = null;
            valid = false;

            try
            {
              message = validate(validationInfo);
              valid = true;
            }
            catch (Exception ex)
            {
              message = ex.getMessage();
            }

            updateLabels(message, valid);
          });
        }
        else
        {
          finished(false);
        }
      }
    }

    protected abstract String getValidationInfo();

    protected abstract String validate(String validationInfo) throws Exception;

    protected void finished(boolean valid)
    {
    }

    private void updateLabels(final String message, final boolean valid)
    {
      Display display = parent.getDisplay();
      if (!parent.isDisposed())
      {
        display.syncExec(new Runnable()
        {
          @Override
          public void run()
          {
            try
            {
              if (message != null)
              {
                imageLabel.setImage(valid ? OK_IMAGE : ERROR_IMAGE);
                statusLabel.setText(message);
                finished(valid);
              }
            }
            catch (Exception ex)
            {
              //$FALL-THROUGH$
            }

            validateController();
          }
        });
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class AddressValidatingText extends ValidatingText
  {
    public AddressValidatingText(Composite parent)
    {
      super(parent, ADDRESS_VALIDATION_DELAY);
    }

    @Override
    protected String getValidationInfo()
    {
      connectorDescription = null;

      String host = hostText.getText();
      if (host.length() == 0)
      {
        return null;
      }

      String port = getText();
      if (port.length() == 0)
      {
        port = DEFAULT_PORT;
      }

      return host + ":" + port;
    }

    @Override
    protected String validate(String validationInfo) throws Exception
    {
      String[] tokens = validationInfo.split(":");
      String host = tokens[0];
      String port = tokens[1];

      InetAddress addr;

      try
      {
        addr = InetAddress.getByName(host);
      }
      catch (Exception ex)
      {
        throw new Exception("Unknown host");
      }

      try
      {
        new InetSocketAddress(addr, Integer.parseInt(port));
      }
      catch (IllegalArgumentException ex)
      {
        throw new Exception("Invalid port");
      }

      connectorDescription = validationInfo;
      return "Valid address";
    }

    @Override
    protected void finished(boolean valid)
    {
      repositoryNameText.modifyText(false);

      if (valid && connectorDescription != null)
      {
        String url = getConnectorURL();
        adminManager.setConnection(url);
      }

      ViewerUtil.refresh(repositoryTableViewer, null);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class RepositoryValidatingText extends ValidatingText
  {
    public RepositoryValidatingText(Composite parent)
    {
      super(parent, REPOSITORY_VALIDATION_DELAY);
    }

    @Override
    protected String getValidationInfo()
    {
      if (connectorDescription == null)
      {
        return null;
      }

      String repositoryName = getText();
      if (repositoryName.length() == 0)
      {
        return null;
      }

      return repositoryName;
    }

    @Override
    protected String validate(String repositoryName) throws Exception
    {
      CDONet4jSession session = null;
      authenticating = false;
      versioningMode = null;
      idGeneration = null;

      try
      {
        IConnector connector = getConnector();
        IPasswordCredentialsProvider credentialsProvider = new PasswordCredentialsProvider(userName, password)
        {
          @Override
          public IPasswordCredentials getCredentials()
          {
            authenticating = true;
            return super.getCredentials();
          }
        };

        try
        {
          CDONet4jSessionConfiguration config = CDONet4jUtil.createNet4jSessionConfiguration();
          config.setConnector(connector);
          config.setRepositoryName(repositoryName);
          config.setCredentialsProvider(credentialsProvider);
          ((InternalCDOSessionConfiguration)config).setLoginPeek(true);

          session = config.openNet4jSession();
          if (session != null && session.isClosed())
          {
            session = null;
          }
        }
        catch (LoginPeekException ex)
        {
          return "Repository reachable";
        }
        catch (NotAuthenticatedException ex)
        {
          authenticating = true;
        }
        catch (Exception ex)
        {
          session = null;
        }

        if (session == null)
        {
          showCredentials(true);
          if (authenticating)
          {
            throw new Exception("Authentication failed");
          }

          throw new Exception("Repository unreachable");
        }

        showCredentials(authenticating);

        CDORepositoryInfo repositoryInfo = session.getRepositoryInfo();
        versioningMode = VersioningMode.from(repositoryInfo);
        idGeneration = IDGeneration.from(repositoryInfo);

        String message = versioningMode.toString();
        if (versioningMode == VersioningMode.Branching && idGeneration == IDGeneration.UUID)
        {
          message += ", Replicable";
        }

        return message;
      }
      finally
      {
        LifecycleUtil.deactivate(session);
      }
    }

    private IConnector getConnector() throws Exception
    {
      IConnector connector = null;

      try
      {
        connector = Net4jUtil.getConnector(container, CONNECTOR_TYPE, connectorDescription);
      }
      catch (Exception ex)
      {
        connector = null;
      }

      if (connector == null)
      {
        throw new Exception("Host unreachable");
      }

      return connector;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class AdminContentProvider implements IStructuredContentProvider
  {
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public Object[] getElements(Object inputElement)
    {
      if (connectorDescription != null)
      {
        String url = getConnectorURL();

        CDOAdminClient admin = adminManager.getConnection(url);
        if (admin != null)
        {
          return admin.getRepositories();
        }
      }

      return ContainerItemProvider.NO_ELEMENTS;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class AdminLabelProvider extends LabelProvider
  {
    @Override
    public Image getImage(Object element)
    {
      if (element instanceof CDOAdminClientRepository)
      {
        return REPOSITORY_IMAGE;
      }

      return super.getImage(element);
    }

    @Override
    public String getText(Object element)
    {
      if (element instanceof CDOAdminClientRepository)
      {
        CDOAdminClientRepository adminRepository = (CDOAdminClientRepository)element;
        return adminRepository.getName();
      }

      return super.getText(element);
    }
  }
}
