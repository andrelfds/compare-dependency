package controller;

import java.util.List;

public class gradle {
	
		
	public static final String CURRENT_PROJECT_DIR = ".";
    private static String gradleInstallationDir = "C:\\softwares\\gradle-2.1-all\\gradle-2.1";
    private static String projectDir = ".";
    private static GradleConnector connector = new GradleConnector(gradleInstallationDir, projectDir);
    
    public static void main(String[] args) {
    	 List<String> dependencies = connector.getProjectDependencyNames();
    	 System.out.println(dependencies);
	}



}
