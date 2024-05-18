This java project links to an SQL database and uses the terminal as a user interface to access employee lists and other informatin.

These SQL queries include:
- Matching Employee SSN to the employee table
- Check whether the employee is a department manager by joining employee table with department table.
- Check whether the employee has any dependents and list them by joining employee table with dependent table.
- Check the projects the employee has worked on by joining employee table with working_on table and projects table.
- If the employee has worked on any projects, find the other employees who have worked on all the projects this employee has worked on. This uses SQL division with 
"NOT EXISTS" to retrieve results.

To run, create SQL database from company.txt file then update the line in Bonus.java below (line 13) with corresponding values.

Connection conn = DriverManager.getConnection("SQL_URL_HERE", "USERNAME_HERE", "PASSWORD_HERE");