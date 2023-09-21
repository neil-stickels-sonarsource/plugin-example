package org.sonarsource.plugins.neil;

import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleDescriptionSection;
import org.sonar.api.server.rule.RulesDefinition;

import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.HOW_TO_FIX_SECTION_KEY;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.INTRODUCTION_SECTION_KEY;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.ROOT_CAUSE_SECTION_KEY;

public class MergeBaseRulesDefinition implements RulesDefinition {
	
	public static final String REPOSITORY = "Mergebase";
	public static final RuleKey MERGEBASE_RULE = RuleKey.of(REPOSITORY, "MergeBase dependency issue identified");

	@Override
	public void define(Context context) {
		NewRepository repository = context.createRepository(REPOSITORY, "xml").setName("MergeBase Dependency Analyzer");
		
		NewRule rule = repository.createRule(MERGEBASE_RULE.rule()).setName("MergeBase Dependency Rule")
				.setHtmlDescription("Generates an issue for every MergeBase Dependency Risk Identified")
				.addDescriptionSection(createDescriptionSection(INTRODUCTION_SECTION_KEY, "This rule identified a dependency risk from MergeBase analysis", null))
				.addDescriptionSection(createDescriptionSection(ROOT_CAUSE_SECTION_KEY, "The root cause of this issue was identified by the MergeBase SCA analysis.  Refer to MergeBase for further information", null))
				.addDescriptionSection(createDescriptionSection(HOW_TO_FIX_SECTION_KEY, "To fix this issue, please refer to the MergeBase UI", null))
				.setTags("sca","mergebase")
				.setType(RuleType.VULNERABILITY)
				.setSeverity(Severity.CRITICAL);
		rule.setDebtRemediationFunction(rule.debtRemediationFunctions().linearWithOffset("1h","1h"));
		
		repository.done();
				
	}
	
	private static RuleDescriptionSection createDescriptionSection(String sectionKey, String htmlContent, org.sonar.api.server.rule.Context context)
	{
		return RuleDescriptionSection.builder().sectionKey(sectionKey).htmlContent(htmlContent).context(context).build();
	}

}
