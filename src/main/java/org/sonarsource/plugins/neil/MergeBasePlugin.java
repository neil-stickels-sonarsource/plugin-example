package org.sonarsource.plugins.neil;

import org.sonar.api.Plugin;

public class MergeBasePlugin implements Plugin {

    @Override
    public void define(Context context)
    {
      context.addExtensions(MergeBaseChecker.class, MergeBaseSensor.class, MergeBaseRulesDefinition.class);
    }
    
}
