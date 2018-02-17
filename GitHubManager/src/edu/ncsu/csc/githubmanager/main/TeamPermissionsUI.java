package edu.ncsu.csc.githubmanager.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.kohsuke.github.GHOrganization.Permission;

import edu.ncsu.csc.githubmanager.manager.GitHubManager;

/**
 * Update Team Permissions using information provided in the config.properties
 * file
 * 
 * @author Sarah Heckman
 * @author Jason King
 *
 */
public class TeamPermissionsUI {

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
	 * Starts the execution of the GitHubManager program for updating team
	 * permissions.
	 * 
	 * @param args
	 *            - command-line argument for team name prefix
	 */
	public static void main(String[] args) {
		// Output usage instructions
		if (args.length != 1) {
			System.out.println("Usage: java -jar GitHubManager.jar <repository pattern>");
		}

		// Create a new Properties object
		Properties prop = new Properties();

		// Load the content from the config.properties file
		try (InputStream input = new FileInputStream(PROP_FILE)) {
			prop.load(input);

			// Create a new instance of GitHubManager with the correct
			// properties
			GitHubManager m = new GitHubManager(API_URL, prop.getProperty("token"), prop.getProperty("org"));

			// UPDATE PERMISSIONS
			// - call updateTeamPermissions with a prefix for the teams to
			// update permissions for (provided as a command-line argument), and
			// the permission that needs to be applied
			m.updateTeamPermissions(args[0], Permission.PULL);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
