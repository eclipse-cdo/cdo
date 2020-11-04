/*
 * Copyright (c) 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.repositories;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.IDGeneration;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.State;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.VersioningMode;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.actions.ShowInActionProvider;
import org.eclipse.emf.cdo.explorer.ui.handlers.RepositoryCheckoutHandlerQuick;
import org.eclipse.emf.cdo.explorer.ui.repositories.wizards.NewRepositoryWizard;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryImpl;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryManagerImpl;
import org.eclipse.emf.cdo.internal.explorer.repositories.LocalCDORepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.navigator.ICommonMenuConstants;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class CDORepositoriesView extends ContainerView
{
  public static final String ID = "org.eclipse.emf.cdo.explorer.ui.CDORepositoriesView";

  public static final String SHOW_IN_MENU_ID = ID + ".ShowInMenu";

  private final ActivityDetector activityDetector = new ActivityDetector();

  private CDORepositoryItemProvider itemProvider;

  private NewRepositoryAction newAction;

  public CDORepositoriesView()
  {
  }

  private static int getRepositoryTimeoutMillis()
  {
    return OM.PREF_REPOSITORY_TIMEOUT_MINUTES.getValue() * 60 * 1000;
  }

  @Override
  protected IContainer<?> getContainer()
  {
    return CDOExplorerUtil.getRepositoryManager();
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    itemProvider = new CDORepositoryItemProvider(this);
    return itemProvider;
  }

  @Override
  protected Control createUI(Composite parent)
  {
    newAction = new NewRepositoryAction();
    return super.createUI(parent);
  }

  @Override
  protected void initViewer()
  {
    super.initViewer();

    TreeViewer viewer = getViewer();
    viewer.addTreeListener(activityDetector);
    viewer.setComparator(null);
    UIUtil.addDragSupport(viewer);

    Tree tree = viewer.getTree();
    tree.addMouseListener(activityDetector);
    tree.addKeyListener(activityDetector);

    tree.getDisplay().timerExec(getRepositoryTimeoutMillis(), activityDetector);
  }

  @Override
  protected void fillLocalPullDown(IMenuManager manager)
  {
    // Do nothing.
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    addCollapseAllAction(manager);

    if (Boolean.getBoolean("cdo.explorer.testRepo"))
    {
      manager.add(new Action("Test")
      {
        @Override
        public void run()
        {
          Properties properties = new Properties();
          properties.setProperty(LocalCDORepository.PROP_TYPE, CDORepository.TYPE_LOCAL);
          properties.setProperty(LocalCDORepository.PROP_LABEL, "repo2");
          properties.setProperty(LocalCDORepository.PROP_NAME, "repo2");
          properties.setProperty(CDORepositoryImpl.PROP_VERSIONING_MODE, VersioningMode.Branching.toString());
          properties.setProperty(CDORepositoryImpl.PROP_ID_GENERATION, IDGeneration.Counter.toString());
          properties.setProperty(LocalCDORepository.PROP_TCP_DISABLED, "false");
          properties.setProperty(LocalCDORepository.PROP_TCP_PORT, "2037");

          CDORepository repository = CDOExplorerUtil.getRepositoryManager().addRepository(properties);
          repository.connect();

          CDOSession session = repository.getSession();
          CDOTransaction transaction = session.openTransaction();
          CDOUtil.configureView(transaction);

          try
          {
            EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/emf/CDO/examples/company/1.0.0");
            EClass eClass = (EClass)ePackage.getEClassifier("Company");

            EObject company = EcoreUtil.create(eClass);
            addChild(company, "categories", "Category");
            addChild(company, "suppliers", "Supplier");
            addChild(company, "customers", "Customer");
            addChild(company, "purchaseOrders", "PurchaseOrder");
            addChild(company, "salesOrders", "SalesOrder");

            CDOResource resource = transaction.createResource("model1");
            resource.getContents().add(company);

            transaction.commit();

            RepositoryCheckoutHandlerQuick.checkout(repository, CDOCheckout.TYPE_ONLINE_TRANSACTIONAL);

          }
          catch (ConcurrentAccessException ex)
          {
            ex.printStackTrace();
          }
          catch (CommitException ex)
          {
            ex.printStackTrace();
          }
          finally
          {
            transaction.close();
          }
        }

        private void addChild(EObject company, String featureName, String className)
        {
          EClass companyClass = company.eClass();
          EObject object = EcoreUtil.create((EClass)companyClass.getEPackage().getEClassifier(className));

          @SuppressWarnings("unchecked")
          EList<EObject> list = (EList<EObject>)company.eGet(companyClass.getEStructuralFeature(featureName));
          list.add(object);
        }
      });
    }

    manager.add(newAction);
  }

  @Override
  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    super.fillContextMenu(manager, selection);

    manager.add(new Separator("group.new"));
    manager.add(new GroupMarker("group.new.branch"));
    manager.add(new Separator());
    manager.add(new Separator("group.open"));
    manager.add(new GroupMarker("group.openWith"));
    manager.add(new Separator());
    manager.add(new Separator("group.checkout"));
    manager.add(new Separator("group.edit"));
    manager.add(new Separator("group.port"));
    manager.add(new Separator("group.build"));
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    manager.add(new Separator("group.properties"));

    IWorkbenchPage page = getSite().getPage();
    Object selectedElement = selection.size() == 1 ? selection.getFirstElement() : null;

    IMenuManager showInMenu = new MenuManager(ShowInActionProvider.TITLE, SHOW_IN_MENU_ID);
    showInMenu.add(new GroupMarker(ICommonMenuConstants.GROUP_TOP));

    if (ShowInActionProvider.fillMenu(page, null, showInMenu, selectedElement))
    {
      showInMenu.add(new GroupMarker(ICommonMenuConstants.GROUP_ADDITIONS));
      manager.appendToGroup(ICommonMenuConstants.GROUP_OPEN, showInMenu);
    }
  }

  @Override
  protected void doubleClicked(Object object)
  {
    if (object instanceof CDORepository)
    {
      final CDORepository repository = (CDORepository)object;
      if (repository.getState() == State.Disconnected)
      {
        connectRepository(repository);
        return;
      }
    }

    super.doubleClicked(object);
  }

  public void connectRepository(CDORepository repository)
  {
    itemProvider.connectRepository(repository);
  }

  public static void newRepository(Shell shell)
  {
    try
    {
      WizardDialog dialog = new WizardDialog(shell, new NewRepositoryWizard());
      dialog.open();
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
      throw ex;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ActivityDetector implements ITreeViewerListener, MouseListener, KeyListener, Runnable
  {
    private long lastActivity;

    public ActivityDetector()
    {
      detect();
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event)
    {
      detect();
    }

    @Override
    public void treeExpanded(TreeExpansionEvent event)
    {
      detect();
    }

    @Override
    public void mouseDoubleClick(MouseEvent e)
    {
      detect();
    }

    @Override
    public void mouseDown(MouseEvent e)
    {
      detect();
    }

    @Override
    public void mouseUp(MouseEvent e)
    {
      detect();
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
      detect();
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
      detect();
    }

    private void detect()
    {
      lastActivity = System.currentTimeMillis();
    }

    @Override
    public void run()
    {
      Tree tree = getViewer().getTree();
      if (tree.isDisposed())
      {
        return;
      }

      long now = System.currentTimeMillis();
      int timeout = CDORepositoriesView.getRepositoryTimeoutMillis();
      int wait = timeout;

      if (lastActivity <= now - timeout)
      {
        if (!OM.PREF_REPOSITORY_TIMEOUT_DISABLED.getValue())
        {
          CDORepositoryManagerImpl repositoryManager = (CDORepositoryManagerImpl)getContainer();
          repositoryManager.disconnectUnusedRepositories();
        }
      }
      else
      {
        // TODO React to changed timeout preference.
        wait = (int)(timeout - (now - lastActivity));
      }

      tree.getDisplay().timerExec(wait, this);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class NewRepositoryAction extends Action
  {
    public NewRepositoryAction()
    {
      setText("New Repository");
      setToolTipText("Add a new repository");
      setImageDescriptor(OM.getImageDescriptor("icons/add.gif"));
    }

    @Override
    public void run()
    {
      Shell shell = getSite().getShell();
      newRepository(shell);
    }
  }
}
