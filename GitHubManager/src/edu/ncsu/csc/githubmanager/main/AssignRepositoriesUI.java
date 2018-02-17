package edu.ncsu.csc.githubmanager.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import edu.ncsu.csc.githubmanager.manager.GitHubManager;

/**
 * Assigns repositories using the information in 
 * the config.properties file
 * 
 * @author Sarah Heckman
 * @author Jason King
 */
public class AssignRepositoriesUI {

	/**
	 * GitHub API URL
	 */
	private static final String API_URL = "https://github.ncsu.edu/api/v3/";

	/**
	 * The path to the configuration properties file, which contains your GitHub
	 * access token, GitHub organization name, and path to the input file of
	 * repository assignments
	 */
	private static final String PROP_FILE = "config.properties";

	/**
	 * Starts the execution of the GitHubManager program for assigning repositories
	 * 
	 * @param args
	 *            - command-line arguments, which are not used in this version
	 */
	public static void main(String[] args) {
		// Create a new Properties object
		Properties prop = new Properties();

		// Load the content from the config.properties file
		try (InputStream input = new FileInputStream(PROP_FILE)) {
			prop.load(input);

			// Create a new instance of GitHubManager with the correct
			// properties
			GitHubManager m = new GitHubManager(API_URL, prop.getProperty("token"), prop.getProperty("org"));

			// ASSIGN REPOSITORIES
			// - call assignRepositories with the path to the input file that
			// contains the repo assignments
			m.assignRepositories(prop.getProperty("path"));

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
