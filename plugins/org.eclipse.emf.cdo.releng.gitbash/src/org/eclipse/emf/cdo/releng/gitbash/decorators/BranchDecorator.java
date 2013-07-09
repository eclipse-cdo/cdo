package org.eclipse.emf.cdo.releng.gitbash.decorators;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("restriction")
public class BranchDecorator implements ILabelDecorator
{
  private static final String DEFAULT_PATH = "refs/heads/";

  public BranchDecorator()
  {
  }

  public void dispose()
  {
  }

  public boolean isLabelProperty(Object element, String property)
  {
    return false;
  }

  public void addListener(ILabelProviderListener listener)
  {
  }

  public void removeListener(ILabelProviderListener listener)
  {
  }

  public Image decorateImage(Image image, Object element)
  {
    return null;
  }

  public String decorateText(String text, Object element)
  {
    if (element instanceof org.eclipse.egit.ui.internal.repository.tree.RefNode)
    {
      org.eclipse.egit.ui.internal.repository.tree.RefNode node = (org.eclipse.egit.ui.internal.repository.tree.RefNode)element;
      if (node.getType() == org.eclipse.egit.ui.internal.repository.tree.RepositoryTreeNodeType.REF && isLocal(node))
      {
        String decoration = getDecoration(node);
        if (decoration != null)
        {
          return text + " [" + decoration + "]";
        }
      }
    }

    return null;
  }

  private boolean isLocal(org.eclipse.egit.ui.internal.repository.tree.RepositoryTreeNode<?> node)
  {
    if (node.getType() == org.eclipse.egit.ui.internal.repository.tree.RepositoryTreeNodeType.LOCAL)
    {
      return true;
    }

    node = node.getParent();
    if (node != null)
    {
      return isLocal(node);
    }

    return false;
  }

  private String getDecoration(org.eclipse.egit.ui.internal.repository.tree.RefNode node)
  {
    String branchName = Repository.shortenRefName(node.getObject().getName());
    StoredConfig config = node.getRepository().getConfig();

    String branch = config.getString(ConfigConstants.CONFIG_BRANCH_SECTION, branchName,
        ConfigConstants.CONFIG_KEY_MERGE);

    if (branch != null)
    {
      String remote = config.getString(ConfigConstants.CONFIG_BRANCH_SECTION, branchName,
          ConfigConstants.CONFIG_KEY_REMOTE);

      boolean rebaseFlag = config.getBoolean(ConfigConstants.CONFIG_BRANCH_SECTION, branchName,
          ConfigConstants.CONFIG_KEY_REBASE, false);

      if (branch.startsWith(DEFAULT_PATH))
      {
        branch = branch.substring(DEFAULT_PATH.length());
      }

      String prefix = ".".equals(remote) ? "" : remote + "/";
      return (rebaseFlag ? "REBASE" : "MERGE") + ": " + prefix + branch;
    }

    return null;
  }
}
