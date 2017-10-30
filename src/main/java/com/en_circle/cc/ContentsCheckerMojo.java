package com.en_circle.cc;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "cc", defaultPhase=LifecyclePhase.PACKAGE)
public class ContentsCheckerMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.artifact}", required = true, readonly = true)
	private Artifact artifact;

	@Parameter
	private String[] mustContain;

	@Parameter
	private String[] mustNotContain;

	@Parameter(property = "maven.contents-checker.skip", defaultValue = "false")
	private boolean skip;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skip) {
			getLog().info("Skipping checking contents of file");
		} else {
			File file = artifact.getFile();
			getLog().info("Checking contents of " + file);
			if (!file.exists()) {
				getLog().error(file + " does not exist");
			} else {
				Set<String> pathnames = buildPathnames(file);
				if (mustContain != null && mustContain.length > 0) {
					getLog().info("Checking for files that must be contained by the archive");
					for (String pathname : mustContain) {
						if (!pathnames.contains(pathname)) {
							throw new MojoExecutionException(file + " is missing required pathname " + pathname);
						}
						getLog().info("File contains " + pathname);
					}
				}
				if (mustNotContain != null && mustNotContain.length > 0) {
					getLog().info("Checking for files that must not be contained by the archive");
					for (String pathname : mustContain) {
						if (pathnames.contains(pathname)) {
							throw new MojoExecutionException(file + " is contains pathname " + pathname);
						}
						getLog().info("File does not contains " + pathname);
					}
				}
			}
			getLog().info("File contents checked successfully");
		}
	}

	@SuppressWarnings("unchecked")
	private Set<String> buildPathnames(File file) throws MojoExecutionException {
		ZipFile zp = null;
		try {
			Set<String> pathEntries = new HashSet<String>();
			zp = new ZipFile(file);
			for (Enumeration<ZipEntry> e = (Enumeration<ZipEntry>) zp.entries(); e.hasMoreElements();) {
				ZipEntry entry = e.nextElement();
				String name = entry.getName();
				pathEntries.add(name);
			}
			return pathEntries;
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to open zip file", e);
		} finally {
			if (zp != null)
				try {
					zp.close();
				} catch (IOException e) {
					throw new MojoExecutionException("Failed to close zip file", e);
				}
		}
	}

}
