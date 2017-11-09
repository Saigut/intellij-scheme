package schemely.gotoclass;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SchemeGoToSymbolContributor implements ChooseByNameContributor
{
  public String[] getNames(Project project, boolean includeNonProjectItems)
  {
    Set<String> symbols = new HashSet<String>();
    //    symbols.addAll(StubIndex.getInstance().getAllKeys(ClDefNameIndex.KEY));
    return symbols.toArray(new String[symbols.size()]);

  }

  public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems)
  {
    GlobalSearchScope scope = includeNonProjectItems ? null : GlobalSearchScope.projectScope(project);

    List<NavigationItem> symbols = new ArrayList<NavigationItem>();
    //    symbols.addAll(StubIndex.getInstance().get(ClDefNameIndex.KEY, name, project, scope));
    return symbols.toArray(new NavigationItem[symbols.size()]);
  }
}
