package edu.ncsu.csc.githubmanager.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.ncsu.csc.githubmanager.data.RepositoryAssignment;

public class RepositoryAssignmentLoader {

	/**
	 * Loads the repository assignments from the input file specified
	 * @param filePath - the path to the input file of repository assignments
	 * @return the list of repository assignments
	 */
	public static List<RepositoryAssignment> load(String filePath)
	{
		List<RepositoryAssignment> repoList = new ArrayList<RepositoryAssignment>();
		try(Scanner scan = new Scanner(new FileInputStream(filePath)))
		{
			System.out.println();
			while(scan.hasNextLine())
			{
				repoList.add(processLine(scan.nextLine()));
				System.out.println();
			}
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Repository assignment file not found.");
		}
		return repoList;
	}
	
	/**
	 * Process one repository assignment line in the input file
	 * @param line - a line from the input file
	 * @return the object representing the current RepositoryAssignment
	 */
	private static RepositoryAssignment processLine(String line)
	{
		System.out.print("Loading:");
		RepositoryAssignment ra = new RepositoryAssignment();
		try(Scanner scan = new Scanner(line))
		{
			String id = scan.next().trim();
			System.out.print("\t" + id + " ");
			ra.setTeamId(id);
			while(scan.hasNext())
			{
				String user = scan.next().trim();
				System.out.print("\t" + user + " ");
				ra.addTeamMember(user);
			}
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return ra;
	}
}
