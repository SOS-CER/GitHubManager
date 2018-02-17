package edu.ncsu.csc.githubmanager.data;

import java.util.ArrayList;
import java.util.List;

/**
 * The repository assignment information,
 * including GitHub Team name and list of 
 * team member IDs.
 * @author Jason King
 *
 */
public class RepositoryAssignment {

	private String teamName;
	private List<String> teamMembers;
	
	/**
	 * Create a new repository assignment POJO
	 */
	public RepositoryAssignment()
	{
		teamMembers = new ArrayList<String>();
	}
	
	/**
	 * Return the team name
	 * @return the team name
	 */
	public String getTeamId() {
		return teamName;
	}
	
	/**
	 * Set the team name
	 * @param teamId - the name of the team
	 */
	public void setTeamId(String teamId) {
		this.teamName = teamId;
	}
	
	/**
	 * Return the list of team member IDs
	 * @return the list of team member IDs
	 */
	public List<String> getTeamMembers() {
		return teamMembers;
	}

	/**
	 * Add a team member ID to the list of team members
	 * @param teamMember - the team member ID to add to the list
	 */
	public void addTeamMember(String teamMember)
	{
		teamMembers.add(teamMember);
	}
	
}
