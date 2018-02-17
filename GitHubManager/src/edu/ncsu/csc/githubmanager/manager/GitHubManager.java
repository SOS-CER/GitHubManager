package edu.ncsu.csc.githubmanager.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.kohsuke.github.GHCreateRepositoryBuilder;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHOrganization.Permission;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.HttpException;
import edu.ncsu.csc.githubmanager.data.RepositoryAssignment;
import edu.ncsu.csc.githubmanager.io.RepositoryAssignmentLoader;

/**
 * The GitHubManager program allows you to: (1) assign students to GitHub teams
 * and repositories by providing a formatted input file of repository assignments;
 * and (2) update permissions (read, write, admin) for GitHub teams
 * 
 * When assigning repositories, students are added to GitHub teams that have the
 * same name as the GitHub repository they will be assigned. Using GitHub teams
 * allows fine-grained permissions and access controls.
 * 
 * @author Jason King
 * @author Sarah Heckman
 *
 */
public class GitHubManager {

	/**
	 * The GitHub instance
	 */
	private GitHub gitHub;

	/**
	 * The GitHub organization where the teams/repositories are located
	 */
	private GHOrganization org;

	/**
	 * Sleep duration to throttle API requests
	 */
	private final long SLEEP_DURATION = 500;

	/**
	 * Initialize the GitHub connection
	 * 
	 * @param apiUrl
	 *            - the URL for the GitHub API
	 * @param token
	 *            - your personal access token
	 * @param orgName
	 *            - the GitHub organization you are working with
	 * @throws IOException if there is an error sending/receiving information from GitHub
	 */
	public GitHubManager(String apiUrl, String token, String orgName) throws IOException {
		checkToken(token);
		try {
			gitHub = GitHub.connectToEnterprise(apiUrl, token);
		} catch (HttpException e) {
			throw new IllegalArgumentException("Make sure you have provided a correct API URL and TOKEN");
		}
		org = gitHub.getOrganization(orgName);
		System.out.println("WORKING WITH " + org.getLogin());
	}

	/**
	 * Checks the token to ensure it has been provided
	 * 
	 * @param token
	 *            - the GitHub access token
	 */
	private void checkToken(String token) {
		if (token == null || token.length() == 0) {
			throw new IllegalArgumentException("Make sure you provide your personal access token!");
		}
	}

	/**
	 * Assign repositories given the input file that contains the formatted list
	 * of repository names and student IDs
	 * 
	 * @param filePath
	 *            - path to the file that contains the repository assignments
	 * @throws IOException if there is an error sending/receiving information from GitHub
	 * @throws InterruptedException if the thread is interrupted while sleeping
	 */
	public void assignRepositories(String filePath) throws IOException, InterruptedException {
		// Use this list to keep track of the students who were not able to be
		// added to a team/repository
		List<String> usersNotAdded = new ArrayList<String>();

		// Get the team/repository assignments from the text file
		List<RepositoryAssignment> raList = RepositoryAssignmentLoader.load(filePath);

		// Get all of the currently-existing teams for the organization
		Map<String, GHTeam> teamMap = org.getTeams();

		// Loop to assign the desired repositories
		for (RepositoryAssignment r : raList) {
			// Get the team object that has the given teamID
			GHTeam team = teamMap.get(r.getTeamId());

			// If the team does not currently exist, automatically create it
			if (team == null) {
				System.out.println("\tTEAM: " + r.getTeamId() + " NOT FOUND!");
				team = createTeam(r.getTeamId());
			}

			// Get the set of existing members in the team
			Set<GHUser> existingMembers = team.getMembers();

			// Keep tack of who we add to each team
			// so that we can remove people from the team
			// if they were existing, but not in the new
			// list of team members
			Set<GHUser> newMembers = new HashSet<GHUser>();

			// For each team member assigned to the team
			for (String user : r.getTeamMembers()) {
				// TRY because FileNotFoundException here means the
				// user could not be found on GitHub
				try {
					// Get the user object based on the user ID
					GHUser u = gitHub.getUser(user);

					if (!existingMembers.contains(u)) {
						// If the user was not already a member of the team, add
						// the user
						team.add(u);
						System.out.println("ADDED " + user + " " + u.getName() + " to " + team.getName());
					} else {
						// Otherwise, indicate the user was already a member of
						// the team
						System.out.println(user + " ALREADY A MEMBER of " + team.getName());
					}
					// Add the user to the list of new members
					newMembers.add(u);
				} catch (FileNotFoundException e) {
					// FileNotFoundException means user was not found in GitHub.
					// Add the user to the list of users not found
					System.out.println("NOT FOUND: " + user);
					usersNotAdded.add(user + " not found in GitHub, not added to " + team.getName());
				}
			}

			// Make sure to remove any existing members who were
			// not members of the team in the new repo assignments
			synchronizeMembers(team, existingMembers, newMembers);

			// Limit how frequently we hit the API
			Thread.sleep(SLEEP_DURATION);
		}

		// Print out the list of errors so that you can do manual checks
		// and remind students to login or create accounts
		System.out.println("\nUSERS NOT ADDED TO TEAMS: ");
		for (String na : usersNotAdded) {
			System.out.println("\t" + na);
		}
	}

