package org.sonarsource.plugins.neil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.StringTokenizer;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextPointer;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.plugins.neil.MergeBaseChecker.MergeBaseResult;

public class MergeBaseSensor implements Sensor {
	
	private final MergeBaseChecker checker;
	private static final Logger LOGGER = Loggers.get(MergeBaseSensor.class);
	
	public MergeBaseSensor(MergeBaseChecker checker)
	{
		this.checker = checker;
	}

	@Override
	public void describe(SensorDescriptor descriptor) {
		descriptor.name("Detect issues found by MergeBase scan");
		descriptor.createIssuesForRuleRepositories(MergeBaseRulesDefinition.REPOSITORY);
	}

	@Override
	public void execute(SensorContext context) {
		String mergeBaseFile = context.config().get("mergebase.output").orElse(null);
		int mergeBaseThreshold = context.config().getInt("mergebase.threshold").orElse(75);
		checker.setThreshold(mergeBaseThreshold);
		if(mergeBaseFile == null)
			LOGGER.warn("mergebase.output parameter not specified, no dependency checking will be done");
		else
		{
			FileSystem fs = context.fileSystem();
			File workingDir = fs.workDir();
			LOGGER.warn("*************MergeBasePlugin working dir is "+workingDir+"***********");
			File baseDir = workingDir.getParentFile().getParentFile();
			LOGGER.warn("*************MergeBasePlugin base dir is "+baseDir+"***********");
			File[] baseDirFiles = baseDir.listFiles();
			boolean found = false;
			File file = null;
			for(int i = 0; i < baseDirFiles.length && !found; i++)
			{
				File f = baseDirFiles[i];
				if(f.getName().equals(mergeBaseFile))
				{
					found = true;
					file = f;
				}
			}
			//FilePredicate fp = fs.predicates().hasPath(mergeBaseFile);
			//if(fp == null)
				
			//InputFile file = fs.inputFile(fs.predicates().hasPath(mergeBaseFile));
			//if(file == null)
			//	file = fs.inputFile(fs.predicates().hasFilename(mergeBaseFile));
			if(file == null)
				LOGGER.error("mergebase.output file "+mergeBaseFile+" can't be found");
			else
			{
				LOGGER.info("proceeding with mergebase.output file: "+mergeBaseFile);
				try {
					String fileContents = readFile(file);
					List<MergeBaseResult> results = checker.call(fileContents);
					InputFile pom = fs.inputFile(fs.predicates().hasRelativePath("pom.xml"));
					if(pom == null)
						LOGGER.error("Could not find pom.xml");
					else
					{
						LOGGER.error("found pom.xml");
						for(MergeBaseResult result: results)
						{
							int startLine = findLineNumber(pom,result)-1;
							if(startLine > 0)
							{
								TextRange range = pom.newRange(startLine, 0, startLine+4, 17);
								NewIssue newIssue = context.newIssue().forRule(MergeBaseRulesDefinition.MERGEBASE_RULE);
								NewIssueLocation primaryLocation = newIssue.newLocation()
										.on(pom)
										.at(range)
										.message("Vulernability "+result.getVuln()+" has severity "+result.getSeverity()+" on "+result.getArtifacts().get(0));
								newIssue.at(primaryLocation);
								newIssue.save();
							} else
								LOGGER.error("Couldn't find "+result.getArtifacts().get(0)+" in pom.xml");
							
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}

	private int findLineNumber(InputFile pom, MergeBaseResult result) throws IOException {
		String artifact = result.getArtifacts().get(0);
		StringTokenizer st = new StringTokenizer(artifact,"/");
		String groupId = st.nextToken();
		InputStream in = pom.inputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		int count = 1;
		while((line = br.readLine()) != null)
		{
			if(line.contains(groupId))
				return count;
			count++;
		}
		return -1;
	}

	//private String readFile(InputFile file) throws IOException {
	private String readFile(File file) throws IOException {
		StringBuilder sb = new StringBuilder();
		//InputStream in = file.inputStream();
		InputStream in = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		while((line = br.readLine()) != null)
		{
			sb.append(line+System.lineSeparator());
		}
		br.close();
		in.close();
		return sb.toString();
	}

}
