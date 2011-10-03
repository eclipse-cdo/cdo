package org.eclipse.emf.cdo.dawn.graphiti.editors;

import org.eclipse.emf.cdo.dawn.preferences.PreferenceConstants;
import org.eclipse.emf.cdo.dawn.transaction.DawnTransactionalEditingDomainImpl;
import org.eclipse.emf.cdo.dawn.util.connection.CDOConnectionUtil;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;

import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.graphiti.ui.editor.DiagramEditorFactory;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.internal.editor.GFWorkspaceCommandStackImpl;
import org.eclipse.ui.IMemento;

@SuppressWarnings("restriction")
public class DawnGraphitiDiagramEditorFactory extends DiagramEditorFactory
{
  @Override
  public IAdaptable createElement(IMemento memento)
  {
    final String diagramUriString = memento.getString(DiagramEditorInput.KEY_URI);
    if (diagramUriString == null)
    {
      return null;
    }

    final String providerID = memento.getString(DiagramEditorInput.KEY_PROVIDER_ID);

    final TransactionalEditingDomain domain = createResourceSetAndEditingDomain();
    return new DawnGraphitiEditorInput(diagramUriString, domain, providerID, true);
  }

  public static TransactionalEditingDomain createResourceSetAndEditingDomain()
  {
    // TODO check if this is still needed here
    CDOConnectionUtil.instance.init(PreferenceConstants.getRepositoryName(), PreferenceConstants.getProtocol(),
        PreferenceConstants.getServerName());
    CDOConnectionUtil.instance.getCurrentSession();

    final ResourceSet resourceSet = new ResourceSetImpl();

    final TransactionalCommandStack workspaceCommandStack = new GFWorkspaceCommandStackImpl(
        new DefaultOperationHistory());

    final TransactionalEditingDomainImpl editingDomain = new DawnTransactionalEditingDomainImpl(
        new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE), workspaceCommandStack,
        resourceSet);
    WorkspaceEditingDomainFactory.INSTANCE.mapResourceSet(editingDomain);
    return editingDomain;
  }
}
