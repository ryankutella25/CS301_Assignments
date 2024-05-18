import java.util.Scanner;

import java.sql.*;

public class Bonus {
    public static void main(String[] args) throws Exception {

        //These lines below just for connection to server
        try { Class.forName("oracle.jdbc.driver.OracleDriver");}
        catch (ClassNotFoundException x){
            System.out.println("Driver could not be loaded");
        }
        Connection conn = DriverManager.getConnection("SQL_URL_HERE", "USERNAME_HERE", "PASSWORD_HERE");
        //It is now connected

        //prepared statement for checking if employee exists
        String lname, fname;
        String s1 = "select fname, lname from EMPLOYEE where Ssn = ?";
        PreparedStatement p1 = conn.prepareStatement(s1);

        //prepared statement for checking if employee is a manager
        String department;
        String s2 = "select dname from DEPARTMENT where MGRSSN = ?";
        PreparedStatement p2 = conn.prepareStatement(s2);

        //prepared statement for checking the dependents of employee
        String dependentName;
        String s3 = "select dependent_name from DEPENDENT where ESSN = ?";
        PreparedStatement p3 = conn.prepareStatement(s3);

        //for checking the projects worked on by the employee
        String projectName;
        String s4 = "select pname from WORKS_ON, PROJECT where ESSN = ? AND PNO = PNUMBER";
        PreparedStatement p4 = conn.prepareStatement(s4);

        //for checking other employees that worked on same projects
        String efname, elname;
        String s5 = "SELECT fname, lname From EMPLOYEE Where NOT exists (( "
        + " select pnumber from WORKS_ON, PROJECT where ESSN = ? AND PNO = PNUMBER ) Minus ( "
        + " Select Pno from Works_ON Where ESSN=SSN AND ESSN != ? )) ";
        PreparedStatement p5 = conn.prepareStatement(s5);

        //Scanner is just for reading in SSN, ssn string stores the input
        Scanner scan = new Scanner(System.in);
        String ssn;

        System.out.println();

        //Loop that runs until input = 0;
        while(true){
            System.out.print("Enter an SSN: ");
            ssn = scan.nextLine();

            System.out.println();

            if(ssn.equals("0")) {
                System.out.print("Good Bye!\n");
                break;
            }
            
            //setup and run p1 query to check if valid employee
            p1.clearParameters();
            p1.setString(1, ssn);
            ResultSet r1 = p1.executeQuery();

            if(!r1.next()){ //is not an employee
                System.out.println("No Employee with SSN="+ssn+".\n");
            }
            else{   //is an employee so go through 4 tasks
                fname = r1.getString(1);
                lname = r1.getString(2);

                //check if manager
                p2.clearParameters();
                p2.setString(1, ssn);
                ResultSet r2 = p2.executeQuery();
                if(!r2.next()){ //is not manager
                    System.out.println(fname + " "+ lname + " is not a manager.\n");
                }
                else{   //is manager
                    department = r2.getString(1);
                    System.out.println(fname + " "+ lname + " is manager of the " + department+" department.\n");
                }

                //check for dependents 
                p3.clearParameters();
                p3.setString(1, ssn);
                ResultSet r3 = p3.executeQuery();
                if(!r3.next()){ //has no dependents
                    System.out.println(fname + " "+ lname + " has no dependents.\n");
                }
                else{   //has dependents
                    String output1 = "";
                    Integer count = 0;

                    do {    //count the number of dependents and add their names to output1
                        dependentName = r3.getString(1);
                        output1 += "\t" + dependentName + "\n";
                        count++;
                    }
                    while(r3.next());   //go through all dependents

                    output1 = fname + " "+ lname + " has the following " + count + " dependents. \n" + output1;
                    
                    System.out.println(output1);
                }

                //check for projects 
                p4.clearParameters();
                p4.setString(1, ssn);
                ResultSet r4 = p4.executeQuery();

                Integer count1 = 0;
                
                if(!r4.next()){ //has no projects
                    System.out.println(fname + " "+ lname + " has no dependents.");
                }
                else{   //has projects

                    String output2 = "";

                    do {
                        projectName = r4.getString(1);
                        output2 += "\t" + projectName + "\n";
                        count1++;
                    }
                    while(r4.next());

                    output2 = fname + " "+ lname + " has worked on the following " + count1 + " projects. \n" + output2;
                    
                    System.out.println(output2);
                }

                //check others who have worked on same projects (only if this employee has worked on projects)
                if(count1>0){

                    p5.clearParameters();  
                    p5.setString(1, ssn);
                    p5.setString(2, ssn);
                    ResultSet r5 = p5.executeQuery();
                    
                    String output3 = "";    //incase multiple people
                    Integer count2 = 0;

                    if(!r5.next()){ //has no projects123456789
                        System.out.println("No employee has worked on all the projects "+ fname + " " + lname + " has worked on.\n");
                    }
                    else{   
                        do{
                            efname = r5.getString(1);
                            elname = r5.getString(2);
                            output3 += "\t" + efname + " " + elname + "\n";
                            count2++;
                        }
                        while(r5.next());

                        System.out.println("The following " + count2 + " employees have worked on all the projects " + fname + " " + lname + " has worked on.\n" + output3);
                    }
                }

            }
        }
        scan.close(); //close scanner to prevent memory leaks
    }
}