	/**
	 * Removes existing team members who are no longer part of the team.
	 * 
	 * @param team
	 *            - the team whose member list is being synchronized
	 * @param existingMembers
	 *            - set of existingMembers before updating
	 * @param newMembers
	 *            - set of members after updating
	 * @throws IOException
	 */
	private void synchronizeMembers(GHTeam team, Set<GHUser> existingMembers, Set<GHUser> newMembers)
			throws IOException {
		for (GHUser user : existingMembers) {
			if (!newMembers.contains(user)) {
				team.remove(user);
				System.out.println("REMOVED " + user.getLogin() + " from " + team.getName());
			}
		}
	}

	/**
	 * Updates team permissions for the repository. Prefix represents the first
	 * part of the team name, such as: "csc316-001-P1" will match all teams
	 * whose names begin with that prefix, but will not affect other teams in
	 * the organization
	 * 
	 * @param prefix
	 *            - team name prefix, such as "CSC316-001-P1"
	 * @param permission
	 *            - permission to set for the team's repo
	 * @throws IOException if there is an error sending/receiving information from GitHub
	 * @throws InterruptedException if the thread is interrupted while sleeping
	 */
	public void updateTeamPermissions(String prefix, GHOrganization.Permission permission)
			throws IOException, InterruptedException {
		// Get all the teams for the organization
		Map<String, GHTeam> teamMap = org.getTeams();

		// Get all the repos for the organization
		Map<String, GHRepository> repoMap = org.getRepositories();

		// For each repository, set the permission
		for (String repo : repoMap.keySet()) {
			if (repo.startsWith(prefix)) {
				GHTeam team = teamMap.get(repo);
				System.out.println("\tTEAM MATCHED: " + team.getName());
				System.out.println("\tEXISTING PERMISSION: " + team.getPermission());

				team.add(repoMap.get(repo), permission);
				System.out.println("\tUPDATED PERMISSION:\t" + team.getName() + "\t" + repo + "\t" + permission.name());
				Thread.sleep(SLEEP_DURATION);
			}
		}
	}

	/**
	 * Creates a team with the provided team name
	 * 
	 * @param teamName
	 *            - the name of the team
	 * @return the GitHub Team object that represents the team that was created
	 */
	public GHTeam createTeam(String teamName) {
		GHTeam t = null;
		try {
			GHCreateRepositoryBuilder nr = org.createRepository(teamName);
			// Initialize the repository with a README
			nr.autoInit(true);
			// Make the repository private
			nr.private_(true);
			GHRepository r = nr.create();
			System.out.println("\tCREATED REPO: " + nr.toString());

			t = org.createTeam(teamName, Permission.PUSH, r);
			System.out.println("\tCREATE TEAM: " + t.toString());

			// Set the Team permission to write/PUSH access
			t.add(r, Permission.PUSH);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return t;
	}
}
