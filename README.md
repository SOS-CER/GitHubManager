# GitHubManager

## Table of Contents
* [Setup a GitHub Personal Access Token](#setup-a-github-personal-access-token)
* [Add Users to GitHub Teams](#add-users-to-github-teams)
* [Update Team Permissions](#update-team-permissions)
* [Cloning Repositories](#cloning-all-repositories)

### Configuration File
All local configurations should be stored in a file named `config.properties`.  Copy the `config.properties.empty` and create a new file `config.properties`.

  * `token`: Set the `token` as described below.
  * `org`: the GitHub organization that you're working with
  * `path`: the path to the repository assignment file for adding teams to the repo.  This is not needed for changing permissions.


### Setup a GitHub Personal Access Token
* In GitHub, go to your user settings.
* Click "personal access tokens" in the left side menu
* Add a new "personal access token", but **SAVE THE TOKEN SOMEWHERE** because you will only see it once
* Add your token to the `config.properties` field for TOKEN.
	 
### Add Users to GitHub Teams
Team assignments are provided in an input file. The input file must follow this format:
     
     teamName[whitespace]userID1[whitespace]userID2[whitespace]...

where each team must have at least one team member separated by whitespace

For example:

     csc316-001-P1-001 jtking sesmith5 jdyoung2
     csc316-001-P1-002 jtking
     csc316-001-P1-003 sesmith5 jdyoung2
	 
Update the `config.properties` file PATH to indicate the path to your input file with your team assignments.

Run `AssignRepositoriesUI.java` to create/assign your repositories and teams.

After adding users to teams, the script will output the user IDs and teams of users that were not found in GitHub search so that you can manually check spellings or follow-up with users who many not have logged-in (or created an account) in GitHub yet.

### Update Team Permissions
Team permissions can be updated for all teams that match a given prefix. For example, prefix `csc316-001-P1` will match all of the following teams, but will not update permissions for any other teams:

     csc316-001-P1-001
     csc316-001-P1-002
     csc316-001-P1-003

Run `TeamPermissionsUI.java` by providing a command-line argument for the prefix to use when searching for teams. By default, the team permissions will be echanged to PULL (read access), which prevents users from pushing any additional commits to their repository.

```  
java TeamPermissionsUI csc316-001-P1
```

You can also update the `TeamPermissionsUI.java` code to use a different permission. Permission options are:

     Permission.PUSH (write permission)
     Permission.PULL (read permission)
     Permission.ADMIN (admin permission)

### Cloning All Repositories
**NOTE:** The clone script (Python script) has not been integrated into the Eclipse Java project yet. To run the clone script:
  * Make sure you have setup SSH keys and added those to your GitHub profile
  * Edit `clone_repos.py` to update the clone commands and the following variables, assuming your repositories use the following naming convention: `cscXXX-YYY-ID-ZZ` where `XXX` is the course number (like 116, 216, 316); `YYY` is the section number (like 001, 002, 601); ID is an assignment identifier (like P1, HW1, Lab1); and `ZZ` is an identifier for the the repository (like 01, 02, 03,... 001, 002, 003, ...).

```python
SECTION = "002"                  // The Course Section
CLASS = "csc316-"                // The Course prefix for the repository
ORG = "engr-csc316-fall2017"     // The organization the repositories are located in
ASSIGNMENT = "P1"                // Assignment identifier portion of the repository URL
NUMSTUDENTS = 80                 // Number of repositories that match the above criteria
```

* Save your changes, then compile and execute the `clone_repos.py` script to clone all of the repositories that match the criteria above.
