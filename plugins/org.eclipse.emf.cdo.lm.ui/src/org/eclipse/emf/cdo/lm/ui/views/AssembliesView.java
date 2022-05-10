/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.views;

import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Dependency;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;
import org.eclipse.emf.cdo.lm.assembly.provider.AssemblyEditPlugin;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor.AvailableUpdatesChangedEvent;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor.UpdateStateChangedEvent;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor.Updates;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.internal.client.AssemblyManager;
import org.eclipse.emf.cdo.lm.internal.client.LMManager;
import org.eclipse.emf.cdo.lm.modules.DependencyDefinition;
import org.eclipse.emf.cdo.lm.modules.ModulesFactory;
import org.eclipse.emf.cdo.lm.modules.provider.ModulesEditPlugin;
import org.eclipse.emf.cdo.lm.ui.actions.CheckoutAction;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.ui.decorators.AvailableUpdatesDecorator;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.OverlayImage;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;

import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.ui.MenuFiller;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider.Node;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.p2.metadata.VersionRange;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class AssembliesView extends ContainerView
{
  public static final String ID = "org.eclipse.emf.cdo.lm.AssembliesView";

  public static final String SHOW_IN_MENU_ID = ID + ".ShowInMenu";

  private final InstallUpdatesAction installUpdatesAction = new InstallUpdatesAction();

  private Font bold;

  public AssembliesView()
  {
  }

  @Override
  protected IAssemblyManager getContainer()
  {
    return IAssemblyManager.INSTANCE;
  }

  @Override
  protected void initViewer()
  {
    bold = UIUtil.getBoldFont(getViewer().getControl());
    super.initViewer();
  }

  @Override
  public void dispose()
  {
    UIUtil.dispose(bold);
    super.dispose();
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    IElementFilter rootElementFilter = getRootElementFilter();
    return new ContainerViewItemProvider(rootElementFilter)
    {
      @Override
      protected Object[] getContainerChildren(ContainerItemProvider<IContainer<Object>>.AbstractContainerNode containerNode, IContainer<?> container)
      {
        Object[] children = super.getContainerChildren(containerNode, container);
        children = addNewModules(container, children);
        return children;
      }

      @Override
      protected Node createNode(Node parent, Object element)
      {
        if (element instanceof IAssemblyDescriptor)
        {
          IAssemblyDescriptor descriptor = (IAssemblyDescriptor)element;

          List<String> errors = descriptor.getResolutionErrors();
          if (!ObjectUtil.isEmpty(errors))
          {
            return new FixedChildrenNode(parent, element, errors);
          }
        }

        if (element instanceof AssemblyModule)
        {
          EList<DependencyDefinition> dependencies = getDependencies(parent, (AssemblyModule)element);
          return new FixedChildrenNode(parent, element, dependencies);
        }

        return super.createNode(parent, element);
      }
    };
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(installUpdatesAction);
    super.fillLocalToolBar(manager);
  }

  @Override
  @SuppressWarnings("restriction")
  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    if (selection == null || selection.size() != 1)
    {
      return;
    }

    Object selectedElement = selection.getFirstElement();
    if (selectedElement == null)
    {
      return;
    }

    IWorkbenchPage page = getSite().getPage();
    TreeViewer viewer = getViewer();

    MenuManager showInMenu = new MenuManager("Show In", SHOW_IN_MENU_ID);
    boolean[] showInFilled = { false };

    if (selectedElement instanceof AssemblyModule)
    {
      AssemblyModule module = (AssemblyModule)selectedElement;

      IAssemblyDescriptor descriptor = IAssemblyManager.INSTANCE.getDescriptor(module);
      if (descriptor != null)
      {
        List<CDOCheckout> checkouts = new ArrayList<>();

        Baseline moduleBaseline = addCheckoutActions(manager, descriptor, module);
        if (moduleBaseline != null)
        {
          IAssemblyManager.INSTANCE.forEachDescriptor(d -> {
            if (d.getBaseline() == moduleBaseline)
            {
              checkouts.add(d.getCheckout());
            }
          });
        }

        String moduleSystemName = module.getAssembly().getSystemName();
        for (CDOCheckout checkout : CDOExplorerUtil.getCheckoutManager().getCheckouts())
        {
          if (!checkout.isOpen())
          {
            Properties properties = LMManager.loadProperties(checkout);
            if (properties != null)
            {
              String systemName = properties.getProperty(AssemblyManager.PROP_SYSTEM_NAME);
              if (Objects.equals(systemName, moduleSystemName))
              {
                CDOID baselineID = CDOIDUtil.read(properties.getProperty(AssemblyManager.PROP_BASELINE_ID));
                if (Objects.equals(baselineID, moduleBaseline.cdoID()))
                {
                  checkouts.add(checkout);
                }
              }
            }
          }
        }

        if (!checkouts.isEmpty())
        {
          showInMenu.add(new org.eclipse.emf.cdo.explorer.ui.checkouts.actions.ShowInActionProvider.ShowInProjectExplorerAction(page,
              checkouts.toArray(new CDOCheckout[checkouts.size()])));
          showInFilled[0] |= true;
        }
      }
    }
    else if (selectedElement instanceof IAssemblyDescriptor)
    {
      IAssemblyDescriptor descriptor = (IAssemblyDescriptor)selectedElement;
      showInMenu.add(new org.eclipse.emf.cdo.explorer.ui.checkouts.actions.ShowInActionProvider.ShowInProjectExplorerAction(page,
          new CDOCheckout[] { descriptor.getCheckout() }));
      showInMenu.add(new org.eclipse.emf.cdo.explorer.ui.checkouts.actions.ShowInActionProvider.ShowInSystemExplorerAction(
          descriptor.getCheckout().getStateFolder(LMManager.STATE_FOLDER_NAME)));
      showInFilled[0] |= true;
    }

    addMenuGroup(manager, "show-in");
    IPluginContainer.INSTANCE.forEachElement(MenuFiller.Factory.PRODUCT_GROUP, MenuFiller.class,
        filler -> showInFilled[0] |= filler.fillMenu(page, viewer, showInMenu, selectedElement));

    if (showInFilled[0])
    {
      manager.add(showInMenu);
    }

    addMenuGroupAdditions(manager);
    IPluginContainer.INSTANCE.forEachElement(MenuFiller.Factory.PRODUCT_GROUP, MenuFiller.class,
        filler -> showInFilled[0] |= filler.fillMenu(page, viewer, manager, selectedElement));

    addMenuGroup(manager, "errors");
    super.fillContextMenu(manager, selection);
  }

  private Baseline addCheckoutActions(IMenuManager manager, IAssemblyDescriptor descriptor, AssemblyModule module)
  {
    Updates updates = descriptor.getAvailableUpdates();
    if (updates != null)
    {
      String moduleName = module.getName();
      AssemblyModule incomingModule = updates.getModifications().get(moduleName);
      if (incomingModule != null)
      {
        Baseline baseline = addCheckoutAction(manager, descriptor, module);
        addCheckoutAction(manager, descriptor, incomingModule);

        manager.add(new Action("Preview Incoming Changes...", ImageDescriptor.createFromImage(CDOCompareEditorUtil.compareImage()))
        {
          @Override
          public void run()
          {
            ISystemDescriptor systemDescriptor = descriptor.getSystemDescriptor();
            CDORepository moduleRepository = systemDescriptor.getModuleRepository(moduleName);
            CDOSession session = moduleRepository.acquireSession();

            try
            {
              CDOBranchManager branchManager = session.getBranchManager();
              CDOBranchPoint leftPoint = module.getBranchPoint().resolve(branchManager);
              CDOBranchPoint rightPoint = incomingModule.getBranchPoint().resolve(branchManager);
              CDOCompareEditorUtil.openDialog(session, leftPoint, rightPoint);
            }
            finally
            {
              moduleRepository.releaseSession();
            }
          }
        });

        return baseline;
      }

      AssemblyModule newModule = updates.getAdditions().get(moduleName);
      if (newModule != null)
      {
        addCheckoutAction(manager, descriptor, newModule);
        return descriptor.getBaseline(module);
      }
    }

    if (!module.isRoot())
    {
      return addCheckoutAction(manager, descriptor, module);
    }

    return descriptor.getBaseline(module);
  }

  private Baseline addCheckoutAction(IMenuManager manager, IAssemblyDescriptor descriptor, AssemblyModule module)
  {
    Baseline baseline = descriptor.getBaseline(module);
    if (baseline != null)
    {
      manager.add(new CheckoutAction(getSite().getPage(), baseline));
    }

    return baseline;
  }

  @Override
  protected void doubleClicked(Object object)
  {
    super.doubleClicked(object);

    if (object instanceof DependencyDefinition)
    {
      DependencyDefinition definition = (DependencyDefinition)object;

      ContainerItemProvider<IContainer<Object>> itemProvider = getItemProvider();
      AssemblyModule module = (AssemblyModule)itemProvider.getParent(definition);
      IAssemblyDescriptor descriptor = (IAssemblyDescriptor)itemProvider.getParent(module);

      AssemblyModule target = descriptor.getModule(definition.getTargetName());
      if (target != null)
      {
        selectReveal(new StructuredSelection(target));
      }
    }
  }

  @Override
  protected void selectionChanged(IActionBars bars, ITreeSelection selection)
  {
    installUpdatesAction.selectionChanged(selection);
  }

  @Override
  protected void handleElementEvent(IEvent event)
  {
    if (event instanceof UpdateStateChangedEvent)
    {
      UIUtil.asyncExec(() -> installUpdatesAction.selectionChanged(getSelection()));
    }
    else if (event instanceof AvailableUpdatesChangedEvent)
    {
      UIUtil.asyncExec(() -> refreshPressed());
    }
  }

  @Override
  protected ViewerComparator createViewerComparator()
  {
    return new ViewerComparator()
    {
      @Override
      public void sort(Viewer viewer, Object[] elements)
      {
        if (elements != null && elements.length != 0 && elements[0] instanceof AssemblyModule)
        {
          // Don't sort dependencies.
          return;
        }

        super.sort(viewer, elements);
      }
    };
  }

  @Override
  protected String getElementText(Object element)
  {
    String text = getText(element);
    if (text != null)
    {
      return text;
    }

    return super.getElementText(element);
  }

  @Override
  protected Image getElementImage(Object element)
  {
    Image image = getImage(element);
    if (image != null)
    {
      return image;
    }

    return super.getElementImage(element);
  }

  @Override
  protected Font getElementFont(Object element)
  {
    Font font = getFont(element, bold, getItemProvider());
    if (font != null)
    {
      return font;
    }

    return super.getElementFont(element);
  }

  @Override
  protected Color getElementForeground(Object element)
  {
    Color foreground = getForeground(element, getDisplay());
    if (foreground != null)
    {
      return foreground;
    }

    return super.getElementForeground(element);
  }

  private static boolean isModuleAddition(Object element)
  {
    if (element instanceof AssemblyModule)
    {
      AssemblyModule module = (AssemblyModule)element;

      IAssemblyDescriptor descriptor = IAssemblyManager.INSTANCE.getDescriptor(module);
      if (descriptor != null)
      {
        if (descriptor.getModule(module.getName()) == null)
        {
          // This must be a module addition in the available updates of the descriptor.
          return true;
        }
      }
    }

    return false;
  }

  public static Object[] addNewModules(IContainer<?> container, Object[] children)
  {
    if (container instanceof IAssemblyDescriptor)
    {
      IAssemblyDescriptor descriptor = (IAssemblyDescriptor)container;

      Updates updates = descriptor.getAvailableUpdates();
      if (updates != null)
      {
        Collection<AssemblyModule> newModules = updates.getAdditions().values();
        if (!newModules.isEmpty())
        {
          List<AssemblyModule> list = new ArrayList<>();

          for (Object child : children)
          {
            list.add((AssemblyModule)child);
          }

          for (AssemblyModule newModule : newModules)
          {
            list.add(newModule);
          }

          children = list.toArray(new AssemblyModule[list.size()]);
        }
      }
    }

    return children;
  }

  public static EList<DependencyDefinition> getDependencies(Node parent, AssemblyModule module)
  {
    IAssemblyDescriptor descriptor = IAssemblyManager.INSTANCE.getDescriptor(module);
    Baseline baseline = descriptor.getBaseline(module);
    if (baseline instanceof FixedBaseline)
    {
      EList<Dependency> dependencies = ((FixedBaseline)baseline).getDependencies();
      EList<DependencyDefinition> definitions = new BasicEList<>(dependencies.size());

      for (Dependency dependency : dependencies)
      {
        DependencyDefinition definition = ModulesFactory.eINSTANCE.createDependencyDefinition(dependency.getTarget().getName(), dependency.getVersionRange());
        definitions.add(definition);
      }

      return definitions;
    }

    Annotation annotation = module.getAnnotation(LMPackage.ANNOTATION_SOURCE);
    if (annotation != null)
    {
      EList<EObject> contents = annotation.getContents();
      EList<DependencyDefinition> definitions = new BasicEList<>(contents.size());

      for (EObject content : contents)
      {
        if (content instanceof DependencyDefinition)
        {
          DependencyDefinition definition = (DependencyDefinition)content;
          definitions.add(definition);
        }
      }

      return definitions;
    }

    return ECollections.emptyEList();
  }

  public static String getText(Object element)
  {
    if (element instanceof IAssemblyDescriptor)
    {
      IAssemblyDescriptor descriptor = (IAssemblyDescriptor)element;
      return descriptor.getName() + " [" + descriptor.getAssembly().getSystemName() + "]";
    }

    if (element instanceof AssemblyModule)
    {
      AssemblyModule module = (AssemblyModule)element;
      return module.getName() + " " + AvailableUpdatesDecorator.getSuffix(module);
    }

    if (element instanceof DependencyDefinition)
    {
      DependencyDefinition dependency = (DependencyDefinition)element;
      VersionRange versionRange = dependency.getVersionRange();

      String text = dependency.getTargetName();
      if (versionRange != null)
      {
        text += " " + versionRange;
      }

      return text;
    }

    return null;
  }

  public static Image getImage(Object element)
  {
    if (element instanceof IAssemblyDescriptor)
    {
      IAssemblyDescriptor descriptor = (IAssemblyDescriptor)element;
      Object imageKey = AssemblyEditPlugin.INSTANCE.getImage("full/obj16/Assembly");

      List<String> resolutionErrors = descriptor.getResolutionErrors();
      if (!ObjectUtil.isEmpty(resolutionErrors))
      {
        return ExtendedImageRegistry.INSTANCE.getImage(
            new OverlayImage(imageKey, org.eclipse.emf.cdo.ui.shared.SharedIcons.getImage(org.eclipse.emf.cdo.ui.shared.SharedIcons.OVR_ERROR), 8, 8));
      }

      return ExtendedImageRegistry.INSTANCE.getImage(imageKey);
    }

    if (element instanceof AssemblyModule)
    {
      return ExtendedImageRegistry.INSTANCE.getImage( //
          AssemblyEditPlugin.INSTANCE.getImage(isModuleAddition(element) ? "full/obj16/AssemblyModuleDisabled" : "full/obj16/AssemblyModule"));
    }

    if (element instanceof DependencyDefinition)
    {
      return ExtendedImageRegistry.INSTANCE.getImage( //
          ModulesEditPlugin.INSTANCE.getImage("full/obj16/DependencyDefinition"));
    }

    if (element instanceof String)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_ERROR);
    }

    return null;
  }

  public static Font getFont(Object element, Font bold, ContainerItemProvider<IContainer<Object>> itemProvider)
  {
    if (element instanceof AssemblyModule)
    {
      AssemblyModule module = (AssemblyModule)element;

      if (module.isRoot())
      {
        return bold;
      }
    }

    if (element instanceof DependencyDefinition)
    {
      DependencyDefinition definition = (DependencyDefinition)element;

      AssemblyModule module = (AssemblyModule)itemProvider.getParent(definition);
      if (module.isRoot())
      {
        return bold;
      }
    }

    return null;
  }

  public static Color getForeground(Object element, Display display)
  {
    if (isModuleAddition(element))
    {
      return display.getSystemColor(SWT.COLOR_DARK_GRAY);
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  private static final class InstallUpdatesAction extends LongRunningAction
  {
    private IAssemblyDescriptor descriptor;

    public InstallUpdatesAction()
    {
      super("Install Updates...", OM.getImageDescriptor("icons/Update.gif"));
      setToolTipText("Install the available updates into the checkout");
      setEnabled(false);
    }

    public void selectionChanged(ISelection selection)
    {
      descriptor = UIUtil.adaptElement(selection, IAssemblyDescriptor.class);

      if (descriptor == null)
      {
        EObject assemblyElement = UIUtil.adaptElement(selection, EObject.class);
        descriptor = IAssemblyManager.INSTANCE.getDescriptor(assemblyElement);
      }

      setEnabled(descriptor != null && descriptor.hasUpdatesAvailable());
    }

    @Override
    protected void doRun(IProgressMonitor progressMonitor) throws Exception
    {
      if (isEnabled())
      {
        descriptor.update();
      }
    }
  }
}
