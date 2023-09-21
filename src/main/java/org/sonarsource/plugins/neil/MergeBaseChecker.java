package org.sonarsource.plugins.neil;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.sonar.api.scanner.ScannerSide;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

@ScannerSide
public class MergeBaseChecker {
	private static final Logger LOGGER = Loggers.get(MergeBaseChecker.class);
	private int threshold = 75;

	public List<MergeBaseResult> call(String json)
	{
		List<MergeBaseResult> errorResults = new ArrayList<MergeBaseResult>();
		Gson gson = new Gson();
		try
		{
			MergeBaseResults mbr = gson.fromJson(json, MergeBaseResults.class);
			List<MergeBaseResult> results = mbr.getResults();
			for(MergeBaseResult result: results)
			{
				String vuln = result.getVuln();
				int severity = result.getSeverity();
				String artifact = result.getArtifacts().get(0);
				if(severity >= threshold)
				{
					LOGGER.info("\n\nMergeBase result: "+artifact+" has severity of "+severity+" with vulnerability "+vuln);
					errorResults.add(result);
				}
			}
		} catch (JsonSyntaxException e)
		{
			LOGGER.error("MergeBase Plugin currently only supports json output files from MergeBase");
		}
		return errorResults;
	}
	
	public void setThreshold(int threshold)
	{
		this.threshold = threshold;
	}
	
	class MergeBaseResults
	{
		List<MergeBaseResult> results;
		String version;
		
		public MergeBaseResults()
		{
			
		}
		
		public MergeBaseResults(List<MergeBaseResult> results, String version)
		{
			this.results = results;
			this.version = version;
		}

		public List<MergeBaseResult> getResults() {
			return results;
		}

		public void setResults(List<MergeBaseResult> results) {
			this.results = results;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}
	}
	
	class MergeBaseResult
	{
		String vuln;
		int severity;
		int cvss2;
		int cvss3;
		String fixVersion;
		String maxVulnerabilityVersion;
		List<String> artifacts;
		List<String> paths;
		String href;
		String description;
		transient Object components;
		
		public MergeBaseResult()
		{
			
		}
		
		public MergeBaseResult(String vuln, int severity, int cvss2, int cvss3,
				String fixVersion, String maxVulnerabilityVersion,
				List<String> artifacts, List<String> paths, String href,
				String description, Object components)
		{
			this.vuln = vuln;
			this.severity = severity;
			this.cvss2 = cvss2;
			this.cvss3 = cvss3;
			this.fixVersion = fixVersion;
			this.maxVulnerabilityVersion = maxVulnerabilityVersion;
			this.artifacts = artifacts;
			this.paths = paths;
			this.href = href;
			this.description = description;
			this.components = components;
		}

		public String getVuln() {
			return vuln;
		}

		public void setVuln(String vuln) {
			this.vuln = vuln;
		}

		public int getSeverity() {
			return severity;
		}

		public void setSeverity(int severity) {
			this.severity = severity;
		}

		public int getCvss2() {
			return cvss2;
		}

		public void setCvss2(int cvss2) {
			this.cvss2 = cvss2;
		}

		public int getCvss3() {
			return cvss3;
		}

		public void setCvss3(int cvss3) {
			this.cvss3 = cvss3;
		}

		public String getFixVersion() {
			return fixVersion;
		}

		public void setFixVersion(String fixVersion) {
			this.fixVersion = fixVersion;
		}

		public String getMaxVulnerabilityVersion() {
			return maxVulnerabilityVersion;
		}

		public void setMaxVulnerabilityVersion(String maxVulnerabilityVersion) {
			this.maxVulnerabilityVersion = maxVulnerabilityVersion;
		}

		public List<String> getArtifacts() {
			return artifacts;
		}

		public void setArtifacts(List<String> artifacts) {
			this.artifacts = artifacts;
		}

		public List<String> getPaths() {
			return paths;
		}

		public void setPaths(List<String> paths) {
			this.paths = paths;
		}

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Object getComponents() {
			return components;
		}

		public void setComponents(Object components) {
			this.components = components;
		}
		
		
	}
}
