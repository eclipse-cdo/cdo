/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.State;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.actions.ShowInActionProvider;
import org.eclipse.emf.cdo.explorer.ui.repositories.wizards.NewRepositoryWizard;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryManagerImpl;
import org.eclipse.emf.cdo.internal.explorer.repositories.LocalCDORepository;
import org.eclipse.emf.cdo.internal.explorer.repositories.LocalCDORepository.IDGeneration;
import org.eclipse.emf.cdo.internal.explorer.repositories.LocalCDORepository.VersioningMode;
import org.eclipse.emf.cdo.internal.ui.actions.OpenTransactionAction;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.emf.ecore.EClass;
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
    viewer.setSorter(null);
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

    int xxx;
    manager.add(new Action("Local")
    {
      @Override
      public void run()
      {
        Properties properties = new Properties();
        properties.put(LocalCDORepository.PROP_TYPE, CDORepository.TYPE_LOCAL);
        properties.put(LocalCDORepository.PROP_LABEL, "repo2");
        properties.put(LocalCDORepository.PROP_NAME, "repo2");
        properties.put(LocalCDORepository.PROP_VERSIONING_MODE, VersioningMode.Branching.toString());
        properties.put(LocalCDORepository.PROP_ID_GENERATION, IDGeneration.UUID.toString());
        properties.put(LocalCDORepository.PROP_TCP_DISABLED, "false");
        properties.put(LocalCDORepository.PROP_TCP_PORT, "2037");

        CDORepository repository = CDOExplorerUtil.getRepositoryManager().addRepository(properties);
        repository.connect();

        CDOSession session = repository.getSession();
        CDOTransaction transaction = session.openTransaction();
        OpenTransactionAction.configureTransaction(transaction);

        try
        {
          EPackage ePackage = EPackage.Registry.INSTANCE
              .getEPackage("http://www.eclipse.org/emf/CDO/examples/company/1.0.0");
          EClass eClass = (EClass)ePackage.getEClassifier("Company");

          CDOResource resource = transaction.createResource("model1");
          resource.getContents().add(EcoreUtil.create(eClass));

          transaction.commit();
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
    });

    manager.add(newAction);
  }

  @Override
  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    super.fillContextMenu(manager, selection);

    manager.add(new Separator("group.new"));
    manager.add(new GroupMarker("group.new.branch"));
    manager.add(new Separator("group.open"));
    manager.add(new GroupMarker("group.openWith"));
    manager.add(new Separator("group.checkout"));
    manager.add(new Separator("group.edit"));
    manager.add(new Separator("group.port"));
    manager.add(new Separator("group.build"));
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    manager.add(new Separator("group.properties"));

    IWorkbenchPage page = getSite().getPage();
    Object selectedElement = selection.getFirstElement();

    IMenuManager subMenu = new MenuManager(ShowInActionProvider.TITLE, ICommonMenuConstants.GROUP_OPEN_WITH);
    subMenu.add(new GroupMarker(ICommonMenuConstants.GROUP_TOP));

    if (ShowInActionProvider.fillMenu(page, subMenu, selectedElement))
    {
      subMenu.add(new GroupMarker(ICommonMenuConstants.GROUP_ADDITIONS));
      manager.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH, subMenu);
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

    public void treeCollapsed(TreeExpansionEvent event)
    {
      detect();
    }

    public void treeExpanded(TreeExpansionEvent event)
    {
      detect();
    }

    public void mouseDoubleClick(MouseEvent e)
    {
      detect();
    }

    public void mouseDown(MouseEvent e)
    {
      detect();
    }

    public void mouseUp(MouseEvent e)
    {
      detect();
    }

    public void keyPressed(KeyEvent e)
    {
      detect();
    }

    public void keyReleased(KeyEvent e)
    {
      detect();
    }

    private void detect()
    {
      lastActivity = System.currentTimeMillis();
    }

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
      try
      {
        Shell shell = getSite().getShell();
        WizardDialog dialog = new WizardDialog(shell, new NewRepositoryWizard());
        dialog.open();
      }
      catch (RuntimeException ex)
      {
        OM.LOG.error(ex);
        throw ex;
      }
    }
  }
}
